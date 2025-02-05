package no.dfo.gui.controller;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logger.MyLogger;
import no.dfo.peppol.outbound.PeppolOutbound;
import no.dfo.peppol.outbound.UtilityValueSetting;

public class DemoTestFileGeneratorGUI implements Initializable {
	protected Stage stage;

	@FXML
	private Button ISOXMLFileSelectionButton;
	@FXML
	private Label XMLFileSelecLabel;
	@FXML
	private HBox EncryptCertInputmode;
	@FXML
	private ToggleGroup CertificateCategory;
	@FXML
	private ToggleGroup EncCertificateCategory;
	@FXML
	private ToggleGroup HashAlgo;

	@FXML
	private RadioButton EncKeystore;
	@FXML
	private RadioButton EncPublicCert;
	@FXML
	private RadioButton EncBCPPublicCert;

	@FXML
	private RadioButton CertificateDemoRadio;
	@FXML
	private RadioButton Sha3_256;
	@FXML
	private RadioButton Sha3_384;
	@FXML
	private RadioButton Sha3_512;
	@FXML
	private RadioButton CertificateManualRadio;
	@FXML
	private HBox EncryptPkcsFile;
	@FXML
	private Button EncryptionKeystore;
	@FXML
	private Label fileForPKCSStoreEnc;
	@FXML
	private HBox BCLEnvironment;

	@FXML
	private ToggleGroup BCLEnvToggle;
	@FXML
	private RadioButton BCLTest;
	@FXML
	private RadioButton BCLProd;
	@FXML
	private HBox EncryptPkcsPassword;
	@FXML
	private PasswordField encpkcsPassword;
	@FXML
	private TextField AliasTextEnc;
	@FXML
	private TextField customerIdentifier;
	@FXML
	private TextField userIdentifier;
	@FXML
	private TextField divisionIdentifier;
	@FXML
	private HBox SigntPkcsFile;
	@FXML
	private Button SigningKeystore;
	@FXML
	private Label fileForPKCSStoreSign;
	@FXML
	private HBox SignPkcsPassword;
	@FXML
	private PasswordField SignkcsPassword;
	@FXML
	private TextField AliasTextSign;
	@FXML
	private Button DirectoryChooserRepack;
	@FXML
	private Button signButton;
	@FXML
	private Label warningLabel;
	@FXML
	private Label EncKeystoreLabelID;
	@FXML
	private TextArea LogTextAreaSBD;

	@FXML
	private Label senderAndReciever;
	@FXML
	private TextField senderOrg;
	@FXML
	private TextField recieverOrg;

	UtilityValueSetting uv;
	StringBuilder Logs = new StringBuilder();
	UtilityValuesGUI uvg = new UtilityValuesGUI();
	String certUsageCategory = "";
	String hashAlgorithm = "";
	String encCertInputType = "";
	String bclEnv = "";

	

	static {
		// Fix a freeze in Windows 10, JDK 8 and touchscreen

		System.setProperty("glass.accessible.force", "false");
	}

	public void setStage(Stage stage) {
		this.stage = stage;

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		uv = new UtilityValueSetting();

		LogTextAreaSBD.clear();
		Logger logger = Logger.getLogger("Peppol_Outbound");
		logger.addHandler(new MyLogger(LogTextAreaSBD));
		uvg.setOuboundLog(logger);
		logger.info("Started PEPPOL Test File Generator");

		LogTextAreaSBD.setEditable(false);
		BCLEnvironment.setVisible(false);
		CertificateDemoRadio.setUserData("DemoStore");
		CertificateManualRadio.setUserData("UserStore");

		Sha3_256.setUserData("SHA3_256");
		Sha3_384.setUserData("SHA3_384");
		Sha3_512.setUserData("SHA3_512");

		EncKeystore.setUserData("EncryptionKeyStore");
		EncPublicCert.setUserData("X509Certificate");
		EncBCPPublicCert.setUserData("BCLDownload");
		BCLTest.setUserData("BCLTest");
		BCLProd.setUserData("BCLProd");

		HashAlgo.selectToggle(Sha3_256);
		hashAlgorithm = (String) Sha3_256.getUserData();
		uvg.setSignatureDigest(DigestAlgorithm.valueOf(hashAlgorithm));

		HashAlgo.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue != null) {

					hashAlgorithm = (String) newValue.getUserData();
					uvg.setSignatureDigest(DigestAlgorithm.valueOf(hashAlgorithm));

				}
			}
		});

		BCLEnvToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue != null) {

					bclEnv = (String) newValue.getUserData();
					uvg.setBCLEnvironment(bclEnv);

				}
			}
		});

		EncCertificateCategory.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue != null) {
					encCertInputType = (String) newValue.getUserData();
					System.out.println(encCertInputType);
					if (encCertInputType.equalsIgnoreCase("EncryptionKeyStore")) {
						EncryptPkcsFile.setDisable(false);
						EncKeystoreLabelID.setText("Select Encryption Keystore");
						EncryptPkcsPassword.setDisable(false);
						BCLEnvironment.setVisible(false);
					} else if (encCertInputType.equalsIgnoreCase("X509Certificate")) {
						EncKeystoreLabelID.setText("Select X509Certificate");
						EncryptPkcsFile.setDisable(false);
						EncryptPkcsPassword.setDisable(true);
						BCLEnvironment.setVisible(false);
					} else if (encCertInputType.equalsIgnoreCase("BCLDownload")) {
						EncryptPkcsFile.setDisable(true);
						EncryptPkcsPassword.setDisable(true);
						BCLEnvironment.setVisible(true);
					}

				}
			}
		});

		CertificateCategory.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue != null) {

					certUsageCategory = (String) newValue.getUserData();
					if (certUsageCategory.equalsIgnoreCase("DemoStore")) {
						ISOXMLFileSelectionButton.setDisable(true);
						
						InputStream is1=null ;
						URL resource=getClass().getClassLoader().getResource("ISO_Test_File.xml");
						try {
							InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream("ISO_Test_File.xml");
							File fo=new File("DemoTestFile.xml");
						FileUtils.copyInputStreamToFile(ioStream,fo );
							
							List<File> ISOContent = new ArrayList<File>();
							ISOContent.add(fo);
							uvg.setISOFiles(ISOContent);
							ArrayList<String> fileNames = new ArrayList<String>();
							for (File fn : ISOContent) {
								fileNames.add(fn.getName());
								System.out.println(fn.getName());
							}
							uvg.setFileName(fileNames);

							if (uvg.getFileName().size() > 1) {
								XMLFileSelecLabel.setText(uvg.getFileName().size() + " Files selected");
							} else {
								XMLFileSelecLabel.setText(uvg.getFileName().get(0));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						senderOrg.setText("986252932");
						recieverOrg.setText("986252932");
						EncryptPkcsFile.setDisable(true);
						EncryptPkcsPassword.setDisable(true);
						SigntPkcsFile.setDisable(true);
						SignPkcsPassword.setDisable(true);
						EncryptCertInputmode.setDisable(true);
						customerIdentifier.setDisable(true);
						userIdentifier.setDisable(true);
						divisionIdentifier.setDisable(true);

					} else {
						ISOXMLFileSelectionButton.setDisable(false);
						customerIdentifier.setDisable(false);
						userIdentifier.setDisable(false);
						divisionIdentifier.setDisable(false);
						EncryptPkcsFile.setDisable(false);
						EncryptPkcsPassword.setDisable(false);
						SigntPkcsFile.setDisable(false);
						SignPkcsPassword.setDisable(false);
						EncryptCertInputmode.setDisable(false);
						senderOrg.setText("");
						recieverOrg.setText("");
					}

				} else if (newValue == null) {
					Alert alert = new Alert(AlertType.ERROR,
							"Certificate to use field cannot be null: Please select option", ButtonType.CLOSE);
					alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
					alert.showAndWait();
					logger.log(Level.SEVERE, "Certificate to use field cannot be null: Please select option");
					// throw new ErrorCode("certUsageCategory cannot be null: Please select
					// option");
				}
			}
		});

		DirectoryChooserRepack.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle("Select folder for ASiC-e Package");
				File EncDir = directoryChooser.showDialog(stage);

				uvg.setRepackPath(EncDir.getAbsolutePath());
				DirectoryChooserRepack.setText(EncDir.getAbsolutePath());

			}
		});

		ISOXMLFileSelectionButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("ISO File");
				List<File> ISOContent = fileChooser.showOpenMultipleDialog(stage);
				uvg.setISOFiles(ISOContent);
				ArrayList<String> fileNames = new ArrayList<String>();
				for (File fn : ISOContent) {
					fileNames.add(fn.getName());
				}
				uvg.setFileName(fileNames);

				// uv.setFilename(SBDXML.getName());
				if (uvg.getFileName().size() > 1) {
					XMLFileSelecLabel.setText(uvg.getFileName().size() + " Files selected");
				} else {
					XMLFileSelecLabel.setText(uvg.getFileName().get(0));
				}

			}
		});

		EncryptionKeystore.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Select P12 Encryption File for Encryption");
				File p12EncStore = fileChooser.showOpenDialog(stage);
				fileForPKCSStoreEnc.setText(p12EncStore.getName());
				uvg.setP12EncryptionStore(p12EncStore);
				uvg.setEncKeystoreName(p12EncStore.getName());
				uvg.setEncKeystorePath(p12EncStore.getPath());
			}
		});

		SigningKeystore.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Select P12 Sign File for Decryption");
				File p12SignStore = fileChooser.showOpenDialog(stage);
				fileForPKCSStoreSign.setText(p12SignStore.getName());
				uvg.setP12SignStore(p12SignStore);
				uvg.setSignKeystoreName(p12SignStore.getName());
				uvg.setSignKeystorePath(p12SignStore.getPath());
				// System.out.println("Buton Pressed"+p12DecryptionStore. );
			}
		});

		signButton.setOnAction(event -> {
			LogTextAreaSBD.clear();
			uvg.setRecOrgFX(recieverOrg);
			try {
			if(uvg.getRepackPath().equalsIgnoreCase("") ||uvg.getRepackPath()==null ) {
				throw new NullPointerException();
				
			}
			
			if (certUsageCategory.equalsIgnoreCase("DemoStore")) {

				uvg.setSignKeystorePath("DemoKeys.p12");
				uvg.setEncKeystorePath("DemoKeys.p12");
				uvg.setSignkeystorePwd("123456");
				uvg.setEnckeystorePwd("123456");
				uvg.setAliasSign("demosign");
				uvg.setAliasEnc("demoenc");
				uvg.setSenderOrg("986252932");
				uvg.setRecOrg("986252932");
				uv.setCertUsed("DEMO");
				uvg.setEncCertInputType("demo");
				userIdentifier.setText("Dummy_User_Identifier");
				customerIdentifier.setText("Dummy_Customer_Identifier");
				divisionIdentifier.setText("Dummy_Division_Identifier");

			} else {
				if (userIdentifier.getText() == null || userIdentifier.getText().equalsIgnoreCase("")) {
					userIdentifier.setText("Dummy_User_Identifier");

				}
				if (customerIdentifier.getText() == null || customerIdentifier.getText().equalsIgnoreCase("")) {
					customerIdentifier.setText("Dummy_Customer_Identifier");
				}
				if (divisionIdentifier.getText() == null || divisionIdentifier.getText().equalsIgnoreCase("")) {
					divisionIdentifier.setText("Dummy_Division_Identifier");
				}

				uvg.setSignkeystorePwd(SignkcsPassword.getText());
				uvg.setAliasSign(AliasTextSign.getText());
				if (senderOrg.getText() == null || senderOrg.getText().equalsIgnoreCase("")) {
					senderOrg.setText("986252932");

				} else {

				}
				if (recieverOrg.getText() == null || recieverOrg.getText().equalsIgnoreCase("")) {
					recieverOrg.setText("986252932");

				}

				uvg.setSenderOrg(senderOrg.getText());
				uvg.setRecOrg(recieverOrg.getText());
				uv.setCertUsed("User");

				if (encCertInputType.equalsIgnoreCase("EncryptionKeyStore")) {
					uvg.setEnckeystorePwd(encpkcsPassword.getText());
					System.out.println(encpkcsPassword.getText());
					
					System.out.println(AliasTextEnc.getText());
					uvg.setAliasEnc(AliasTextEnc.getText());
					uvg.setEncCertInputType("EncryptionKeyStore");
				} else if (encCertInputType.equalsIgnoreCase("X509Certificate")) {
					uvg.setEncCertInputType("X509Certificate");
				} else if (encCertInputType.equalsIgnoreCase("BCLDownload")) {
					uvg.setEncCertInputType("BCLDownload");
				}

			}
			uvg.setDivisionIdentifier(divisionIdentifier.getText());
			uvg.setCustomerIdentfier(customerIdentifier.getText());
			uvg.setUserIdentifier(userIdentifier.getText());
			PeppolOutbound po = new PeppolOutbound(uv);
			try {
				po.PEPPOL_Outbound(uvg);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error :"+e.toString());
				
				e.printStackTrace();
			}
		}catch(NullPointerException E) {
			
			  Alert alert = new Alert(AlertType.ERROR,
			  "Please select output folder for SBD Package",
			  ButtonType.OK); alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			  alert.showAndWait(); 
			  logger.log(Level.WARNING,
			  "Please select output folder for SBD Package");
			 
		}
		});

	}

}
