package no.dfo.peppol.inbound.inboundMain;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import no.dfo.gui.controller.UtilityValuesGUI;
import no.dfo.peppol.common.functions.CommonFunction;
import no.dfo.peppol.common.functions.KeystoreManager;
import no.dfo.peppol.inbound.crypto.CMSDecrypt;
import no.dfo.peppol.inbound.signverification.SignatureVerifier;
import no.dfo.peppol.outbound.UtilityValueSetting;
import no.dfo.peppol.outbound.asicmanifestreader.AsicManifestReader;
import no.dfo.peppol.outbound.asicmanifestreader.CalculateHash;
import no.dfo.peppol.peppolcustomexception.DecryptionError;
import no.dfo.peppol.peppolcustomexception.PeppolGeneralExceptions;
import no.dfo.peppol.peppolcustomexception.SBDHError;
import no.dfo.peppol.peppolcustomexception.SignatureValidationErrorInner;

public class PeppolInbound {
	Logger Log;
	public String getBinaryContent(Document doc) {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//*[local-name()='BinaryContent']/text()");
			StringBuilder sb = new StringBuilder(
					((String) expr.evaluate(doc, XPathConstants.STRING)).replace("\r\n", ""));
			return sb.toString();

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] getZipEntryByFileNameContent(byte[] compressed, String nameContent) {

		byte[] buffer = new byte[1024];

		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed)) {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				try (ZipInputStream zis = new ZipInputStream(byteArrayInputStream)) {
					ZipEntry zipentry = zis.getNextEntry();
					// get *.p7m
					while (null != zipentry && !zipentry.isDirectory()) {
						if (zipentry.getName().toLowerCase().contains(nameContent)) {
							int len;
							while ((len = zis.read(buffer)) > 0) {
								baos.write(buffer, 0, len);
							}
							break;
						}
						zipentry = zis.getNextEntry();
					}
					return baos.toByteArray();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("getZipEntryByFileNameContent() error", e);
		}
	}
	public byte[] getZipEntryByDirFileNameContent(byte[] compressed, String nameContent) {

		byte[] buffer = new byte[1024];

		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed)) {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				try (ZipInputStream zis = new ZipInputStream(byteArrayInputStream)) {
					ZipEntry zipentry = zis.getNextEntry();
					// get *.p7m
					while (null != zipentry) {
						if(zipentry.isDirectory()) {
							zipentry = zis.getNextEntry();
						}else
						if (zipentry.getName().toLowerCase().contains(nameContent.toLowerCase())) {
							int len;
							while ((len = zis.read(buffer)) > 0) {
								baos.write(buffer, 0, len);
							}
							break;
						}
						zipentry = zis.getNextEntry();
						
					}
					return baos.toByteArray();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("getZipEntryByFileNameContent() error", e);
		}
	}

	public ArrayList<String> listFilesForFolder(final File folder) {
		ArrayList<String> FileName = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				FileName.add(fileEntry.getName());
				Log.info(fileEntry.getName());
			}
		}
		return FileName;
	}

	public String PeppolInboundFunc(UtilityValuesGUI uvg,PeppolInbound PI) throws Exception{
		Log=uvg.getInboundLog();
		
	    

		try {
			
			 
			UtilityValueSetting uv=new UtilityValueSetting();
			Log.info("["+uvg.getFileName()+"_"+uvg.getTimestamp()+"]"+" Files Loaded");
			for(int i=0;i<uvg.getSBDFile().size();i++) {
				
			uv.setFilename(uvg.getFileName().get(i));
			
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" ISO FILE COLLECTION START FOR:" + uvg.getFileName().get(i) );
				
				
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+"SBD File Loaded" );
				String sbd = FileUtils.readFileToString(new File(uvg.getSBDFile().get(i).getAbsolutePath()), StandardCharsets.UTF_8).replace("\uFEFF", "") + "\n";
				if("efbbbf".equalsIgnoreCase(new String(Hex.encodeHex(sbd.substring(0, 3).getBytes())))) {
					sbd=sbd.substring(3);
				}
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

				dbf.setNamespaceAware(true);
				dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

				DocumentBuilder builder = dbf.newDocumentBuilder();
				Document doc = builder.parse(new InputSource(new StringReader(sbd)));
				
				
				doc.getDocumentElement().normalize();
				Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"Fetching Base64 Content" );
				String b64Content = PI.getBinaryContent(doc).replace("&#xd;", "").replaceAll("[\n]+", "");

				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Reading Binary Content End");
				
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Base64 Decode Start");
				final byte[] outerAsiceZipBytes = Base64.getDecoder().decode(b64Content.getBytes("UTF-8"));
				File OuterAsic=new File(uvg.getDecryptedPath().get(i)+File.separator+uvg.getFileName().get(i)+"_outer.zip");
				FileUtils.writeByteArrayToFile(OuterAsic, outerAsiceZipBytes);
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Base64 Decode End");
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" OuterAsic Function Start");
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Start to Unzip");
				byte[] enctyptedConten = PI.getZipEntryByFileNameContent(outerAsiceZipBytes, ".p7m");
				
				
				String SBDH= new String(PI.getZipEntryByFileNameContent(outerAsiceZipBytes, "sbdh.xml")).replace("\uFEFF", "") + "\n";
				if("efbbbf".equalsIgnoreCase(new String(Hex.encodeHex(SBDH.substring(0, 3).getBytes())))) {
					SBDH=SBDH.substring(3);
				}
				uv.setSbdh(SBDH);
				Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"SBD Comparision Start SBD and SBDH " );
				String compSBD=doc.getElementsByTagName("StandardBusinessDocumentHeader").item(0).getTextContent().trim().replaceAll("\\s", "");
				
				DocumentBuilderFactory sbdhComparision = DocumentBuilderFactory.newInstance();

				sbdhComparision.setNamespaceAware(true);
				sbdhComparision.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

				DocumentBuilder buildersbdhComparision = sbdhComparision.newDocumentBuilder();
				
				
				Document sbdhComparisionDoc = buildersbdhComparision.parse(IOUtils.toInputStream(SBDH,"UTF-8"));
				String compSBDH=sbdhComparisionDoc.getElementsByTagName("StandardBusinessDocumentHeader").item(0).getTextContent().trim().replaceAll("\\s", "");
				
				if(compSBD.equals(compSBDH)) {
					Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"SBD and SBDH Outer are Equal." );
				}else {
					Log.log(Level.SEVERE, "SBD and SBDH Comparision Failed ");
					throw new SBDHError("SBD and SBDH Comparision Failed ");
					
				}
				
				
				
				Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"SBD Comparision End SBD and SBDH " );
				
				
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Unzip End");
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Start Hash Value Checking Outer Asic");
				AsicManifestReader reader=new AsicManifestReader();
				ArrayList<String> outerList=reader.readOuterAsicmanifest(PI.getZipEntryByDirFileNameContent(outerAsiceZipBytes, "ASiCManifest"));
					if(outerList.get(0).equalsIgnoreCase("content.asice.p7m")) {
						
						String contentEncFileHash=CalculateHash.valueOf(outerList.get(1).toUpperCase().replace("-", "")).checkSum(enctyptedConten);
						String AsicContentHash=outerList.get(2);
						String SBDHEncFileHash=CalculateHash.valueOf(outerList.get(4).toUpperCase().replace("-", "")).checkSum(PI.getZipEntryByFileNameContent(outerAsiceZipBytes, "sbdh.xml"));
						String AsicSBDHHash=outerList.get(5);
						
						if(contentEncFileHash.equals(AsicContentHash)) {
							Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" ---Hash Value matched for "+outerList.get(0));
						}else {
							Log.log(Level.SEVERE, "Failed to match Hash value for:"+outerList.get(3));
							throw new Exception("Failed to match Hash value for:"+outerList.get(3));
						}
						
						if(SBDHEncFileHash.equals(AsicSBDHHash)) {
							Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" ---Hash Value matched for "+outerList.get(0));
						}else {
							Log.log(Level.SEVERE, "Failed to match Hash value for:"+outerList.get(3));
							throw new Exception("Failed to match Hash value for:"+outerList.get(3));
						}
						
						
						
					}else if(outerList.get(0).equalsIgnoreCase("sbdh.xml")) {
						String contentEncFileHash=CalculateHash.valueOf(outerList.get(4).toUpperCase().replace("-", "")).checkSum(enctyptedConten);
						String AsicContentHash=outerList.get(5);
						String SBDHEncFileHash=CalculateHash.valueOf(outerList.get(1).toUpperCase().replace("-", "")).checkSum(PI.getZipEntryByFileNameContent(outerAsiceZipBytes, "sbdh.xml"));
						String AsicSBDHHash=outerList.get(2);
						
						if(contentEncFileHash.equals(AsicContentHash)) {
							Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" ---Hash Value matched for "+outerList.get(3));
						}else {
							Log.log(Level.SEVERE, "Failed to match Hash value for:"+outerList.get(3));
							throw new Exception("Failed to match Hash value for:"+outerList.get(3));
						}
						
						if(SBDHEncFileHash.equals(AsicSBDHHash)) {
							Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" ---Hash Value matched for "+outerList.get(0));
						}else {
							Log.log(Level.SEVERE, "Failed to match Hash value for:"+outerList.get(0));
							throw new PeppolGeneralExceptions("Failed to match Hash value for:"+outerList.get(0));
						}
						
					}
					
				
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+"  Hash Value Checking Outer Asic End");
				
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Start Signature Verification Start");
				byte[] ASicFile = PI.getZipEntryByDirFileNameContent(outerAsiceZipBytes, "asicmanifest");
				byte[] signatureFile = PI.getZipEntryByDirFileNameContent(outerAsiceZipBytes, "signature");
				SignatureVerifier snSignatureVerifier =new SignatureVerifier();
				
				boolean verificationRes=snSignatureVerifier.SignVerify(ASicFile, signatureFile,"Outer_Asic");
				if(verificationRes) {
					Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+"  Signature Verification End");
					
				}else {
					Log.log(Level.SEVERE, "Signature Verification End with :"+verificationRes);
					throw new SignatureValidationErrorInner("Signature Verification End with :"+verificationRes);
				}
				
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Start Signature Verification End");
				
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Start to decrypt");
				
				KeystoreManager knKeystoreManager = new KeystoreManager(uvg.getKeystorePath(), uvg.getKeystorePwd());
				CMSDecrypt decrypt = new CMSDecrypt();
				InputStream decrypted = decrypt.decryptDataRSA(enctyptedConten,
						knKeystoreManager.readPrivateKey(uvg.getAlias()));
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Decryption End Successfully");
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Outer_Asic Function End");
				
				
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Collecting ISO File");
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();

				int nRead;
				byte[] data = new byte[16384];

				while ((nRead = decrypted.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}
				
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" Start Hash Value Checking Inner Asic");
				
				ArrayList<String> innerList=reader.readInnerAsicmanifest(PI.getZipEntryByDirFileNameContent(buffer.toByteArray(), "ASiCManifest"));
				
				if(innerList.get(0).equalsIgnoreCase("content.xml")) {
					String contHashEnum=innerList.get(1).toUpperCase();
					String metadataHashEnum=innerList.get(4).toUpperCase();
					if(innerList.get(1).contains("-")&& innerList.get(4).contains("-")) {
						contHashEnum=innerList.get(1).toUpperCase().replace("-", "");
						 metadataHashEnum=innerList.get(4).toUpperCase().replace("-", "");
						
					}
					
					
					String IsoFileHash=CalculateHash.valueOf(contHashEnum).checkSum(PI.getZipEntryByFileNameContent(buffer.toByteArray(), innerList.get(0)));
					String AsicIsoHash=innerList.get(2);
					String metadataFileHash=CalculateHash.valueOf(metadataHashEnum).checkSum(PI.getZipEntryByFileNameContent(buffer.toByteArray(), innerList.get(3)));
					String AsicmetadataHash=innerList.get(5);
					
					
					
					if(IsoFileHash.equals(AsicIsoHash)) {
						Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" ---Hash Value matched for "+innerList.get(0));
					}else {
						throw new PeppolGeneralExceptions("Failed to match Hash value for:"+innerList.get(0));
					}
					
					if(metadataFileHash.equals(AsicmetadataHash)) {
						Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" ---Hash Value matched for "+innerList.get(3));
					}else {
						throw new PeppolGeneralExceptions("Failed to match Hash value for:"+innerList.get(3));
					}
					
					
					
				}else if(innerList.get(0).toLowerCase().equalsIgnoreCase("metadata.xml")) {
					String contHashEnum=innerList.get(4).toUpperCase();
					String metadataHashEnum=innerList.get(1).toUpperCase();
					
					if(innerList.get(1).contains("-") && innerList.get(4).contains("-")) {
						contHashEnum=innerList.get(4).toUpperCase().replace("-", "");
						 metadataHashEnum=innerList.get(1).toUpperCase().replace("-", "");
						
					}
					
					String IsoFileHash=CalculateHash.valueOf(contHashEnum).checkSum(PI.getZipEntryByFileNameContent(buffer.toByteArray(), innerList.get(3)));
					String AsicIsoHash=outerList.get(5);
					String metadataFileHash=CalculateHash.valueOf(metadataHashEnum).checkSum(PI.getZipEntryByFileNameContent(buffer.toByteArray(), innerList.get(0)));
					String AsicmetadataHash=outerList.get(2);
					
					if(IsoFileHash.equals(AsicIsoHash)) {
						Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" ---Hash Value matched for "+innerList.get(3));
					}
					
					if(metadataFileHash.equals(AsicmetadataHash)) {
						Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" ---Hash Value matched for "+innerList.get(0));
					}
					
					
					
					
					
				}
				
				
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+"  Signature Verification Inner Asic Start");
				
				//Please add the code here
				byte[] ASicFileinner = PI.getZipEntryByDirFileNameContent(buffer.toByteArray(), "asicmanifest");
				byte[] signatureFileinner = PI.getZipEntryByDirFileNameContent(buffer.toByteArray(), "signature");
				SignatureVerifier snSignatureVerifierinner =new SignatureVerifier();
				
				boolean verificationResinner=snSignatureVerifier.SignVerify(ASicFileinner, signatureFileinner,"Inner_ASiC");
				if(verificationResinner) {
					Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+"  Signature Verified successfully");
					
				}else {
					throw new SignatureValidationErrorInner("Signature Verification End with :"+verificationResinner);
				}
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+"  Signature Verification Inner Asic End");
				
				byte[] ISOFile = PI.getZipEntryByFileNameContent(buffer.toByteArray(), "content.xml");
				
				uv.setIsocontent(new String(ISOFile));
				byte[] metadataFile = PI.getZipEntryByFileNameContent(buffer.toByteArray(), "metadata.xml");
				uv.setMetadata(new String(metadataFile));
				File InnerAsic=new File(uvg.getDecryptedPath().get(i)+File.separator+uvg.getFileName().get(i)+"_inner.zip");
				FileUtils.writeByteArrayToFile(InnerAsic, buffer.toByteArray());
				
				File ISOFileStream = new File(uvg.getDecryptedPath().get(i)+File.separator+"content_"+uvg.getFileName().get(i));
				FileUtils.writeByteArrayToFile(ISOFileStream, ISOFile);
				Log.info("["+uvg.getFileName().get(i)+"_"+uvg.getTimestamp()+"]"+" ISO File Written successfully");
				
				
				 
				 
				 
			}
			

		} catch (DecryptionError E) {
			Log.log(Level.SEVERE, "ERROR :"+E.toString());
			throw new DecryptionError(E.getMessage());
		}catch(SignatureValidationErrorInner E) {
			Log.log(Level.SEVERE, "ERROR :"+E.toString());
			throw new SignatureValidationErrorInner(E.getMessage());
		}catch(Exception E) {
			Log.log(Level.SEVERE,"ERROR :"+ E.toString());
			throw new PeppolGeneralExceptions(E.getMessage());
		}
		
		return "Sucessfull";

	}

}
