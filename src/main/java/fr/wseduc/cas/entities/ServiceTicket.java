package fr.wseduc.cas.entities;

import java.util.UUID;

public class ServiceTicket {

	private String ticket;
	private String service;
	private ProxyGrantingTicket pgt;
	private long issued;
	private boolean used;

	public ServiceTicket() {

	}

	public ServiceTicket(String service) {
		this.service = service;
		this.ticket = "ST-" + UUID.randomUUID().toString();
		this.issued = System.currentTimeMillis();
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

	public ProxyGrantingTicket getPgt() {
		return pgt;
	}

	public void setPgt(ProxyGrantingTicket pgt) {
		this.pgt = pgt;
	}

	public long getIssued() {
		return issued;
	}

	public void setIssued(long issued) {
		this.issued = issued;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

}
