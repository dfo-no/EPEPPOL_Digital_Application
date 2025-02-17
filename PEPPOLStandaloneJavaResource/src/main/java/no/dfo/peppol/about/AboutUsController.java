package no.dfo.peppol.about;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutUsController implements Initializable{
	protected Stage stage;
    @FXML
    private Hyperlink websiteLink;
    @FXML
    private Hyperlink dssGitHub;
    @FXML
    private Hyperlink dssWebApp;
    @FXML
    private Hyperlink peppolRulebook;
    @FXML
    private Hyperlink peppolSecurityRequirement;
    @FXML
    private Hyperlink peppolDocument;
    @FXML
    private Hyperlink dssApplication;
    
    
    
    

    
    static {
		// Fix a freeze in Windows 10, JDK 8 and touchscreen
		System.setProperty("glass.accessible.force", "false");
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
    private void openWebpage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		 dssApplication.setOnAction(event -> openWebpage("https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/doc/dss-documentation.html#CAdES"));
		 dssGitHub.setOnAction(event -> openWebpage("https://github.com/esig/dss-demonstrations"));
		 dssWebApp.setOnAction(event -> openWebpage("https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/sign-a-document"));
		 peppolRulebook.setOnAction(event -> openWebpage("https://anskaffelser.dev/payment/g1/docs/current/rulebook/"));
		 peppolSecurityRequirement.setOnAction(event -> openWebpage("https://anskaffelser.dev/payment/g1/docs/current/security/"));
		 peppolDocument.setOnAction(event -> openWebpage("https://anskaffelser.dev/payment/g1/files/20170119%20Use%20of%20PEPPOL%20eDelivery%20network%20for%20ISO%2020022%20v_1.pdf"));
		
	}
}