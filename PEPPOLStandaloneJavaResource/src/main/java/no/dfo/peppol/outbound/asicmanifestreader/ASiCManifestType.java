package no.dfo.peppol.outbound.asicmanifestreader;



import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ASiCManifestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ASiCManifestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://uri.etsi.org/02918/v1.2.1#}SigReference"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02918/v1.2.1#}DataObjectReference" maxOccurs="unbounded"/&gt;
 *         &lt;element name="ASiCManifestExtensions" type="{http://uri.etsi.org/02918/v1.2.1#}ExtensionsListType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ASiCManifestType", propOrder = {
    "sigReference",
    "dataObjectReference"
})
public class ASiCManifestType {

    @XmlElement(name = "SigReference", required = true)
    protected SigReferenceType sigReference;
    @XmlElement(name = "DataObjectReference", required = true)
    protected List<DataObjectReferenceType> dataObjectReference;
  
    

    /**
     * Gets the value of the sigReference property.
     * 
     * @return
     *     possible object is
     *     {@link SigReferenceType }
     *     
     */
    public SigReferenceType getSigReference() {
        return sigReference;
    }

    /**
     * Sets the value of the sigReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link SigReferenceType }
     *     
     */
    public void setSigReference(SigReferenceType value) {
        this.sigReference = value;
    }

    /**
     * Gets the value of the dataObjectReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataObjectReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataObjectReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataObjectReferenceType }
     * 
     * 
     */
    public List<DataObjectReferenceType> getDataObjectReference() {
        if (dataObjectReference == null) {
            dataObjectReference = new ArrayList<DataObjectReferenceType>();
        }
        return this.dataObjectReference;
    }

    /**
     * Gets the value of the aSiCManifestExtensions property.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionsListType }
     *     
     */
   


}
