package client;

import client.data.entity.GameView;
import client.data.ItemView;
import client.data.entity.PlayerView;
import client.data.TileView;
import data.Pose;
import data.item.ItemList;
import data.item.weapon.gun.AmmoList;
import data.map.GameMap;
import data.map.Meadow;
import data.map.tile.Tile;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestRenderer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        primaryStage.show();
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
        PlayerView playerView = new PlayerView(new Pose(30, 30, 30), 1, 20, 20, playerItems, 0, 0, "Bob", new LinkedHashMap<AmmoList, Integer>(), 0);
        playersView.add(playerView);
        GameView view1 = new GameView(playersView, new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), tileMapView);
        playersView = new LinkedHashSet<>();
        playerView = new PlayerView(new Pose(90, 90, 210), 1, 20, 20, playerItems, 0, 0, "Bob", new LinkedHashMap<AmmoList, Integer>(), 0);
        playersView.add(playerView);
        GameView view2 = new GameView(playersView, new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), tileMapView);

        // Set up renderer
        GameRenderer rend = new GameRenderer(primaryStage, view1, 0);
        rend.run();

        // Alternate between the 2 gameviews on a timer
        final AtomicBoolean a = new AtomicBoolean(true);
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (a.get()) {
                    rend.updateGameView(view1);
                    a.set(false);
                } else {
                    rend.updateGameView(view2);
                    a.set(true);
                }

            }

        }, 0, 50);
    }
}
