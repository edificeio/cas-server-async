package edu.yale.tp.cas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.w3c.dom.Element;


/**
 * <p>Java class for AttributesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AttributesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="authenticationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="longTermAuthenticationRequestTokenUsed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isFromNewLogin" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="memberOf" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="userAttributes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributesType", propOrder = {
    "authenticationDate",
    "longTermAuthenticationRequestTokenUsed",
    "isFromNewLogin",
    "memberOf",
    "userAttributes"
})
public class AttributesType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar authenticationDate;
    protected boolean longTermAuthenticationRequestTokenUsed;
    protected boolean isFromNewLogin;
    protected List<String> memberOf;
    protected AttributesType.UserAttributes userAttributes;

    /**
     * Gets the value of the authenticationDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getAuthenticationDate() {
        return authenticationDate;
    }

    /**
     * Sets the value of the authenticationDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setAuthenticationDate(XMLGregorianCalendar value) {
        this.authenticationDate = value;
    }

    /**
     * Gets the value of the longTermAuthenticationRequestTokenUsed property.
     *
     */
    public boolean isLongTermAuthenticationRequestTokenUsed() {
        return longTermAuthenticationRequestTokenUsed;
    }

    /**
     * Sets the value of the longTermAuthenticationRequestTokenUsed property.
     *
     */
    public void setLongTermAuthenticationRequestTokenUsed(boolean value) {
        this.longTermAuthenticationRequestTokenUsed = value;
    }

    /**
     * Gets the value of the isFromNewLogin property.
     *
     */
    public boolean isIsFromNewLogin() {
        return isFromNewLogin;
    }

    /**
     * Sets the value of the isFromNewLogin property.
     *
     */
    public void setIsFromNewLogin(boolean value) {
        this.isFromNewLogin = value;
    }

    /**
     * Gets the value of the memberOf property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the memberOf property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMemberOf().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getMemberOf() {
        if (memberOf == null) {
            memberOf = new ArrayList<String>();
        }
        return this.memberOf;
    }

    /**
     * Gets the value of the userAttributes property.
     *
     * @return
     *     possible object is
     *     {@link AttributesType.UserAttributes }
     *
     */
    public AttributesType.UserAttributes getUserAttributes() {
        return userAttributes;
    }

    /**
     * Sets the value of the userAttributes property.
     *
     * @param value
     *     allowed object is
     *     {@link AttributesType.UserAttributes }
     *
     */
    public void setUserAttributes(AttributesType.UserAttributes value) {
        this.userAttributes = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class UserAttributes {

        @XmlAnyElement(lax = true)
        protected List<Object> any;

        /**
         * Gets the value of the any property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the any property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAny().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Element }
         * {@link Object }
         *
         *
         */
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<Object>();
            }
            return this.any;
        }

    }

}
