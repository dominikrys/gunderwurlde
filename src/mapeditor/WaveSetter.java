package mapeditor;

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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import shared.lists.EntityList;
import shared.lists.TileState;

public class WaveSetter {
	
	private WaveSetter waveSetter = this;
	private MapEditor mapEditor;
	private Stage stage;
	private StackPane root;
	private Scene scene;
	private Rectangle zoneInfoBg;
	private GridPane mainViewer;
	private VBox zoneInfo;
	private HBox zoneLabelComboBoxHBox;
	private Label zoneLabel;
	private ComboBox<String> zoneComboBox;
	private HBox zoneAddDeleteHBox;
	private Button zoneAdd;
	private Button zoneDelete;
	private Label selectedXYLabel;
	private HBox enemySpawnLabelComboBoxHBox;
	private Label enemySpawnLabel;
	private ComboBox<String> enemySpawnComboBox;
	private HBox enemySpawnAddSetDeleteHBox;
	private Button enemySpawnAdd;
	private Button enemySpawnSet;
	private Button enemySpawnDelete;
	private HBox triggerLabelComboBoxHBox;
	private Label triggerLabel;
	private ComboBox<String> triggerComboBox;
	private HBox triggerAddSetDeleteHBox;
	private Button triggerAdd;
	private Button triggerSet;
	private Button triggerDelete;
	private HBox roundLabelComboBoxHBox;
	private Label roundLabel;
	private ComboBox<String> roundComboBox;
	private HBox roundAddDeleteHBox;
	private Button roundAdd;
	private Button roundDelete;
	private ScrollPane waveScrollPane;
	private GridPane waveGridPane;
	private HashMap<EntityList, Label> enemyStartTimeLabels;
	private HashMap<EntityList, Label> enemySpawnIntervalLabels;
	private HashMap<EntityList, Label> enemyAmountPerSpawnLabels;
	private HashMap<EntityList, Label> enemyTotalLabels;
	private HashMap<EntityList, Label> enemyReadyLabels;
	private HashMap<EntityList, Button> enemySetButtons;
	private HashMap<String, ZoneSettings> zoneMap;
	private HashMap<EntityList, ImageView> enemySprite;
	private int[] selectedXY;
	
	public WaveSetter(MapEditor mapEditor) {
		this.mapEditor = mapEditor;
		this.zoneMap = new HashMap<String, ZoneSettings>();
		this.selectedXY = new int[] {0, 0};
		loadEnemySprite();
		this.init();
	}
	
	public void setSelectedXY(int[] selectedXY) {
		this.selectedXY = selectedXY;
		selectedXYLabel.setText("X: " + selectedXY[0] + " Y: " + selectedXY[1]);
	}
	
	public HashMap<String, ZoneSettings> getZoneMap() {
		return this.zoneMap;
	}
	
	public ComboBox<String> getZoneComboBox() {
		return this.zoneComboBox;
	}
	
	public ComboBox<String> getEnemySpawnComboBox() {
		return this.enemySpawnComboBox;
	}
	
	public Button getEnemySpawnSet() {
		return this.enemySpawnSet;
	}
	
	public Button getEnemySpawnDelete() {
		return this.enemySpawnDelete;
	}
	
	public ComboBox<String> getTriggerComboBox() {
		return this.triggerComboBox;
	}
	
	public Button getTriggerSet() {
		return this.triggerSet;
	}
	
	public Button getTriggerDelete() {
		return this.triggerDelete;
	}
	
	private void init() {
		stage = new Stage();
		stage.setTitle("Wave Setter");
		stage.setResizable(false);
		stage.setFullScreen(false);
		stage.centerOnScreen();
		
		// root
		root = new StackPane();
		scene = new Scene(root, 800, 600);
		stage.setScene(scene);
		root.setAlignment(Pos.CENTER_LEFT);
		
		// > Zone Info Background
		zoneInfoBg = new Rectangle(300, 600);
		zoneInfoBg.setFill(Color.LIGHTYELLOW);
		root.getChildren().add(zoneInfoBg);
		
		// > mainViewer
		mainViewer = new GridPane();
		root.getChildren().add(mainViewer);
		mainViewer.setAlignment(Pos.CENTER);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPrefWidth(300);
		col1.setHalignment(HPos.CENTER);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPrefWidth(scene.getWidth() - 300);
		col2.setHalignment(HPos.CENTER);
		mainViewer.getColumnConstraints().addAll(col1,col2);
		
		// > > Zone VBox
		zoneInfo = new VBox();
		mainViewer.add(zoneInfo, 0, 0);
		zoneInfo.setSpacing(10);
		zoneInfo.setAlignment(Pos.CENTER);
		
		// > > > Zone Label & Combo box HBox
		zoneLabelComboBoxHBox = new HBox();
		zoneInfo.getChildren().add(zoneLabelComboBoxHBox);
		zoneLabelComboBoxHBox.setSpacing(10);
		zoneLabelComboBoxHBox.setAlignment(Pos.CENTER);
		
		// > > > > Zone Label
		zoneLabel = new Label("Zone:");
		zoneLabelComboBoxHBox.getChildren().add(zoneLabel);
		
		// > > > > Zone Combo box
		zoneComboBox = new ComboBox<>();
		zoneLabelComboBoxHBox.getChildren().add(zoneComboBox);
		zoneComboBox.setDisable(true);
		zoneComboBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!zoneComboBox.getItems().isEmpty()) {
					enemySpawnComboBox.getItems().clear();
					if(zoneComboBox.getValue() != null) {
						for(int i = 0 ; i < zoneMap.get(zoneComboBox.getValue()).getEnemySpawns().size() ; i++) {
							enemySpawnComboBox.getItems().add(Arrays.toString(zoneMap.get(zoneComboBox.getValue()).getEnemySpawns().get(i)));
						}
					}
					enemySpawnComboBox.getSelectionModel().select(0);
					if(!enemySpawnComboBox.getItems().isEmpty()) {
						enemySpawnComboBox.setDisable(false);
						enemySpawnSet.setDisable(false);
						enemySpawnDelete.setDisable(false);
					}
					else {
						enemySpawnComboBox.setDisable(true);
						enemySpawnSet.setDisable(true);
						enemySpawnDelete.setDisable(true);
					}
					
					triggerComboBox.getItems().clear();
					if(zoneComboBox.getValue() != null) {
						for(int i = 0 ; i < zoneMap.get(zoneComboBox.getValue()).getTriggers().size() ; i++) {
							triggerComboBox.getItems().add(Arrays.toString(zoneMap.get(zoneComboBox.getValue()).getTriggers().get(i)));
						}
					}
					triggerComboBox.getSelectionModel().select(0);
					if(!triggerComboBox.getItems().isEmpty()) {
						triggerComboBox.setDisable(false);
						triggerSet.setDisable(false);
						triggerDelete.setDisable(false);
					}
					else {
						triggerComboBox.setDisable(true);
						triggerSet.setDisable(true);
						triggerDelete.setDisable(true);
					}
					
					roundComboBox.getItems().clear();
					if(zoneComboBox.getValue() != null) {
						for(Map.Entry<String, RoundSettings> entry : zoneMap.get(zoneComboBox.getValue()).getRounds().entrySet()) {
							roundComboBox.getItems().add(entry.getKey());
						}
					}
					roundComboBox.getSelectionModel().select(0);
					if(!roundComboBox.getItems().isEmpty()) {
						roundComboBox.setDisable(false);
						roundDelete.setDisable(false);
						for(Map.Entry<EntityList, Button> entry : enemySetButtons.entrySet()) {
							WaveSettings wave = zoneMap.get(zoneComboBox.getValue()).getRounds().get(roundComboBox.getValue()).getWaves().get(entry.getKey());
							if(wave != null) {
								setEnemyInfo(entry.getKey(), Integer.toString(wave.getStartTime()), Integer.toString(wave.getSpawnInterval()), Integer.toString(wave.getAmountPerSpawn()), Integer.toString(wave.getTotal()), Boolean.toString(wave.isReady()));
							}
							else {
								setEnemyInfo(entry.getKey(), "0", "0", "0", "0", "false");
							}
							entry.getValue().setDisable(false);
						}
					}
					else {
						for(Map.Entry<EntityList, Button> entry : enemySetButtons.entrySet()) {
							setEnemyInfo(entry.getKey(), "0", "0", "0", "0", "false");
							entry.getValue().setDisable(true);
						}
						roundDelete.setDisable(true);
						roundComboBox.setDisable(true);
					}
				}
			}
		});
		
		// > > > Zone Add & Delete Button HBox
		zoneAddDeleteHBox = new HBox();
		zoneInfo.getChildren().add(zoneAddDeleteHBox);
		zoneAddDeleteHBox.setSpacing(10);
		zoneAddDeleteHBox.setAlignment(Pos.CENTER);
		
		// > > > > Zone Add Button
		zoneAdd = new Button("Add");
		zoneAddDeleteHBox.getChildren().add(zoneAdd);
		zoneAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("New zone");
				dialog.setHeaderText("Please enter a name");
				
				Optional<String> name = dialog.showAndWait();
				if(name.isPresent() && !name.get().equals("") && !zoneComboBox.getItems().contains(name.get())) {
					zoneComboBox.getItems().add(name.get());
					zoneMap.put(name.get(), new ZoneSettings(name.get()));
					zoneComboBox.getSelectionModel().selectLast();
					zoneComboBox.setDisable(false);
					zoneDelete.setDisable(false);
					enemySpawnAdd.setDisable(false);
					triggerAdd.setDisable(false);
					roundAdd.setDisable(false);
					
				}
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Error with zone name");
					alert.setContentText("Input name is empty or is a duplicate");
					alert.showAndWait();
				}
			}
		});
		
		// > > > > Zone Delete Button
		zoneDelete = new Button("Delete");
		zoneAddDeleteHBox.getChildren().add(zoneDelete);
		zoneDelete.setDisable(true);
		zoneDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Alert");
				alert.setHeaderText("Zone Deletion");
				alert.setContentText("Do you want to delete this zone " + zoneComboBox.getValue() + " ?");
				
				Optional<ButtonType> result = alert.showAndWait();
				if(result.get() == ButtonType.OK) {
					zoneMap.remove(zoneComboBox.getValue());
					zoneComboBox.getItems().remove(zoneComboBox.getSelectionModel().getSelectedIndex());
					if(zoneComboBox.getItems().isEmpty()) {
						zoneComboBox.setDisable(true);
						zoneDelete.setDisable(true);
						enemySpawnComboBox.setDisable(true);
						enemySpawnAdd.setDisable(true);
						enemySpawnSet.setDisable(true);
						enemySpawnDelete.setDisable(true);
						triggerComboBox.setDisable(true);
						triggerAdd.setDisable(true);
						triggerSet.setDisable(true);
						triggerDelete.setDisable(true);
						roundComboBox.setDisable(true);
						roundAdd.setDisable(true);
						roundDelete.setDisable(true);
						for(Map.Entry<EntityList, Button> entry : enemySetButtons.entrySet()) {
							setEnemyInfo(entry.getKey(), "0", "0", "0", "0", "false");
							entry.getValue().setDisable(true);
						}
					}
					else {
						zoneComboBox.getSelectionModel().selectNext();
					}
				}
			}
		});
		
		// > > > Selected X & Y
		selectedXYLabel = new Label(Arrays.toString(selectedXY));
		zoneInfo.getChildren().add(selectedXYLabel);
		
		// > > > Enemy Spawn Label & Combo box HBox
		enemySpawnLabelComboBoxHBox = new HBox();
		zoneInfo.getChildren().add(enemySpawnLabelComboBoxHBox);
		enemySpawnLabelComboBoxHBox.setSpacing(10);
		enemySpawnLabelComboBoxHBox.setAlignment(Pos.CENTER);
		
		// > > > > Enemy Spawn Label
		enemySpawnLabel = new Label("Enemy Spawn:");
		enemySpawnLabelComboBoxHBox.getChildren().add(enemySpawnLabel);
		
		// > > > > Enemy Spawn ComboBox
		enemySpawnComboBox = new ComboBox<>();
		enemySpawnLabelComboBoxHBox.getChildren().add(enemySpawnComboBox);
		enemySpawnComboBox.setDisable(true);
		
		// > > > Enemy Spawn Add & Set & Delete Button HBox
		enemySpawnAddSetDeleteHBox = new HBox();
		zoneInfo.getChildren().add(enemySpawnAddSetDeleteHBox);
		enemySpawnAddSetDeleteHBox.setSpacing(10);
		enemySpawnAddSetDeleteHBox.setAlignment(Pos.CENTER);
		
		// > > > > Enemy Spawn Add
		enemySpawnAdd = new Button("Add");
		enemySpawnAddSetDeleteHBox.getChildren().add(enemySpawnAdd);
		enemySpawnAdd.setDisable(true);
		enemySpawnAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(checkDuplicate(zoneMap.get(zoneComboBox.getValue()).getEnemySpawns(), selectedXY) && mapEditor.getMapTiles()[selectedXY[0]][selectedXY[1]] != null && !mapEditor.getMapTiles()[selectedXY[0]][selectedXY[1]].getState().equals(TileState.SOLID)) {
					enemySpawnComboBox.getItems().add(Arrays.toString(selectedXY));
					zoneMap.get(zoneComboBox.getValue()).getEnemySpawns().add(selectedXY);
					enemySpawnComboBox.getSelectionModel().selectLast();
					enemySpawnComboBox.setDisable(false);
					enemySpawnSet.setDisable(false);
					enemySpawnDelete.setDisable(false);
				}
				else {
					selectionError("enemy spawn");
				}
			}
		});
		
		// > > > > Enemy Spawn Set
		enemySpawnSet = new Button("Set");
		enemySpawnAddSetDeleteHBox.getChildren().add(enemySpawnSet);
		enemySpawnSet.setDisable(true);
		enemySpawnSet.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(checkDuplicate(zoneMap.get(zoneComboBox.getValue()).getEnemySpawns(), selectedXY) && mapEditor.getMapTiles()[selectedXY[0]][selectedXY[1]] != null && !mapEditor.getMapTiles()[selectedXY[0]][selectedXY[1]].getState().equals(TileState.SOLID)) {
					for(int i = 0 ; i < zoneMap.get(zoneComboBox.getValue()).getEnemySpawns().size() ; i++) {
						if(enemySpawnComboBox.getValue().equals(Arrays.toString(zoneMap.get(zoneComboBox.getValue()).getEnemySpawns().get(i)))) {
							zoneMap.get(zoneComboBox.getValue()).getEnemySpawns().set(i, selectedXY);
							enemySpawnComboBox.getItems().remove(enemySpawnComboBox.getSelectionModel().getSelectedIndex());
							enemySpawnComboBox.getItems().add(Arrays.toString(selectedXY));
							enemySpawnComboBox.getSelectionModel().selectLast();
						}
					}
				}
				else {
					selectionError("enemy spawn");
				}
			}
		});
		
		// > > > > Enemy Spawn Delete
		enemySpawnDelete = new Button("Delete");
		enemySpawnAddSetDeleteHBox.getChildren().add(enemySpawnDelete);
		enemySpawnDelete.setDisable(true);
		enemySpawnDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Alert");
				alert.setHeaderText("Enemy Spawn Deletion");
				alert.setContentText("Do you want to delete this enemy spawn " + enemySpawnComboBox.getValue() + " ?");
				
				Optional<ButtonType> result = alert.showAndWait();
				if(result.get() == ButtonType.OK) {
					for(int i = 0 ; i < zoneMap.get(zoneComboBox.getValue()).getEnemySpawns().size() ; i++) {
						if(enemySpawnComboBox.getValue().equals(Arrays.toString(zoneMap.get(zoneComboBox.getValue()).getEnemySpawns().get(i)))) {
							zoneMap.get(zoneComboBox.getValue()).getEnemySpawns().remove(i);
						}
					}
					enemySpawnComboBox.getItems().remove(enemySpawnComboBox.getSelectionModel().getSelectedIndex());
					if(enemySpawnComboBox.getItems().isEmpty()) {
						enemySpawnComboBox.setDisable(true);
						enemySpawnSet.setDisable(true);
						enemySpawnDelete.setDisable(true);
					}
					else {
						enemySpawnComboBox.getSelectionModel().selectNext();
					}
				}
			}
		});
		
		// > > > Trigger Label & Combo box HBox
		triggerLabelComboBoxHBox = new HBox();
		zoneInfo.getChildren().add(triggerLabelComboBoxHBox);
		triggerLabelComboBoxHBox.setSpacing(10);
		triggerLabelComboBoxHBox.setAlignment(Pos.CENTER);
		
		// > > > > Trigger Label
		triggerLabel = new Label("Trigger:");
		triggerLabelComboBoxHBox.getChildren().add(triggerLabel);
		
		// > > > > Trigger ComboBox
		triggerComboBox = new ComboBox<>();
		triggerLabelComboBoxHBox.getChildren().add(triggerComboBox);
		triggerComboBox.setDisable(true);
		
		// > > > Trigger Add & Set & Delete Button HBox
		triggerAddSetDeleteHBox = new HBox();
		zoneInfo.getChildren().add(triggerAddSetDeleteHBox);
		triggerAddSetDeleteHBox.setSpacing(10);
		triggerAddSetDeleteHBox.setAlignment(Pos.CENTER);
		
		// > > > > Trigger Add
		triggerAdd = new Button("Add");
		triggerAddSetDeleteHBox.getChildren().add(triggerAdd);
		triggerAdd.setDisable(true);
		triggerAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(checkDuplicate(zoneMap.get(zoneComboBox.getValue()).getTriggers(), selectedXY) && mapEditor.getMapTiles()[selectedXY[0]][selectedXY[1]] != null && !mapEditor.getMapTiles()[selectedXY[0]][selectedXY[1]].getState().equals(TileState.SOLID)) {
					triggerComboBox.getItems().add(Arrays.toString(selectedXY));
					zoneMap.get(zoneComboBox.getValue()).getTriggers().add(selectedXY);
					triggerComboBox.getSelectionModel().selectLast();
					triggerComboBox.setDisable(false);
					triggerSet.setDisable(false);
					triggerDelete.setDisable(false);
				}
				else {
					selectionError("trigger");
				}
			}
		});
		
		// > > > > Trigger Set
		triggerSet = new Button("Set");
		triggerAddSetDeleteHBox.getChildren().add(triggerSet);
		triggerSet.setDisable(true);
		triggerSet.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(checkDuplicate(zoneMap.get(zoneComboBox.getValue()).getTriggers(), selectedXY) && mapEditor.getMapTiles()[selectedXY[0]][selectedXY[1]] != null && !mapEditor.getMapTiles()[selectedXY[0]][selectedXY[1]].getState().equals(TileState.SOLID)) {
					for(int i = 0 ; i < zoneMap.get(zoneComboBox.getValue()).getTriggers().size() ; i++) {
						if(triggerComboBox.getValue().equals(Arrays.toString(zoneMap.get(zoneComboBox.getValue()).getTriggers().get(i)))) {
							zoneMap.get(zoneComboBox.getValue()).getTriggers().set(i, selectedXY);
							triggerComboBox.getItems().remove(triggerComboBox.getSelectionModel().getSelectedIndex());
							triggerComboBox.getItems().add(Arrays.toString(selectedXY));
							triggerComboBox.getSelectionModel().selectLast();
						}
					}
				}
				else {
					selectionError("trigger");
				}
			}
		});
		
		// > > > > Trigger Delete
		triggerDelete = new Button("Delete");
		triggerAddSetDeleteHBox.getChildren().add(triggerDelete);
		triggerDelete.setDisable(true);
		triggerDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Alert");
				alert.setHeaderText("Trigger Deletion");
				alert.setContentText("Do you want to delete this trigger " + triggerComboBox.getValue() + " ?");
				
				Optional<ButtonType> result = alert.showAndWait();
				if(result.get() == ButtonType.OK) {
					for(int i = 0 ; i < zoneMap.get(zoneComboBox.getValue()).getTriggers().size() ; i++) {
						if(triggerComboBox.getValue().equals(Arrays.toString(zoneMap.get(zoneComboBox.getValue()).getTriggers().get(i)))) {
							zoneMap.get(zoneComboBox.getValue()).getTriggers().remove(i);
						}
					}
					triggerComboBox.getItems().remove(triggerComboBox.getSelectionModel().getSelectedIndex());
					if(triggerComboBox.getItems().isEmpty()) {
						triggerComboBox.setDisable(true);
						triggerSet.setDisable(true);
						triggerDelete.setDisable(true);
					}
					else {
						triggerComboBox.getSelectionModel().selectNext();
					}
				}
			}
		});
		
		// > > > Round Label & ComboBox HBox
		roundLabelComboBoxHBox = new HBox();
		zoneInfo.getChildren().add(roundLabelComboBoxHBox);
		roundLabelComboBoxHBox.setAlignment(Pos.CENTER);
		roundLabelComboBoxHBox.setSpacing(10);
		
		// > > > > Round Label
		roundLabel = new Label("Round:");
		roundLabelComboBoxHBox.getChildren().add(roundLabel);
		
		// > > > > Round ComboBox
		roundComboBox = new ComboBox<>();
		roundLabelComboBoxHBox.getChildren().add(roundComboBox);
		roundComboBox.setDisable(true);
		roundComboBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!roundComboBox.getItems().isEmpty() && zoneComboBox.getValue() != null) {
					for(Map.Entry<EntityList, Button> entry : enemySetButtons.entrySet()) {
						WaveSettings wave = zoneMap.get(zoneComboBox.getValue()).getRounds().get(roundComboBox.getValue()).getWaves().get(entry.getKey());
						if(wave != null) {
							setEnemyInfo(entry.getKey(), Integer.toString(wave.getStartTime()), Integer.toString(wave.getSpawnInterval()), Integer.toString(wave.getAmountPerSpawn()), Integer.toString(wave.getTotal()), Boolean.toString(wave.isReady()));
						}
						else {
							setEnemyInfo(entry.getKey(), "0", "0", "0", "0", "false");
						}
					}
				}
				else {
					
				}
			}
		});
		
		
		// > > > Round Add & Delete HBox
		roundAddDeleteHBox = new HBox();
		zoneInfo.getChildren().add(roundAddDeleteHBox);
		roundAddDeleteHBox.setAlignment(Pos.CENTER);
		roundAddDeleteHBox.setSpacing(10);
		
		// > > > > Round Add
		roundAdd = new Button("Add");
		roundAddDeleteHBox.getChildren().add(roundAdd);
		roundAdd.setDisable(true);
		roundAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("New round");
				dialog.setHeaderText("Please enter a name");
				
				Optional<String> name = dialog.showAndWait();
				if(name.isPresent() && !name.get().equals("") && !roundComboBox.getItems().contains(name.get())) {
					roundComboBox.getItems().add(name.get());
					zoneMap.get(zoneComboBox.getValue()).getRounds().put(name.get(), new RoundSettings());
					roundComboBox.getSelectionModel().selectLast();
					roundComboBox.setDisable(false);
					roundDelete.setDisable(false);
					for(Map.Entry<EntityList, Button> entry : enemySetButtons.entrySet()) {
						entry.getValue().setDisable(false);
					}
				}
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Error with round name");
					alert.setContentText("Input name is empty of is a duplicate");
					alert.showAndWait();
				}
			}
		});
		
		// > > > > Round Delete
		roundDelete = new Button("Delete");
		roundAddDeleteHBox.getChildren().add(roundDelete);
		roundDelete.setDisable(true);
		roundDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Alert");
				alert.setHeaderText("Round Deletion");
				alert.setContentText("Do you want to delete this round " + roundComboBox.getValue() + "?");
				
				Optional<ButtonType> result = alert.showAndWait();
				if(result.get() == ButtonType.OK) {
					zoneMap.get(zoneComboBox.getValue()).getRounds().remove(roundComboBox.getValue());
					roundComboBox.getItems().remove(roundComboBox.getSelectionModel().getSelectedIndex());
					if(roundComboBox.getItems().isEmpty()) {
						roundComboBox.setDisable(true);
						roundDelete.setDisable(true);
						for(Map.Entry<EntityList, Button> entry : enemySetButtons.entrySet()) {
							setEnemyInfo(entry.getKey(), "0", "0", "0", "0", "false");
							entry.getValue().setDisable(true);
						}
					}
					else {
						roundComboBox.getSelectionModel().selectNext();
						for(Map.Entry<EntityList, ImageView> entry : enemySprite.entrySet()) {
							WaveSettings wave = zoneMap.get(zoneComboBox.getValue()).getRounds().get(roundComboBox.getValue()).getWaves().get(entry.getKey());
							setEnemyInfo(entry.getKey(), Integer.toString(wave.getStartTime()), Integer.toString(wave.getSpawnInterval()), Integer.toString(wave.getAmountPerSpawn()), Integer.toString(wave.getTotal()), Boolean.toString(wave.isReady()));
						}
					}
				}
			}
		});
		
		// > > Wave ScrollPane
		waveScrollPane = new ScrollPane();
		mainViewer.add(waveScrollPane, 1, 0);
		waveScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		waveScrollPane.setPrefHeight(scene.getHeight());
		waveScrollPane.setPrefWidth(scene.getWidth() - 300);
		
		// > > > Wave GridPane
		waveGridPane = new GridPane();
		waveScrollPane.setContent(waveGridPane);
		waveGridPane.setGridLinesVisible(true);
		ColumnConstraints eCol = new ColumnConstraints();
		eCol.setPrefWidth(250);
		eCol.setHalignment(HPos.CENTER);
		waveGridPane.getColumnConstraints().addAll(eCol,eCol);
		int row = 0;
		int column = 0;
		enemyStartTimeLabels = new HashMap<EntityList, Label>();
		enemySpawnIntervalLabels = new HashMap<EntityList, Label>();
		enemyAmountPerSpawnLabels = new HashMap<EntityList, Label>();
		enemyTotalLabels = new HashMap<EntityList, Label>();
		enemyReadyLabels = new HashMap<EntityList, Label>();
		enemySetButtons = new HashMap<EntityList, Button>();
		for(Map.Entry<EntityList,ImageView> entry : enemySprite.entrySet()) {
			VBox vBox = new VBox();
			vBox.getChildren().add(entry.getValue());
			vBox.setAlignment(Pos.CENTER);
			vBox.setSpacing(10);
			Label enemyLabel = new Label(entry.getKey().toString());
			vBox.getChildren().add(enemyLabel);
			Label startTimeLabel = new Label("Start Time: 0");
			vBox.getChildren().add(startTimeLabel);
			enemyStartTimeLabels.put(entry.getKey(), startTimeLabel);
			Label spawnIntervalLabel = new Label("Spawn Interval: 0");
			vBox.getChildren().add(spawnIntervalLabel);
			enemySpawnIntervalLabels.put(entry.getKey(), spawnIntervalLabel);
			Label amountPerSpawnLabel = new Label("Amount Per Spawn: 0");
			vBox.getChildren().add(amountPerSpawnLabel);
			enemyAmountPerSpawnLabels.put(entry.getKey(), amountPerSpawnLabel);
			Label totalLabel = new Label("Total: 0");
			vBox.getChildren().add(totalLabel);
			enemyTotalLabels.put(entry.getKey(), totalLabel);
			Label readyLabel = new Label("Ready: false");
			vBox.getChildren().add(readyLabel);
			enemyReadyLabels.put(entry.getKey(), readyLabel);
			Button button = new Button("Set");
			vBox.getChildren().add(button);
			enemySetButtons.put(entry.getKey(), button);
			button.setDisable(true);
			button.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if(!zoneMap.get(zoneComboBox.getValue()).getRounds().get(roundComboBox.getValue()).getWaves().containsKey(entry.getKey())) {
						zoneMap.get(zoneComboBox.getValue()).getRounds().get(roundComboBox.getValue()).getWaves().put(entry.getKey(), new WaveSettings(waveSetter, entry.getKey()));
					}
					zoneMap.get(zoneComboBox.getValue()).getRounds().get(roundComboBox.getValue()).getWaves().get(entry.getKey()).show();
				}
			});
			
			waveGridPane.add(vBox, column, row);
			if(column++ == 1) {
				row++;
				column = 0;
			}
		}
		
		
		stage.setOnCloseRequest(we -> {
			stage.close();
        });
	}
	
	protected void show() {
		stage.show();
	}
	
	// Load entity sprites
	private void loadEnemySprite() {
		enemySprite = new HashMap<EntityList, ImageView>();
		EnumSet.allOf(EntityList.class).forEach(entityList -> {
			ImageView sprite;
			sprite = new ImageView(entityList.getPath());
			sprite.setEffect(entityList.getColorAdjust());
			switch(entityList) {
				case ZOMBIE:
					enemySprite.put(EntityList.ZOMBIE, sprite);
					break;
				case RUNNER:
					enemySprite.put(EntityList.RUNNER, sprite);
					break;
				case SOLDIER:
					enemySprite.put(EntityList.SOLDIER, sprite);
					break;
				case MIDGET:
					enemySprite.put(EntityList.MIDGET, sprite);
					break;
				case BOOMER:
					enemySprite.put(EntityList.BOOMER, sprite);
					break;
				case MACHINE_GUNNER:
					enemySprite.put(EntityList.MACHINE_GUNNER, sprite);
					break;
				case SNIPER:
					enemySprite.put(EntityList.SNIPER, sprite);
					break;
			}
		});
	}
	
	private boolean checkDuplicate(ArrayList<int[]> list, int[] selectedXY) {
		for(int i = 0 ; i < list.size() ; i++) {
			if(Arrays.toString(list.get(i)).equals(Arrays.toString(selectedXY))) {
				return false;
			}
		}
		return true;
	}
	
	public void setEnemyInfo(EntityList enemy, String startTime, String spawnInterval, String amountPerSpawn, String total, String ready) {
		enemyStartTimeLabels.get(enemy).setText("Start Time: " + startTime);
		enemySpawnIntervalLabels.get(enemy).setText("Spawn Interval: " + spawnInterval);
		enemyAmountPerSpawnLabels.get(enemy).setText("Amount Per Spawn: " + amountPerSpawn);
		enemyTotalLabels.get(enemy).setText("Total: " + total);
		enemyReadyLabels.get(enemy).setText("Ready: " + ready);
	}
	
	private void selectionError(String source) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Invalid or duplicate " + source);
		alert.showAndWait();
	}

}
