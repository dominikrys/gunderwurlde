package mapeditor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import server.engine.state.map.Round;
import server.engine.state.map.Zone;
import server.engine.state.map.tile.Door;
import shared.Location;

public class WaveSetter {
	
	private Stage stage;
	private StackPane root;
	private Scene scene;
	private Rectangle zoneInfoBg;
	private GridPane mainViewer;
	private VBox zoneInfo;
	private HBox zoneLabelComboBoxHBox;
	private Label zoneLabel;
	private ComboBox<Integer> zoneComboBox;
	private HBox zoneAddDeleteHBox;
	private Button zoneAdd;
	private Button zoneDelete;
	private HBox enemySpawnLabelComboBoxHBox;
	private Label enemySpawnLabel;
	private ComboBox enemySpawnComboBox;
	private HBox enemySpawnAddSetDeleteHBox;
	private Button enemySpawnAdd;
	private Button enemySpawnSet;
	private Button enemySpawnDelete;
	private HBox triggerLabelComboBoxHBox;
	private Label triggerLabel;
	private ComboBox triggerComboBox;
	private HBox triggerAddSetDeleteHBox;
	private Button triggerAdd;
	private Button triggerSet;
	private Button triggerDelete;
	private HashMap<Integer, Zone> zoneMap;
	private int zoneInt;
	
	public WaveSetter() {
		this.zoneMap = new HashMap<Integer, Zone>();
		this.zoneInt = 0;
		this.init();
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
				zoneComboBox.getItems().add(zoneInt++);
				zoneMap.put(zoneInt, new Zone(new LinkedHashSet<Location>(), new LinkedList<Round>(), new LinkedHashSet<int[]>(), new LinkedHashMap<int[], Door>()));
				//zoneMap.get(1).
			}
		});
		
		// > > > > Zone Delete Button
		zoneDelete = new Button("Delete");
		zoneAddDeleteHBox.getChildren().add(zoneDelete);
		
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
		
		// > > > Enemy Spawn Add & Set & Delete Button HBox
		enemySpawnAddSetDeleteHBox = new HBox();
		zoneInfo.getChildren().add(enemySpawnAddSetDeleteHBox);
		enemySpawnAddSetDeleteHBox.setSpacing(10);
		enemySpawnAddSetDeleteHBox.setAlignment(Pos.CENTER);
		
		// > > > > Enemy Spawn Add
		enemySpawnAdd = new Button("Add");
		enemySpawnAddSetDeleteHBox.getChildren().add(enemySpawnAdd);
		
		// > > > > Enemy Spawn Set
		enemySpawnSet = new Button("Set");
		enemySpawnAddSetDeleteHBox.getChildren().add(enemySpawnSet);
		
		// > > > > Enemy Spawn Delete
		enemySpawnDelete = new Button("Delete");
		enemySpawnAddSetDeleteHBox.getChildren().add(enemySpawnDelete);
		
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
		
		// > > > Trigger Add & Set & Delete Button HBox
		triggerAddSetDeleteHBox = new HBox();
		zoneInfo.getChildren().add(triggerAddSetDeleteHBox);
		triggerAddSetDeleteHBox.setSpacing(10);
		triggerAddSetDeleteHBox.setAlignment(Pos.CENTER);
		
		// > > > > Trigger Add
		triggerAdd = new Button("Add");
		triggerAddSetDeleteHBox.getChildren().add(triggerAdd);
		
		// > > > > Trigger Set
		triggerSet = new Button("Set");
		triggerAddSetDeleteHBox.getChildren().add(triggerSet);
		
		// > > > > Trigger Delete
		triggerDelete = new Button("Delete");
		triggerAddSetDeleteHBox.getChildren().add(triggerDelete);
		
		stage.setOnCloseRequest(we -> {
			stage.close();
        });
	}
	
	protected void show() {
		stage.show();
	}

}
