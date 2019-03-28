package mapeditor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * MapSizeOption class. Contains gui for setting map size.
 *
 * @author Mak Hong Lun Timothy
 */
public class MapSizeOption {
	/**
     * mapEditor - Map editor that opened this gui
     */
	private MapEditor mapEditor;
	/**
     * stage - Stage to display the gui
     */
	private Stage stage;
	/**
     * root - Root of the stage
     */
	private StackPane root;
	/**
     * scene - Scene of the stage
     */
	private Scene scene;
	/**
     * vBox - Parent vBox for all children
     */
	private VBox vBox;
	/**
     * mapSizeHBox - HBox for map size
     */
	private HBox mapSizeHBox;
	/**
     * mapWidth - Width of the map
     */
	private int mapWidth;
	/**
     * mapHeight - Height of the map
     */
	private int mapHeight;
	/**
     * widthTextField - Text field for entering desired width for map
     */
	private TextField widthTextField;
	/**
     * heightTextField - Text field for entering desired height for map
     */
	private TextField heightTextField;
	/**
     * saveAndCancelHBox - HBox for save and cancel button
     */
	private HBox saveAndCancelHBox;
	/**
     * saveButton - Button for saving map size
     */
	private Button saveButton;
	/**
     * cancelButton - Button for canceling and closing the gui
     */
	private Button cancelButton;

	/**
     * Constructor
     *
     * @param mapEditor Map editor that opened this gui
     * @param mapWidth Width of the map
     * @param mapHeight Height of the map
     */
	public MapSizeOption(MapEditor mapEditor, int mapWidth, int mapHeight) {
		this.mapEditor = mapEditor;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.init();
	}

	/**
     * Initialize the gui and show it
     */
	private void init() {
		stage = new Stage();
		stage.setTitle("Map Size Option");
		stage.setResizable(false);
		stage.setFullScreen(false);
		stage.centerOnScreen();
		stage.initModality(Modality.APPLICATION_MODAL);
		
		// root
		root = new StackPane();
		scene = new Scene(root, 400, 300);
		stage.setScene(scene);
		root.setAlignment(Pos.CENTER);
		
		// > vBox
		vBox = new VBox();
		root.getChildren().add(vBox);
		vBox.setSpacing(25);
		vBox.setAlignment(Pos.CENTER);
		
		// > > Map Size:
		mapSizeHBox = new HBox();
		vBox.getChildren().add(mapSizeHBox);
		mapSizeHBox.setSpacing(10);
		mapSizeHBox.setAlignment(Pos.CENTER);
		Label mapSizeLabel = new Label("Map Size:");

		// > > > W
		widthTextField = new TextField(Integer.toString(mapWidth));
		widthTextField.setPrefWidth(75);
		widthTextField.setPromptText("W");
		widthTextField.setTooltip(new Tooltip("Width of map"));

		// > > > X
		Label mapSizeX = new Label("X");

		// > > > H
		heightTextField = new TextField(Integer.toString(mapHeight));
		heightTextField.setPrefWidth(75);
		heightTextField.setPromptText("H");
		heightTextField.setTooltip(new Tooltip("Height of map"));
		mapSizeHBox.getChildren().addAll(mapSizeLabel, widthTextField, mapSizeX, heightTextField);

		// > > Save & Cancel
		saveAndCancelHBox = new HBox();
		vBox.getChildren().add(saveAndCancelHBox);
		saveAndCancelHBox.setSpacing(50);
		saveAndCancelHBox.setAlignment(Pos.CENTER);
		
		// > > > Save
		saveButton = new Button("Save");
		saveAndCancelHBox.getChildren().add(saveButton);
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(widthTextField.getText().matches("\\d+") && !widthTextField.getText().equals("0") && heightTextField.getText().matches("\\d+") && !heightTextField.getText().equals("0")) {
					int newWidth = Integer.parseInt(widthTextField.getText());
					int newHeight = Integer.parseInt(heightTextField.getText());
					if (newWidth < mapWidth || newHeight < mapHeight) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error");
						alert.setHeaderText("Cropping may occur");
						alert.showAndWait();
					}
					else {
						mapWidth = newWidth;
						mapHeight = newHeight;
						mapEditor.setMapWidth(mapWidth);
						mapEditor.setMapHeight(mapHeight);
						mapEditor.drawMapTiles();
					}
				}
				else {
					sizeErrorPopup();
				}
			}
		});
		
		// > > > Cancel
		cancelButton = new Button("Cancel");
		saveAndCancelHBox.getChildren().add(cancelButton);
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
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

	/**
     * Error when a non-number is entered in size text field
     */
	private void sizeErrorPopup() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Please only enter non-zero numbers");
		alert.show();
	}

}
