package no.dfo.peppol.PEPPOLStandalone;


import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import no.dfo.gui.controller.MainController;
import no.dfo.gui.controller.OCSPMain;
import no.dfo.gui.controller.SFTPGuiMain;
public class Main extends Application {
	private Stage stage;
	
	
	@Override
	public void start(Stage stage)throws Exception {
		this.stage = stage;
		this.stage.setTitle("Digital PEPPOL Application");
		this.stage.setResizable(true);

		this.stage.getIcons().add(new Image("/dfo_symbol_pos_rgb_large.png"));

		initLayout();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		       @Override
		       public void handle(WindowEvent e) {
		          Platform.exit();
		          System.exit(0);
		       }
		    });
	}
	
	/*
	 * public Stage getStage() { return stage; }
	 */
	
	
	private void initLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("/Application.fxml"));
			
			//TabPane pane = loader.load();
			TabPane view = loader.load();
			
			Scene scene = new Scene(view, 1050, 420);
			scene.getStylesheets().add("/style.css");
			stage.setScene(scene);
			stage.show();

			MainController cn=loader.getController();
			
			
			
			
			


			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
   public static void main(String args[]){
      launch(Main.class,args);
   }
}