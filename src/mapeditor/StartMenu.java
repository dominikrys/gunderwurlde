package mapeditor;

import java.io.File;
import java.nio.file.Paths;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StartMenu {
	
	public StartMenu(Stage stage) {
		this.init(stage);
	}
	
	private void init(Stage stage) {
		VBox root = new VBox();
		root.setSpacing(10);
		root.setPadding(new Insets(10));
		root.setAlignment(Pos.BASELINE_CENTER);
		
        Scene scene = new Scene(root, 300, 250);
        
        Label label = new Label("Gunderwurlde Map Editor");
        root.getChildren().add(label);
        
        Button newMap = new Button("New Map");
        newMap.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				MapEditor.init(new Stage(), null);
			}
		});
        root.getChildren().add(newMap);
        
        Button openMap = new Button("Open Map");
        openMap.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open your map");
				String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
				fileChooser.setInitialDirectory(new File(currentPath + "/maps"));
				fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GAMEMAP Files", "*.gamemap"));
				File map = fileChooser.showOpenDialog(stage);
				if(map != null) {
					//MapEditor.init(new Stage(), map.getName().substring(0, map.getName().lastIndexOf(".")));
					MapEditor.init(new Stage(), map.getName());
				}
			}
		});
        root.getChildren().add(openMap);
        
        Button option = new Button("Option");
        option.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
        
        Button exit = new Button("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.close();
			}
		});
        root.getChildren().add(exit);

        stage.setTitle("Gunderwurlde Map Editor");
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.show();
        
        stage.setOnCloseRequest(we -> {
            stage.close();
        });
    }

}
