package no.dfo.peppol.outbound.asicmanifestreader;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import no.dfo.peppol.outbound.sbdh.StandardBusinessDocumentHeader;

public class createAsicManifest {
	
	public static void main(String[] args) throws JAXBException {
		    ObjectFactory objectFactory = new ObjectFactory();
		 boolean rootFilenameIsSet = false;
		ASiCManifestType ASiCManifestType = new ASiCManifestType();
		SigReferenceType sigReferenceType = new SigReferenceType();
        sigReferenceType.setURI("META-INF/signature-DBT4A2405294.p7s");
        sigReferenceType.setMimeType("application/pkcs7-signature");
        ASiCManifestType.setSigReference(sigReferenceType);
		 DataObjectReferenceType dataObject = new DataObjectReferenceType();
	        dataObject.setURI("content.asice.p7m");
	        dataObject.setMimeType("application/pkcs7-signature");
	        dataObject.setDigestValue("digestvaluetest");

	        DigestMethodType digestMethodType = new DigestMethodType();
	        digestMethodType.setAlgorithm("http://www.w3.org/2007/05/xmldsig-more#sha3-256");
	        dataObject.setDigestMethod(digestMethodType);

	        ASiCManifestType.getDataObjectReference().add(dataObject);
	        for (DataObjectReferenceType dataObject1 : ASiCManifestType.getDataObjectReference()) {
	            if (dataObject1.getURI().equals("META-INF/signature-DBT4A2405294.p7s")) {
	                dataObject1.setRootfile(true);
	                rootFilenameIsSet = true;
	                
	            }
	        
	        JAXBContext context = JAXBContext.newInstance(ASiCManifestType.class);
		    Marshaller mar= context.createMarshaller();
		    mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    StringWriter stringWriter = new StringWriter();
			    mar.marshal(objectFactory.createASiCManifest(ASiCManifestType), stringWriter);
			   System.out.println(stringWriter.toString());
			
		
	}

}}
