package no.dfo.peppol.inbound.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Collection;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import no.dfo.peppol.peppolcustomexception.DecryptionError;

public class CMSDecrypt {

	public InputStream decryptDataRSA(byte[] encryptedData, PrivateKey decryptionKey)
			throws CMSException, GeneralSecurityException, IOException, DecryptionError {
		try {
			InputStream decrypedInputStream = null;
			Security.addProvider(new BouncyCastleProvider());

			CMSEnvelopedDataParser envelopedData = new CMSEnvelopedDataParser(encryptedData);
			Collection<RecipientInformation> recip = envelopedData.getRecipientInfos().getRecipients();
			RecipientInformation recipientInfo = recip.iterator().next();
			decrypedInputStream = recipientInfo.getContentStream(new JceKeyTransEnvelopedRecipient(decryptionKey)
					.setAlgorithmMapping(PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA/GCM/OAEPWithSHA256AndMGF1Padding")
					.setProvider("BC").setContentProvider("BC")).getContentStream();

			return decrypedInputStream;
		} catch (Exception E) {

			throw new DecryptionError(E.getMessage());
		}
	}

}
