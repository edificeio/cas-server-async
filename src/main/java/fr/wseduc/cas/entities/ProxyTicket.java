package fr.wseduc.cas.entities;

import java.util.UUID;

public class ProxyTicket {

	private String pgId;
	private long issued;
	private boolean used;

	public ProxyTicket() {
		pgId = UUID.randomUUID().toString();
		issued = System.currentTimeMillis();
	}

	public String getPgId() {
		return pgId;
	}

	public void setPgId(String pgId) {
		this.pgId = pgId;
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
