package mapeditor;

import javafx.application.Application;
import javafx.stage.Stage;

public class MapEditorMain extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		StartMenu startMenu = new StartMenu(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
