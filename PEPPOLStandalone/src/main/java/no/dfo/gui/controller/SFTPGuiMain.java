package no.dfo.gui.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.LogManager;
import java.util.logging.Logger;


import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logger.MyLogger;
import no.dfo.peppol.PEPPOLStandalone.Main;
import no.dfo.peppol.inbound.inboundMain.PeppolInbound;
import no.dfo.peppol.outbound.PeppolOutbound;

public class SFTPGuiMain implements Initializable {
	
	private Stage stage;
	private Main main;

	public void setMain(Main main) {
		this.main = main;
	}

	private int flag = 0;
	@FXML
	private Button SFTPConnect;
	@FXML
	private Label selectedDirectoryLabel;

	@FXML
	private Button DirTest;

	@FXML
	private Button selectDIR;

	@FXML
	private Button putFileButton;

	@FXML
	private Button SFTPFile;
	@FXML
	private Label localFileselected;
	@FXML
	private TextField SFTPUserName;
	@FXML
	private TextArea LogTextAreaSFTP;
	@FXML
	private PasswordField SFTPPassword;
	@FXML
	private TextField SFTPHost;
	@FXML
	private ListView DirectoryListing;
	SFTPFunctions PI = null;
	String dynamicDirectory;
	UtilityValueSFTP uv = null;

	static {
		// Fix a freeze in Windows 10, JDK 8 and touchscreen
		System.setProperty("glass.accessible.force", "false");
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Logger logger = Logger.getLogger("Peppol_SFTP");
		logger.addHandler(new MyLogger(LogTextAreaSFTP));
		LogTextAreaSFTP.setEditable(false);
		SFTPConnect.setText("Connect");
		try {
			uv = new UtilityValueSFTP();
			logger.info("SFTP App Started");

			SFTPConnect.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {

					try {
						if (SFTPConnect.getText().equalsIgnoreCase("Disconnect")) {
							PI.disconnectSFTP();
							logger.info("[Session Disconnected]");
							SFTPConnect.setText("Connect");
							DirectoryListing.getItems().clear();
						}else {
						uv.setPath("/");
						uv.setLog(logger);
						flag = 0;
						LogTextAreaSFTP.clear();
						DirectoryListing.getItems().clear();
						LogTextAreaSFTP.clear();
						PI = new SFTPFunctions();

						
						
						  uv.setPassword(SFTPPassword.getText());
						  uv.setRemoteHost(SFTPHost.getText());
						  uv.setUsername(SFTPUserName.getText());
						uv.setPath("");

						PI.setupJsch(uv);
						

						ArrayList<String> dirList = PI.listFiles(uv.getChannelSftp(), uv, flag);
						flag = flag + 1;
						for (String x : dirList) {
							DirectoryListing.getItems().add(x);
						}
						dirList.clear();
						if (uv.getChannelSftp().isConnected()) {
							SFTPConnect.setText("Disconnect");
						} else {
							SFTPConnect.setText("Connect");
						}
						}

					} catch (Exception e) {
						Alert alert = new Alert(AlertType.ERROR, "Failed in SFTP" + e.getMessage(), ButtonType.CLOSE);
						alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
						alert.showAndWait();
						e.printStackTrace();
					}
				}
			});

			DirectoryListing.setOnMouseClicked(event -> {
				try {

					ObservableList selectedIndices = DirectoryListing.getSelectionModel().getSelectedIndices();

					for (Object o : selectedIndices) {

						String x = ((String) DirectoryListing.getItems().get((Integer) o));
						uv.setPath(uv.getPath() + uv.getInitialpath()
								+ ((String) DirectoryListing.getItems().get((Integer) o)));

						if (!x.equalsIgnoreCase("..")) {

						} else {

						}
						// System.out.println(o.toString());

					}

					ArrayList<String> dirList = PI.listFiles(uv.getChannelSftp(), uv, flag);
					flag = flag + 1;
					DirectoryListing.getItems().clear();
					for (String x : dirList) {
						DirectoryListing.getItems().add(x);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			});

			SFTPFile.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Select Local File");
					List<File> localFileToPlace = fileChooser.showOpenMultipleDialog(stage);

					uv.setLocalFile(localFileToPlace);
					if (localFileToPlace.size() > 1) {
						localFileselected.setText(localFileToPlace.size() + " Files selected");
					} else {
						localFileselected.setText(localFileToPlace.get(0).getName());
					}

				}
			});

			putFileButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						logger.info("[Started Put Session]");
						PI.whenUploadFileUsingJsch_thenSuccess(uv);
						
						  logger.info("[Disconnecting SFTP Session ...]"); PI.disconnectSFTP();
						  DirectoryListing.getItems().clear();
						  logger.info("[Session disconnected Please connect again if required...]");
						  SFTPConnect.setText("Connect");
						  
						 
					} catch (JSchException e) {
						Alert alert = new Alert(AlertType.ERROR, "JSCHException:" + e.getMessage(), ButtonType.CLOSE);
						alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
						alert.showAndWait();
						e.printStackTrace();
					} catch (SftpException e) {
						Alert alert = new Alert(AlertType.ERROR, "SftpException:" + e.getMessage(), ButtonType.CLOSE);
						alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
						alert.showAndWait();
						e.printStackTrace();
					}

				}
			});

		} catch (Exception E) {
			Alert alert = new Alert(AlertType.ERROR, "SftpException:" + E.getMessage(), ButtonType.CLOSE);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.showAndWait();
			E.printStackTrace();

		}

	}

}
