package edu.yale.tp.cas;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.w3c.dom.Element;


/**
 * <p>Java class for AuthenticationSuccessType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AuthenticationSuccessType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attributes" type="{http://www.yale.edu/tp/cas}AttributesType" minOccurs="0"/>
 *         &lt;element name="proxyGrantingTicket" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proxies" type="{http://www.yale.edu/tp/cas}ProxiesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticationSuccessType", propOrder = {
    "user",
    "attributes",
    "proxyGrantingTicket",
    "proxies",
    "additionalAttributes"
})
public class AuthenticationSuccessType {

    @XmlElement(required = true)
    protected String user;
    protected AttributesType attributes;
    protected String proxyGrantingTicket;
    protected ProxiesType proxies;

    @XmlAnyElement(lax = true)
    protected List<Object> additionalAttributes;

    /**
     * Gets the value of the user property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Gets the value of the attributes property.
     *
     * @return
     *     possible object is
     *     {@link AttributesType }
     *
     */
    public AttributesType getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     *
     * @param value
     *     allowed object is
     *     {@link AttributesType }
     *
     */
    public void setAttributes(AttributesType value) {
        this.attributes = value;
    }

    /**
     * Gets the value of the proxyGrantingTicket property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProxyGrantingTicket() {
        return proxyGrantingTicket;
    }

    /**
     * Sets the value of the proxyGrantingTicket property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProxyGrantingTicket(String value) {
        this.proxyGrantingTicket = value;
    }

    /**
     * Gets the value of the proxies property.
     *
     * @return
     *     possible object is
     *     {@link ProxiesType }
     *
     */
    public ProxiesType getProxies() {
        return proxies;
    }

    /**
     * Sets the value of the proxies property.
     *
     * @param value
     *     allowed object is
     *     {@link ProxiesType }
     *
     */
    public void setProxies(ProxiesType value) {
        this.proxies = value;
    }

	/**
	 * CAS 2.0 protocol extension support
	 * Returns a reference to the live list
	 * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     *
	 * @return the additionalAttributes
	 */
	public List<Object> getAdditionalAttributes() {
		if (additionalAttributes == null) {
			additionalAttributes = new ArrayList<Object>();
		}
		return additionalAttributes;
	}

	/**
	 * CAS 2.0 protocol extension support
	 * @param additionalAttributes the additionalAttributes to set
	 *
	 * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
	 */
	public void setAdditionalAttributes(List<Object> additionalAttributes) {
		this.additionalAttributes = additionalAttributes;
	}

}
