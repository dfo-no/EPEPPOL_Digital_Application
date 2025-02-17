package no.dfo.peppol.outbound.signing;


import java.io.InputStream;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Logger;

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
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import no.dfo.gui.controller.UtilityValuesGUI;
import no.dfo.peppol.outbound.UtilityValueSetting;

public class SignASiCwithCAdES {

	private PrivateKey privateKey;
	private Certificate[] certificateChain;
	private Logger Log;
	UtilityValuesGUI uvg;
	UtilityValueSetting uv;
	
//	private static DigestAlgorithm DIGEST_ALGO = DigestAlgorithm.SHA3_256;  // SHA3-256
//	private static SignatureAlgorithm SIGNATURE_ALGO = SignatureAlgorithm.ECDSA_SHA256;  // ECDSA_SHA3_256

	public SignASiCwithCAdES(
			PrivateKey privateKey, 			/* signing key */
			Certificate[] certificateChain ) 
	{
		this.privateKey = privateKey;
		this.certificateChain = certificateChain;
	}

	// InstanceIdentifier becomes part of p7sFilename
	public InputStream signASiCSBaselineB(List<DSSDocument> toSignDocuments, String messageId,UtilityValuesGUI uvg,UtilityValueSetting uv) 
	{
		Log=uvg.getOuboundLog();
	this.uvg=uvg;
	this.uv=uv;
		// get private key
	Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"      Signing Start ");
		// Preparing parameters for the ASiC-E signature
		ASiCWithCAdESSignatureParameters signatureParameters = new ASiCWithCAdESSignatureParameters();
		
		// set the signing certificate
		signatureParameters.setSigningCertificate(getSigningCert());
		
		// set the signing certificate chain
		signatureParameters.setCertificateChain(getCertificateChain(certificateChain));

		// choose the level of the signature (-B, -T, -LT, LTA).
		signatureParameters.setSignatureLevel(SignatureLevel.CAdES_BASELINE_B);
		Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"        Signature Level=CAdES_BASELINE_B ");
		// choose the container type (ASiC-S or ASiC-E)
		signatureParameters.aSiC().setContainerType(ASiCContainerType.ASiC_E);
		Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"        Signature Container Type=ASiC_E ");

		// set the digest algorithm to use with the signature algorithm. You must use the
		// same parameter when you invoke the method sign on the token.
		signatureParameters.setDigestAlgorithm(uvg.getSignatureDigest());  // DigestAlgorithm.SHA256
		Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"        Signature Hash Algorithm Type="+uvg.getSignatureDigest().getName());
		
		// set the encryption algorithm
		signatureParameters.setEncryptionAlgorithm(EncryptionAlgorithm.ECDSA);
		Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"        Signature Algorithm Type=ECDSA ");

		// Create common certificate verifier
	CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();

		// Create ASiC signature service
		ASiCWithCAdESService service = new ASiCWithCAdESService(commonCertificateVerifier);

		// use factory to define signature- and manifest file names
		SimpleASiCWithCAdESFilenameFactory filenameFactory = new SimpleASiCWithCAdESFilenameFactory();
        if(messageId == null || messageId.equals("")) messageId = "001";
		filenameFactory.setSignatureFilename("signature-" + messageId + ".p7s" );
		filenameFactory.setManifestFilename("ASiCManifest-" + messageId + ".xml");
		service.setAsicFilenameFactory(filenameFactory);

		// Get the SignedInfo segment that need to be signed.
		ToBeSigned dataToSign = service.getDataToSign(toSignDocuments, signatureParameters);

		// This method obtains the signature value for signed information using the private key and specified algorithm
		DigestAlgorithm digestAlgorithm = signatureParameters.getDigestAlgorithm();
		SignatureValue signatureValue = sign(dataToSign, digestAlgorithm, privateKey);  // SignatureAlgorithm.ECDSA_SHA256;

		// We invoke the CAdESService to sign the document with the signature value obtained in previous step.
		DSSDocument signedDocument = service.signDocument(toSignDocuments, signatureParameters, signatureValue);

		return signedDocument.openStream();
	}

	private CertificateToken getSigningCert() 
	{
		X509Certificate x509Cert = (X509Certificate)certificateChain[0];
		Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"        Signature Certificate Serial = "+x509Cert.getSerialNumber());
	
		return new CertificateToken(x509Cert);
	}

	private CertificateToken[] getCertificateChain(Certificate[] certificateChain) 
	{
		CertificateToken[] certificateTokenChain = new CertificateToken[certificateChain.length];
		for (int i = 0; i < certificateChain.length; i++)
			certificateTokenChain[i] = new CertificateToken((X509Certificate)certificateChain[i]); 
		return certificateTokenChain;
	}

	// DigestAlgorithm = SHA256, EncryptionAlgorithm = ECDSA, SignatureAlgorithm = ECDSA_SHA256
	private SignatureValue sign(ToBeSigned toBeSigned, DigestAlgorithm digestAlgorithm, PrivateKey privateKey) 
			throws DSSException 
	{
		EncryptionAlgorithm encryptionAlgorithm = EncryptionAlgorithm.ECDSA; 
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.getAlgorithm(encryptionAlgorithm, digestAlgorithm);
		String javaSignatureAlgorithm = signatureAlgorithm.getJCEId(); // SHA3-256withECDSA
		try 
		{
			Signature signature = Signature.getInstance(javaSignatureAlgorithm);  // SHA256withECDSA
			signature.initSign(privateKey);
			signature.update(toBeSigned.getBytes());
			final byte[] signatureValue = signature.sign();
			SignatureValue value = new SignatureValue();
			value.setAlgorithm(signatureAlgorithm);
			value.setValue(signatureValue);
			Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"      Signing End ");
			return value;
			
		} catch (Exception e) {
			throw new DSSException(e);
		} 
	}
} // end