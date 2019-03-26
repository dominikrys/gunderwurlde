package mapeditor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.lists.EntityList;

public class EnemySetter {
	
	private WaveSetter waveSetter;
	private EntityList enemy;
	private WaveSettings enemySettings;
	
	private Stage stage;
    private StackPane root;
    private Scene scene;
    private VBox vBox;
    private HBox startTimeLabelTextFieldHBox;
    private Label startTimeLabel;
    private TextField startTimeTextField;
    private HBox spawnIntervalLabelTextFieldHBox;
    private Label spawnIntervalLabel;
    private TextField spawnIntervalTextField;
    private HBox amountPerSpawnLabelTextFieldHBox;
    private Label amountPerSpawnLabel;
    private TextField amountPerSpawnTextField;
    private HBox totalLabelTextFieldHBox;
    private Label totalLabel;
    private TextField totalTextField;
    private HBox saveCancelHBox;
    private Button saveButton;
    private Button cancelButton;
    
    public EnemySetter(WaveSetter waveSetter, EntityList enemy, WaveSettings enemySettings) {
    	this.waveSetter = waveSetter;
    	this.enemy = enemy;
    	this.enemySettings = enemySettings;
    	this.init();
    }
    
    private void init() {
    	stage = new Stage();
		stage.setTitle(enemy.toString() + " settings");
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
		
		// > > StartTime Label & TextField HBox
		startTimeLabelTextFieldHBox = new HBox();
		vBox.getChildren().add(startTimeLabelTextFieldHBox);
		startTimeLabelTextFieldHBox.setAlignment(Pos.CENTER);
		startTimeLabelTextFieldHBox.setSpacing(10);
		
		// > > > StartTime Label
		startTimeLabel = new Label("Start Time:");
		startTimeLabelTextFieldHBox.getChildren().add(startTimeLabel);
		
		// > > > StartTime TextField
		startTimeTextField = new TextField(Integer.toString(enemySettings.getStartTime()));
		startTimeLabelTextFieldHBox.getChildren().add(startTimeTextField);
		
		// > > SpawnInterval Label & TextField HBox
		spawnIntervalLabelTextFieldHBox = new HBox();
		vBox.getChildren().add(spawnIntervalLabelTextFieldHBox);
		spawnIntervalLabelTextFieldHBox.setAlignment(Pos.CENTER);
		spawnIntervalLabelTextFieldHBox.setSpacing(10);
		
		// > > > SpawnInterval Label
		spawnIntervalLabel = new Label("Spawn Interval:");
		spawnIntervalLabelTextFieldHBox.getChildren().add(spawnIntervalLabel);
		
		// > > > SpawnInterval TextField
		spawnIntervalTextField = new TextField(Integer.toString(enemySettings.getSpawnInterval()));
		spawnIntervalLabelTextFieldHBox.getChildren().add(spawnIntervalTextField);
		
		// > > AmountPerSpawn Label & TextField HBox
		amountPerSpawnLabelTextFieldHBox = new HBox();
		vBox.getChildren().add(amountPerSpawnLabelTextFieldHBox);
		amountPerSpawnLabelTextFieldHBox.setAlignment(Pos.CENTER);
		amountPerSpawnLabelTextFieldHBox.setSpacing(10);
		
		// > > > AmountPerSpawn Label
		amountPerSpawnLabel = new Label("Amount Per Spawn:");
		amountPerSpawnLabelTextFieldHBox.getChildren().add(amountPerSpawnLabel);
		
		// > > > AmountPerSpawn TextField
		amountPerSpawnTextField = new TextField(Integer.toString(enemySettings.getAmountPerSpawn()));
		amountPerSpawnLabelTextFieldHBox.getChildren().add(amountPerSpawnTextField);
		
		// > > Total Label & TextField HBox
		totalLabelTextFieldHBox = new HBox();
		vBox.getChildren().add(totalLabelTextFieldHBox);
		totalLabelTextFieldHBox.setAlignment(Pos.CENTER);
		totalLabelTextFieldHBox.setSpacing(10);
		
		// > > > Total Label
		totalLabel = new Label("Total:");
		totalLabelTextFieldHBox.getChildren().add(totalLabel);
		
		// > > > Total TextField
		totalTextField = new TextField(Integer.toString(enemySettings.getTotal()));
		totalLabelTextFieldHBox.getChildren().add(totalTextField);
		
		// > > Save & Cancel HBox
		saveCancelHBox = new HBox();
		vBox.getChildren().add(saveCancelHBox);
		saveCancelHBox.setAlignment(Pos.CENTER);
		saveCancelHBox.setSpacing(10);
		
		// > > > Save Button
		saveButton = new Button("Save");
		saveCancelHBox.getChildren().add(saveButton);
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(startTimeTextField.getText().matches("\\d+") && spawnIntervalTextField.getText().matches("\\d+") && amountPerSpawnTextField.getText().matches("\\d+") && totalTextField.getText().matches("\\d+")) {
					int startTime = Integer.parseInt(startTimeTextField.getText());
					int spawnInterval = Integer.parseInt(spawnIntervalTextField.getText());
					int amountPerSpawn = Integer.parseInt(amountPerSpawnTextField.getText());
					int total = Integer.parseInt(totalTextField.getText());
					boolean isReady = true;
					waveSetter.setEnemyInfo(enemy, startTime, spawnInterval, amountPerSpawn, total, isReady);
				}
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Invalid input");
					alert.setContentText("Please enter only numbers");
					alert.showAndWait();
				}
				stage.close();
			}
		});
		
		// > > > Cancel Button
		cancelButton = new Button("Cancel");
		saveCancelHBox.getChildren().add(cancelButton);
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

}
