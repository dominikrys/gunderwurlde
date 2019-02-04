package client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import client.data.GameView;
import client.data.ItemView;
import client.data.PlayerView;
import client.data.TileView;
import data.Pose;
import data.entity.item.ItemList;
import data.entity.item.weapon.gun.AmmoList;
import data.map.GameMap;
import data.map.Meadow;
import data.map.tile.Tile;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestRenderer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        Renderer rend = new Renderer(primaryStage);
        GameMap map = new Meadow();
        int xDim = map.getXDim();
        int yDim = map.getYDim();
        TileView[][] tileMapView = new TileView[xDim][yDim];
        Tile[][] tileMap = map.getTileMap();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                Tile tile = tileMap[x][y];
                tileMapView[x][y] = new TileView(tile.getType(), tile.getState());
            }
        }
        LinkedHashSet<PlayerView> playersView = new LinkedHashSet<>();
        ArrayList<ItemView> playerItems = new ArrayList<>();
        playerItems.add(new ItemView(ItemList.PISTOL, AmmoList.BASIC_AMMO, 12, 12));
        PlayerView playerView = new PlayerView(new Pose(30,30,30), 1, 20, 20, playerItems, 0, 0, "Bob", new LinkedHashMap<AmmoList,Integer>(), 0);
        playersView.add(playerView);
        GameView view1 = new GameView(playersView, new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), tileMapView);
        playersView = new LinkedHashSet<>();
        playerView = new PlayerView(new Pose(90,90,210), 1, 20, 20, playerItems, 0, 0, "Bob", new LinkedHashMap<AmmoList,Integer>(), 0);
        playersView.add(playerView);
        GameView view2 = new GameView(playersView, new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), tileMapView);
        
        for (int i=0;i<20;i++) {
            rend.renderGameView(view1, 0);
            //Thread.sleep(500);
            rend.renderGameView(view2, 0);
            //Thread.sleep(500);
        }
        
    }

}
