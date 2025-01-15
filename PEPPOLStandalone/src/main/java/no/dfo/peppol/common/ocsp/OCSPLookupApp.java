package no.dfo.peppol.common.ocsp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;



public class OCSPLookupApp {
	private Logger Log;
	UtilityValueOCSP uv;
	public OCSPLookupApp(UtilityValueOCSP uv) {
		this.uv=uv;
	}
	
	public OCSPLookupApp() {
		
	}
	public boolean getIntermediateCert(String certType) throws CertificateException, IOException {
		Log=uv.getLog();
		X509Certificate cert=null;
		Log.info(" OCSP: Intermediate Certificate fetch start" );
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		if(certType.equalsIgnoreCase("USER")) {
			cert=uv.getCertPub();
		}else if(certType.equalsIgnoreCase("APP")) {
			InputStream certStream=new FileInputStream(uv.getLeafCert());
			 cert = (X509Certificate) certFactory.generateCertificate(certStream);
			 
		}
		
		
		
	
		Log.info(" OCSP: Leaf/Public  Certificate Serial Number:"+cert.getSerialNumber());
		Log.info(" OCSP: Leaf/Public  Algorithm :"+cert.getSigAlgName());
		URL url=null;
		 X509Certificate issuer=null;
		

	// get Authority Information Access extension (will be null if extension is not present)
	 byte[] extVal = cert.getExtensionValue(Extension.authorityInfoAccess.getId());
	AuthorityInformationAccess aia = AuthorityInformationAccess.getInstance(JcaX509ExtensionUtils.parseExtensionValue(extVal));
	//.fromExtensionValue(extVal)

	// check if there is a URL to issuer's certificate
	AccessDescription[] descriptions = aia.getAccessDescriptions();
	for (AccessDescription ad : descriptions) {
	    // check if it's a URL to issuer's certificate
	    if (ad.getAccessMethod().equals(X509ObjectIdentifiers.id_ad_caIssuers)) {
	        GeneralName location = ad.getAccessLocation();
	        if (location.getTagNo() == GeneralName.uniformResourceIdentifier) {
	            String issuerUrl = location.getName().toString();
	            // http URL to issuer (test in your browser to see if it's a valid certificate)
	            // you can use java.net.URL.openStream() to create a InputStream and create
	            // the certificate with your CertificateFactory
	             url = new URL(issuerUrl);
	             Log.info(" OCSP: Intermediate Certificate URL :"+issuerUrl);
	             issuer = (X509Certificate) certFactory.generateCertificate(url.openStream());
	             Log.info(" OCSP: Intermediate Certificate Serial :"+issuer.getSerialNumber());
	            
	           
	        }
	    }
	}
	InputStream issuerCert=url.openStream();
	
	 Log.info(" OCSP: Intermediate Successfully Fetched ");
		InputStream certStreamLeaf=null;
	 if(certType.equalsIgnoreCase("USER")) {
		 
		 byte[] encodedCert = cert.getEncoded();
         
         // Encode to Base64
         String base64Cert = Base64.getEncoder().encodeToString(encodedCert);
         String cert_begin = "-----BEGIN CERTIFICATE-----\n";
         String end_cert = "\n-----END CERTIFICATE-----";
         base64Cert=cert_begin+base64Cert+end_cert;
		 System.out.println(base64Cert);
		 certStreamLeaf=IOUtils.toInputStream(base64Cert, "UTF-8");
		}else if(certType.equalsIgnoreCase("APP")) {
			
			 certStreamLeaf= new FileInputStream(uv.getLeafCert());
			 
		}

	getOCSPStatus(certStreamLeaf,issuerCert,uv);
	
	return true;
	}
	
	
	  private boolean getOCSPStatus(InputStream SigningLeaf,InputStream SigningInter,UtilityValueOCSP uv) throws IOException {
		 
	  // the "3rd" party signing certificate CertificateToken signingToken =
		  CertificateToken signingToken = DSSUtils.loadCertificate(SigningLeaf);
		  Log.info(" OCSP: Leaf Successfully loaded ");
	  // the "3rd" party intermediate-/root certificate CertificateToken rootToken
		  CertificateToken rootToken = DSSUtils.loadCertificate(SigningInter);
		  Log.info(" OCSP: Intermediate Successfully loaded ");
		  OCSPDataLoader ocspDataLoader = new OCSPDataLoader();
		  
			// Retrieves OCSP response from online source.
			OnlineOCSPSource ocspSource = new OnlineOCSPSource(ocspDataLoader);
			
			// Extract OCSP for a certificate
			OCSPToken ocspToken = ocspSource.getRevocationToken(signingToken, rootToken);
			if(ocspToken == null) throw new RuntimeException("OCSP lookup failure.");
			
			if(ocspSource.getRevocationToken(signingToken, rootToken).getStatus().isGood()) {
				Log.info(" OCSP: Status :"+ocspSource.getRevocationToken(signingToken, rootToken).getStatus().name());
				Log.info(" OCSP: Successfull ");
				System.out.println("\nOCSP status is good");
				return true;
			} else {
				return false;
			}
		}
	
	
}
