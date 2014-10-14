package fr.wseduc.cas.entities;

import java.util.UUID;

public class ServiceTicket {

	private String ticket;
	private String service;

	public ServiceTicket() {

	}

	public ServiceTicket(String service) {
		this.service = service;
		this.ticket = "ST-" + UUID.randomUUID().toString();
	}

	public String getTicket() {
		return ticket;
	}

	public String getService() {
		return service;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String redirectUri() {
		return (service.contains("?")) ?
				service + "&ticket=" + ticket : service + "?ticket=" + ticket;
	}

}
