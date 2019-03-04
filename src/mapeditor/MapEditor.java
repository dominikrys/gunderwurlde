package mapeditor;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public interface MapEditor {
	
	static void init(Stage stage, String fileName) {
		GridPane root = new GridPane();
		
		Scene scene = new Scene(root, 800, 600);
		
		if(fileName != null) {
			stage.setTitle(fileName);
		}
		else {
			stage.setTitle("New Map");
		}
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
