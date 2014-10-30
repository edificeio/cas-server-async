package fr.wseduc.cas.exceptions;

public enum ErrorCodes {

	INVALID_REQUEST("not all of the required request parameters were present"),
	INVALID_TICKET_SPEC("failure to meet the requirements of validation specification"),
	UNAUTHORIZED_SERVICE_PROXY("the service is not authorized to perform proxy authentication"),
	INVALID_PROXY_CALLBACK("The proxy callback specified is invalid. The credentials specified for proxy authentication do not meet the security requirements"),
	INVALID_TICKET("the ticket provided was not valid, or the ticket did not come from an initial login and \"renew\" was set on validation."),
	INVALID_SERVICE("the ticket provided was valid, but the service specified did not match the service associated with the ticket. CAS MUST invalidate the ticket and disallow future validation of that same ticket."),
	INTERNAL_ERROR("an internal error occurred during ticket validation"),
	UNSUPPORTED_SAML_PROXY_REQUEST("Proxy requests are not supported by Saml procotocol"),
	UNSUPPORTED_SAML_PROXY_VALIDATION("Proxy validation is not supported by Saml procotocol");

	private final String message;

	ErrorCodes(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
