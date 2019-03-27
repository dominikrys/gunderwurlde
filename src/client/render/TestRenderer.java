package client.render;

import client.Settings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.engine.state.map.GameMap;
import server.engine.state.map.MapReader;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.*;
import shared.view.GameView;
import shared.view.GunView;
import shared.view.ItemView;
import shared.view.TileView;
import shared.view.entity.PlayerView;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class TestRenderer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Settings settings = new Settings();
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(new VBox(), settings.getScreenWidth(), settings.getScreenHeight()));
        primaryStage.show();
        GameMap map = MapReader.readMap(MapList.MEADOW);
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
        playerItems.add(new GunView(ItemList.PISTOL, AmmoList.BASIC_AMMO, 12, 12, true, 3000));
        EnumMap<AmmoList, Integer> playerAmmo = new EnumMap<AmmoList, Integer>(AmmoList.class);
        playerAmmo.put(AmmoList.BASIC_AMMO, 36);
        PlayerView playerView = new PlayerView(new Pose(30, 30, 30), 1, 20, 20, playerItems, 0, 0, "Bob", playerAmmo, 0, Team.RED, false, EntityStatus.NONE,
                ActionList.NONE, false, false, false);
        playersView.add(playerView);
        GameView view1 = new GameView(playersView, new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(),
                new LinkedHashSet<>(), new LinkedHashMap<>(), MapList.MEADOW, Team.NONE);
        view1.setTileMap(tileMapView);
        playersView = new LinkedHashSet<>();
        playerView = new PlayerView(new Pose(90, 90, 210), 1, 20, 20, playerItems, 0, 0, "Bob", new EnumMap<AmmoList, Integer>(AmmoList.class), 0, Team.RED,
                false, EntityStatus.NONE, ActionList.NONE, false, false, false);
        playersView.add(playerView);
        GameView view2 = new GameView(playersView, new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>(),
                new LinkedHashSet<>(), new LinkedHashMap<>(), MapList.MEADOW, Team.NONE);
        view2.setTileMap(tileMapView);

        // Set up renderer
        // GameRenderer rend = new GameRenderer(primaryStage, view1, 0, settings);
//        rend.run();
//
//        // Alternate between the 2 gameviews on a timer
//        final AtomicBoolean a = new AtomicBoolean(true);
//        Timer t = new Timer();
//        t.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                if (a.get()) {
//                    rend.updateGameView(view1);
//                    a.set(false);
//                } else {
//                    rend.updateGameView(view2);
//                    a.set(true);
//                }
//
//            }
//
//        }, 0, 50);
//    }
    }
}

