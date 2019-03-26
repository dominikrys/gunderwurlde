package mapeditor;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
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
import server.engine.state.map.tile.Door;
import server.engine.state.map.tile.Tile;
import shared.Constants;
import shared.lists.MapEditorAssetList;
import shared.lists.Team;
import shared.lists.TileState;
import shared.lists.TileList;

public class MapEditor {
	
	private File saveFile;
	private int resWidth;
	private int resHeight;
	private MapEditor mapEditor = this;
	private String mapName;
	private Stage stage;
	private StackPane root;
	private Scene scene;
	private StackPane background;
	private Rectangle mapViewerBackground;
	private Rectangle infoBackground;
	private GridPane mainViewer;
	private Canvas mapCanvas;
	private Image mapSnapshot;
	private StackPane infoViewer;
	private VBox info;
	private Label mapNameLabel;
	private Button mapNameButton;
	//private HBox tileIDInfo;
	//private Label tileIDLabel1;
	//private Label tileIDLabel2;
	private ImageView tileImageView;
	private HBox tileTypeInfo;
	private Label tileTypeLabel1;
	private Label tileTypeLabel2;
	private HBox tileStateInfo;
	private Label tileStateLabel1;
	private Label tileStateLabel2;
	private HBox frictionInfo;
	private Label frictionLabel1;
	private Label frictionLabel2;
	private HBox bounceInfo;
	private Label bounceLabel1;
	private Label bounceLabel2;
	private MapSizeOption mapSizeOption;
	private GridPane teamSpawnInfo;
	private Label redTeamSpawnLabel1;
	private Label redTeamSpawnLabel2;
	private Label blueTeamSpawnLabel1;
	private Label blueTeamSpawnLabel2;
	private Label greenTeamSpawnLabel1;
	private Label greenTeamSpawnLabel2;
	private Label yellowTeamSpawnLabel1;
	private Label yellowTeamSpawnLabel2;
	private Button setRedSpawn;
	private Button setBlueSpawn;
	private Button setGreenSpawn;
	private Button setYellowSpawn;
	private HashMap<Team,int[]> teamSpawns;
	private VBox paintVBox;
	private CheckBox paintCheckbox;
	private HBox paintHBox;
	private ImageView paintTileImageView;
	private Button paintChangeButton;
	private CheckBox deleteCheckbox;
	private HBox waveDoorHBox;
	private Button waveButton;
	private Button doorButton;
	private HashMap<String, TileList> doors;
	private HBox saveCompleteHBox;
	private Button saveButton;
	private Button completeButton;
	private int mapWidth;
	private int mapHeight;
	private GraphicsContext mapGc;
	private Tile[][] mapTiles;
	private HashMap<MapEditorAssetList, Image> mapEditorAssets;
	private HashMap<TileList, Image> tileSprite;
	private boolean keysActivated;
	private int selectedX = 0;
	private int selectedY = 0;
	private Tile paintTile;
	private WaveSetter waveSetter;
	
	// New map
	public MapEditor(int resWidth, int resHeight) {
		this.resWidth = resWidth;
		this.resHeight = resHeight;
		this.waveSetter = new WaveSetter(mapEditor);
		this.init();
	}
	
	// Open map
	public MapEditor(File saveFile, int resWidth, int resHeight) {
		this.saveFile = saveFile;
		this.resWidth = resWidth;
		this.resHeight = resHeight;
		this.init();
	}
	
	public String getMapName() {
		return this.mapName;
	}
	
	public Tile[][] getMapTiles() {
		return this.mapTiles;
	}
	
	public HashMap<TileList, Image> getTileSprite() {
		return this.tileSprite;
	}
	
	public void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}
	
	public void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}
	
	public int[] getSelectedXY() {
		return new int[]{selectedX, selectedY};
	}
	
	// Initialize
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
		
		/*
		// > > Tile ID
		tileIDInfo = new HBox();
		info.getChildren().add(tileIDInfo);
		tileIDInfo.setSpacing(10);
		tileIDInfo.setAlignment(Pos.CENTER);
		tileIDLabel1 = new Label("Tile ID:");
		tileIDInfo.getChildren().add(tileIDLabel1);
		tileIDLabel2 = new Label();
		tileIDInfo.getChildren().add(tileIDLabel2);
		*/
		
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
				mapSizeOption = new MapSizeOption(mapEditor, mapEditorAssets, mapWidth, mapHeight);
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
		paintCheckbox = new CheckBox("Activate Paint Mode");
		paintVBox.getChildren().add(paintCheckbox);
		paintCheckbox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(paintCheckbox.isSelected()) {
					paintChangeButton.setDisable(false);
					deleteCheckbox.setDisable(false);
				}
				else {
					paintChangeButton.setDisable(true);
					deleteCheckbox.setDisable(true);
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
		
		// > > > > Delete mode
		deleteCheckbox = new CheckBox("Activate Delete Mode");
		paintVBox.getChildren().add(deleteCheckbox);
		deleteCheckbox.setDisable(true);
		
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
				MapWriter.completeMap(new MapSave(mapName, teamSpawns, mapWidth, mapHeight, mapTiles, waveSetter.getZoneMap(), doors));
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
	
	// NOT DONE
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
			for(int i = 0 ; i < mapTiles.length - 1 ; i++) {
				for(int j = 0 ; j < mapTiles[0].length - 1 ; j++) {
					if(i < oldMapTiles.length - 1 && j < oldMapTiles.length - 1) {
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
	
	// Draw a tile
	protected void drawTile(int tileX, int tileY, Tile tile) {
		if(checkTile(tileX, tileY) && 0 <= tileX && tileX < mapTiles.length && 0 <= tileY && tileY < mapTiles[0].length) {
			mapGc.drawImage(mapSnapshot, 0, 0);
			mapGc.drawImage(tileSprite.get(tile.getType()), tileX*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE);
			drawEdge(tileX, tileY, Color.GREY);
			SnapshotParameters params = new SnapshotParameters();
			params.setFill(Color.TRANSPARENT);
			mapSnapshot = mapCanvas.snapshot(params, null);
			mapTiles[tileX][tileY] = tile;
			if(tile.getType() == TileList.DOOR || tile.getType() == TileList.RUINS_DOOR) {
				doors.put(Arrays.toString(new int[] {tileX, tileY}), tile.getType());
			}
			if(!paintCheckbox.isSelected()) {
				selectTile(tileX, tileY);
			}
		}
	}
	
	// Tile remove
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
	
	private boolean keysActivated() {
		return keysActivated;
	}
	
	// Activate keyboard control
	private void activateKeys() {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				moveCamera(event.getCode());
			}
		});
	}
	
	// Move camera
	private void moveCamera(KeyCode key) {
		switch(key) {
			case W:
				mapCanvas.setTranslateY(mapCanvas.getTranslateY() - (double)Constants.TILE_SIZE);
				break;
			case A:
				mapCanvas.setTranslateX(mapCanvas.getTranslateX() - (double)Constants.TILE_SIZE);
				break;
			case S:
				mapCanvas.setTranslateY(mapCanvas.getTranslateY() + (double)Constants.TILE_SIZE);
				break;
			case D:
				mapCanvas.setTranslateX(mapCanvas.getTranslateX() + (double)Constants.TILE_SIZE);
				break;
		}
		infoViewer.toFront();
	}
	
	private void resetFocus() {
		mapCanvas.requestFocus();
	}
	
	// Load needed assets
	private void loadAssets() {
		loadMapEditorAssets();
		loadTileSprite();
	}
	
	// Load assets used specifically for the editor
	private void loadMapEditorAssets() {
		mapEditorAssets = new HashMap<MapEditorAssetList, Image>();
		EnumSet.allOf(MapEditorAssetList.class).forEach(mapEditorAssetList -> mapEditorAssets.put(mapEditorAssetList, new Image(mapEditorAssetList.getPath())));
	}
	
	// Load tile sprites
	private void loadTileSprite() {
		tileSprite = new HashMap<TileList, Image>();
		EnumSet.allOf(TileList.class).forEach(tileList -> tileSprite.put(tileList, new Image(tileList.getEntityListName().getPath())));
	}
	
	// Tile selection
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
					drawTile(selectedX, selectedY, paintTile);
				}
			}
			else {
				removeTile(selectedX, selectedY);
			}
		}
	}
	
	// I made this before learning about strokeRect
	private void drawEdge(int tileX, int tileY, Color color) {
		mapGc.setStroke(color);
		mapGc.strokeLine(tileX*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE, (tileX + 1)*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE);
		mapGc.strokeLine(tileX*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE, (tileX + 1)*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE);
		mapGc.strokeLine(tileX*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE, tileX*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE);
		mapGc.strokeLine((tileX + 1)*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE, (tileX + 1)*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE);
	}
	
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
	
	// Display tile info on the right
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
	
	// Set team spawn tile
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
	
	// Set team spawn info
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
	
	// Check if tile is a spawn etc.
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
	
	// Display tile info
	private void setDisplayTileInfo(Tile tile) {
		//tileIDLabel2.setText(Character.toString(tileID));
		tileImageView.setImage(tileSprite.get(tile.getType()));
		tileTypeLabel2.setText(tile.getType().toString());
		tileStateLabel2.setText(tile.getState().toString());
		frictionLabel2.setText(Double.toString(tile.getFrictionCoefficient()));
		bounceLabel2.setText(Double.toString(tile.getBounceCoefficient()));
	}
	
	// Error when selected spawn location is invalid
	private void invalidSpawnLocationErrorPopUp() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Invalid spawn location");
		alert.show();
	}
	
	// Overwrite alert
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
	
	// Set paint tile
	protected void setPaintTile(Tile tile) {
		paintTileImageView.setImage(tileSprite.get(tile.getType()));
		paintTile = tile;
	}
	
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
