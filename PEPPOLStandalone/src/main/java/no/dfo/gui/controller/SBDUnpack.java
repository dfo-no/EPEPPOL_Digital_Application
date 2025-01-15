package no.dfo.gui.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import logger.MyLogger;
import no.dfo.peppol.inbound.inboundMain.PeppolInbound;

public class SBDUnpack implements Initializable {
	protected Stage stage;
	private final List<DigestAlgorithm> supportedDigestAlgorithms = Arrays.asList(DigestAlgorithm.SHA1,
			DigestAlgorithm.SHA224, DigestAlgorithm.SHA256, DigestAlgorithm.SHA384, DigestAlgorithm.SHA512,
			DigestAlgorithm.SHA3_224, DigestAlgorithm.SHA3_256, DigestAlgorithm.SHA3_384, DigestAlgorithm.SHA3_512);
	/*
	 * private Main main; public void setMain(Main main) { this.main = main; }
	 */

	@FXML
	private Button SBDfileSelectButton;

	@FXML
	private Button setPassButton;

	@FXML
	private HBox setPassHbox;

	@FXML
	private HBox SigntPkcsFile;

	@FXML
	private HBox SigntPkcsPassword;

	@FXML
	private HBox SignPkcsFile;

	@FXML
	private HBox SignPkcsPassword;

	@FXML
	private Label fileForPKCSStoreSign;

	@FXML
	private PasswordField SignkcsPassword;

	@FXML
	private TextField AliasTextEnc;
	@FXML
	private TextField AliasTextSign;

	//////////////////////////////////
	@FXML
	private TextField AliasText;

	@FXML
	private Button DecryptionKeystore;

	@FXML
	private HBox DecryptPkcsPassword;

	@FXML
	private PasswordField pkcsPassword;

	@FXML
	private Button DirectoryChooser;

	@FXML
	private ToggleGroup toogleSigFormat;

	@FXML
	private ToggleGroup toggleSigPackaging;

	@FXML
	private ToggleGroup toggleSignatureOption;

	@FXML
	private RadioButton cadesRadio;

	@FXML
	private RadioButton padesRadio;

	@FXML
	private RadioButton xadesRadio;

	@FXML
	private RadioButton jadesRadio;

	@FXML
	private HBox hSignaturePackaging;

	@FXML
	private HBox hSignatureOption;

	@FXML
	private HBox hBoxDigestAlgos;

	@FXML
	private HBox LogBox;

	@FXML
	private TextArea LogTextAreaSBD;
	@FXML
	private TextArea LogTextAreaSign;
	@FXML
	private RadioButton envelopedRadio;

	@FXML
	private RadioButton envelopingRadio;

	@FXML
	private RadioButton detachedRadio;

	@FXML
	private RadioButton internallyDetachedRadio;

	@FXML
	private RadioButton tlSigning;

	@FXML
	private RadioButton xmlManifest;

	@FXML
	private Label warningLabel;

	@FXML
	private Label fileForPKCSStore;

	@FXML
	private ToggleGroup toggleDigestAlgo;

	@FXML
	private ToggleGroup toggleSigToken;

	@FXML
	private RadioButton pkcs11Radio;

	@FXML
	private RadioButton pkcs12Radio;

	@FXML
	private RadioButton mscapiRadio;

	@FXML
	private HBox hPkcsFile;

	@FXML
	private Label labelPkcs11File;

	@FXML
	private Label labelPkcs12File;

	@FXML
	private Label fileForSigning;

	@FXML
	private Button pkcsFileButton;

	@FXML
	private Button signButton;

	@FXML
	private ProgressIndicator progressSign;

	@FXML
	private Button refreshLOTL;
	@FXML
	private HBox RepackReqHbox;
	@FXML
	private HBox refreshBox;

	@FXML
	private Label nbCertificates;

	StringBuilder Logs = new StringBuilder();
	UtilityValuesGUI uv = new UtilityValuesGUI();
	String Repack;
	File DecryptedDir ;

	static {
		// Fix a freeze in Windows 10, JDK 8 and touchscreen
		System.setProperty("glass.accessible.force", "false");
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LogTextAreaSBD.clear();
		Logger logger = Logger.getLogger("Peppol_Inbound");
		logger.addHandler(new MyLogger(LogTextAreaSBD));
		uv.setInboundLog(logger);

		try {

			logger.info("Started PEPPOL Application");

			LogTextAreaSBD.setEditable(false);
			SBDfileSelectButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("SBD FIle");
					List<File> SBDXML = fileChooser.showOpenMultipleDialog(stage);
					uv.setSBDFile(SBDXML);
					ArrayList<String> fileNames = new ArrayList<String>();
					for (File fn : SBDXML) {
						fileNames.add(fn.getName());
					}
					uv.setFileName(fileNames);
					if(uv.getFileName().size()>1) {
						fileForSigning.setText(uv.getFileName().size()+" Files selected");
					}else {
						fileForSigning.setText(uv.getFileName().get(0));
					}
					
				}
			});
			// SBDfileSelectButton.textProperty().bindBidirectional(uv.fileToSignProperty(),
			// new FIleToStringConverter());

			DecryptionKeystore.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Select P12 File for Decryption");
					File p12DecryptionStore = fileChooser.showOpenDialog(stage);
					fileForPKCSStore.setText(p12DecryptionStore.getName());
					uv.setP12DecryptionStore(p12DecryptionStore);
					uv.setKeystoreName(p12DecryptionStore.getName());
					uv.setKeystorePath(p12DecryptionStore.getPath());
				}
			});

			/*
			 * for (DigestAlgorithm digestAlgo : supportedDigestAlgorithms) { RadioButton rb
			 * = new RadioButton(digestAlgo.getName()); rb.setUserData(digestAlgo);
			 * rb.setToggleGroup(toggleDigestAlgo); hBoxDigestAlgos.getChildren().add(rb); }
			 * 
			 * toggleDigestAlgo.selectedToggleProperty().addListener(new
			 * ChangeListener<Toggle>() {
			 * 
			 * @Override public void changed(ObservableValue<? extends Toggle> observable,
			 * Toggle oldValue, Toggle newValue) { if (newValue != null) { DigestAlgorithm
			 * digestAlgorithm = (DigestAlgorithm) newValue.getUserData();
			 * uv.setSupportedDigestAlgorithms(digestAlgorithm); } else {
			 * uv.setSupportedDigestAlgorithms(null); } } });
			 */
			DirectoryChooser.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					DirectoryChooser directoryChooser = new DirectoryChooser();
					directoryChooser.setTitle("Select P12 File for Decryption");
					DecryptedDir = directoryChooser.showDialog(stage);
					DirectoryChooser.setText(DecryptedDir.getAbsolutePath());
					/*
					 * uv.setP12DecryptionStore(p12DecryptionStore);
					 * uv.setKeystoreName(p12DecryptionStore.getAbsolutePath());
					 */
					// uv.setDecryptedPath(DecryptedDir.getAbsolutePath());
					
					// Buton Pressed C:\PEPPOLTest\PEPPOLEndToEnd\ZipOuterAsic
				}
			});

			signButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					LogTextAreaSBD.clear();
					ArrayList<String> decryptedPathList = new ArrayList<String>();
					for (File fn : uv.getSBDFile()) {
						File fn1 = new File(DecryptedDir.getAbsolutePath() + File.separator + fn.getName());
						fn.mkdir();
						decryptedPathList.add(DecryptedDir.getAbsolutePath() + File.separator + fn.getName());
					}

					uv.setDecryptedPath(decryptedPathList);
					System.out.println(decryptedPathList);
					

					uv.setAlias(AliasText.getText());

					uv.setKeystorePwd(pkcsPassword.getText());

					PeppolInbound PI = new PeppolInbound();
					try {
						LogTextAreaSBD.appendText("Inside App" + "\n");
						PI.PeppolInboundFunc(uv, PI);

					} catch (Exception e) {
						Alert alert = new Alert(AlertType.ERROR, "Unable to save file : " + e.getMessage(),
								ButtonType.CLOSE);
						alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
						alert.showAndWait();
						e.printStackTrace();
					}
					// Buton Pressed C:\PEPPOLTest\PEPPOLEndToEnd\ZipOuterAsic
				}
			});

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
