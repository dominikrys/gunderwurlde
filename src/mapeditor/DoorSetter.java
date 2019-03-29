package mapeditor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import server.engine.state.map.tile.Door;
import server.engine.state.map.tile.Tile;
import shared.lists.MapEditorAssetList;
import shared.lists.TileList;

/**
 * DoorSetter class. Contains the gui for door settings.
 *
 * @author Mak Hong Lun Timothy
 */
public class DoorSetter {
	/**
     * DoorSetter - This object
     */
	private DoorSetter doorSetter = this;
	/**
     * tileSprite - Loaded tile sprites
     */
	HashMap<TileList, Image> tileSprite;
	/**
     * doors - Coordinates and info on all doors currently on the canvas
     */
	private HashMap<String, TileList> doors;
	/**
     * zoneMap - Zone settings of the current map
     */
	private HashMap<String, ZoneSettings> zoneMap;
	/**
     * stage - Stage to display the gui
     */
	private Stage stage;
	/**
     * root - Root of stage
     */
	private StackPane root;
	/**
     * scene - Scene of stage
     */
	private Scene scene;
	/**
     * vBox - Parent vBox for all children
     */
	private VBox vBox;
	/**
     * imageAndLabelHBox - HBox for tile images and labels
     */
	private HBox imageAndLabelHBox;
	/**
     * doorVBox - VBox for door
     */
	private VBox doorVBox;
	/**
     * doorImageView - Image of selected door
     */
	private ImageView doorImageView;
	/**
     * doorLabel - Label of selected door's name
     */
	private Label doorLabel;
	/**
     * tileVBox - VBox for tile
     */
	private VBox tileVBox;
	/**
     * tileImageView - Image of selected tile
     */
	private ImageView tileImageView;
	/**
     * tileLabel - Label of selected tile's name
     */
	private Label tileLabel;
	/**
     * tileSet - Button for selecting tile
     */
	private Button tileSet;
	/**
     * locationLabelComboBoxHBox - HBox for location label and combo box
     */
	private HBox locationLabelComboBoxHBox;
	/**
     * locationLabel - Label for location
     */
	private Label locationLabel;
	/**
     * locationComboBox - ComboBox for location selecting
     */
	private ComboBox<String> locationComboBox;
	/**
     * zoneLabelComboBoxHBox - HBox for zone label and combo box
     */
	private HBox zoneLabelComboBoxHBox;
	/**
     * zoneLabel - Label for zone
     */
	private Label zoneLabel;
	/**
     * zoneComboBox - ComboBox for zone selecting
     */
	private ComboBox<String> zoneComboBox;
	/**
     * killsRequiredLabelTextFieldHBox - HBox for kills required label and text field
     */
	private HBox killsRequiredLabelTextFieldHBox;
	/**
     * killsRequiredLabel - Label for kills required
     */
	private Label killsRequiredLabel;
	/**
     * TextField - Text field for entering no. of kills required to selected door to open
     */
	private TextField killsRequiredTextField;
	/**
     * saveCloseHBox - HBox for save and close button
     */
	private HBox saveCloseHBox;
	/**
     * saveButton - Button for saving the door settings
     */
	private Button saveButton;
	/**
     * closeButton - Button for closing the gui
     */
	private Button closeButton;
	/**
     * tileToReturn - Tile object for substituting the opened door 
     */
	private Tile tileToReturn;
	
	/**
     * Constructor
     *
     * @param tileSprite Loaded tile sprites
     * @param doors Coordinates and info on all doors currently on the canvas
     * @param zoneMap Zone settings of the current map
     */
	public DoorSetter(HashMap<TileList, Image> tileSprite, HashMap<String, TileList> doors, HashMap<String, ZoneSettings> zoneMap) {
		this.tileSprite = tileSprite;
		this.doors = doors;
		this.zoneMap = zoneMap;
		this.init();
	}
	
	/**
     * Initialize the gui and show it
     */
	private void init() {
		stage = new Stage();
		stage.setTitle("Door Setter");
		stage.setResizable(false);
		stage.setFullScreen(false);
		stage.centerOnScreen();
		stage.initModality(Modality.APPLICATION_MODAL);
		
		// root
		root = new StackPane();
		scene = new Scene(root, 400, 300);
		stage.setScene(scene);
		root.setAlignment(Pos.CENTER);
		
		// > VBox
		vBox = new VBox();
		root.getChildren().add(vBox);
		vBox.setAlignment(Pos.CENTER);
		vBox.setSpacing(10);
		
		// > > Image & Label HBox
		imageAndLabelHBox = new HBox();
		vBox.getChildren().add(imageAndLabelHBox);
		imageAndLabelHBox.setAlignment(Pos.CENTER);
		imageAndLabelHBox.setSpacing(20);
		
		// > > > Door VBox
		doorVBox = new VBox();
		imageAndLabelHBox.getChildren().add(doorVBox);
		doorVBox.setAlignment(Pos.CENTER);
		doorVBox.setSpacing(10);
		
		// > > > > Door ImageView
		doorImageView = new ImageView();
		doorVBox.getChildren().add(doorImageView);
		
		// > > > > Door Label
		doorLabel = new Label();
		doorVBox.getChildren().add(doorLabel);
		
		// > > > Arrow ImageView
		ImageView arrowImageView = new ImageView(MapEditorAssetList.ARROW.getPath());
		imageAndLabelHBox.getChildren().add(arrowImageView);
		arrowImageView.setRotate(90);
		
		// > > > Tile VBox
		tileVBox = new VBox();
		imageAndLabelHBox.getChildren().add(tileVBox);
		tileVBox.setAlignment(Pos.CENTER);
		tileVBox.setSpacing(10);
		
		// > > > > Tile ImageView
		tileImageView = new ImageView();
		tileVBox.getChildren().add(tileImageView);
		
		// > > > > Tile Label
		tileLabel = new Label();
		tileVBox.getChildren().add(tileLabel);
		
		// > > > > Tile Set Button
		tileSet = new Button("Set");
		tileVBox.getChildren().add(tileSet);
		tileSet.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TileSelector tileSelector = new TileSelector(doorSetter, tileSprite);
			}
		});
		
		// > > > Location Label & ComboBox HBox
		locationLabelComboBoxHBox = new HBox();
		vBox.getChildren().add(locationLabelComboBoxHBox);
		locationLabelComboBoxHBox.setAlignment(Pos.CENTER);
		locationLabelComboBoxHBox.setSpacing(10);
		
		// > > > > Location Label
		locationLabel = new Label("Location:");
		locationLabelComboBoxHBox.getChildren().add(locationLabel);
		
		// > > > > Location ComboBox
		locationComboBox = new ComboBox<String>();
		locationLabelComboBoxHBox.getChildren().add(locationComboBox);
		locationComboBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				displayInfo();
			}
		});
		
		// > > > Zone Label & ComboBox HBox
		zoneLabelComboBoxHBox = new HBox();
		vBox.getChildren().add(zoneLabelComboBoxHBox);
		zoneLabelComboBoxHBox.setAlignment(Pos.CENTER);
		zoneLabelComboBoxHBox.setSpacing(10);
		
		// > > > > Zone Label
		zoneLabel = new Label("Zone:");
		zoneLabelComboBoxHBox.getChildren().add(zoneLabel);
		
		// > > > > Zone ComboBox
		zoneComboBox = new ComboBox<String>();
		zoneLabelComboBoxHBox.getChildren().add(zoneComboBox);
		
		// > > > Kills Required Label & TextField HBox
		killsRequiredLabelTextFieldHBox = new HBox();
		vBox.getChildren().add(killsRequiredLabelTextFieldHBox);
		killsRequiredLabelTextFieldHBox.setAlignment(Pos.CENTER);
		killsRequiredLabelTextFieldHBox.setSpacing(10);
		
		// > > > > Kills Required Label
		killsRequiredLabel = new Label("Kills Required:");
		killsRequiredLabelTextFieldHBox.getChildren().add(killsRequiredLabel);
		
		// > > > > Kills Required TextField
		killsRequiredTextField = new TextField();
		killsRequiredLabelTextFieldHBox.getChildren().add(killsRequiredTextField);
		
		// > > > Save & Close HBox
		saveCloseHBox = new HBox();
		vBox.getChildren().add(saveCloseHBox);
		saveCloseHBox.setAlignment(Pos.CENTER);
		saveCloseHBox.setSpacing(10);
		
		// > > > > Save Button
		saveButton = new Button("Save");
		saveCloseHBox.getChildren().add(saveButton);
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!zoneComboBox.getSelectionModel().isEmpty() && killsRequiredTextField.getText().matches("\\d+") && tileToReturn != null) {
					int comma = locationComboBox.getValue().indexOf(",");
					int x = Integer.parseInt(locationComboBox.getValue().substring(1, comma));
					int y = Integer.parseInt(locationComboBox.getValue().substring(comma + 2, locationComboBox.getValue().length() - 1));
					zoneMap.get(zoneComboBox.getValue()).getDoors().put(new int[] {x, y}, new Door(tileToReturn, Integer.parseInt(killsRequiredTextField.getText())));
					stage.close();
				}
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Invalid inputs");
					alert.showAndWait();
				}
			}
		});
		
		// > > > > Close Button
		closeButton = new Button("Close");
		saveCloseHBox.getChildren().add(closeButton);
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.close();
			}
		});
		
		// Load info
		for(Map.Entry<String, TileList> entry : doors.entrySet()) {
			locationComboBox.getItems().add(entry.getKey());
		}
		locationComboBox.getSelectionModel().selectFirst();
		for(Map.Entry<String, ZoneSettings> entry : zoneMap.entrySet()) {
			zoneComboBox.getItems().add(entry.getKey());
		}
		displayInfo();
		
		stage.show();
		
		stage.setOnCloseRequest(we -> {
			stage.close();
        });
	}
	
	/**
     * Display relevant info on all combo boxes and text field if they exist
     */
	private void displayInfo() {
		if(!locationComboBox.getItems().isEmpty()) {
			doorImageView.setImage(tileSprite.get(doors.get(locationComboBox.getValue())));
			doorLabel.setText(doors.get(locationComboBox.getValue()).toString());
			boolean clear = true;
			for(Map.Entry<String, ZoneSettings> zone : zoneMap.entrySet()) {
				for(Map.Entry<int[], Door> door : zone.getValue().getDoors().entrySet()) {
					if(Arrays.toString(door.getKey()).equals(locationComboBox.getValue())) {
						tileImageView.setImage(tileSprite.get(door.getValue().getTileToReturn().getType()));
						tileLabel.setText(door.getValue().getTileToReturn().getType().toString());
						zoneComboBox.getSelectionModel().select(zone.getKey());
						killsRequiredTextField.setText(Integer.toString(door.getValue().getKillsLeft()));
						clear = false;
					}
				}
			}
			if(clear) {
				clearInfo();
			}
		}
	}
	
	/**
     * Clear info for tile image, label, zone combo box, kills required text field
     */
	private void clearInfo() {
		tileImageView.setImage(null);
		tileLabel.setText("");
		zoneComboBox.getSelectionModel().clearSelection();
		killsRequiredTextField.clear();
	}
	
	/**
	 * Set the tile object to substitute, also display the correct image and name
	 * 
     * @param tile Tile to set as the substitute tile
     */
	public void displayTile(Tile tile) {
		tileImageView.setImage(tileSprite.get(tile.getType()));
		tileLabel.setText(tile.getType().toString());
		tileToReturn = tile;
	}

}
