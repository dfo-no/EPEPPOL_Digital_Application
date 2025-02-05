package no.dfo.peppol.outbound;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class UtilityValueSetting {
	private String sbdh;
	private String metadata;
	private String isocontent;
	private byte[] innerAsiceZipBytes;
	private String filename;
	private Logger log;
	private String certUsed;

	public String getCertUsed() {
		return certUsed;
	}

	public void setCertUsed(String certUsed) {
		this.certUsed = certUsed;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public String getSbdh() {
		return sbdh;
	}

	public void setSbdh(String sbdh) {
		this.sbdh = sbdh;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getIsocontent() {
		return isocontent;
	}

	public void setIsocontent(String isocontent) {
		this.isocontent = isocontent;
	}

	public byte[] getInnerAsiceZipBytes() {
		return innerAsiceZipBytes;
	}

	public void setInnerAsiceZipBytes(byte[] innerAsiceZipBytes) {
		this.innerAsiceZipBytes = innerAsiceZipBytes;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
