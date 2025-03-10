package no.dfo.peppol.outbound.asicmanifestreader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AsicManifestReader {
	
	public ArrayList<String> readOuterAsicmanifest(byte outerAsic[]) throws ParserConfigurationException, SAXException, IOException, JAXBException{
		 String Asic=new String(outerAsic).replace("\uFEFF", "") + "\n";
	
       JAXBContext jc = JAXBContext.newInstance(ASiCManifestType.class);
       
      
       Unmarshaller u = jc.createUnmarshaller();
       ASiCManifestType manifest = (ASiCManifestType) ((JAXBElement) u.unmarshal(new ByteArrayInputStream(Asic.getBytes()))).getValue();
       ArrayList <String> OuterAsicData=new ArrayList<String>(6);
       
		/*
		 * 0:Content.asice Filename 1:content.asice hash 2:SBDH Filename 3:SBDH Hash
		 */
       
     
		
		  for(int i=0;i<manifest.getDataObjectReference().size();i++) {
		  
		  OuterAsicData.add(manifest.getDataObjectReference().get(i).getURI());
		  OuterAsicData.add(manifest.getDataObjectReference().get(i).getDigestMethod().getAlgorithm().split("#")[1]);
		  OuterAsicData.add(manifest.getDataObjectReference().get(i).getDigestValue());
		  
		  
		  }
		 
      
		return OuterAsicData;

		
	}
	
	public ArrayList<String> readInnerAsicmanifest(byte innerAsic[]) throws JAXBException{
		String Asic=new String(innerAsic).replace("\uFEFF", "") + "\n";


   JAXBContext jc = JAXBContext.newInstance(ASiCManifestType.class);
   
  
   Unmarshaller u = jc.createUnmarshaller();
   ASiCManifestType manifest = (ASiCManifestType) ((JAXBElement) u.unmarshal(new ByteArrayInputStream(Asic.getBytes()))).getValue();
   ArrayList <String> InnerAsicData=new ArrayList<String>(6);
   
	/*
	 * 0:content.xml
	 * 1:Content Hash Algo
	 * 2:content hash value 
	 * 3:Metadata Filename
	 * 4:Metadata HashAlgo 
	 * 5:Metadata Hash Value
	 */
   
   
	
 
	
	  for(int i=0;i<manifest.getDataObjectReference().size();i++) {
	  
		  InnerAsicData.add(manifest.getDataObjectReference().get(i).getURI());
		  InnerAsicData.add(manifest.getDataObjectReference().get(i).getDigestMethod().getAlgorithm().split("#")[1]);
		  InnerAsicData.add(manifest.getDataObjectReference().get(i).getDigestValue());
	  
	  
	  }
	 
  
	return InnerAsicData;

		
	}

}
