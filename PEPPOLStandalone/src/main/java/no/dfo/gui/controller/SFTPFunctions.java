package no.dfo.gui.controller;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class SFTPFunctions {
	static Session jschSession = null;
	static Logger Log;
	ChannelSftp channelSftp;

	public boolean setupJsch(UtilityValueSFTP uv) throws JSchException, Exception {
		Log = uv.getLog();

		String remoteHost = uv.getRemoteHost();
		String username = uv.getUsername();
		String password = uv.getPassword();
		
		if(remoteHost.equalsIgnoreCase("") || username.equalsIgnoreCase("")||password.equalsIgnoreCase("")) {
			throw new Exception("Mandatory Prameter missing");
		}
		String command = "ssh-keyscan -H -t rsa " + remoteHost;
		Log.info("[" + "Connecting to SFTP]:UserName:" + username + " Host:" + remoteHost);

		Process p = Runtime.getRuntime().exec(command);
		InputStream kh = p.getInputStream();

		JSch jsch = new JSch();

		jsch.setKnownHosts(kh);

		jschSession = jsch.getSession(username, remoteHost);
		jschSession.setPassword(password);
		jschSession.connect();
		
		channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
		channelSftp.connect();

		if (channelSftp.isConnected()) {
			Log.info("[" + "SFTP Connection successfull]");

		}
		uv.setChannelSftp(channelSftp);

		return true;
	}

	public boolean disconnectSFTP() {

		if (jschSession != null) {

			jschSession.disconnect();
			channelSftp.disconnect();

		}
		return true;

	}

	public ArrayList<String> listFiles(ChannelSftp channelSftp, UtilityValueSFTP uv, int flag)
			throws JSchException, Exception {
		ArrayList<String> directoryItem = new ArrayList<String>();
		String PathNew = null;

		if (flag == 0) {
			PathNew = channelSftp.getHome();
		} else if (channelSftp.stat(uv.getPath()).isDir()) {
			PathNew = uv.getPath();
		} else if (!channelSftp.stat(uv.getPath()).isDir()) {
			Log.info("[" + "ERROR:Please select directory instead of file]");
			PathNew = channelSftp.getHome();
			uv.setPath("");
		}
		channelSftp.cd(PathNew);
		Log.info("[" + "Working Directory:]" + channelSftp.pwd());
		uv.setPutSFTPPath(channelSftp.pwd());
		Vector<LsEntry> filelist = channelSftp.ls(PathNew);

		for (int i = 0; i < filelist.size(); i++) {
			LsEntry entry = (LsEntry) filelist.get(i);
			directoryItem.add(entry.getFilename());
		}

		return directoryItem;

	}

	public void whenUploadFileUsingJsch_thenSuccess(UtilityValueSFTP uv) throws JSchException, SftpException {

		ChannelSftp channelSftp = uv.getChannelSftp();
		Log.info("[Starting to Put File to Path]" + uv.getPutSFTPPath() + channelSftp.getHome());

		String remoteDir = uv.getPutSFTPPath() + channelSftp.getHome();
		for (File fn : uv.getLocalFile()) {
			Log.info("[Putting File:]" + fn.getName());
			channelSftp.put(fn.getAbsolutePath(), remoteDir + fn.getName());
		}
		channelSftp.disconnect();

		Log.info("[File successfuly Placed]:" + uv.getPutSFTPPath() + channelSftp.getHome());

	}

}
