package no.dfo.peppol.common.functions;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class KeystoreManager {

	private String keystoreName;
	private String keystorePwd;
	protected X509Certificate x509Certificate;
	String cryptoAlias;
	

	 InputStream is1=null ;
	 FileInputStream is=null ;
	
	public KeystoreManager(String keystoreName, String keystorePwd) throws IOException {

		this.keystoreName = keystoreName;
		this.keystorePwd = keystorePwd;
		try{
			is=new FileInputStream(keystoreName);
		}catch(FileNotFoundException fnNotFound) {
			URL resource=getClass().getClassLoader().getResource("DemoKeys.p12");
			is1=resource.openStream();
		}
	}
	public PrivateKey readPrivateKey(String Alias)throws Exception {
		PrivateKey privateKey;
		KeyStore keystore = KeyStore.getInstance("PKCS12");
		try{
			is=new FileInputStream(keystoreName);
			keystore = KeyStore.getInstance("PKCS12");
			keystore.load(
					is ,
					this.keystorePwd.toCharArray());
			 privateKey = (PrivateKey) keystore.getKey(Alias, this.keystorePwd.toCharArray());
		}catch(FileNotFoundException fnNotFound) {
			URL resource=getClass().getClassLoader().getResource("DemoKeys.p12");
			is1= resource.openStream();
			keystore = KeyStore.getInstance("PKCS12");
			keystore.load(
					is1 ,
					this.keystorePwd.toCharArray());
			 privateKey = (PrivateKey) keystore.getKey(Alias, this.keystorePwd.toCharArray());
		}
		System.out.println(keystoreName);
		
		 
		
		return privateKey;
		
		
		
	}
	
	public java.security.cert.Certificate[] getSigningCertificateChain(String Alias) throws Exception 
	{
		KeyStore keyStore;
		try{
			is=new FileInputStream(keystoreName);
			keyStore = KeyStore.getInstance("PKCS12");
			// Provide password for access
			keyStore.load(
					is,
					this.keystorePwd.toCharArray());
			is.close();
		}catch(FileNotFoundException fnNotFound) {
			URL resource=getClass().getClassLoader().getResource("DemoKeys.p12");
			is1=resource.openStream();
			keyStore = KeyStore.getInstance("PKCS12");
			// Provide password for access
			keyStore.load(
					is1,
					this.keystorePwd.toCharArray());
			is1.close();
		}
			
			
		
		
		return   keyStore.getCertificateChain(Alias);
	}

	public X509Certificate getPublicCertificate(String Alias) throws Exception
	{	KeyStore keystore = KeyStore.getInstance("PKCS12");
		try{
		is=new FileInputStream(keystoreName);
		keystore.load(is ,
				this.keystorePwd.toCharArray());
		x509Certificate=(X509Certificate)	keystore.getCertificate(Alias);
		
	is.close();
	}catch(FileNotFoundException fnNotFound) {
		URL resource=getClass().getClassLoader().getResource("DemoKeys.p12");
		is1= resource.openStream();
		keystore.load(is1 ,
				this.keystorePwd.toCharArray());
		x509Certificate=(X509Certificate)	keystore.getCertificate(Alias);
		
	is1.close();
	}
		
			
			
	return x509Certificate;
	}

	

} 
