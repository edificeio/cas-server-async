package edu.yale.tp.cas;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProxySuccessType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ProxySuccessType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="proxyTicket" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProxySuccessType", propOrder = {
    "proxyTicket"
})
public class ProxySuccessType {

    @XmlElement(required = true)
    protected String proxyTicket;

    /**
     * Gets the value of the proxyTicket property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProxyTicket() {
        return proxyTicket;
    }

    /**
     * Sets the value of the proxyTicket property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProxyTicket(String value) {
        this.proxyTicket = value;
    }

}
