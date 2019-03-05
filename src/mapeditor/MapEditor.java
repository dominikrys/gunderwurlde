package mapeditor;

import java.util.EnumSet;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
import shared.lists.EntityList;
import shared.lists.MapEditorAssetList;
import shared.lists.TileTypes;

public class MapEditor {
	
	private String fileName;
	private Stage stage;
	private Canvas mapCanvas;
	private Canvas resizeAnchorCanvas;
	private Canvas resizeArrowsCanvas;
	private TextField widthTextField;
	private TextField heightTextField;
	private int mapWidth;
	private int mapHeight;
	private Tile[][] mapTiles;
	private HashMap<MapEditorAssetList, Image> mapEditorAssets;
	private HashMap<EntityList, Image> tileSprite;
	private HashMap<Integer, Image> rotatedArrows;
	private int dotX;
	private int dotY;
	
	
	// New map
	public MapEditor() {
		this.init();
	}
	
	// Open map
	public MapEditor(String fileName) {
		this.fileName = fileName;
		this.init();
	}
	
	// Initialize
	private void init() {
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
        
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        root.setAlignment(Pos.CENTER);
		
        // Background
		GridPane background = new GridPane();
		root.getChildren().add(background);
		background.setAlignment(Pos.CENTER);
		
		// > Map Viewer Background
		Rectangle mapViewerBackground = new Rectangle(500, 600);
		mapViewerBackground.setFill(Color.GREY);
		background.add(mapViewerBackground, 0, 0);
		
		// > Info Viewer Background
		Rectangle infoBackground = new Rectangle(300,600);
		infoBackground.setFill(Color.DARKGRAY);
		background.add(infoBackground, 1, 0);
		
		// Main Viewer
		GridPane mainViewer = new GridPane();
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
		mapCanvas = new Canvas(500, 600);
		mainViewer.add(mapCanvas, 0, 0);
		
		// > Info Viewer
		VBox info = new VBox();
		mainViewer.add(info, 1, 0);
		info.setSpacing(10);
		info.setAlignment(Pos.CENTER);
		
		// > > Map Size:
		HBox mapSizeHBox = new HBox();
		info.getChildren().add(mapSizeHBox);
		mapSizeHBox.setSpacing(10);
		mapSizeHBox.setAlignment(Pos.CENTER);
		Label mapSizeLabel = new Label("Map Size:");
		
		// > > W
		widthTextField = new TextField();
		widthTextField.setPrefWidth(75);
		widthTextField.setPromptText("W");
		widthTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.ENTER)) {
					if(widthTextField.getText().matches("\\d*") && !widthTextField.getText().trim().isEmpty()) {
						int newWidth = Integer.parseInt(widthTextField.getText());
						if(newWidth < mapWidth) {
							System.out.println("Cropping may occur");
							// TODO: cropping warning, anchor arrowhead change
						}
						else {
							mapWidth = newWidth;
							System.out.println("Map width: " + mapWidth);
							if(!heightTextField.getText().trim().isEmpty()) {
								mapHeight = Integer.parseInt(heightTextField.getText());
								drawMapTiles();
								// TODO: draw tiles here
							}
						}
					}
					else {
						widthTextField.clear();
						sizeErrorPopup();
					}
				}
			}
		});
		widthTextField.setTooltip(new Tooltip("Width of map"));
		
		// > > X
		Label mapSizeX = new Label("X");
		
		// > > H
		heightTextField = new TextField();
		heightTextField.setPrefWidth(75);
		heightTextField.setPromptText("H");
		heightTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.ENTER)) {
					if(heightTextField.getText().matches("\\d*") && !heightTextField.getText().trim().isEmpty()) {
						int newHeight = Integer.parseInt(heightTextField.getText());
						if(newHeight < mapHeight) {
							System.out.println("Cropping may occur");
						}
						else {
							mapHeight = newHeight;
							System.out.println("Map height: " + mapHeight);
							if(!widthTextField.getText().trim().isEmpty()) {
								mapWidth = Integer.parseInt(widthTextField.getText());
								drawMapTiles();
								// TODO: draw tiles here
							}
						}
					}
					else {
						heightTextField.clear();
						sizeErrorPopup();
					}
				}
			}
		});
		heightTextField.setTooltip(new Tooltip("Height of map"));
		mapSizeHBox.getChildren().addAll(mapSizeLabel, widthTextField, mapSizeX, heightTextField);
		
		// > > Resize anchor
		StackPane resizePane = new StackPane();
		info.getChildren().add(resizePane);
		resizePane.setAlignment(Pos.CENTER);
		resizeAnchorCanvas = new Canvas(90, 90);
		resizePane.getChildren().add(resizeAnchorCanvas);
		drawResizeAnchorGrid();
		resizeArrowsCanvas = new Canvas(90, 90);
		resizePane.getChildren().add(resizeArrowsCanvas);
		drawResizeArrows();
		
		stage.show();
        
		stage.setOnCloseRequest(we -> {
			stage.close();
        });
	}
	
	// Draw grid for resize anchor
	private void drawResizeAnchorGrid() {
		GraphicsContext gc = resizeAnchorCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, resizeAnchorCanvas.getWidth(), resizeAnchorCanvas.getHeight());
		gc.rect(0, 0, resizeAnchorCanvas.getWidth(), resizeAnchorCanvas.getHeight());
		gc.setFill(Color.WHITE);
		gc.fill();
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		for(int i = 0 ; i <= 90 ;) {
			gc.strokeLine(0, i, 90, i);
			i += 30;
		}
		for(int i = 0 ; i <= 90 ;) {
			gc.strokeLine(i, 0, i, 90);
			i += 30;
		}
	}
	
	// Draw arrows and dot on the anchor grid
	private void drawResizeArrows() {
		GraphicsContext gc = resizeArrowsCanvas.getGraphicsContext2D();
		moveResizeArrows(gc, 45, 45);
		
		resizeArrowsCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				moveResizeArrows(gc, event.getX(), event.getY());
			}
		});
	}
	
	// Move arrows and dot on the anchor grid based on mouse click
	private void moveResizeArrows(GraphicsContext gc, double clickedX, double clickedY) {
		dotX = (int)clickedX/30;
		dotY = (int)clickedY/30;
		gc.clearRect(0, 0, resizeArrowsCanvas.getWidth(), resizeArrowsCanvas.getHeight());
		gc.drawImage(mapEditorAssets.get(MapEditorAssetList.DOT), dotX*30, dotY*30);
		for(int i = -1 ; i <= 1 ; i++) {
			for(int j = -1 ; j <= 1 ; j++) {
				if(!(dotX + i < 0) && !(dotY + j < 0)) {
					if(i == -1 && j == -1) {
						gc.drawImage(rotatedArrows.get(315), (dotX - 1)*30, (dotY - 1)*30);
					}
					else if(i == 0 && j == -1) {
						gc.drawImage(rotatedArrows.get(0), (dotX*30 + 15) - rotatedArrows.get(0).getWidth()/2, (dotY - 1)*30);
					}
					else if(i == 1 && j == -1) {
						gc.drawImage(rotatedArrows.get(45), (dotX + 1)*30, (dotY - 1)*30);
					}
					else if(i == -1 && j == 0) {
						gc.drawImage(rotatedArrows.get(270), (dotX - 1)*30, (dotY*30 + 15) - rotatedArrows.get(270).getHeight()/2);
					}
					else if(i == 1 && j == 0) {
						gc.drawImage(rotatedArrows.get(90), (dotX + 1)*30, (dotY*30 + 15) - rotatedArrows.get(90).getHeight()/2);
					}
					else if(i == -1 && j == 1) {
						gc.drawImage(rotatedArrows.get(225), (dotX - 1)*30, (dotY + 1)*30);
					}
					else if(i == 0 && j == 1) {
						gc.drawImage(rotatedArrows.get(180), (dotX*30 + 15) - rotatedArrows.get(180).getWidth()/2, (dotY + 1)*30);
					}
					else if(i == 1 && j == 1) {
						gc.drawImage(rotatedArrows.get(135), (dotX + 1)*30, (dotY + 1)*30);
					}
				}
			}
		}
	}
	
	// NOT DONE
	private void drawMapTiles() {
		Tile[][] oldMapTiles;
		GraphicsContext gc = mapCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
		gc.rect(0, 0, mapWidth*32, mapHeight*32);
		gc.setFill(Color.BLACK);
		gc.fill();
		gc.setLineWidth(1);
		gc.setStroke(Color.GREY);
		for(int i = 0 ; i < mapWidth ; i++) {
			gc.strokeLine(i*32, 0, i*32, mapWidth*32);
		}
		for(int i = 0 ; i < mapHeight ; i++) {
			gc.strokeLine(0, i*32, mapHeight*32, i*32);
		}
		
		if(mapTiles == null) {
			mapTiles = new Tile[mapWidth][mapHeight];
			oldMapTiles = new Tile[0][0];
		}
		else {
			oldMapTiles = mapTiles;
		}
		
		System.out.println(mapTiles.length);
		System.out.println(mapTiles[0].length);
		for(int i = 0 ; i < mapTiles.length - 1 ; i++) {
			for(int j = 0 ; j < mapTiles[0].length - 1 ; j++) {
				if(i < oldMapTiles.length - 1 && j < oldMapTiles.length - 1) {
					System.out.println("here1");
				}
				else {
					System.out.println("here2");
				}
			}
		}
		
	}
	
	// Load needed assets
	private void loadAssets() {
		loadMapEditorAssets();
		loadTileSprite();
	}
	
	// Load assets used specifically for the editor
	private void loadMapEditorAssets() {
		mapEditorAssets = new HashMap<MapEditorAssetList, Image>();
		EnumSet.allOf(MapEditorAssetList.class).forEach(MapEditorAssetList -> mapEditorAssets.put(MapEditorAssetList, new Image(MapEditorAssetList.getPath())));
		rotateResizeArrows();
	}
	
	// Load tile sprites
	private void loadTileSprite() {
		tileSprite = new HashMap<EntityList, Image>();
		EnumSet.allOf(TileTypes.class).forEach(TileTypes -> tileSprite.put(TileTypes.getEntityListName(), new Image(TileTypes.getEntityListName().getPath())));
	}
	
	// Create images of rotated arrows and stored them in rotatedArrows
	private void rotateResizeArrows() {
		rotatedArrows = new HashMap<Integer, Image>();
		ImageView arrowView = new ImageView(mapEditorAssets.get(MapEditorAssetList.ARROW));
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		rotatedArrows.put(0, arrowView.snapshot(params, null));
		arrowView.setRotate(45);
		rotatedArrows.put(45, arrowView.snapshot(params, null));
		arrowView.setRotate(90);
		rotatedArrows.put(90, arrowView.snapshot(params, null));
		arrowView.setRotate(135);
		rotatedArrows.put(135, arrowView.snapshot(params, null));
		arrowView.setRotate(180);
		rotatedArrows.put(180, arrowView.snapshot(params, null));
		arrowView.setRotate(225);
		rotatedArrows.put(225, arrowView.snapshot(params, null));
		arrowView.setRotate(270);
		rotatedArrows.put(270, arrowView.snapshot(params, null));
		arrowView.setRotate(315);
		rotatedArrows.put(315, arrowView.snapshot(params, null));
	}
	
	// Error when a non-number is entered in size text field
	private void sizeErrorPopup() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Please only enter a number");
		alert.show();
	}
	
}
