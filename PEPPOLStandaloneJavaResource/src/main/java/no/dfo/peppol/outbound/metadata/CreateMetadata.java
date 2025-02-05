package no.dfo.peppol.outbound.metadata;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import no.dfo.gui.controller.UtilityValuesGUI;
import no.dfo.peppol.outbound.UtilityValueSetting;

public class CreateMetadata {

	public void createMetadata(UtilityValueSetting uv,UtilityValuesGUI uvg) throws JAXBException {

		MetadataXMLCreation metadata=new MetadataXMLCreation();
		metadata.setVersion("1.0");
		metadata.setCustomerIdentifier(uvg.getCustomerIdentfier());
		metadata.setDivisionIdentifier(uvg.getDivisionIdentifier());
		metadata.setUserIdentifier(uvg.getUserIdentifier());
		JAXBContext context = JAXBContext.newInstance(MetadataXMLCreation.class);
	    Marshaller mar= context.createMarshaller();
	    mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    StringWriter stringWriter = new StringWriter();
		    mar.marshal(metadata, stringWriter);
		    uv.setMetadata(stringWriter.toString());
		    System.out.println(stringWriter.toString());
		
	}

}
