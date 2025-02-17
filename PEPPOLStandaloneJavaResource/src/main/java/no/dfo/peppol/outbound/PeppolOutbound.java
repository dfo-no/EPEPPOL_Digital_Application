package no.dfo.peppol.outbound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import jakarta.xml.bind.JAXBException;
import no.dfo.gui.controller.UtilityValuesGUI;
import no.dfo.peppol.common.functions.KeystoreManager;
import no.dfo.peppol.common.functions.MimeTypeEnum;
import no.dfo.peppol.common.ocsp.OCSPLookupApp;
import no.dfo.peppol.common.ocsp.UtilityValueOCSP;
import no.dfo.peppol.outbound.crypto.CMSEncryptData;
import no.dfo.peppol.outbound.metadata.CreateMetadata;
import no.dfo.peppol.outbound.sbdh.SBDHCreation;
import no.dfo.peppol.outbound.signing.SignASiCwithCAdES;
import no.dfo.peppol.peppolcustomexception.PeppolGeneralExceptions;
import no.dfo.peppol.peppolcustomexception.SigningError;

public class PeppolOutbound {
	public static UtilityValueSetting uv;
	private byte[] innerAsicBytes;
	private Logger Log;
	public String SwiftOrgnrPairs = "DNBANOKKXXX#0192:984851006;DNBANOKK#0192:984851006;NDEANOKK#0192:920058817;NDEASESS#0192:920058817";

	public PeppolOutbound(UtilityValueSetting uv) {
		super();
		this.uv = uv;
	}

	private Document updateReceiverIdentifier(Document doc, String orgnr) {

		NodeList list = doc.getElementsByTagNameNS("*", "Identifier");
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && i == 1) {
				node.setTextContent(orgnr);
				break;
			}
		}
		return doc;
	}

	private HashMap<String, String> convert(String str) {
		String[] tokens = str.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < tokens.length; i++) {
			String[] strings = tokens[i].split("#");
			if (strings.length == 2)
				map.put(strings[0], strings[1]);
		}
		return map;
	}

	private String getOrgnrForBIC(String bic, String swiftOrgnrs) {
		HashMap<String, String> map = convert(swiftOrgnrs);
		Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			if (entry.getKey().equals(bic))
				return entry.getValue();
		}
		return null;
	}

	private String createStandardBusinessDocument(String sbdh, String binaryContent) {
		// strip xml declaration and namespace from sbdh.xml
		StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<StandardBusinessDocument xmlns=\"http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader\">"
				+ sbdh.substring(sbdh.indexOf("<StandardBusinessDocumentHeader"))
						.replace(" xmlns=\"http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader\"", "")
				+ "<BinaryContent mimeType=\"application/vnd.etsi.asic-e+zip\" xmlns=\"http://peppol.eu/xsd/ticc/envelope/1.0\">"
				+ binaryContent + "</BinaryContent>" + "</StandardBusinessDocument>");
		return sb.toString();
	}

	public boolean PEPPOL_Outbound(UtilityValuesGUI uvg) throws Exception {
		for (int i = 0; i < uvg.getISOFiles().size(); i++) {
			uv.setFilename(uvg.getISOFiles().get(i).getName());
			try {
				String IsoString = FileUtils.readFileToString(uvg.getISOFiles().get(i), "UTF-8");
				uv.setIsocontent(IsoString);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log = uvg.getOuboundLog();
			Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + " PEPPOL Outbound Start:"
					+ uv.getFilename());
			UtilityValueOCSP ocsp = new UtilityValueOCSP();
			ocsp.setLog(Log);
			SBDHCreation sbdhCreate = new SBDHCreation();
			CreateMetadata metadataCreation = new CreateMetadata();

			try {
				metadataCreation.createMetadata(uv, uvg);
				sbdhCreate.createSBDH(uv, uvg.getSenderOrg(), uvg.getRecOrg());
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}

			KeystoreManager knKeystoreManagerSign = new KeystoreManager(uvg.getSignKeystorePath(),
					uvg.getSignkeystorePwd());
			KeystoreManager knKeystoreManagerEnc = null;

			if (uv.getCertUsed().equalsIgnoreCase("USER")
					&& uvg.getEncCertInputType().equalsIgnoreCase("EncryptionKeyStore")) {
				
				knKeystoreManagerEnc = new KeystoreManager(uvg.getEncKeystorePath(), uvg.getEnckeystorePwd());
				if(uvg.getAliasEnc().contains("demo") || uvg.getAliasSign().contains("demo")) {
					Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]"
							+ " Demo Cert OSCP not required ");
				}else {
					Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]"
							+ " OCSP Check for Signing cert Started ");
					ocsp.setCertPub(knKeystoreManagerSign.getPublicCertificate(uvg.getAliasSign()));
					OCSPLookupApp app = new OCSPLookupApp(ocsp);
					app.getIntermediateCert("User");
					Log.info(
							"[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + " OCSP Check for Signing cert Ends ");
					Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]"
							+ " OCSP Check for Encyption cert Started ");
					ocsp.setCertPub(knKeystoreManagerEnc.getPublicCertificate(uvg.getAliasEnc()));
					app.getIntermediateCert("User");
					Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]"
							+ " OCSP Check for Encyption cert Ended ");

				}
				
			} else if (uv.getCertUsed().equalsIgnoreCase("demo")) {
				knKeystoreManagerEnc = new KeystoreManager(uvg.getEncKeystorePath(), uvg.getEnckeystorePwd());
				Log.warning("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]"
						+ "Demo Certificate -No OSCP Required Skipping...");
			}
			FileFunction fn = new FileFunction();

			SignASiCwithCAdES signer = new SignASiCwithCAdES(knKeystoreManagerSign.readPrivateKey(uvg.getAliasSign()),
					knKeystoreManagerSign.getSigningCertificateChain(uvg.getAliasSign()));
			Document doc;
			// create document list for inner asic
			doc = fn.convertStringToDocument(uv.getIsocontent());
			if (null == doc)
				throw new RuntimeException("convertStringToDocument() - conversion error");
			NodeList list = doc.getElementsByTagNameNS("*", "MsgId");
			String messageId = "";
			String bic = "";
			String orgnr = "";

			try {
				messageId = list.item(0).getTextContent();
			} catch (NullPointerException nullEx) {
				Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]"
						+ "Message ID not found inserting Test Message ID ");
				messageId = "001";
			}
			System.out.println("\nConstruct messageId: " + messageId);
			Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + " MessageID " + messageId);

			// get SWIFT Code/BIC from pain.001
			list = doc.getElementsByTagNameNS("*", "BIC");
			try {
				bic = list.item(0).getTextContent();
				orgnr = this.getOrgnrForBIC(bic, SwiftOrgnrPairs);
				uvg.setRecOrg(orgnr.split(":")[1]);
				Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "BIC  Found BIC=" + bic
						+ ",Org Number=" + orgnr);
				uvg.getRecOrgFX().setText(uvg.getRecOrg());

			} catch (NullPointerException nullEx) {
				Log.warning("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]"
						+ "BIC Not Found in message Replacing with User Value:" + uvg.getRecOrg());
				bic = uvg.getRecOrg();
				orgnr = "0192:" + uvg.getRecOrg();
			}

			String timeStamp = new SimpleDateFormat("ddMMYYYYHHmmsssss").format(new java.util.Date());


			if (orgnr == null) {
				throw new PeppolGeneralExceptions("'BIC-Orgnr' not found");
				}
			doc = fn.convertStringToDocument(uv.getSbdh());
			doc = updateReceiverIdentifier(doc, orgnr);
			String newSbdh = fn.nodeToString(doc); // updated sbdh
			final String binaryContent;

			Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "Inner ASiC : Packaging Start ");

			List<DSSDocument> docsToSign = new ArrayList<>();

			// choose MimeType
			/*
			 * MimeType. appXml = new MimeType();
			 * appXml.setMimeTypeString("application/xml");
			 */
			
			
			Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "    Inner ASiC:Mimetype Added ");
			// add ISO content.xml
			InMemoryDocument inMemoryDocument1 = new InMemoryDocument(uv.getIsocontent().getBytes("UTF-8"));
			inMemoryDocument1.setName("content.xml");
			inMemoryDocument1.setMimeType(MimeTypeEnum.XMLAPP);
			docsToSign.add(inMemoryDocument1);
			Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "    Inner ASiC:Content.xml added");
			// add metadata.xml
			InMemoryDocument inMemoryDocument2 = new InMemoryDocument(uv.getMetadata().getBytes("UTF-8"));
			inMemoryDocument2.setName("metadata.xml");
			inMemoryDocument2.setMimeType(MimeTypeEnum.XMLAPP);
			docsToSign.add(inMemoryDocument2);
			Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "    Inner ASiC:Metadata.xml added");
			Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "    Inner ASiC:Signing Start");
			try (InputStream isInnerAsic = signer.signASiCSBaselineB(docsToSign, messageId, uvg, uv);) {
				Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "    Inner ASiC:Signing End");
				byte[] encryptedInnerAsicBytes = null;
				if (isInnerAsic.available() > 1000) {
					this.innerAsicBytes = new byte[isInnerAsic.available()];
					isInnerAsic.read(this.innerAsicBytes);
					CMSEncryptData cmsEncrypt = new CMSEncryptData(uvg, uv);
					Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "    Inner ASiC:Encryption Start");
					if (uvg.getEncCertInputType().equalsIgnoreCase("EncryptionKeyStore")
							|| uv.getCertUsed().equalsIgnoreCase("demo")) {
						encryptedInnerAsicBytes = cmsEncrypt.encryptDataRSA(innerAsicBytes,
								knKeystoreManagerEnc.getPublicCertificate(uvg.getAliasEnc()));
					} else if (uvg.getEncCertInputType().equalsIgnoreCase("X509Certificate")) {
						CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
						InputStream pubCert = new FileInputStream(uvg.getP12EncryptionStore());
						X509Certificate cert = (X509Certificate) certFactory.generateCertificate(pubCert);
						encryptedInnerAsicBytes = cmsEncrypt.encryptDataRSA(innerAsicBytes, cert);
					} else if (uvg.getEncCertInputType().equalsIgnoreCase("BCLDownload")) {

						uvg.setEncCertInputType("BCLDownload");
						BCLCertLoader bcl = new BCLCertLoader();
						try {
							X509Certificate bclCert = bcl.downloadCert(uvg);
							uvg.setBclCert(bclCert);

						} catch (XPathExpressionException e) {
							e.printStackTrace();
						} catch (CertificateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						}
						encryptedInnerAsicBytes = cmsEncrypt.encryptDataRSA(innerAsicBytes, uvg.getBclCert());
					}
					Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "    Inner ASiC:Encryption End");
				} else {
					Log.log(Level.SEVERE, "cloneStandardBusinessDocument() - error signing inner asic");
					throw new PeppolGeneralExceptions("cloneStandardBusinessDocument() - error signing inner asic");
				}
				String folderName=uv.getFilename() ;
				if(folderName.contains(".xml") ||folderName.contains(".XML") ) {
					folderName=uv.getFilename().substring(0, uv.getFilename().length()-4);
					}
				File ZipPackageInner = new File(uvg.getRepackPath() + File.separator + folderName+ File.separator
						+ uv.getFilename() + "_inner-asice.zip");
				
				FileUtils.writeByteArrayToFile(ZipPackageInner, innerAsicBytes);
				Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "Inner ASiC : Packaging End ");
				Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "Outer ASiC : Packaging Start ");
				docsToSign = new ArrayList<>();
				InMemoryDocument inMemoryDocument3 = new InMemoryDocument(encryptedInnerAsicBytes);
				inMemoryDocument3.setName("content.asice.p7m");
				inMemoryDocument3.setMimeType(MimeTypeEnum.ASICE);
				docsToSign.add(inMemoryDocument3);
				Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]"
						+ "    Outer ASiC : content.asice.p7m File added ");

				InMemoryDocument inMemoryDocument4 = new InMemoryDocument(newSbdh.getBytes("UTF-8"));
				inMemoryDocument4.setName("sbdh.xml");
				inMemoryDocument4.setMimeType(MimeTypeEnum.XMLAPP);
				docsToSign.add(inMemoryDocument4);
				Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]" + "    Outer ASiC : sbdh.xml File added ");

				// sign outer asice (*.zip)
				try (InputStream isOuterAsic = signer.signASiCSBaselineB(docsToSign, messageId, uvg, uv);) {
					if (isOuterAsic.available() > 1000) {
						byte[] outerAsicBytes = new byte[isOuterAsic.available()];
						isOuterAsic.read(outerAsicBytes);

						// StandardBusinessDocument-BinaryContent
						binaryContent = Base64.getEncoder().encodeToString(outerAsicBytes);
						final byte[] PackagezipDecoded = Base64.getDecoder().decode(binaryContent.getBytes("UTF-8"));

						// create StandardBusinessDocument
						newSbdh = createStandardBusinessDocument(newSbdh, binaryContent);
						SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
				        Date currentDate = new Date();
				        String currentDateString = dateFormat.format(currentDate);
						File SBDFile = new File(uvg.getRepackPath() + File.separator + folderName + File.separator
								+ uvg.getSenderOrg()+"_"+uvg.getRecOrg()+"_" +currentDateString+"_"+ uv.getFilename());
						FileUtils.writeStringToFile(SBDFile, newSbdh);
						File ZipPackage = new File(uvg.getRepackPath() + File.separator + folderName
								+ File.separator + uv.getFilename() + "_outer-asice.zip");
						Log.info("[" + uv.getFilename() + "_" + uvg.getTimestamp() + "]"
								+ "Outer ASiC : Packaging End : sbd.xml File Created successfully with name :"+uvg.getRepackPath() + File.separator + folderName + File.separator
								+ uvg.getSenderOrg()+"_"+uvg.getRecOrg()+"_" +currentDateString+"_"+ uv.getFilename());
						
						//
						FileUtils.writeByteArrayToFile(ZipPackage, PackagezipDecoded);

					} else {
						Log.log(Level.SEVERE, "Error :error in signing outer asic");
						throw new SigningError("Error signing outer asic");

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.log(Level.SEVERE, "Error :"+e.toString());
				throw new SigningError("cloneStandardBusinessDocument() - error signing inner asic");
				

			}
		}
		return true;
	}

}
