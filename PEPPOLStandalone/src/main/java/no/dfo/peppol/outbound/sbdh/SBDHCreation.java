package no.dfo.peppol.outbound.sbdh;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import no.dfo.peppol.outbound.UtilityValueSetting;

public class SBDHCreation {
	
	public void createSBDH(UtilityValueSetting uv,String senderOrg,String recOrg) throws JAXBException {
		StandardBusinessDocumentHeader sbdHeader=new StandardBusinessDocumentHeader();
		sbdHeader.setHeaderVersion("1.0");
        String uniqueID = UUID.randomUUID().toString();
        String ICD="0192";
		/*
		 * String senderOrgNum="986252932"; String recOrgNum="986252932";
		 */
        
        String creationDateAndTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ; 
        PartnerIdentification partnerIdentificationSender=new PartnerIdentification();
        partnerIdentificationSender.setAuthority("iso6523-actorid-upis");
        partnerIdentificationSender.setValue(ICD+":"+senderOrg);
        Partner pn= new Partner();
        pn.setIdentifier(partnerIdentificationSender);
        
        PartnerIdentification partnerIdentificationReceiver=new PartnerIdentification();
        partnerIdentificationReceiver.setAuthority("iso6523-actorid-upis");
        partnerIdentificationReceiver.setValue(ICD+":"+recOrg);
        Partner pn2= new Partner();
        pn2.setIdentifier(partnerIdentificationReceiver);
        
   
        
        sbdHeader.setSender(pn);
        sbdHeader.setReceiver(pn2);
        String timeStamp = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss").format(new java.util.Date());
		
		
		
        DocumentIdentification documentIdentification=new DocumentIdentification();
        documentIdentification.setStandard("urn:iso:std:iso:20022:tech:xsd:pain.001.001.03");
        documentIdentification.setTypeVersion("3");
        documentIdentification.setInstanceIdentifier(uniqueID);
        documentIdentification.setType("Document");
        documentIdentification.setCreationDateAndTime(creationDateAndTime);
        sbdHeader.setDocumentIdentification(documentIdentification);
        
        BusinessScope businessScope=new BusinessScope();
        Scope documentID=new Scope();
        documentID.setType("DOCUMENTID");
        documentID.setInstanceIdentifier("urn:iso:std:iso:20022:tech:xsd:pain.001.001.03::Document##urn:fdc:bits.no:2017:iso20022:1.5::03");
        
        Scope processID=new Scope();
        processID.setType("PROCESSID");
        processID.setInstanceIdentifier("urn:fdc:bits.no:2017:profile:01:1.0");
        
        Scope countryC1=new Scope();
        countryC1.setType("COUNTRY_C1");
        countryC1.setInstanceIdentifier("NO");
		/*
		 * <Scope> <Type>COUNTRY_C1</Type> <InstanceIdentifier>NO</InstanceIdentifier>
		 * </Scope>
		 */
        
        businessScope.getScope().add(documentID);
        businessScope.getScope().add(processID);
        businessScope.getScope().add(countryC1);
        sbdHeader.setBusinessScope(businessScope);
             
        
        
		JAXBContext context = JAXBContext.newInstance(StandardBusinessDocumentHeader.class);
	    Marshaller mar= context.createMarshaller();
	    mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    StringWriter stringWriter = new StringWriter();
		    mar.marshal(sbdHeader, stringWriter);
		    uv.setSbdh(stringWriter.toString());
		
		
	}

}
