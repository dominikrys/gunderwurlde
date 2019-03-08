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
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.Constants;

public class OptionMenu {
	
	private StartMenu startMenu;
	
	public OptionMenu(StartMenu startMenu) {
		this.startMenu = startMenu;
		this.init();
	}
	
	// Initialize
	private void init() {
		Stage stage = new Stage();
		stage.setTitle("Option");
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);
		
        // root
		VBox root = new VBox();
		Scene scene = new Scene(root, 350, 250);
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
		ComboBox<String> resolutions = new ComboBox<String>();
		for (int[] res : Constants.SCREEN_RESOLUTIONS) {
            resolutions.getItems().add(res[0] + "x" + res[1]);
        }
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
				String[] res = resolutions.getValue().split("x");
				startMenu.setResWidth(Integer.parseInt(res[0]));
				startMenu.setResHeight(Integer.parseInt(res[1]));
				stage.close();
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
