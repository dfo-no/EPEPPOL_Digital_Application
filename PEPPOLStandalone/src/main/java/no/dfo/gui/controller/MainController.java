package no.dfo.gui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import AboutUs.AboutUsController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController implements Initializable {
	
	@FXML
	private  SBDUnpack SBDUn;
	@FXML
	private OCSPMain OCSPM;
	@FXML
	private SFTPGuiMain SFTPM;
	@FXML
	private DemoTestFileGeneratorGUI DemoFileGen;
	@FXML
	private TabPane tabPane;

	@FXML
	private Tab DecryptTab;
	@FXML
	private Tab OCSPTab;
	@FXML
	private Tab SFTPTab;
	@FXML
	private Tab DemoSBDFileGeneratorTab;
	@FXML
	private  AboutUsController aboutUs;
	@FXML
	private Tab AboutUsTab;
 
	
	 private Stage stage;

	    public void setStage(Stage stage) {
	        this.stage = stage;
	    }
	static {
        // Fix a freeze in Windows 10, JDK 8 and touchscreen
        System.setProperty("glass.accessible.force", "false");
    }
	
	@Override
    public void initialize(URL location, ResourceBundle resources) {
		SBDUn.setStage(stage);
		OCSPM.setStage(stage);
		SFTPM.setStage(stage);
		SFTPM.setStage(stage);
		aboutUs.setStage(stage);
		DemoFileGen.setStage(stage);
	
	
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        tabPane.getSelectionModel().selectFirst();

		
	}
	
}
