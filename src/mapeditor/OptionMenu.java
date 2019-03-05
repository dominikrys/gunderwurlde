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
	
	private void init() {
		Stage stage = new Stage();
		stage.setTitle("Option");
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.centerOnScreen();
		
		VBox root = new VBox();
		root.setSpacing(50);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(50));
		
		HBox resolution = new HBox();
		resolution.setSpacing(20);
		resolution.setAlignment(Pos.CENTER);
		Label resolutionLabel = new Label("Resolution:");
		ComboBox resolutions = new ComboBox();
		resolutions.getItems().add("800x600");
		resolutions.getSelectionModel().selectFirst();
		resolution.getChildren().addAll(resolutionLabel, resolutions);
		root.getChildren().add(resolution);
		
		HBox hBox = new HBox();
		hBox.setSpacing(50);
		hBox.setAlignment(Pos.CENTER);
		Button save = new Button("Save");
		save.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
		hBox.getChildren().add(save);
		
		Button cancel = new Button("Cancel");
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.close();
			}
		});
		hBox.getChildren().add(cancel);
		
		
		root.getChildren().add(hBox);
		Scene scene = new Scene(root, 300, 250);
		
        stage.setScene(scene);
        stage.show();
        
        stage.setOnCloseRequest(we -> {
            stage.close();
        });
	}
	
}
