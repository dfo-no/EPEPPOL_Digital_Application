package no.dfo.peppol.outbound.asicmanifestreader;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


public enum CalculateHash {
	SHA3256("SHA3-256"),SHA256("SHA256"),SHA3512("SHA3-512"),SHA3384("SHA3-384");
	private String name;
	
	CalculateHash(String name)
	{
		this.name=name;
	}

	public String getName() {
		return name;
	}
	
	public String checkSum(byte[] filetoHash) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
		Provider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
        MessageDigest digest = MessageDigest.getInstance(getName());
        byte[] hash = digest.digest(filetoHash);
        return Base64.getEncoder().encodeToString(hash);
		
	}
	
}
