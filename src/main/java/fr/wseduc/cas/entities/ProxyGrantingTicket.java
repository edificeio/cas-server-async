package fr.wseduc.cas.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProxyGrantingTicket {

	private String pgtId;
	private String pgtIOU;
	private List<String> pgtUrls;
	private List<ProxyTicket> proxyTickets;

	public ProxyGrantingTicket() {
		this.pgtId = "PGT-" + UUID.randomUUID().toString();
		this.pgtIOU = "PGTIOU-" + UUID.randomUUID().toString();
		this.pgtUrls = new ArrayList<>();
		this.proxyTickets = new ArrayList<>();
	}

	public synchronized void addUrl(String url) {
		if (!pgtUrls.contains(url)) {
			pgtUrls.add(url);
		}
	}

	public boolean exists(String proxyTicket) {
		return getProxyTicket(proxyTicket) != null;
	}

	public String getPgtId() {
		return pgtId;
	}

	public void setPgtId(String pgtId) {
		this.pgtId = pgtId;
	}

	public String getPgtIOU() {
		return pgtIOU;
	}

	public void setPgtIOU(String pgtIOU) {
		this.pgtIOU = pgtIOU;
	}

	public List<String> getPgtUrls() {
		return pgtUrls;
	}

	public void setPgtUrls(List<String> pgtUrls) {
		this.pgtUrls = pgtUrls;
	}

	public List<ProxyTicket> getProxyTickets() {
		return proxyTickets;
	}

	public void setProxyTickets(List<ProxyTicket> proxyTickets) {
		this.proxyTickets = proxyTickets;
	}

	public ProxyTicket getProxyTicket(String proxyTicket) {
		for (ProxyTicket pt : proxyTickets) {
			if (pt.getPgId().equals(proxyTicket)) {
				return pt;
			}
		}
		return null;
	}

}
