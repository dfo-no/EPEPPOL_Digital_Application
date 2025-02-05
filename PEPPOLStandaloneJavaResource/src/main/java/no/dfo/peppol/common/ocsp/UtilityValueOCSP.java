package no.dfo.peppol.common.ocsp;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

public class UtilityValueOCSP {
	private File leafCert;
	private Logger log;
	private String certType;
	private X509Certificate certPub;

	public X509Certificate getCertPub() {
		return certPub;
	}

	public void setCertPub(X509Certificate certPub) {
		this.certPub = certPub;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public File getLeafCert() {
		return leafCert;
	}

	public void setLeafCert(File leafCert) {
		this.leafCert = leafCert;
	}

}
