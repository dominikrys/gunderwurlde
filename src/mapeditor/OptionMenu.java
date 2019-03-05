package mapeditor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OptionMenu {
	
	public OptionMenu() {
		this.init();
	}
	
	// Initialize
	private void init() {
		Stage stage = new Stage();
		stage.setTitle("Option");
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.centerOnScreen();
		
        // root
		VBox root = new VBox();
		Scene scene = new Scene(root, 300, 250);
        stage.setScene(scene);
		root.setSpacing(50);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(50));
		
		// > Resolution HBox
		HBox resolution = new HBox();
		root.getChildren().add(resolution);
		resolution.setSpacing(20);
		resolution.setAlignment(Pos.CENTER);
		
		// > > Resolution:
		Label resolutionLabel = new Label("Resolution:");
		
		// > > resolutions drop down menu
		ComboBox resolutions = new ComboBox();
		resolutions.getItems().add("800x600");
		resolutions.getSelectionModel().selectFirst();
		resolution.getChildren().addAll(resolutionLabel, resolutions);
		
		// > Save and Cancel HBox
		HBox hBox = new HBox();
		root.getChildren().add(hBox);
		hBox.setSpacing(50);
		hBox.setAlignment(Pos.CENTER);
		
		// > > Save
		Button save = new Button("Save");
		hBox.getChildren().add(save);
		save.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
		
		// > > Cancel
		Button cancel = new Button("Cancel");
		hBox.getChildren().add(cancel);
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.close();
			}
		});
		
        stage.show();
        
        stage.setOnCloseRequest(we -> {
            stage.close();
        });
	}
	
}
