package mapeditor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import server.engine.state.map.tile.Tile;
import shared.lists.TileList;

/**
 * TileSelector class. Contains the gui for selecting tile.
 *
 * @author Mak Hong Lun Timothy
 */
public class TileSelector {
	/**
     * mapEditor - mapEditor that opened this gui
     */
	private MapEditor mapEditor;
	/**
     * doorSetter - doorSetter that opened this gui
     */
	private DoorSetter doorSetter;
	/**
     * tileX - X coordinate of selected tile
     */
	private int tileX;
	/**
     * tileY - Y coordinate of selected tile
     */
	private int tileY;
	/**
     * tileSprite - Loaded tile sprites
     */
	private HashMap<TileList, Image> tileSprite;
	/**
     * tileSettings - HashMap containing the tile settings of each type of tile
     */
	private HashMap<TileList, Tile> tileSettings;
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
     * hBox - HBox for tile image and info
     */
	private HBox hBox;
	/**
     * tileImageView - Image of tile
     */
	private ImageView tileImageView;
	/**
     * vBox - VBox for tile info
     */
	private VBox vBox;
	/**
     * tileMenu - ComboBox for tile selection
     */
	private ComboBox<String> tileMenu;
	/**
     * tileTypeInfo - HBox for tile type info
     */
	private HBox tileTypeInfo;
	/**
     * tileTypeLabel1 - Label for tile type info
     */
	private Label tileTypeLabel1;
	/**
     * tileTypeLabel2 - Label for tile type info of selected tile
     */
	private Label tileTypeLabel2;
	/**
     * tileStateInfo - HBox for tile state info
     */
	private HBox tileStateInfo;
	/**
     * tileStateLabel1 - Label for tile state info
     */
	private Label tileStateLabel1;
	/**
     * tileStateLabel2 - Label for tile state info of selected tile
     */
	private Label tileStateLabel2;
	/**
     * frictionInfo - HBox for friction info
     */
	private HBox frictionInfo;
	/**
     * frictionLabel1 - Label for friction info
     */
	private Label frictionLabel1;
	/**
     * frictionLabel2 - Label for friction info of selected tile
     */
	private Label frictionLabel2;
	/**
     * bounceInfo - HBox for bounce info
     */
	private HBox bounceInfo;
	/**
     * bounceLabel1 - Label for bounce info
     */
	private Label bounceLabel1;
	/**
     * bounceLabel2 - Label for bounce info of selected tile
     */
	private Label bounceLabel2;
	/**
     * saveDeleteCancel - HBox for save and cancel buttons
     */
	private HBox saveDeleteCancel;
	/**
     * saveButton - Button for saving the selected tile
     */
	private Button saveButton;
	/**
     * deleteButton - Button for deleting the selected tile
     */
	private Button deleteButton;
	/**
     * cancelButton - Button for canceling and closing the gui
     */
	private Button cancelButton;
	/**
     * selectCancel - HBox for select and cancel buttons
     */
	private HBox selectCancel;
	/**
     * paintSelectButton - Button for saving the selected tile for paint mode
     */
	private Button paintSelectButton;
	/**
     * paintMode - Boolean whether paint mode is active
     */
	private boolean paintMode;
	/**
     * doorMode - Boolean whether door setter is active
     */
	private boolean doorMode;
	
	/**
     * Constructor when paint mode is inactive
     *
     * @param mapEditor mapEditor that opened this gui
     * @param tileX X coordinate of selected tile
     * @param tileY Y coordinate of selected tile
     */
	public TileSelector(MapEditor mapEditor, int tileX, int tileY) {
		this.mapEditor = mapEditor;
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileSprite = mapEditor.getTileSprite();
		this.paintMode = false;
		this.init();
	}
	
	/**
     * Constructor when paint mode is active
     *
     * @param mapEditor mapEditor that opened this gui
     */
	public TileSelector(MapEditor mapEditor) {
		this.mapEditor = mapEditor;
		this.tileSprite = mapEditor.getTileSprite();
		this.paintMode = true;
		this.init();
	}
	
	/**
     * Constructor when door setter is active
     *
     * @param doorSetter doorSetter that opened this gui
     * @param tileSprite Loaded tile sprites
     */
	public TileSelector(DoorSetter doorSetter, HashMap<TileList, Image> tileSprite) {
		this.doorSetter = doorSetter;
		this.tileSprite = tileSprite;
		this.doorMode = true;
		this.paintMode = true;
		this.init();
	}
	
	/**
     * Initialize the gui and show it
     */
	public void init() {
		stage = new Stage();
		stage.setTitle("Tile Selector");
		stage.setResizable(false);
		stage.setFullScreen(false);
		stage.centerOnScreen();
		stage.initModality(Modality.APPLICATION_MODAL);
		
		// root
		root = new StackPane();
		scene = new Scene(root, 400, 300);
		stage.setScene(scene);
		root.setAlignment(Pos.CENTER);
		
		// > HBox
		hBox = new HBox();
		root.getChildren().add(hBox);
		hBox.setSpacing(30);
		hBox.setAlignment(Pos.CENTER);
		
		// > > Tile Image
		tileImageView = new ImageView();
		hBox.getChildren().add(tileImageView);
		
		// > > VBox
		vBox = new VBox();
		hBox.getChildren().add(vBox);
		vBox.setSpacing(15);
		vBox.setAlignment(Pos.CENTER);
		
		// > > > Drop Down Menu
		tileMenu = new ComboBox<String>();
		vBox.getChildren().add(tileMenu);
		// Initialize tile settings
		getTileSettings();
		tileMenu.getSelectionModel().selectFirst();
		tileMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				changeTileSelection();
			}
		});
		
		// > > > Tile Type
		tileTypeInfo = new HBox();
		vBox.getChildren().add(tileTypeInfo);
		tileTypeInfo.setSpacing(10);
		tileTypeInfo.setAlignment(Pos.CENTER);
		tileTypeLabel1 = new Label("Tile Type:");
		tileTypeInfo.getChildren().add(tileTypeLabel1);
		tileTypeLabel2 = new Label();
		tileTypeInfo.getChildren().add(tileTypeLabel2);
		
		// > > > Tile State
		tileStateInfo = new HBox();
		vBox.getChildren().add(tileStateInfo);
		tileStateInfo.setSpacing(10);
		tileStateInfo.setAlignment(Pos.CENTER);
		tileStateLabel1 = new Label("Tile State:");
		tileStateInfo.getChildren().add(tileStateLabel1);
		tileStateLabel2 = new Label();
		tileStateInfo.getChildren().add(tileStateLabel2);
		
		// > > > Fraction Coefficient
		frictionInfo = new HBox();
		vBox.getChildren().add(frictionInfo);
		frictionInfo.setSpacing(10);
		frictionInfo.setAlignment(Pos.CENTER);
		frictionLabel1 = new Label("Friction Coefficient:");
		frictionInfo.getChildren().add(frictionLabel1);
		frictionLabel2 = new Label();
		frictionInfo.getChildren().add(frictionLabel2);
		
		// > > > Bounce Coefficient
		bounceInfo = new HBox();
		vBox.getChildren().add(bounceInfo);
		bounceInfo.setSpacing(10);
		bounceInfo.setAlignment(Pos.CENTER);
		bounceLabel1 = new Label("Bounce Coefficient");
		bounceInfo.getChildren().add(bounceLabel1);
		bounceLabel2 = new Label();
		bounceInfo.getChildren().add(bounceLabel2);
		
		if(!paintMode) {
			// > > > Save Delete and Cancel
			saveDeleteCancel = new HBox();
			vBox.getChildren().add(saveDeleteCancel);
			saveDeleteCancel.setSpacing(30);
			saveDeleteCancel.setAlignment(Pos.CENTER);
			saveButton = new Button("Save");
			saveDeleteCancel.getChildren().add(saveButton);
			saveButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					for(Map.Entry<TileList, Tile> entry : tileSettings.entrySet()) {
						if(entry.getKey().toString().equals(tileMenu.getValue())) {
							mapEditor.drawTile(tileX, tileY, entry.getValue());
						}
					}
					stage.close();
				}
			});
			deleteButton = new Button("Delete");
			saveDeleteCancel.getChildren().add(deleteButton);
			deleteButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					mapEditor.removeTile(tileX, tileY);
					stage.close();
				}
			});
		}
		else {
			// > > > Select and Cancel
			selectCancel = new HBox();
			vBox.getChildren().add(selectCancel);
			selectCancel.setAlignment(Pos.CENTER);
			selectCancel.setSpacing(30);
			
			paintSelectButton = new Button("Select");
			selectCancel.getChildren().add(paintSelectButton);
			paintSelectButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					for(Map.Entry<TileList, Tile> entry : tileSettings.entrySet()) {
						if(entry.getKey().toString().equals(tileMenu.getValue())) {
							if(!doorMode) {
								mapEditor.setPaintTile(entry.getValue());
							}
							else {
								doorSetter.displayTile(entry.getValue());
							}
						}
					}
					stage.close();
				}
			});
		}
		
		cancelButton = new Button("Cancel");
		if(!paintMode) {
			saveDeleteCancel.getChildren().add(cancelButton);
		}
		else {
			selectCancel.getChildren().add(cancelButton);
		}
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.close();
			}
		});
		
		// Initialize displayed info
		changeTileSelection();
		
		stage.show();
		
		stage.setOnCloseRequest(we -> {
			stage.close();
        });
	}
	
	/**
     * Display info of selected tile on the map editor
     */
	private void changeTileSelection() {
		for(Map.Entry<TileList, Tile> entry : tileSettings.entrySet()) {
			if(entry.getKey().toString().equals(tileMenu.getValue())) {
				tileImageView.setImage(tileSprite.get(entry.getKey()));
				tileTypeLabel2.setText(entry.getKey().toString());
				tileStateLabel2.setText(entry.getValue().getState().toString());
				frictionLabel2.setText(Double.toString(entry.getValue().getFrictionCoefficient()));
				bounceLabel2.setText(Double.toString(entry.getValue().getBounceCoefficient()));
			}
		}
	}
	
	/**
     * Get default settings for tiles
     */
	private void getTileSettings() {
		tileSettings = new HashMap<TileList, Tile>();
		EnumSet.allOf(TileList.class).forEach(tileList -> {
			tileSettings.put(tileList, new Tile(tileList, tileList.getTileState(), tileList.getFriction(), tileList.getBounceCoefficient(), tileList.getDensity()));
			tileMenu.getItems().add(tileList.toString());
		});
	}
}
