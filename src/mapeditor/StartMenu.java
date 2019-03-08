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
	
	private StartMenu startMenu = this;
	private int resWidth;
	private int resHeight;
	
	public StartMenu(Stage stage) {
		this.init(stage);
	}
	
	public void setResWidth(int resWidth) {
		this.resWidth = resWidth;
	}
	
	public void setResHeight(int resHeight) {
		this.resHeight = resHeight;
	}
	
	// Initialize
	private void init(Stage stage) {
		stage.setTitle("Gunderwurlde Map Editor");
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.centerOnScreen();
		
        // root
		VBox root = new VBox();
		root.setSpacing(10);
		root.setPadding(new Insets(10));
		root.setAlignment(Pos.BASELINE_CENTER);
        Scene scene = new Scene(root, 300, 250);
        stage.setScene(scene);
        
        // > Gunderwurlde Map Editor
        Label label = new Label("Gunderwurlde Map Editor");
        root.getChildren().add(label);
        
        // > New Map
        Button newMap = new Button("New Map");
        root.getChildren().add(newMap);
        newMap.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				MapEditor mapEditor = new MapEditor(resWidth, resHeight);
			}
		});
        
        // > Open Map
        Button openMap = new Button("Open Map");
        root.getChildren().add(openMap);
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
					MapEditor mapEditor = new MapEditor(map.getName(), resWidth, resHeight);
				}
			}
		});
        
        // > Option
        Button option = new Button("Option");
        root.getChildren().add(option);
        option.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				OptionMenu optionMenu = new OptionMenu(startMenu);
			}
		});
        
        // > Exit
        Button exit = new Button("Exit");
        root.getChildren().add(exit);
        exit.setOnAction(new EventHandler<ActionEvent>() {
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
