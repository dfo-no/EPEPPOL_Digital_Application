package no.dfo.peppol.outbound;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import no.dfo.gui.controller.UtilityValuesGUI;

public class BCLCertLoader {
	Logger Log;
	URL urlBCL = null;
	X509Certificate cert = null;
	String actorID="iso6523-actorid-upis::0192:";
	String profileURL="/busdox-procid-ubl::urn:fdc:bits.no:2017:profile:01:1.0";
	String apiVersion="api/v1/";
	

	
	
//	BCP: 		https://api.buypass.com/bcp-api/v1/standard/
//	apiVersion: api/v1/
//	actorID: 	iso6523-actorid-upis::0192:
//	RecOrg: 	986252932
//	profileURL: /busdox-procid-ubl::urn:fdc:bits.no:2017:profile:01:1.0
	
	
	public X509Certificate downloadCert(UtilityValuesGUI uvg) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, CertificateException {
		Log=uvg.getOuboundLog();
		Log.info("["+uvg.getFileName()+"_"+uvg.getTimestamp()+"]"+"Starting BCL Certificate Download");
		if(uvg.getBCLEnvironment().equalsIgnoreCase("BCLTest")) {
			urlBCL=new URL("https://test-bcl.difi.blufo.net/lookup/v2/iso6523-actorid-upis::0192:"+uvg.getRecOrg());
		}else if(uvg.getBCLEnvironment().equalsIgnoreCase("BCLProd")) {
			urlBCL=new URL("https://bcl.difi.blufo.net/lookup/v2/iso6523-actorid-upis::0192:"+uvg.getRecOrg());
		}
		
		URLConnection conn = urlBCL.openConnection();
		System.out.println();
		String result = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);

		JSONObject object = new JSONObject(result);


		String BCP = object.getString("difi-bcp-v1");
		String fullCertURL=BCP+apiVersion+actorID+uvg.getRecOrg()+profileURL;
		Log.info("["+uvg.getFileName()+"_"+uvg.getTimestamp()+"]"+"Certificate Location "+fullCertURL);
		URL certDownload=new URL(fullCertURL);
		URLConnection conn1 = certDownload.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(conn1.getInputStream());
		doc.getDocumentElement().normalize();
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//Process/Certificate /text()");
		StringBuilder sb = new StringBuilder(((String) expr.evaluate(doc, XPathConstants.STRING)));
		String cert_begin = "-----BEGIN CERTIFICATE-----\n";
        String end_cert = "\n-----END CERTIFICATE-----";
       String base64Cert=cert_begin+sb+end_cert;
		  CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		  
          cert = (X509Certificate) certFactory.generateCertificate(IOUtils.toInputStream(base64Cert,  StandardCharsets.UTF_8));
          
          Log.info("["+uvg.getFileName()+"_"+uvg.getTimestamp()+"]"+"Certificate download finished Organisation:"+uvg.getRecOrg()+"|| Certificate Serial: "+cert.getSerialNumber());
		
		
		
		
		
		return cert;
		
	}
	
}
