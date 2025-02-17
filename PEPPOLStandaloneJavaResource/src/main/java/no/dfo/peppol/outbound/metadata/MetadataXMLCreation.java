package no.dfo.peppol.outbound.metadata;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlRootElement(name = "Metadata" ,namespace="urn:fdc:difi.no:2017:payment:extras-1")
@XmlType (propOrder = {
    "Version",
    "CustomerIdentifier",
    "DivisionIdentifier",
    "UserIdentifier"
})
public class MetadataXMLCreation {
	  @XmlElement(name = "Version", required = true)
	    protected String Version;
	    public String getVersion() {
		return Version;
	}
	public void setVersion(String version) {
		Version = version;
	}
	public String getCustomerIdentifier() {
		return CustomerIdentifier;
	}
	public void setCustomerIdentifier(String customerIdentifier) {
		CustomerIdentifier = customerIdentifier;
	}
	public String getDivisionIdentifier() {
		return DivisionIdentifier;
	}
	public void setDivisionIdentifier(String divisionIdentifier) {
		DivisionIdentifier = divisionIdentifier;
	}
	public String getUserIdentifier() {
		return UserIdentifier;
	}
	public void setUserIdentifier(String userIdentifier) {
		UserIdentifier = userIdentifier;
	}
		@XmlElement(name = "CustomerIdentifier", required = true)
	    protected String CustomerIdentifier;
	    @XmlElement(name = "DivisionIdentifier", required = true)
	    protected String DivisionIdentifier;
	    @XmlElement(name = "UserIdentifier", required = true)
	    protected String UserIdentifier;
	
	
}
