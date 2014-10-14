package edu.yale.tp.cas;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the edu.yale.tp.cas package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ServiceResponse_QNAME = new QName("http://www.yale.edu/tp/cas", "serviceResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: edu.yale.tp.cas
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AttributesType }
     *
     */
    public AttributesType createAttributesType() {
        return new AttributesType();
    }

    /**
     * Create an instance of {@link ServiceResponseType }
     *
     */
    public ServiceResponseType createServiceResponseType() {
        return new ServiceResponseType();
    }

    /**
     * Create an instance of {@link ProxyFailureType }
     *
     */
    public ProxyFailureType createProxyFailureType() {
        return new ProxyFailureType();
    }

    /**
     * Create an instance of {@link AuthenticationFailureType }
     *
     */
    public AuthenticationFailureType createAuthenticationFailureType() {
        return new AuthenticationFailureType();
    }

    /**
     * Create an instance of {@link ProxySuccessType }
     *
     */
    public ProxySuccessType createProxySuccessType() {
        return new ProxySuccessType();
    }

    /**
     * Create an instance of {@link ProxiesType }
     *
     */
    public ProxiesType createProxiesType() {
        return new ProxiesType();
    }

    /**
     * Create an instance of {@link AuthenticationSuccessType }
     *
     */
    public AuthenticationSuccessType createAuthenticationSuccessType() {
        return new AuthenticationSuccessType();
    }

    /**
     * Create an instance of {@link AttributesType.UserAttributes }
     *
     */
    public AttributesType.UserAttributes createAttributesTypeUserAttributes() {
        return new AttributesType.UserAttributes();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.yale.edu/tp/cas", name = "serviceResponse")
    public JAXBElement<ServiceResponseType> createServiceResponse(ServiceResponseType value) {
        return new JAXBElement<ServiceResponseType>(_ServiceResponse_QNAME, ServiceResponseType.class, null, value);
    }

}
