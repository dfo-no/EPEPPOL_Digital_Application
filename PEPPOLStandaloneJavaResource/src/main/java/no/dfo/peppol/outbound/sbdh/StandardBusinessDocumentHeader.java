package no.dfo.peppol.outbound.sbdh;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

import javax.xml.datatype.XMLGregorianCalendar;




/**
 * <p>Java class for StandardBusinessDocumentHeader complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StandardBusinessDocumentHeader"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="HeaderVersion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Sender" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Partner"/&gt;
 *         &lt;element name="Receiver" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Partner"/&gt;
 *         &lt;element name="DocumentIdentification" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}DocumentIdentification"/&gt;
 *         &lt;element name="Manifest" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Manifest" minOccurs="0"/&gt;
 *         &lt;element name="BusinessScope" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}BusinessScope"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)

@XmlRootElement(name = "StandardBusinessDocumentHeader" ,namespace="http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader")
@XmlType (propOrder = {
    "headerVersion",
    "sender",
    "receiver",
    "documentIdentification",
    "businessScope"
})
public class StandardBusinessDocumentHeader {

    @XmlElement(name = "HeaderVersion", required = true)
    protected String headerVersion;
    @XmlElement(name = "Sender", required = true)
    protected Partner sender;
    @XmlElement(name = "Receiver", required = true)
    protected Partner receiver;
    @XmlElement(name = "DocumentIdentification", required = true)
    protected DocumentIdentification documentIdentification;
    @XmlElement(name = "BusinessScope", required = true)
    protected BusinessScope businessScope;

    
    public String getHeaderVersion() {
        return headerVersion;
    }

    public void setHeaderVersion(String value) {
        this.headerVersion = value;
    }

    
    public Partner getSender() {
        return sender;
    }

    
    public void setSender(Partner value) {
        this.sender = value;
    }

    public Partner getReceiver() {
        return receiver;
    }

    /**
     * Sets the value of the receiver property.
     * 
     * @param value
     *     allowed object is
     *     {@link Partner }
     *     
     */
    public void setReceiver(Partner value) {
        this.receiver = value;
    }

    
    public DocumentIdentification getDocumentIdentification() {
        return documentIdentification;
    }

    
    public void setDocumentIdentification(DocumentIdentification value) {
        this.documentIdentification = value;
    }



  

    
    public BusinessScope getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(BusinessScope value) {
        this.businessScope = value;
    }

}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Partner", propOrder = {
    "identifier",
    
})
class Partner{
	@XmlElement(name = "Identifier", required = true)
    protected PartnerIdentification identifier;

	public PartnerIdentification getIdentifier() {
		return identifier;
	}

	public void setIdentifier(PartnerIdentification identifier) {
		this.identifier = identifier;
	}
	
}

//Sender And Receiver
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartnerIdentification", propOrder = {
    "value"
})
class PartnerIdentification{
	@XmlValue
    protected String value;
    @XmlAttribute(name = "Authority")
    protected String authority;

   
    public String getValue() {
        return value;
    }

   
    public void setValue(String value) {
        this.value = value;
    }

    
    public String getAuthority() {
        return authority;
    }

    
    public void setAuthority(String value) {
        this.authority = value;
    }

	
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentIdentification", propOrder = {
    "standard",
    "typeVersion",
    "instanceIdentifier",
    "type",
    "multipleType",
    "creationDateAndTime"
})

class DocumentIdentification {

    @XmlElement(name = "Standard", required = true)
    protected String standard;
    @XmlElement(name = "TypeVersion", required = true)
    protected String typeVersion;
    @XmlElement(name = "InstanceIdentifier", required = true)
    protected String instanceIdentifier;
    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "MultipleType")
    protected Boolean multipleType;
    @XmlElement(name = "CreationDateAndTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected String creationDateAndTime;

    
    public String getStandard() {
        return standard;
    }

    
    public void setStandard(String value) {
        this.standard = value;
    }

    
    public String getTypeVersion() {
        return typeVersion;
    }

    
    public void setTypeVersion(String value) {
        this.typeVersion = value;
    }

    
    public String getInstanceIdentifier() {
        return instanceIdentifier;
    }

    public void setInstanceIdentifier(String value) {
        this.instanceIdentifier = value;
    }

    
    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    
    public Boolean isMultipleType() {
        return multipleType;
    }

    
    public void setMultipleType(Boolean value) {
        this.multipleType = value;
    }

    
    public String getCreationDateAndTime() {
        return creationDateAndTime;
    }

    public void setCreationDateAndTime(String value) {
        this.creationDateAndTime = value;
    }

}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BusinessScope", propOrder = {
    "scope"
})

 class BusinessScope {

    @XmlElement(name = "Scope", required = true)
    protected List<Scope> scope;

    
    public List<Scope> getScope() {
        if (scope == null) {
            scope = new ArrayList<Scope>();
        }
        return this.scope;
    }

    
    

}


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Scope", propOrder = {
    "type",
    "instanceIdentifier",
    
})

class Scope {

    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "InstanceIdentifier", required = true)
    protected String instanceIdentifier;
    

   
    public String getType() {
        return type;
    }

   
    public void setType(String value) {
        this.type = value;
    }

    public String getInstanceIdentifier() {
        return instanceIdentifier;
    }

    public void setInstanceIdentifier(String value) {
        this.instanceIdentifier = value;
    }
  
   

}





