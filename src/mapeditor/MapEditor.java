package mapeditor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import server.engine.state.map.tile.Tile;
import shared.Constants;
import shared.lists.EntityList;
import shared.lists.MapEditorAssetList;
import shared.lists.Teams;
import shared.lists.TileState;
import shared.lists.TileTypes;

public class MapEditor {
	
	private MapEditor mapEditor = this;
	private String fileName;
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
	private int mapWidth;
	private int mapHeight;
	private GraphicsContext mapGc;
	private Tile[][] mapTiles;
	private HashMap<MapEditorAssetList, Image> mapEditorAssets;
	private HashMap<TileTypes, Image> tileSprite;
	private boolean keysActivated;
	private int selectedX;
	private int selectedY;
	private Button setRedSpawn;
	private Button setBlueSpawn;
	private Button setGreenSpawn;
	private Button setYellowSpawn;
	private HashMap<Teams,int[]> teamSpawns;
	
	// New map
	public MapEditor() {
		this.init();
	}
	
	// Open map
	public MapEditor(String fileName) {
		this.fileName = fileName;
		this.init();
	}
	
	public Tile[][] getMapTiles() {
		return this.mapTiles;
	}
	
	public HashMap<TileTypes, Image> getTileSprite() {
		return this.tileSprite;
	}
	
	public void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}
	
	public void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}
	
	// Initialize
	private void init() {
		keysActivated = false;
		loadAssets();
		
		stage = new Stage();
		if(fileName != null) {
			stage.setTitle(fileName);
		}
		else {
			stage.setTitle("New Map");
		}
		stage.setResizable(false);
		stage.setFullScreen(false);
		stage.centerOnScreen();
        
        root = new StackPane();
        scene = new Scene(root, 800, 600);
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
		col1.setPrefWidth(500);
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
		teamSpawns = new HashMap<Teams, int[]>();
		
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
				setTeamSpawn(selectedX, selectedY, Teams.RED);
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
				setTeamSpawn(selectedX, selectedY, Teams.BLUE);
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
				setTeamSpawn(selectedX, selectedY, Teams.GREEN);
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
				setTeamSpawn(selectedX, selectedY, Teams.YELLOW);
			}
		});
		
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
		
		Tile[][] oldMapTiles;
		mapGc = mapCanvas.getGraphicsContext2D();
		mapGc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
		mapGc.rect(0, 0, mapWidth*Constants.TILE_SIZE, mapHeight*Constants.TILE_SIZE);
		mapGc.setFill(Color.BLACK);
		mapGc.fill();
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
		
		if(mapTiles == null) {
			mapTiles = new Tile[mapWidth][mapHeight];
			oldMapTiles = new Tile[0][0];
		}
		else {
			oldMapTiles = mapTiles;
		}
		
		for(int i = 0 ; i < mapTiles.length - 1 ; i++) {
			for(int j = 0 ; j < mapTiles[0].length - 1 ; j++) {
				if(i < oldMapTiles.length - 1 && j < oldMapTiles.length - 1) {
				}
				else {
				}
			}
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
				if(event.getClickCount() == 2) {
					TileSelector tileSelector = new TileSelector(mapEditor, (int)event.getX()/Constants.TILE_SIZE, (int)event.getY()/Constants.TILE_SIZE);
				}
			}
		});
		
		setRedSpawn.setDisable(false);
		setBlueSpawn.setDisable(false);
		setYellowSpawn.setDisable(false);
		setGreenSpawn.setDisable(false);
		
		resetFocus();
	}
	
	// Draw a tile
	protected void drawTile(int tileX, int tileY, Tile tile) {
		if(checkTile(tileX, tileY)) {
			mapGc.drawImage(mapSnapshot, 0, 0);
			mapGc.drawImage(tileSprite.get(tile.getType()), tileX*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE);
			drawEdge(tileX, tileY, Color.GREY);
			SnapshotParameters params = new SnapshotParameters();
			params.setFill(Color.TRANSPARENT);
			mapSnapshot = mapCanvas.snapshot(params, null);
			mapTiles[tileX][tileY] = tile;
			selectTile(tileX, tileY);
		}
	}
	
	// Tile remove
	protected void removeTile(int tileX, int tileY) {
		if(mapTiles[tileX][tileY] != null && checkTile(tileX, tileY)) {
			mapGc.drawImage(mapSnapshot, 0, 0);
			mapGc.drawImage(mapEditorAssets.get(MapEditorAssetList.VOID), tileX*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE);
			drawEdge(tileX, tileY, Color.BLACK);
			drawEdge(tileX, tileY, Color.GREY);
			SnapshotParameters params = new SnapshotParameters();
			params.setFill(Color.TRANSPARENT);
			mapSnapshot = mapCanvas.snapshot(params, null);
			mapTiles[tileX][tileY] = null;
			selectTile(tileX, tileY);
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
		tileSprite = new HashMap<TileTypes, Image>();
		EnumSet.allOf(TileTypes.class).forEach(tileTypes -> tileSprite.put(tileTypes, new Image(tileTypes.getEntityListName().getPath())));
	}
	
	// Tile selection
	private void selectTile(int tileX, int tileY) {
		selectedX = tileX;
		selectedY = tileY;
		mapGc.drawImage(mapSnapshot, 0, 0);
		drawEdge(tileX, tileY, Color.YELLOW);
		displayTileInfo(tileX, tileY);
	}
	
	// I made this before learning about strokeRect
	private void drawEdge(int tileX, int tileY, Color color) {
		mapGc.setStroke(color);
		mapGc.strokeLine(tileX*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE, (tileX + 1)*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE);
		mapGc.strokeLine(tileX*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE, (tileX + 1)*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE);
		mapGc.strokeLine(tileX*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE, tileX*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE);
		mapGc.strokeLine((tileX + 1)*Constants.TILE_SIZE, tileY*Constants.TILE_SIZE, (tileX + 1)*Constants.TILE_SIZE, (tileY + 1)*Constants.TILE_SIZE);
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
	private void setTeamSpawn(int tileX, int tileY, Teams team) {
		if(mapTiles[tileX][tileY] != null && mapTiles[tileX][tileY].getState().equals(TileState.PASSABLE)) {
			// Check here so no invalid spawn popup
			if(checkTile(tileX, tileY)) {
				teamSpawns.put(team, new int[] {tileX, tileY});
				setTeamSpawnInfo(Integer.toString(tileX), Integer.toString(tileY), team);
			}
		}
		else {
			invalidSpawnLocationErrorPopUp();
		}
	}
	
	// Set team spawn info
	private void setTeamSpawnInfo(String tileX, String tileY, Teams team) {
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
	private boolean checkTile(int tileX, int tileY) {
		for(Map.Entry<Teams, int[]> entry : teamSpawns.entrySet()) {
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
	
}
