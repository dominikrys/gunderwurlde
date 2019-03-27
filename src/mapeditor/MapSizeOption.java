package mapeditor;

import java.util.HashMap;
import java.util.Stack;

import javax.swing.text.html.HTMLDocument.HTMLReader.ParagraphAction;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.lists.MapEditorAssetList;

public class MapSizeOption {

	private MapEditor mapEditor;
	private HBox mapSizeHBox;
	private VBox vBox;
	private int mapWidth;
	private int mapHeight;
	private TextField widthTextField;
	private TextField heightTextField;
	private StackPane resizePane;
	private Canvas resizeAnchorCanvas;
	private Canvas resizeArrowsCanvas;
	private GraphicsContext resizeArrowsCanvasGc;
	private boolean negWidth;
	private boolean negHeight;
	private int dotX;
	private int dotY;
	private HBox saveAndCancelHBox;
	private Button saveButton;
	private Button cancelButton;
	private HashMap<Integer, Image> rotatedArrows;
	private HashMap<MapEditorAssetList, Image> mapEditorAssets;
	private Stage stage;
	private StackPane root;
	private Scene scene;

	public MapSizeOption(MapEditor mapEditor, HashMap<MapEditorAssetList, Image> mapEditorAssets, int mapWidth, int mapHeight) {
		this.mapEditor = mapEditor;
		this.mapEditorAssets = mapEditorAssets;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.negWidth = false;
		this.negHeight = false;
		this.dotX = 1;
		this.dotY = 1;
		rotateResizeArrows();
		this.init();
	}

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
		/*
		widthTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.matches("\\d+")) {
					negWidth = Integer.parseInt(newValue) < mapWidth;
					drawResizeArrows(dotX, dotY);
				}
			}
		});
		*/
		widthTextField.setTooltip(new Tooltip("Width of map"));

		// > > > X
		Label mapSizeX = new Label("X");

		// > > > H
		heightTextField = new TextField(Integer.toString(mapHeight));
		heightTextField.setPrefWidth(75);
		heightTextField.setPromptText("H");
		/*
		heightTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.matches("\\d+")) {
					negHeight = Integer.parseInt(newValue) < mapHeight;
					drawResizeArrows(dotX, dotY);
				}
			}
		});
		*/
		heightTextField.setTooltip(new Tooltip("Height of map"));
		mapSizeHBox.getChildren().addAll(mapSizeLabel, widthTextField, mapSizeX, heightTextField);
/*
		// > > Resize anchor
		resizePane = new StackPane();
		vBox.getChildren().add(resizePane);
		resizePane.setAlignment(Pos.CENTER);
		resizeAnchorCanvas = new Canvas(90, 90);
		resizePane.getChildren().add(resizeAnchorCanvas);
		drawResizeAnchorGrid();
		resizeArrowsCanvas = new Canvas(90, 90);
		resizePane.getChildren().add(resizeArrowsCanvas);
		drawResizeArrows(dotX, dotY);
*/
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

	// Draw grid for resize anchor
	private void drawResizeAnchorGrid() {
		GraphicsContext gc = resizeAnchorCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, resizeAnchorCanvas.getWidth(), resizeAnchorCanvas.getHeight());
		gc.rect(0, 0, resizeAnchorCanvas.getWidth(), resizeAnchorCanvas.getHeight());
		gc.setFill(Color.WHITE);
		gc.fill();
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		for (int i = 0 ; i <= 90 ;) {
			gc.strokeLine(0, i, 90, i);
			i += 30;
		}
		for (int i = 0 ; i <= 90 ;) {
			gc.strokeLine(i, 0, i, 90);
			i += 30;
		}
	}

	// Draw arrows and dot on the anchor grid
	private void drawResizeArrows(int dotX, int dotY) {
		resizeArrowsCanvasGc = resizeArrowsCanvas.getGraphicsContext2D();
		moveResizeArrows(resizeArrowsCanvasGc, dotX*30, dotY*30);

		resizeArrowsCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				moveResizeArrows(resizeArrowsCanvasGc, event.getX(), event.getY());
			}
		});
	}

	// Move arrows and dot on the anchor grid based on mouse click
	private void moveResizeArrows(GraphicsContext gc, double clickedX, double clickedY) {
		dotX = (int) clickedX / 30;
		dotY = (int) clickedY / 30;
		gc.clearRect(0, 0, resizeArrowsCanvas.getWidth(), resizeArrowsCanvas.getHeight());
		gc.drawImage(mapEditorAssets.get(MapEditorAssetList.DOT), dotX * 30, dotY * 30);
		for (int i = -1 ; i <= 1 ; i++) {
			for (int j = -1 ; j <= 1 ; j++) {
				if (!(dotX + i < 0) && !(dotY + j < 0)) {
					if (i == -1 && j == -1) {
						if(!negWidth && !negHeight) {
							gc.drawImage(rotatedArrows.get(315), (dotX - 1) * 30, (dotY - 1) * 30);
						}
						else {
							gc.drawImage(rotatedArrows.get(135), (dotX - 1) * 30, (dotY - 1) * 30);
						}
					} else if (i == 0 && j == -1) {
						if(!negHeight) {
							gc.drawImage(rotatedArrows.get(0), (dotX * 30 + 15) - rotatedArrows.get(0).getWidth() / 2, (dotY - 1) * 30);
						}
						else {
							gc.drawImage(rotatedArrows.get(180), (dotX * 30 + 15) - rotatedArrows.get(0).getWidth() / 2, (dotY - 1) * 30);
						}
					} else if (i == 1 && j == -1) {
						if(!negWidth && !negHeight) {
							gc.drawImage(rotatedArrows.get(45), (dotX + 1) * 30, (dotY - 1) * 30);
						}
						else {
							gc.drawImage(rotatedArrows.get(225), (dotX + 1) * 30, (dotY - 1) * 30);
						}
					} else if (i == -1 && j == 0) {
						if(!negWidth) {
							gc.drawImage(rotatedArrows.get(270), (dotX - 1) * 30, (dotY * 30 + 15) - rotatedArrows.get(270).getHeight() / 2);
						}
						else {
							gc.drawImage(rotatedArrows.get(90), (dotX - 1) * 30, (dotY * 30 + 15) - rotatedArrows.get(270).getHeight() / 2);
						}
					} else if (i == 1 && j == 0) {
						if(!negWidth) {
							gc.drawImage(rotatedArrows.get(90), (dotX + 1) * 30, (dotY * 30 + 15) - rotatedArrows.get(90).getHeight() / 2);
						}
						else {
							gc.drawImage(rotatedArrows.get(270), (dotX + 1) * 30, (dotY * 30 + 15) - rotatedArrows.get(90).getHeight() / 2);
						}
					} else if (i == -1 && j == 1) {
						if(!negWidth && !negHeight) {
							gc.drawImage(rotatedArrows.get(225), (dotX - 1) * 30, (dotY + 1) * 30);
						}
						else {
							gc.drawImage(rotatedArrows.get(45), (dotX - 1) * 30, (dotY + 1) * 30);
						}
					} else if (i == 0 && j == 1) {
						if(!negHeight) {
							gc.drawImage(rotatedArrows.get(180), (dotX * 30 + 15) - rotatedArrows.get(180).getWidth() / 2, (dotY + 1) * 30);
						}
						else {
							gc.drawImage(rotatedArrows.get(0), (dotX * 30 + 15) - rotatedArrows.get(180).getWidth() / 2, (dotY + 1) * 30);
						}
					} else if (i == 1 && j == 1) {
						if(!negWidth && !negHeight) {
							gc.drawImage(rotatedArrows.get(135), (dotX + 1) * 30, (dotY + 1) * 30);
						}
						else {
							gc.drawImage(rotatedArrows.get(315), (dotX + 1) * 30, (dotY + 1) * 30);
						}
					}
				}
			}
		}
	}

	// Create images of rotated arrows and stored them in rotatedArrows
	protected void rotateResizeArrows() {
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
		alert.setHeaderText("Please only enter non-zero numbers");
		alert.show();
	}

}
