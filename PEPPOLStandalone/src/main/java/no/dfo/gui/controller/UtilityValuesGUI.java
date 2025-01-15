package no.dfo.gui.controller;

import java.io.File;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class UtilityValuesGUI {
	private List<File> SBDFile;
	private List<File> ISOFiles;

	private String keystoreName;
	private File p12DecryptionStore;
	private String keystorePwd;
	private String userIdentifier;

	private String divisionIdentifier;
	private String customerIdentfier;

	private DigestAlgorithm supportedDigestAlgorithms;
	private ArrayList<String> fileName;
	private String Alias;
	private String KeystorePath;
	private Logger ouboundLog;

	private Logger inboundLog;
	private Logger OCSPlog;
	private Logger SFTPLog;
	private String encCertInputType;
	private String BCLEnvironment;
	
	@FXML
	private TextField recOrgFX;

	public TextField getRecOrgFX() {
		return recOrgFX;
	}

	public void setRecOrgFX(TextField recOrgFX) {
		this.recOrgFX = recOrgFX;
	}

	private DigestAlgorithm signatureDigest;

	private String Repack;
	private File p12EncryptionStore;
	private String signKeystoreName;
	private String signKeystorePath;
	private File p12SignStore;
	private String encKeystoreName;
	private String encKeystorePath;
	private String RepackPath;

	private String signkeystorePwd;
	private String EnckeystorePwd;
	private String AliasSign;
	private String AliasEnc;
	private String recOrg;
	private String senderOrg;
	private X509Certificate bclCert;

	public String getUserIdentifier() {
		return userIdentifier;
	}

	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	public String getDivisionIdentifier() {
		return divisionIdentifier;
	}

	public void setDivisionIdentifier(String divisionIdentifier) {
		this.divisionIdentifier = divisionIdentifier;
	}

	public String getCustomerIdentfier() {
		return customerIdentfier;
	}

	public void setCustomerIdentfier(String customerIdentfier) {
		this.customerIdentfier = customerIdentfier;
	}

	public Logger getOuboundLog() {
		return ouboundLog;
	}

	public void setOuboundLog(Logger ouboundLog) {
		this.ouboundLog = ouboundLog;
	}

	public Logger getInboundLog() {
		return inboundLog;
	}

	public void setInboundLog(Logger inboundLog) {
		this.inboundLog = inboundLog;
	}

	public Logger getOCSPlog() {
		return OCSPlog;
	}

	public void setOCSPlog(Logger oCSPlog) {
		OCSPlog = oCSPlog;
	}

	public Logger getSFTPLog() {
		return SFTPLog;
	}

	public void setSFTPLog(Logger sFTPLog) {
		SFTPLog = sFTPLog;
	}

	public List<File> getISOFiles() {
		return ISOFiles;
	}

	public void setISOFiles(List<File> iSOFiles) {
		ISOFiles = iSOFiles;
	}

	public X509Certificate getBclCert() {
		return bclCert;
	}

	public void setBclCert(X509Certificate bclCert) {
		this.bclCert = bclCert;
	}

	public String getBCLEnvironment() {
		return BCLEnvironment;
	}

	public void setBCLEnvironment(String bCLEnvironment) {
		BCLEnvironment = bCLEnvironment;
	}

	public String getEncCertInputType() {
		return encCertInputType;
	}

	public void setEncCertInputType(String encCertInputType) {
		this.encCertInputType = encCertInputType;
	}

	public DigestAlgorithm getSignatureDigest() {
		return signatureDigest;
	}

	public void setSignatureDigest(DigestAlgorithm signatureDigest) {
		this.signatureDigest = signatureDigest;
	}

	public String getRecOrg() {
		return recOrg;
	}

	public void setRecOrg(String recOrg) {
		this.recOrg = recOrg;
	}

	public String getSenderOrg() {
		return senderOrg;
	}

	public void setSenderOrg(String senderOrg) {
		this.senderOrg = senderOrg;
	}

	public String getAliasSign() {
		return AliasSign;
	}

	public void setAliasSign(String aliasSign) {
		AliasSign = aliasSign;
	}

	public String getAliasEnc() {
		return AliasEnc;
	}

	public void setAliasEnc(String aliasEnc) {
		AliasEnc = aliasEnc;
	}

	public String getSignkeystorePwd() {
		return signkeystorePwd;
	}

	public void setSignkeystorePwd(String signkeystorePwd) {
		this.signkeystorePwd = signkeystorePwd;
	}

	public String getEnckeystorePwd() {
		return EnckeystorePwd;
	}

	public void setEnckeystorePwd(String enckeystorePwd) {
		EnckeystorePwd = enckeystorePwd;
	}

	public String getRepackPath() {
		return RepackPath;
	}

	public void setRepackPath(String repackPath) {
		RepackPath = repackPath;
	}

	public File getP12EncryptionStore() {
		return p12EncryptionStore;
	}

	public void setP12EncryptionStore(File p12EncryptionStore) {
		this.p12EncryptionStore = p12EncryptionStore;
	}

	public String getSignKeystoreName() {
		return signKeystoreName;
	}

	public void setSignKeystoreName(String signKeystoreName) {
		this.signKeystoreName = signKeystoreName;
	}

	public String getSignKeystorePath() {
		return signKeystorePath;
	}

	public void setSignKeystorePath(String signKeystorePath) {
		this.signKeystorePath = signKeystorePath;
	}

	public File getP12SignStore() {
		return p12SignStore;
	}

	public void setP12SignStore(File p12SignStore) {
		this.p12SignStore = p12SignStore;
	}

	public String getEncKeystoreName() {
		return encKeystoreName;
	}

	public void setEncKeystoreName(String encKeystoreName) {
		this.encKeystoreName = encKeystoreName;
	}

	public String getEncKeystorePath() {
		return encKeystorePath;
	}

	public void setEncKeystorePath(String encKeystorePath) {
		this.encKeystorePath = encKeystorePath;
	}

	public String getRepack() {
		return Repack;
	}

	public void setRepack(String repack) {
		Repack = repack;
	}

	public String getTimestamp() {
		String timeStamp = new SimpleDateFormat("dd-MM-YYYY-HH.mm.ss").format(new java.util.Date());
		return timeStamp;
	}

	public String getKeystorePath() {
		return KeystorePath;
	}

	public void setKeystorePath(String keystorePath) {
		KeystorePath = keystorePath;
	}

	public String getAlias() {
		return Alias;
	}

	public void setAlias(String alias) {
		Alias = alias;
	}

	public ArrayList<String> getFileName() {
		return fileName;
	}

	public void setFileName(ArrayList<String> fileName) {
		this.fileName = fileName;
	}

	public ArrayList<String> getDecryptedPath() {
		return DecryptedPath;
	}

	public void setDecryptedPath(ArrayList<String> decryptedPath) {
		DecryptedPath = decryptedPath;
	}

	ArrayList<String> DecryptedPath;

	public DigestAlgorithm getSupportedDigestAlgorithms() {
		return supportedDigestAlgorithms;
	}

	public void setSupportedDigestAlgorithms(DigestAlgorithm supportedDigestAlgorithms) {
		this.supportedDigestAlgorithms = supportedDigestAlgorithms;
	}

	private ObjectProperty<File> fileToSignP = new SimpleObjectProperty<>();

	private ObjectProperty<File> fileToSignProp = new SimpleObjectProperty<>();

	public File getP12DecryptionStore() {
		return p12DecryptionStore;
	}

	public void setP12DecryptionStore(File p12DecryptionStore) {
		this.p12DecryptionStore = p12DecryptionStore;
	}

	public String getKeystoreName() {
		return keystoreName;
	}

	public void setKeystoreName(String keystoreName) {
		this.keystoreName = keystoreName;
	}

	public String getKeystorePwd() {
		return keystorePwd;
	}

	public void setKeystorePwd(String keystorePwd) {
		this.keystorePwd = keystorePwd;
	}

	public List<File> getSBDFile() {
		return SBDFile;
	}

	public void setSBDFile(List<File> fileToSign) {
		this.SBDFile = fileToSign;
	}

}
