package mapeditor;

import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import server.engine.state.map.tile.Tile;
import shared.Constants;
import shared.lists.MapEditorAssetList;
import shared.lists.Team;
import shared.lists.TileState;
import shared.lists.TileList;

/**
 * MapEditor class. Contains the main gui for the map editor.
 *
 * @author Mak Hong Lun Timothy
 */
public class MapEditor {
	/**
     * saveFile - Save file for the current map
     */
	private File saveFile;
	/**
     * resWidth - Width for the gui
     */
	private int resWidth;
	/**
     * resHeight - Height for the gui
     */
	private int resHeight;
	/**
     * mapEditor - This object
     */
	private MapEditor mapEditor = this;
	/**
     * mapName - Name of this map
     */
	private String mapName;
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
     * background - Stack pane for backgrounds
     */
	private StackPane background;
	/**
     * mapViewerBackbground - Background for the map viewer
     */
	private Rectangle mapViewerBackground;
	/**
     * infoBackground - Background for the map's info
     */
	private Rectangle infoBackground;
	/**
     * mainViewer - GridPane for the map canvas and info viewer
     */
	private GridPane mainViewer;
	/**
     * mapCanvas - Canvas of the map
     */
	private Canvas mapCanvas;
	/**
     * mapSnapshot - Snapshot of the state of the map
     */
	private Image mapSnapshot;
	/**
     * infoViewer - StackPane for all info
     */
	private StackPane infoViewer;
	/**
     * info - Parent vBox for all info children 
     */
	private VBox info;
	/**
     * mapNameLabel - Label for map name
     */
	private Label mapNameLabel;
	/**
     * mapNameButton - Button for changing map name
     */
	private Button mapNameButton;
	/**
     * tileImageView - Image of selected tile
     */
	private ImageView tileImageView;
	/**
     * tileTypeInfo - HBox for tile info
     */
	private HBox tileTypeInfo;
	/**
     * tileTypeLabel1 - Label for tile type
     */
	private Label tileTypeLabel1;
	/**
     * tileTypeLabel2 - Label for tile type of selected tile
     */
	private Label tileTypeLabel2;
	/**
     * tileStateInfo - HBox for tile state
     */
	private HBox tileStateInfo;
	/**
     * tileStateLabel1 - Label for tile state
     */
	private Label tileStateLabel1;
	/**
     * tileStateLabel2 - Label for tile state of selected tile
     */
	private Label tileStateLabel2;
	/**
     * frictionInfo - HBox for friction info
     */
	private HBox frictionInfo;
	/**
     * frictionLabel1 - Label for friction
     */
	private Label frictionLabel1;
	/**
     * frictionLabel2 - Label for friction of selected tile
     */
	private Label frictionLabel2;
	/**
     * bounceInfo - HBox for bounce info
     */
	private HBox bounceInfo;
	/**
     * bounceLabel1 - Label for bounce
     */
	private Label bounceLabel1;
	/**
     * bounceLabel2 - Label for bounce of selected tile
     */
	private Label bounceLabel2;
	/**
     * mapSizeOption - Gui for map size option
     */
	private MapSizeOption mapSizeOption;
	/**
     * teamSpawnInfo - GridPane for team spawn info
     */
	private GridPane teamSpawnInfo;
	/**
     * redTeamSpawnLabel1 - Label for red team spawn
     */
	private Label redTeamSpawnLabel1;
	/**
     * redTeamSpawnLabel2 - Label for coordinates of red team spawn
     */
	private Label redTeamSpawnLabel2;
	/**
     * blueTeamSpawnLabel1 - Label for blue team spawn
     */
	private Label blueTeamSpawnLabel1;
	/**
     * blueTeamSpawnLabel2 - Label for coordinates of blue team spawn
     */
	private Label blueTeamSpawnLabel2;
	/**
     * greenTeamSpawnLabel1 - Label for green team spawn
     */
	private Label greenTeamSpawnLabel1;
	/**
     * greenTeamSpawnLabel2 - Label for coordinates of green team spawn
     */
	private Label greenTeamSpawnLabel2;
	/**
     * yellowTeamSpawnLabel1 - Label for yellow team spawn
     */
	private Label yellowTeamSpawnLabel1;
	/**
     * yellowTeamSpawnLabel2 - Label for coordinates of yellow team spawn
     */
	private Label yellowTeamSpawnLabel2;
	/**
     * setRedSpawn - Button for setting red spawn
     */
	private Button setRedSpawn;
	/**
     * setBlueSpawn - Button for setting blue spawn
     */
	private Button setBlueSpawn;
	/**
     * setGreenSpawn - Button for setting green spawn
     */
	private Button setGreenSpawn;
	/**
     * setYellowSpawn - Button for setting yellow spawn
     */
	private Button setYellowSpawn;
	/**
     * teamSpawns - HashMap containing all teams and their spawn coordinates
     */
	private HashMap<Team,int[]> teamSpawns;
	/**
     * paintVBox - VBox for all paint options
     */
	private VBox paintVBox;
	/**
     * paintCheckbox - CheckBox for turning on/off paint mode
     */
	private CheckBox paintCheckbox;
	/**
     * paintHBox - HBox for paint tile
     */
	private HBox paintHBox;
	/**
     * paintTileImageView - Image of selected tile to paint
     */
	private ImageView paintTileImageView;
	/**
     * paintChangeButton - Button for selecting tile to paint
     */
	private Button paintChangeButton;
	/**
     * squareDeleteHBox - HBox for square and delete mode
     */
	private HBox squareDeleteHBox;
	/**
     * squareCheckBox - CheckBox for turning on/off square mode
     */
	private CheckBox squareCheckBox;
	/**
     * squarePoints - Number of points clicked, used in square mode
     */
	private int squarePoints;
	/**
     * deleteCheckbox - CheckBox for turning on/off delete mode
     */
	private CheckBox deleteCheckbox;
	/**
     * waveDoorHBox - HBox for wave and door buttons
     */
	private HBox waveDoorHBox;
	/**
     * waveButton - Button for opening the wave setter gui
     */
	private Button waveButton;
	/**
     * doorButton - Button for opening the door setter gui
     */
	private Button doorButton;
	/**
     * doors - HashMap containing info on all door tiles currently on the map
     */
	private HashMap<String, TileList> doors;
	/**
     * saveCompleteHBox - HBox for save and complete buttons
     */
	private HBox saveCompleteHBox;
	/**
     * saveButton - Button for saving the map into a .gm file
     */
	private Button saveButton;
	/**
     * completeButton - Button for completing the map into a .GAMEMAP file
     */
	private Button completeButton;
	/**
     * mapWidth - Width of the map
     */
	private int mapWidth;
	/**
     * mapHeight - Height of the map
     */
	private int mapHeight;
	/**
     * mapGc - GraphicsContext for drawing the map
     */
	private GraphicsContext mapGc;
	/**
     * mapTiles - 2D matrix representation of the map
     */
	private Tile[][] mapTiles;
	/**
     * mapEditorAssets - Loaded image assets for the map editor
     */
	private HashMap<MapEditorAssetList, Image> mapEditorAssets;
	/**
     * tileSprite - Loaded tile sprites
     */
	private HashMap<TileList, Image> tileSprite;
	/**
     * keysActivated - Boolean whether the key controls for the map camera is active
     */
	private boolean keysActivated;
	/**
     * selectedX - X coordinate of the currently selected tile on the map
     */
	private int selectedX = 0;
	/**
     * selectedY - Y coordinate of the currently selected tile on the map
     */
	private int selectedY = 0;
	/**
     * squareXY1 - int array containing both X and Y coordinate of the first selected tile in square mode
     */
	private int[] squareXY1;
	/**
     * squareXY2 - int array containing both X and Y coordinate of the second selected tile in square mode
     */
	private int[] squareXY2;
	/**
     * numOfTiles - Number of tiles to draw, used in square mode
     */
	private int numOfTiles;
	/**
     * drawnTiles - Number of tiles drawn, used in square mode
     */
	private int drawnTiles;
	/**
     * paintTile - Tile used to paint in paint mode
     */
	private Tile paintTile;
	/**
     * waveSetter - Gui for the wave setter
     */
	private WaveSetter waveSetter;
	
	/**
     * Constructor for new map
     *
     * @param resWidth Width for the gui
     * @param resHeight Height for the gui
     */
	public MapEditor(int resWidth, int resHeight) {
		this.resWidth = resWidth;
		this.resHeight = resHeight;
		this.waveSetter = new WaveSetter(mapEditor);
		this.init();
	}
	
	/**
     * Constructor for saved map
     *
     * @param saveFile Save file for the current map
     * @param resWidth Width for the gui
     * @param resHeight Height for the gui
     */
	public MapEditor(File saveFile, int resWidth, int resHeight) {
		this.saveFile = saveFile;
		this.resWidth = resWidth;
		this.resHeight = resHeight;
		this.init();
	}
	
	/**
     * Getter for mapName
     * 
     * @return mapName
     */
	public String getMapName() {
		return this.mapName;
	}
	
	/**
     * Getter for mapTiles
     * 
     * @return mapTiles
     */
	public Tile[][] getMapTiles() {
		return this.mapTiles;
	}
	
	/**
     * Getter for tileSprite
     * 
     * @return tileSprite
     */
	public HashMap<TileList, Image> getTileSprite() {
		return this.tileSprite;
	}
	
	/**
     * Setter for mapWidth
     * 
     * @param mapWidth Width to set to
     */
	public void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}
	
	/**
     * Setter for mapHeight
     * 
     * @param mapHeight Height to set to
     */
	public void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}
	
	/**
     * Getter for SelectedXY
     * 
     * @return SelectedXY
     */
	public int[] getSelectedXY() {
		return new int[]{selectedX, selectedY};
	}
	
	/**
     * Initialize the gui and show it
     */
	private void init() {
		keysActivated = false;
		loadAssets();
		
		stage = new Stage();
		if(saveFile != null) {
			MapSave mapSave = MapWriter.readSave(saveFile);
			mapName = mapSave.getMapName();
			stage.setTitle(mapName);
			teamSpawns = mapSave.getTeamSpawns();
			mapWidth = mapSave.getMapWidth();
			mapHeight = mapSave.getMapHeight();
			mapTiles = mapSave.getMapTiles();
			waveSetter = new WaveSetter(mapEditor, mapSave.getZoneMap());
			doors = mapSave.getDoors();
		}
		else {
			stage.setTitle("New Map");
		}
		stage.setResizable(false);
		stage.setFullScreen(false);
		stage.centerOnScreen();
        
        root = new StackPane();
        scene = new Scene(root, resWidth, resHeight);
        stage.setScene(scene);
        root.setAlignment(Pos.CENTER);
		
        // Background
		background = new StackPane();
		root.getChildren().add(background);
		background.setAlignment(Pos.CENTER);
		
		// > Map Viewer Background
		mapViewerBackground = new Rectangle(scene.getWidth(), scene.getHeight());
		mapViewerBackground.setFill(Color.GREY);
		background.getChildren().add(mapViewerBackground);
		
		// Main Viewer
		mainViewer = new GridPane();
		root.getChildren().add(mainViewer);
		mainViewer.setAlignment(Pos.CENTER);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPrefWidth(scene.getWidth() - 300);
		col1.setHalignment(HPos.CENTER);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPrefWidth(300);
		col2.setHalignment(HPos.CENTER);
		mainViewer.getColumnConstraints().addAll(col1,col2);
		
		// > Map Viewer
		mapCanvas = new Canvas(0, 0);
		mainViewer.add(mapCanvas, 0, 0);
		mapCanvas.requestFocus();
		
		// > Info Viewer
		infoViewer = new StackPane();
		mainViewer.add(infoViewer, 1, 0);
		
		// > > Info Viewer Background
		infoBackground = new Rectangle(mainViewer.getColumnConstraints().get(1).getPrefWidth(), scene.getHeight());
		infoViewer.getChildren().add(infoBackground);
		infoBackground.setFill(Color.DARKGRAY);
		infoBackground.setOpacity(0.75);
		
		// > > Info VBox
		info = new VBox();
		infoViewer.getChildren().add(info);
		info.setSpacing(10);
		info.setAlignment(Pos.CENTER);

		// > > Map Name Label
		mapNameLabel = new Label("Map Name: ");
		info.getChildren().add(mapNameLabel);
		if(mapName != null) {
			mapNameLabel.setText("Map Name: " + mapName);
		}
		
		// > > Map Name Button
		mapNameButton = new Button("Set Name");
		info.getChildren().add(mapNameButton);
		mapNameButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				nameSetter();
			}
		});
		
		// > > Tile Image
		tileImageView = new ImageView();
		info.getChildren().add(tileImageView);
		
		// > > Tile Type
		tileTypeInfo = new HBox();
		info.getChildren().add(tileTypeInfo);
		tileTypeInfo.setSpacing(10);
		tileTypeInfo.setAlignment(Pos.CENTER);
		tileTypeLabel1 = new Label("Tile Type:");
		tileTypeInfo.getChildren().add(tileTypeLabel1);
		tileTypeLabel2 = new Label("-");
		tileTypeInfo.getChildren().add(tileTypeLabel2);
		
		// > > Tile State
		tileStateInfo = new HBox();
		info.getChildren().add(tileStateInfo);
		tileStateInfo.setSpacing(10);
		tileStateInfo.setAlignment(Pos.CENTER);
		tileStateLabel1 = new Label("Tile State:");
		tileStateInfo.getChildren().add(tileStateLabel1);
		tileStateLabel2 = new Label("-");
		tileStateInfo.getChildren().add(tileStateLabel2);
		
		// > > Friction
		frictionInfo = new HBox();
		info.getChildren().add(frictionInfo);
		frictionInfo.setSpacing(10);
		frictionInfo.setAlignment(Pos.CENTER);
		frictionLabel1 = new Label("Friction Coefficient:");
		frictionInfo.getChildren().add(frictionLabel1);
		frictionLabel2 = new Label("-");
		frictionInfo.getChildren().add(frictionLabel2);
		
		// > > Bounce
		bounceInfo = new HBox();
		info.getChildren().add(bounceInfo);
		bounceInfo.setSpacing(10);
		bounceInfo.setAlignment(Pos.CENTER);
		bounceLabel1 = new Label("Bounce Coefficient");
		bounceInfo.getChildren().add(bounceLabel1);
		bounceLabel2 = new Label("-");
		bounceInfo.getChildren().add(bounceLabel2);
		
		// > > > Map Size Option Button
		Button mapSizeOptionButton = new Button("Map Size");
		info.getChildren().add(mapSizeOptionButton);
		mapSizeOptionButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mapSizeOption = new MapSizeOption(mapEditor, mapWidth, mapHeight);
			}
		});
		
		// > > > Team spawns
		if(teamSpawns == null) {
			teamSpawns = new HashMap<Team, int[]>(); // {-1, -1} means unset
		}
		
		teamSpawnInfo = new GridPane();
		info.getChildren().add(teamSpawnInfo);
		teamSpawnInfo.setHgap(10);
		teamSpawnInfo.setAlignment(Pos.CENTER);
		
		// > > > > Red Team
		redTeamSpawnLabel1 = new Label("Red Team:");
		teamSpawnInfo.add(redTeamSpawnLabel1, 0, 0);
		redTeamSpawnLabel2 = new Label("(-, -)");
		teamSpawnInfo.add(redTeamSpawnLabel2, 1, 0);
		setRedSpawn = new Button("Set");
		teamSpawnInfo.add(setRedSpawn, 2, 0);
		setRedSpawn.setDisable(true);
		setRedSpawn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setTeamSpawn(selectedX, selectedY, Team.RED);
			}
		});
		
		// > > > > Blue Team
		blueTeamSpawnLabel1 = new Label("Blue Team:");
		teamSpawnInfo.add(blueTeamSpawnLabel1, 0, 1);
		blueTeamSpawnLabel2 = new Label("(-, -)");
		teamSpawnInfo.add(blueTeamSpawnLabel2, 1, 1);	
		setBlueSpawn = new Button("Set");
		teamSpawnInfo.add(setBlueSpawn, 2, 1);
		setBlueSpawn.setDisable(true);
		setBlueSpawn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setTeamSpawn(selectedX, selectedY, Team.BLUE);
			}
		});
		
		// > > > > Green Team
		greenTeamSpawnLabel1 = new Label("Green Team:");
		teamSpawnInfo.add(greenTeamSpawnLabel1, 0, 2);
		greenTeamSpawnLabel2 = new Label("(-, -)");
		teamSpawnInfo.add(greenTeamSpawnLabel2, 1, 2);
		setGreenSpawn = new Button("Set");
		teamSpawnInfo.add(setGreenSpawn, 2, 2);
		setGreenSpawn.setDisable(true);
		setGreenSpawn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setTeamSpawn(selectedX, selectedY, Team.GREEN);
			}
		});
		
		// > > > > Yellow Team
		yellowTeamSpawnLabel1 = new Label("Yellow Team:");
		teamSpawnInfo.add(yellowTeamSpawnLabel1, 0, 3);
		yellowTeamSpawnLabel2 = new Label("(-, -)");
		teamSpawnInfo.add(yellowTeamSpawnLabel2, 1, 3);
		setYellowSpawn = new Button("Set");
		teamSpawnInfo.add(setYellowSpawn, 2, 3);
		setYellowSpawn.setDisable(true);
		setYellowSpawn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setTeamSpawn(selectedX, selectedY, Team.YELLOW);
			}
		});
		
		// > > > Paint Mode VBox
		paintVBox = new VBox();
		info.getChildren().add(paintVBox);
		paintVBox.setSpacing(10);
		paintVBox.setAlignment(Pos.CENTER);
		
		// > > > > Paint Mode Checkbox
		paintCheckbox = new CheckBox("Paint Mode");
		paintVBox.getChildren().add(paintCheckbox);
		paintCheckbox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(paintCheckbox.isSelected()) {
					paintChangeButton.setDisable(false);
					squareCheckBox.setDisable(false);
					deleteCheckbox.setDisable(false);
				}
				else {
					paintChangeButton.setDisable(true);
					squareCheckBox.setDisable(true);
					deleteCheckbox.setDisable(true);
					squareCheckBox.setSelected(false);
					deleteCheckbox.setSelected(false);
				}
			}
		});
		
		// > > > > Paint mode HBox
		paintHBox = new HBox();
		paintVBox.getChildren().add(paintHBox);
		paintHBox.setSpacing(10);
		paintHBox.setAlignment(Pos.CENTER);
		
		// > > > > > Paint tile image view
		paintTileImageView = new ImageView();
		paintHBox.getChildren().add(paintTileImageView);
		
		// > > > > > Paint tile select button
		paintChangeButton = new Button("Change");
		paintHBox.getChildren().add(paintChangeButton);
		paintChangeButton.setDisable(true);
		paintChangeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TileSelector paintTileSelector = new TileSelector(mapEditor);
			}
		});
		
		// > > > > Square & Delete Mode HBox
		squareDeleteHBox = new HBox();
		paintVBox.getChildren().add(squareDeleteHBox);
		squareDeleteHBox.setAlignment(Pos.CENTER);
		squareDeleteHBox.setSpacing(10);
		
		// > > > > > Square mode
		squareCheckBox = new CheckBox("Square Mode");
		squareDeleteHBox.getChildren().add(squareCheckBox);
		squareCheckBox.setDisable(true);
		squareCheckBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!squareCheckBox.isSelected()) {
					squarePoints = 0;
				}
				else {
					deleteCheckbox.setSelected(false);
				}
			}
		});
		
		// > > > > > Delete mode
		deleteCheckbox = new CheckBox("Delete Mode");
		squareDeleteHBox.getChildren().add(deleteCheckbox);
		deleteCheckbox.setDisable(true);
		deleteCheckbox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(deleteCheckbox.isSelected()) {
					squareCheckBox.setSelected(false);
				}
			}
		});
		
		// > > > Wave & Door HBox
		waveDoorHBox = new HBox();
		info.getChildren().add(waveDoorHBox);
		waveDoorHBox.setAlignment(Pos.CENTER);
		waveDoorHBox.setSpacing(10);
		
		// > > > > Wave Settings
		waveButton = new Button("Set Waves");
		waveDoorHBox.getChildren().add(waveButton);
		waveButton.setDisable(true);
		waveButton.setDisable(true);
		waveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				waveSetter.show();
			}
		});
		
		// > > > > Door Settings
		if(doors == null) {
			doors = new HashMap<String, TileList>();
		}
		
		doorButton = new Button("Set Doors");
		waveDoorHBox.getChildren().add(doorButton);
		doorButton.setDisable(true);
		doorButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DoorSetter doorSetter = new DoorSetter(tileSprite, doors, waveSetter.getZoneMap());
			}
		});
		
		// > > > Save & Complete HBox
		saveCompleteHBox = new HBox();
		info.getChildren().add(saveCompleteHBox);
		saveCompleteHBox.setAlignment(Pos.CENTER);
		saveCompleteHBox.setSpacing(10);
		
		// > > > > Save Button
		saveButton = new Button("Save");
		saveCompleteHBox.getChildren().add(saveButton);
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(mapName == null) {
					nameSetter();
				}
				MapWriter.saveMap(new MapSave(mapName, teamSpawns, mapWidth, mapHeight, mapTiles, waveSetter.getZoneMap(), doors));
			}
		});
		
		// > > > > Complete Button
		completeButton = new Button("Complete");
		saveCompleteHBox.getChildren().add(completeButton);
		completeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(mapName == null) {
					nameSetter();
				}
				if(teamSpawns.size() == 4) {
					MapWriter.completeMap(new MapSave(mapName, teamSpawns, mapWidth, mapHeight, mapTiles, waveSetter.getZoneMap(), doors));
				}
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("You must set all team spawns first");
					alert.showAndWait();
				}
			}
		});
		
		if(saveFile != null) {
			drawMapTiles();
			for(Map.Entry<Team, int[]> entry : teamSpawns.entrySet()) {
				drawSpawnLetter(entry.getValue()[0], entry.getValue()[1], entry.getKey());
				setTeamSpawnInfo(Integer.toString(entry.getValue()[0]), Integer.toString(entry.getValue()[1]), entry.getKey());
			}
		}
		
		stage.show();
        
		stage.setOnCloseRequest(we -> {
			stage.close();
        });
	}
	
	/**
     * Draw map grid, also draw the tiles if there is a save file
     */
	protected void drawMapTiles() {
		mainViewer.getChildren().remove(mapCanvas);
		mapCanvas = new Canvas(mapWidth*Constants.TILE_SIZE, mapHeight*Constants.TILE_SIZE);
		mainViewer.add(mapCanvas, 0, 0);
		
		mapGc = mapCanvas.getGraphicsContext2D();
		mapGc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
		mapGc.rect(0, 0, mapWidth*Constants.TILE_SIZE, mapHeight*Constants.TILE_SIZE);
		mapGc.setFill(Color.BLACK);
		mapGc.fill();
		
		if(mapTiles == null) {
			mapTiles = new Tile[mapWidth][mapHeight];
		}
		else {
			Tile[][] oldMapTiles = mapTiles;
			mapTiles = new Tile[mapWidth][mapHeight];
			for(int i = 0 ; i < mapTiles.length ; i++) {
				for(int j = 0 ; j < mapTiles[0].length ; j++) {
					if(i < oldMapTiles.length && j < oldMapTiles.length) {
						mapTiles[i][j] = oldMapTiles[i][j];
						if(mapTiles[i][j] != null) {
							mapGc.drawImage(tileSprite.get(mapTiles[i][j].getType()), i*Constants.TILE_SIZE, j*Constants.TILE_SIZE);
						}
					}
				}
			}
		}
		
		mapGc.setLineWidth(1);
		mapGc.setStroke(Color.GREY);
		// Draw vertical grid lines
		for(int i = 0 ; i < mapWidth ; i++) {
			mapGc.strokeLine(i*Constants.TILE_SIZE, 0, i*Constants.TILE_SIZE, mapHeight*Constants.TILE_SIZE);
		}
		// Draw horizontal grid lines
		for(int i = 0 ; i < mapHeight ; i++) {
			mapGc.strokeLine(0, i*Constants.TILE_SIZE, mapWidth*Constants.TILE_SIZE, i*Constants.TILE_SIZE);
		}
		
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		mapSnapshot = mapCanvas.snapshot(params, null);
		
		if(!keysActivated()) {
			activateKeys();
		}
		
		mapCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				selectTile((int)event.getX()/Constants.TILE_SIZE, (int)event.getY()/Constants.TILE_SIZE);
				if(event.getClickCount() == 2 && !paintCheckbox.isSelected()) {
					TileSelector tileSelector = new TileSelector(mapEditor, (int)event.getX()/Constants.TILE_SIZE, (int)event.getY()/Constants.TILE_SIZE);
				}
			}
		});
		mapCanvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				selectTile((int)event.getX()/Constants.TILE_SIZE, (int)event.getY()/Constants.TILE_SIZE);
				if(event.getClickCount() == 2 && !paintCheckbox.isSelected()) {
					TileSelector tileSelector = new TileSelector(mapEditor, (int)event.getX()/Constants.TILE_SIZE, (int)event.getY()/Constants.TILE_SIZE);
				}
			}
		});
		
		setRedSpawn.setDisable(false);
		setBlueSpawn.setDisable(false);
		setYellowSpawn.setDisable(false);
		setGreenSpawn.setDisable(false);
		waveButton.setDisable(false);
		doorButton.setDisable(false);
		
		selectTile(selectedX, selectedY);
		infoViewer.toFront();
		resetFocus();
	}
	
	/**
     * Draw a single tile on map and save it in mapTiles
     * 
     * @param tileX X coordinate of the tile
     * @param tileY Y coordinate of the tile
     * @param tile Tile object
     */
	protected void drawTile(int tileX, int tileY, Tile tile) {
		if(checkTile(tileX, tileY) && 0 <= tileX && tileX < mapTiles.length && 0 <= tileY && tileY < mapTiles[0].length) {
			if(drawnTiles == 0 || !squareCheckBox.isSelected()) {
				mapGc.drawImage(mapSnapshot, 0, 0);
			}
			mapGc.drawImage(tileSprite.get(tile.getType()), tileX*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE);
			drawEdge(tileX, tileY, Color.GREY);
			if(drawnTiles + 1 == numOfTiles || !squareCheckBox.isSelected()) {
			SnapshotParameters params = new SnapshotParameters();
			params.setFill(Color.TRANSPARENT);
			mapSnapshot = mapCanvas.snapshot(params, null);
			}
			mapTiles[tileX][tileY] = tile;
			if(tile.getType() == TileList.DOOR) {
				doors.put(Arrays.toString(new int[] {tileX, tileY}), tile.getType());
			}
			if(!paintCheckbox.isSelected()) {
				selectTile(tileX, tileY);
			}
		}
	}
	
	/**
     * Remove a single tile on map and remove it in mapTiles
     * 
     * @param tileX X coordinate of the tile
     * @param tileY Y coordinate of the tile
     */
	protected void removeTile(int tileX, int tileY) {
		if(checkTile(tileX, tileY) && 0 <= tileX && tileX < mapTiles.length && 0 <= tileY && tileY < mapTiles[0].length) {
			if(mapTiles[tileX][tileY] != null) {
				mapGc.drawImage(mapSnapshot, 0, 0);
				mapGc.drawImage(mapEditorAssets.get(MapEditorAssetList.VOID), tileX*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE);
				drawEdge(tileX, tileY, Color.BLACK);
				drawEdge(tileX, tileY, Color.GREY);
				SnapshotParameters params = new SnapshotParameters();
				params.setFill(Color.TRANSPARENT);
				mapSnapshot = mapCanvas.snapshot(params, null);
				mapTiles[tileX][tileY] = null;
				if(!paintCheckbox.isSelected()) {
					selectTile(tileX, tileY);
				}
			}
		}
	}
	
	/**
     * Draw a square of tiles on map in square mode
     */
	private void drawSquare() {
		switch(squarePoints) {
			case 0:
				squareXY1 = new int[] {selectedX, selectedY};
				squarePoints++;
				break;
			case 1:
				squareXY2 = new int[] {selectedX, selectedY};
				int[] topLeft = new int[] {Math.min(squareXY1[0], squareXY2[0]), Math.min(squareXY1[1], squareXY2[1])};
				int[] bottomRight = new int[] {Math.max(squareXY1[0], squareXY2[0]), Math.max(squareXY1[1], squareXY2[1])};
				squarePoints = 0;
				drawnTiles = 0;
				numOfTiles = (bottomRight[0] + 1 - topLeft[0])*(bottomRight[1] + 1 - topLeft[1]);
				for(int i = topLeft[0] ; i <= bottomRight[0] ; i++) {
					for(int j = topLeft[1] ; j <= bottomRight[1] ; j++) {
						drawTile(i, j, paintTile);
						drawnTiles++;
					}
				}
				
				break;
		}
	}
	
	/**
	 * Check if the key controls for map camera is activated
     * 
     * @return keysActivated True if key controls for map camera is activated, false otherwise
     */
	private boolean keysActivated() {
		return keysActivated;
	}
	
	/**
	 * Activate the key controls for map camera
     */
	private void activateKeys() {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				moveCamera(event.getCode());
			}
		});
	}
	
	/**
	 * Move the map camera based on pressed key
	 * 
	 * @param key Pressed key
     */
	private void moveCamera(KeyCode key) {
		switch(key) {
			case S:
				mapCanvas.setTranslateY(mapCanvas.getTranslateY() - (double)Constants.TILE_SIZE);
				break;
			case D:
				mapCanvas.setTranslateX(mapCanvas.getTranslateX() - (double)Constants.TILE_SIZE);
				break;
			case W:
				mapCanvas.setTranslateY(mapCanvas.getTranslateY() + (double)Constants.TILE_SIZE);
				break;
			case A:
				mapCanvas.setTranslateX(mapCanvas.getTranslateX() + (double)Constants.TILE_SIZE);
				break;
		}
		infoViewer.toFront();
	}
	
	/**
	 * Reset focus on mapCanvas
     */
	private void resetFocus() {
		mapCanvas.requestFocus();
	}
	
	/**
	 * Load needed assets
     */
	private void loadAssets() {
		loadMapEditorAssets();
		loadTileSprite();
	}
	
	/**
	 * Load assets used specifically for the editor
     */
	private void loadMapEditorAssets() {
		mapEditorAssets = new HashMap<MapEditorAssetList, Image>();
		EnumSet.allOf(MapEditorAssetList.class).forEach(mapEditorAssetList -> mapEditorAssets.put(mapEditorAssetList, new Image(mapEditorAssetList.getPath())));
	}
	
	/**
	 * Load tile sprites
     */
	private void loadTileSprite() {
		tileSprite = new HashMap<TileList, Image>();
		EnumSet.allOf(TileList.class).forEach(tileList -> tileSprite.put(tileList, new Image(tileList.getEntityListName().getPath())));
	}
	
	/**
	 * Tile selection
	 * 
	 * @param tileX X coordinate of selected tile
	 * @param tileY Y coordinate of selected tile
     */
	private void selectTile(int tileX, int tileY) {
		if(tileX < 0) {
			tileX = 0;
		}
		else if(tileX > mapWidth - 1) {
			tileX = mapWidth - 1;
		}
		if(tileY < 0) {
			tileY = 0;
		}
		else if(tileY > mapHeight - 1) {
			tileY = mapHeight - 1;
		}
		selectedX = tileX;
		selectedY = tileY;
		waveSetter.setSelectedXY(new int[] {selectedX, selectedY});
		if(!paintCheckbox.isSelected()) {
			mapGc.drawImage(mapSnapshot, 0, 0);
			drawEdge(tileX, tileY, Color.YELLOW);
			displayTileInfo(tileX, tileY);
		}
		else {
			if(!deleteCheckbox.isSelected()) {
				if(paintTile == null) {
					TileSelector paintTileSelector = new TileSelector(mapEditor);
				}
				if(paintTile != null) {
					if(!squareCheckBox.isSelected()) {
						drawTile(selectedX, selectedY, paintTile);
					}
					else {
						drawSquare();
					}
				}
			}
			else {
				removeTile(selectedX, selectedY);
			}
		}
	}
	
	/**
	 * Draw edges on a tile
	 * 
	 * @param tileX X coordinate of tile to draw to
	 * @param tileY Y coordinate of tile to draw to
	 * @param color Color of edge
     */
	private void drawEdge(int tileX, int tileY, Color color) {
		mapGc.setStroke(color);
		mapGc.strokeLine(tileX*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE, (tileX + 1)*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE);
		mapGc.strokeLine(tileX*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE, (tileX + 1)*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE);
		mapGc.strokeLine(tileX*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE, tileX*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE);
		mapGc.strokeLine((tileX + 1)*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE, (tileX + 1)*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE);
	}
	
	/**
	 * Draw team spawn letter on map
	 * 
	 * @param tileX X coordinate of tile to draw to
	 * @param tileY Y coordinate of tile to draw to
	 * @param team Team
     */
	private void drawSpawnLetter(int tileX, int tileY, Team team) {
		mapGc.drawImage(mapSnapshot, 0, 0);
		Paint p = Color.TRANSPARENT;
		String s = "";
		switch(team) {
			case RED:
				p = Color.RED;
				s = "R";
				break;
			case BLUE:
				p = Color.BLUE;
				s = "B";
				break;
			case GREEN:
				p = Color.GREEN;
				s = "G";
				break;
			case YELLOW:
				p = Color.YELLOW;
				s = "Y";
				break;
		}
		mapGc.setFill(p);
		mapGc.setTextAlign(TextAlignment.CENTER);
		mapGc.setTextBaseline(VPos.CENTER);
		mapGc.fillText(s, tileX*Constants.TILE_SIZE + Constants.TILE_SIZE/2, tileY*Constants.TILE_SIZE + Constants.TILE_SIZE/2);
		drawEdge(tileX, tileY, Color.GREY);
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		mapSnapshot = mapCanvas.snapshot(params, null);
	}
	
	/**
	 * Display tile info on the right
	 * 
	 * @param x X coordinate of tile to display
	 * @param y Y coordinate of tile to display
     */
	private void displayTileInfo(int x, int y) {
		Tile tile = mapTiles[x][y];
		if(tile != null) {
			setDisplayTileInfo(tile);
		}
		else {
			tileImageView.setImage(null);
			tileTypeLabel2.setText("-");
			tileStateLabel2.setText("-");
			frictionLabel2.setText("-");
			bounceLabel2.setText("-");
		}
	}
	
	/**
	 * Set team spawn tile
	 * 
	 * @param tileX X coordinate of tile to set
	 * @param tileY Y coordinate of tile to set
	 * @param team Team
     */
	private void setTeamSpawn(int tileX, int tileY, Team team) {
		if(mapTiles[tileX][tileY] != null && !mapTiles[tileX][tileY].getState().equals(TileState.SOLID)) {
			// Check here so no invalid spawn popup
			if(checkTile(tileX, tileY)) {
				if(teamSpawns.containsKey(team)) {
					if(teamSpawns.get(team)[0] != -1) {
						mapGc.drawImage(tileSprite.get(mapTiles[teamSpawns.get(team)[0]][teamSpawns.get(team)[1]].getType()), teamSpawns.get(team)[0]*Constants.TILE_SIZE, teamSpawns.get(team)[1]*Constants.TILE_SIZE);
						drawEdge(tileX, tileY, Color.GREY);
						SnapshotParameters params = new SnapshotParameters();
						params.setFill(Color.TRANSPARENT);
						mapSnapshot = mapCanvas.snapshot(params, null);
					}
				}
				drawEdge(tileX, tileY, Color.GREY);
				teamSpawns.put(team, new int[] {tileX, tileY});
				setTeamSpawnInfo(Integer.toString(tileX), Integer.toString(tileY), team);
				drawSpawnLetter(tileX, tileY, team);
				selectTile(tileX, tileY);
			}
		}
		else {
			invalidSpawnLocationErrorPopUp();
		}
	}
	
	/**
	 * Set team spawn info
	 * 
	 * @param tileX X coordinate of tile to set
	 * @param tileY Y coordinate of tile to set
	 * @param team Team
     */
	private void setTeamSpawnInfo(String tileX, String tileY, Team team) {
		String s = "(" + tileX + ", " + tileY + ")";
		switch(team) {
			case RED:
				redTeamSpawnLabel2.setText(s);
				break;
			case BLUE:
				blueTeamSpawnLabel2.setText(s);
				break;
			case GREEN:
				greenTeamSpawnLabel2.setText(s);
				break;
			case YELLOW:
				yellowTeamSpawnLabel2.setText(s);
				break;
		}
	}
	
	/**
	 * Check if tile is a spawn etc.
	 * 
	 * @param tileX X coordinate of tile to check
	 * @param tileY Y coordinate of tile to check
	 * @return true if tile is free to overwritten, false otherwise
     */
	public boolean checkTile(int tileX, int tileY) {
		for(Map.Entry<Team, int[]> entry : teamSpawns.entrySet()) {
			if(entry.getValue()[0] == tileX && entry.getValue()[1] == tileY) {
				if(tileOverWritePopUp(entry.getKey().toString() + " spawn")) {
					setTeamSpawnInfo("-", "-", entry.getKey());
					entry.setValue(new int[] {-1, -1});
				}
				else {
					return false;
				}
			}
		}
		for(Map.Entry<String, ZoneSettings> entry : waveSetter.getZoneMap().entrySet()) {
			for(int i = 0 ; i < entry.getValue().getEnemySpawns().size() ; i++) {
				if(Arrays.toString(entry.getValue().getEnemySpawns().get(i)).equals(Arrays.toString(new int[] {tileX, tileY}))) {
					if(tileOverWritePopUp("enemy spawn at (" + tileX + ", " + tileY + ") of zone " + entry.getKey())) {
						entry.getValue().getEnemySpawns().remove(i);
						if(waveSetter.getZoneComboBox().getValue().equals(entry.getKey())) {
							waveSetter.getEnemySpawnComboBox().getItems().remove(Arrays.toString(new int[] {tileX, tileY}));
							if(waveSetter.getEnemySpawnComboBox().getItems().isEmpty()) {
								waveSetter.getEnemySpawnComboBox().setDisable(true);
								waveSetter.getEnemySpawnSet().setDisable(true);
								waveSetter.getEnemySpawnDelete().setDisable(true);
							}
						}
					}
					else {
						return false;
					}
				}
			}
			for(int i = 0 ; i < entry.getValue().getTriggers().size() ; i++) {
				if(Arrays.toString(entry.getValue().getTriggers().get(i)).equals(Arrays.toString(new int[] {tileX, tileY}))) {
					if(tileOverWritePopUp("trigger at (" + tileX + ", " + tileY + ") of zone " + entry.getKey())) {
						entry.getValue().getTriggers().remove(i);
						if(waveSetter.getZoneComboBox().getValue().equals(entry.getKey())) {
							waveSetter.getTriggerComboBox().getItems().remove(Arrays.toString(new int[] {tileX, tileY}));
							if(waveSetter.getTriggerComboBox().getItems().isEmpty()) {
								waveSetter.getTriggerComboBox().setDisable(true);
								waveSetter.getTriggerSet().setDisable(true);
								waveSetter.getTriggerDelete().setDisable(true);
							}
						}
					}
					else {
						return false;
					}
				}
			}
		}
		if(doors.containsKey(Arrays.toString(new int[] {tileX, tileY}))) {
			if(tileOverWritePopUp("door at (" + tileX + ", " + tileY + ")")) {
				doors.remove(Arrays.toString(new int[] {tileX, tileY}));
			}
			else {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Display tile info
	 * 
	 * @param tile Tile to display
     */
	private void setDisplayTileInfo(Tile tile) {
		//tileIDLabel2.setText(Character.toString(tileID));
		tileImageView.setImage(tileSprite.get(tile.getType()));
		tileTypeLabel2.setText(tile.getType().toString());
		tileStateLabel2.setText(tile.getState().toString());
		frictionLabel2.setText(Double.toString(tile.getFrictionCoefficient()));
		bounceLabel2.setText(Double.toString(tile.getBounceCoefficient()));
	}
	
	/**
	 * Error when selected spawn location is invalid
     */
	private void invalidSpawnLocationErrorPopUp() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Invalid spawn location");
		alert.show();
	}
	
	/**
	 * Overwrite alert
	 * 
	 * @return true if the user decides to overwrite a tile, false otherwise
     */
	private boolean tileOverWritePopUp(String s) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Alert");
		alert.setHeaderText("Overwrite " + s + "?");
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK) {
			return true;
		}
		return false;
	}
	
	/**
	 * Set paint tile
	 * 
	 * @param tile Tile to set to
     */
	protected void setPaintTile(Tile tile) {
		paintTileImageView.setImage(tileSprite.get(tile.getType()));
		paintTile = tile;
	}
	
	/**
	 * Name setter for the map
     */
	private void nameSetter() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Name the map");
		dialog.setHeaderText("Enter a name for the map");
		
		Optional<String> name = dialog.showAndWait();
		if(name.isPresent() && !name.get().equals("")) {
			mapName = name.get();
			mapNameLabel.setText("Map Name: " + mapName);
			stage.setTitle(mapName);
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error with map name");
			alert.setContentText("Input name is empty");
			alert.showAndWait();
		}
	}
	
}
