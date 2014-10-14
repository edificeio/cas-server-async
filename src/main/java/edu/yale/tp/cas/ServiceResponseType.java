package edu.yale.tp.cas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceResponseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ServiceResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="authenticationSuccess" type="{http://www.yale.edu/tp/cas}AuthenticationSuccessType"/>
 *         &lt;element name="authenticationFailure" type="{http://www.yale.edu/tp/cas}AuthenticationFailureType"/>
 *         &lt;element name="proxySuccess" type="{http://www.yale.edu/tp/cas}ProxySuccessType"/>
 *         &lt;element name="proxyFailure" type="{http://www.yale.edu/tp/cas}ProxyFailureType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceResponseType", propOrder = {
    "authenticationSuccess",
    "authenticationFailure",
    "proxySuccess",
    "proxyFailure"
})
public class ServiceResponseType {

    protected AuthenticationSuccessType authenticationSuccess;
    protected AuthenticationFailureType authenticationFailure;
    protected ProxySuccessType proxySuccess;
    protected ProxyFailureType proxyFailure;

    /**
     * Gets the value of the authenticationSuccess property.
     *
     * @return
     *     possible object is
     *     {@link AuthenticationSuccessType }
     *
     */
    public AuthenticationSuccessType getAuthenticationSuccess() {
        return authenticationSuccess;
    }

    /**
     * Sets the value of the authenticationSuccess property.
     *
     * @param value
     *     allowed object is
     *     {@link AuthenticationSuccessType }
     *
     */
    public void setAuthenticationSuccess(AuthenticationSuccessType value) {
        this.authenticationSuccess = value;
    }

    /**
     * Gets the value of the authenticationFailure property.
     *
     * @return
     *     possible object is
     *     {@link AuthenticationFailureType }
     *
     */
    public AuthenticationFailureType getAuthenticationFailure() {
        return authenticationFailure;
    }

    /**
     * Sets the value of the authenticationFailure property.
     *
     * @param value
     *     allowed object is
     *     {@link AuthenticationFailureType }
     *
     */
    public void setAuthenticationFailure(AuthenticationFailureType value) {
        this.authenticationFailure = value;
    }

    /**
     * Gets the value of the proxySuccess property.
     *
     * @return
     *     possible object is
     *     {@link ProxySuccessType }
     *
     */
    public ProxySuccessType getProxySuccess() {
        return proxySuccess;
    }

    /**
     * Sets the value of the proxySuccess property.
     *
     * @param value
     *     allowed object is
     *     {@link ProxySuccessType }
     *
     */
    public void setProxySuccess(ProxySuccessType value) {
        this.proxySuccess = value;
    }

    /**
     * Gets the value of the proxyFailure property.
     *
     * @return
     *     possible object is
     *     {@link ProxyFailureType }
     *
     */
    public ProxyFailureType getProxyFailure() {
        return proxyFailure;
    }

    /**
     * Sets the value of the proxyFailure property.
     *
     * @param value
     *     allowed object is
     *     {@link ProxyFailureType }
     *
     */
    public void setProxyFailure(ProxyFailureType value) {
        this.proxyFailure = value;
    }

}
