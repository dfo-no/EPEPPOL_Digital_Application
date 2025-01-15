package no.dfo.peppol.outbound.signing;


import java.io.InputStream;
import java.security.Signature;
import java.util.List;

import org.apache.commons.io.IOUtils;

import eu.europa.esig.dss.asic.cades.ASiCWithCAdESSignatureParameters;
import eu.europa.esig.dss.asic.cades.SimpleASiCWithCAdESFilenameFactory;
import eu.europa.esig.dss.asic.cades.signature.ASiCWithCAdESService;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.KSPrivateKeyEntry;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;

public class ASiCEwithCAdES {

	private DigestAlgorithm digestAlgoUsed; // Default: DigestAlgorithm.SHA3_256
	private String instanceId;    // ISO msgID
	private DSSPrivateKeyEntry keyEntry; // signing key
	private String orgnr;
	
	public ASiCEwithCAdES(
			DSSPrivateKeyEntry dssPrivateKeyEntry, 
			String instanceId,
			String orgnr) 
	{
		this.keyEntry = dssPrivateKeyEntry;
		this.instanceId = instanceId;
		this.orgnr = orgnr; 
		
	
			digestAlgoUsed = DigestAlgorithm.SHA3_256;
		
	}
	
	// sign documents
	public byte[] sign(List<DSSDocument> documentsToBeSigned) 
	{
		try 
		{
			// Preparing parameters for the ASiC-E signature
			ASiCWithCAdESSignatureParameters parameters = new ASiCWithCAdESSignatureParameters();

			// We choose the level of the signature (-B, -T, -LT or -LTA).
			parameters.setSignatureLevel(SignatureLevel.CAdES_BASELINE_B);

			// We choose the container type (ASiC-S pr ASiC-E)
			parameters.aSiC().setContainerType(ASiCContainerType.ASiC_E);

			// Import DSS note: "We set the digest algorithm to use with the signature algorithm. You must use the same parameter 
			// when you invoke the method sign on the token. The default value is SHA256"
			parameters.setDigestAlgorithm(digestAlgoUsed);

			// We set the signing certificate
			parameters.setSigningCertificate(keyEntry.getCertificate());

			// We set the certificate chain
			parameters.setCertificateChain(keyEntry.getCertificateChain());

			// Create common certificate verifier
			CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();

			// Create ASiC service for signature
			ASiCWithCAdESService service = new ASiCWithCAdESService(commonCertificateVerifier);

			// use factory to define signature- and manifest file names
			SimpleASiCWithCAdESFilenameFactory filenameFactory = new SimpleASiCWithCAdESFilenameFactory();
			filenameFactory.setSignatureFilename("signature-" + instanceId + ".p7s" );
			filenameFactory.setManifestFilename("ASiCManifest-" + instanceId + ".xml");
			service.setAsicFilenameFactory(filenameFactory);

			// Get the SignedInfo segment that need to be signed.
			ToBeSigned toBeSigned = service.getDataToSign(documentsToBeSigned, parameters);

			// This function obtains the signature value for signed information using the private key and specified algorithm
			DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();
			SignatureValue signatureValue = this.getSignature(toBeSigned, digestAlgorithm, keyEntry);

			// We invoke the ASiCWithCAdESService to sign the document with the signature
			// value obtained in the previous step.
			DSSDocument signedDocument = service.signDocument(documentsToBeSigned, parameters, signatureValue);
			
			// return signed document result as byte array
			try(InputStream docStream = signedDocument.openStream(); )
			{
				return IOUtils.toByteArray(docStream);  
			} 
		} catch (Exception e) {
			throw new RuntimeException("Signing failed. " + e.getMessage());
		}
	}
	
	
	// sign the document with the signature values obtained in previous steps
	// compilation of "sign" methods from DSS AbstractSignatureTokenConnection class
	private SignatureValue getSignature(
			ToBeSigned toBeSigned, 
			DigestAlgorithm digestAlgorithm, 
			DSSPrivateKeyEntry keyEntry) throws DSSException 
	{
		final EncryptionAlgorithm encryptionAlgorithm = keyEntry.getEncryptionAlgorithm();
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.getAlgorithm(encryptionAlgorithm, digestAlgorithm, null);
		String javaSignatureAlgorithm = signatureAlgorithm.getJCEId(); // SHA3-256withECDSA

		try 
		{
			final Signature signature = Signature.getInstance(javaSignatureAlgorithm);  // Signature.getInstance(signatureAlgorithm.getJCEId()); SHA256withECDSA or ECDSA_SHA3_256
			signature.initSign(((KSPrivateKeyEntry) keyEntry).getPrivateKey());
			signature.update(toBeSigned.getBytes());
			byte[] signatureValue = signature.sign();
			SignatureValue value = new SignatureValue();
			value.setAlgorithm(signatureAlgorithm);
			value.setValue(signatureValue);
			return value;

		} catch (Exception e) {
			throw new DSSException(e);
		} 
	}
}  // end
