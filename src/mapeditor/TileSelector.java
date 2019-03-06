package mapeditor;

import java.util.HashMap;
import java.util.Map;

import javax.naming.TimeLimitExceededException;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.lists.EntityList;
import shared.lists.TileTypes;

public class TileSelector {
	
	private MapEditor mapEditor;
	private int tileX;
	private int tileY;
	private HashMap<TileTypes, Image> tileSprite;
	private Stage stage;
	private StackPane root;
	private Scene scene;
	private HBox hBox;
	private ImageView tileImageView;
	private VBox vBox;
	private ComboBox<String> tileMenu;
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
	
	public TileSelector(MapEditor mapEditor, int tileX, int tileY) {
		this.mapEditor = mapEditor;
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileSprite = mapEditor.getTileSprite();
		this.init();
	}
	
	public void init() {
		stage = new Stage();
		stage.setTitle("Tile Selector");
		stage.setResizable(false);
		stage.setFullScreen(false);
		stage.centerOnScreen();
		stage.initModality(Modality.APPLICATION_MODAL);
		
		// root
		root = new StackPane();
		scene = new Scene(root, 500, 500);
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
		for(TileTypes key : tileSprite.keySet()) {
			tileMenu.getItems().add(key.toString());
		}
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
		
		changeTileSelection();
		
		stage.show();
		
		stage.setOnCloseRequest(we -> {
			stage.close();
        });
	}
	
	private void changeTileSelection() {
		for(Map.Entry<TileTypes, Image> entry : tileSprite.entrySet()) {
			if(entry.getKey().toString().equals(tileMenu.getValue())) {
				tileImageView.setImage(tileSprite.get(entry.getKey()));
				tileTypeLabel2.setText(entry.getKey().toString());
				//tileStateLabel2.setText(entry.getKey().getEntityListName().get);
				//frictionLabel2.setText(Double.toString(frictionCoefficient));
				//bounceLabel2.setText(Double.toString(bounceCoefficient));
			}
		}
	}

}
