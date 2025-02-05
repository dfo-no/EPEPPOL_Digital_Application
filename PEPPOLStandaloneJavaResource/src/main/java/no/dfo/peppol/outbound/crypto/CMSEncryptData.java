package no.dfo.peppol.outbound.crypto;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.MGF1ParameterSpec;
import java.util.logging.Logger;

import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JcaAlgorithmParametersConverter;

import CustomExceptionPEPPOL.PeppolGeneralExceptions;
import no.dfo.gui.controller.UtilityValuesGUI;
import no.dfo.peppol.outbound.UtilityValueSetting;

public class CMSEncryptData {
	UtilityValuesGUI uvg;
	Logger Log;
	UtilityValueSetting uv;
	
	
	public CMSEncryptData(UtilityValuesGUI uvg,UtilityValueSetting uv) {
		super();
		this.uvg = uvg;
		this.uv=uv;
	}


	public byte[] encryptDataRSA(byte[] data, X509Certificate recipientCert) throws PeppolGeneralExceptions{
		Log=uvg.getOuboundLog();
		Security.addProvider(new BouncyCastleProvider());

		// General class for generating a CMS enveloped-data message
		CMSEnvelopedDataGenerator envelopedGenerator = new CMSEnvelopedDataGenerator();
		JcaAlgorithmParametersConverter paramsConverter = new JcaAlgorithmParametersConverter();

		// Constructs a parameter set for OAEP padding as defined in the PKCS #1 standard using the specified message digest algorithm 
		OAEPParameterSpec oaepSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
		
		try 
		{
			AlgorithmIdentifier algorithmIdentifier = paramsConverter.getAlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, oaepSpec);
			Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"   Encryption Algorithm=RSAES_OAEP");
			Log.info("["+uv.getFilename()+"_"+uvg.getTimestamp()+"]"+"   Encryption Certificate Serial ="+recipientCert.getSerialNumber());
			
			/*
			 * @param recipientCert certificate carrying the public key.
			 * @param algorithmIdentifier the identifier and parameters for the encryption algorithm to be used.
			 */
			JceKeyTransRecipientInfoGenerator recipient = new JceKeyTransRecipientInfoGenerator(recipientCert, algorithmIdentifier).setProvider("BC");  

			envelopedGenerator.addRecipientInfoGenerator((RecipientInfoGenerator)recipient);

			// a holding class for a byte array of data to be processed
			CMSProcessableByteArray cMSProcessableByteArray = new CMSProcessableByteArray(data);

			// General interface for an operator that is able to produce an OutputStream that will output encrypted data
			OutputEncryptor encryptor = (new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_GCM)).setProvider("BC").build();

			// encrypt
			CMSEnvelopedData cmsEnvelopedData = envelopedGenerator.generate((CMSTypedData)cMSProcessableByteArray, encryptor);
			return cmsEnvelopedData.getEncoded();

		} catch (CertificateEncodingException | CMSException | IOException | InvalidAlgorithmParameterException e) {

			throw new PeppolGeneralExceptions("Encryption Error: "+ e.getMessage());
		}
	}

}
