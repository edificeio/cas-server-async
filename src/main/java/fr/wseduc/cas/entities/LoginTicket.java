package fr.wseduc.cas.entities;

import java.util.UUID;

public class LoginTicket {

	private String ticket = "LT-" + UUID.randomUUID().toString();

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getTicket() {
		return ticket;
	}

}
