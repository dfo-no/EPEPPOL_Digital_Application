package no.dfo.gui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import eu.europa.esig.dss.enumerations.ASiCContainerType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logger.MyLogger;
import no.dfo.peppol.PEPPOLStandalone.Main;
import no.dfo.peppol.common.ocsp.OCSPLookupApp;
import no.dfo.peppol.common.ocsp.UtilityValueOCSP;
import no.dfo.peppol.inbound.inboundMain.PeppolInbound;

public class OCSPMain implements Initializable{
	private Stage stage;
	private Main main;
	
	public void setMain(Main main)   {
	    this.main = main;   
	}
	
	
	@FXML
	private Button OCSPLeafCertificate;
	
	
	@FXML
	private Button DirTest;
	@FXML
	private ListView DirectoryListing;
	
	@FXML
	private Button VerificationStart;
	

	@FXML
	private Label certficateselected;
	@FXML
	private TextArea LogTextArea;
	
	StringBuilder Logs=new StringBuilder();
	UtilityValueOCSP uv=new UtilityValueOCSP();
	
	
	static {
		System.setProperty("glass.accessible.force", "false");
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Logger logger = Logger.getLogger("Peppol_OCSP");
		logger.addHandler(new MyLogger(LogTextArea));
		uv.setLog(logger);
		LogTextArea.setEditable(false);
		logger.info("OCSP Check Started");
		
		
		OCSPLeafCertificate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Select Public certificate");
				File publicCert = fileChooser.showOpenDialog(stage);
				uv.setLeafCert(publicCert);
				logger.info("Public certificate for OCSP Check: "+publicCert.getName());
				certficateselected.setText(publicCert.getName());
				
			}
		});
		
		VerificationStart.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					LogTextArea.clear();
				OCSPLookupApp app=new OCSPLookupApp(uv);
					
					try {
						
						app.getIntermediateCert("APP");
						
					} catch (Exception e) {
						logger.info("Error in OCSP Check "+e.getMessage());
						Alert alert = new Alert(AlertType.ERROR, "Error in OCSP Check: " + e.getMessage(), ButtonType.CLOSE);
						alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
						alert.showAndWait();
						e.printStackTrace();
					}
				}
			});
		
		
		
		
		
		
	}
	
	
	

}
