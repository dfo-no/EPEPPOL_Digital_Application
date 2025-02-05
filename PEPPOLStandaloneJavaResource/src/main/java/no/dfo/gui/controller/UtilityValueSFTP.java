package no.dfo.gui.controller;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import com.jcraft.jsch.ChannelSftp;

public class UtilityValueSFTP {
	private String remoteHost ;
	private String username ;
	private String password;
	private static ChannelSftp channelSftp;
	private String Initialpath="/" ;
	private List<File> localFile;
	private String path ;
	private Logger log;
	private String putSFTPPath;
	private String localFileName;
	
	
	public String getPutSFTPPath() {
		return putSFTPPath;
	}
	public void setPutSFTPPath(String putSFTPPath) {
		this.putSFTPPath = putSFTPPath;
	}
	public Logger getLog() {
		return log;
	}
	public void setLog(Logger log) {
		this.log = log;
	}
	public List<File> getLocalFile() {
		return localFile;
	}
	public void setLocalFile(List<File> localFile) {
		this.localFile = localFile;
	}
	public String getInitialpath() {
		return Initialpath;
	}

	
	
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public ChannelSftp getChannelSftp() {
		return channelSftp;
	}
	public void setChannelSftp(ChannelSftp channelSftp) {
		this.channelSftp = channelSftp;
	}
	public String getRemoteHost() {
		return remoteHost;
	}
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
