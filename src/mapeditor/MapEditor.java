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
	private Canvas resizePaneCanvas;
	private Canvas resizeArrowsCanvas;
	private int mapWidth;
	private int mapHeight;
	private Tile[][] mapTiles;
	private HashMap<MapEditorAssetList, Image> mapEditorAssets;
	private HashMap<EntityList, Image> tileSprite;
	private HashMap<Integer, Image> rotatedArrows;
	
	
	// New map
	public MapEditor() {
		this.init();
	}
	
	// Open map
	public MapEditor(String fileName) {
		this.fileName = fileName;
		this.init();
	}
	
	
	private void init() {
		this.loadAssets();
		
		this.stage = new Stage();
		if(fileName != null) {
			this.stage.setTitle(fileName);
		}
		else {
			this.stage.setTitle("New Map");
		}
		this.stage.setResizable(false);
		this.stage.setFullScreen(false);
		this.stage.centerOnScreen();
        
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 600);
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
		this.mapCanvas = new Canvas(500, 600);
		mainViewer.add(this.mapCanvas, 0, 0);
		
		// > Info Viewer
		VBox info = new VBox();
		mainViewer.add(info, 1, 0);
		info.setSpacing(10);
		info.setAlignment(Pos.CENTER);
		
		// > > Label("Map Size:")
		HBox mapSizeHBox = new HBox();
		info.getChildren().add(mapSizeHBox);
		mapSizeHBox.setSpacing(10);
		mapSizeHBox.setAlignment(Pos.CENTER);
		Label mapSizeLabel = new Label("Map Size:");
		// > > TextField("W")
		TextField widthTextField = new TextField();
		widthTextField.setPrefWidth(75);
		widthTextField.setPromptText("W");
		widthTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.ENTER)) {
					if(widthTextField.getText().matches("\\d*")) {
						int newWidth = Integer.parseInt(widthTextField.getText());
						if(newWidth < mapWidth) {
							System.out.println("Cropping may occur");
						}
						else {
							mapWidth = newWidth;
							System.out.println("Map width: " + mapWidth);
							//drawMapTiles();
						}
					}
					else {
						sizeErrorPopup();
					}
				}
			}
		});
		widthTextField.setTooltip(new Tooltip("Width of map"));
		// > > Label("X")
		Label mapSizeX = new Label("X");
		// > > TextField("H")
		TextField heightTextField = new TextField();
		heightTextField.setPrefWidth(75);
		heightTextField.setPromptText("H");
		heightTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode().equals(KeyCode.ENTER)) {
					if(heightTextField.getText().matches("\\d*")) {
						int newHeight = Integer.parseInt(heightTextField.getText());
						if(newHeight < mapHeight) {
							System.out.println("Cropping may occur");
						}
						else {
							mapHeight = newHeight;
							System.out.println("Map height: " + mapHeight);
							//drawMapTiles();
						}
					}
					else {
						sizeErrorPopup();
					}
				}
			}
		});
		heightTextField.setTooltip(new Tooltip("Height of map"));
		mapSizeHBox.getChildren().addAll(mapSizeLabel, widthTextField, mapSizeX, heightTextField);
		
		// Resize 9-grid
		StackPane resizePane = new StackPane();
		info.getChildren().add(resizePane);
		resizePane.setAlignment(Pos.CENTER);
		this.resizePaneCanvas = new Canvas(90, 90);
		resizePane.getChildren().add(this.resizePaneCanvas);
		this.drawResizePane();
		this.resizeArrowsCanvas = new Canvas(90, 90);
		resizePane.getChildren().add(this.resizeArrowsCanvas);
		this.drawResizeArrows();
		
		
		this.stage.setScene(scene);
		this.stage.show();
        
		this.stage.setOnCloseRequest(we -> {
			this.stage.close();
        });
	}
	
	// NOT DONE
	private void drawResizePane() {
		GraphicsContext gc = this.resizePaneCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, this.resizePaneCanvas.getWidth(), this.resizePaneCanvas.getHeight());
		gc.rect(0, 0, this.resizePaneCanvas.getWidth(), this.resizePaneCanvas.getHeight());
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
		
		/*
		this.resizePaneCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println(event.getX());
				System.out.println(event.getY());
			}
		});
		*/
	}
	
	private void drawResizeArrows() {
		GraphicsContext gc = this.resizeArrowsCanvas.getGraphicsContext2D();
		this.moveResizeArrows(gc, 45, 45);
		
		this.resizeArrowsCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				moveResizeArrows(gc, event.getX(), event.getY());
			}
		});
	}
	
	private void moveResizeArrows(GraphicsContext gc, double clickedX, double clickedY) {
		int dotX = (int)clickedX/30;
		int dotY = (int)clickedY/30;
		gc.clearRect(0, 0, this.resizeArrowsCanvas.getWidth(), this.resizeArrowsCanvas.getHeight());
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
		Tile[][] oldMapTiles = mapTiles;
		
		if(mapTiles == null) {
			mapTiles = new Tile[this.mapWidth][this.mapHeight];
		}
		
		for(int i = 0 ; i < mapTiles.length - 1 ; i++) {
			for(int j = 0 ; j < mapTiles[0].length - 1 ; j++) {
				if(i < oldMapTiles.length - 1 && j < oldMapTiles.length - 1) {
					
				}
				else {
					
				}
			}
		}
		
	}
	
	// NOT DONE
	private void loadAssets() {
		this.loadMapEditorAssets();
		this.loadTileSprite();
	}
	
	private void loadMapEditorAssets() {
		this.mapEditorAssets = new HashMap<MapEditorAssetList, Image>();
		EnumSet.allOf(MapEditorAssetList.class).forEach(MapEditorAssetList -> mapEditorAssets.put(MapEditorAssetList, new Image(MapEditorAssetList.getPath())));
		this.rotateResizeArrows();
	}
	
	private void loadTileSprite() {
		this.tileSprite = new HashMap<EntityList, Image>();
		EnumSet.allOf(TileTypes.class).forEach(TileTypes -> tileSprite.put(TileTypes.getEntityListName(), new Image(TileTypes.getEntityListName().getPath())));
	}
	
	private void rotateResizeArrows() {
		this.rotatedArrows = new HashMap<Integer, Image>();
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
	
	private void sizeErrorPopup() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Please only enter a number");
		alert.show();
	}
	
}
