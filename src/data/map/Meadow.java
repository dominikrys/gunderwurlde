package data.map;

import data.Location;
import data.map.tile.Tile;
import data.map.tile.TileState;
import data.map.tile.TileTypes;

import java.util.ArrayList;

public class Meadow extends GameMap {
    public static final int DEFAULT_X_DIM = 32;
    public static final int DEFAULT_Y_DIM = 22;

    public Meadow() {
        super(DEFAULT_X_DIM, DEFAULT_Y_DIM, generateTileMap(), generatePlayerSpawns(), generateEnemySpawns());
    }

    private static Tile[][] generateTileMap() {
        Tile[][] tileMap = new Tile[DEFAULT_X_DIM][DEFAULT_Y_DIM];

        for (int x = 0; x < DEFAULT_X_DIM; x++) {
            tileMap[x][0] = new Tile(TileTypes.WOOD, TileState.SOLID);
            for (int y = 1; y < DEFAULT_Y_DIM; y++) {
                if (x == 0 || x == DEFAULT_X_DIM - 1)
                    tileMap[x][y] = new Tile(TileTypes.WOOD, TileState.SOLID);
                else
                    tileMap[x][y] = new Tile(TileTypes.GRASS, TileState.PASSABLE);
            }
            tileMap[x][DEFAULT_Y_DIM - 1] = new Tile(TileTypes.WOOD, TileState.SOLID);
        }
        tileMap[0][(DEFAULT_Y_DIM - 1) / 2] = new Tile(TileTypes.GRASS, TileState.PASSABLE);
        tileMap[DEFAULT_X_DIM - 1][(DEFAULT_Y_DIM - 1) / 2] = new Tile(TileTypes.GRASS, TileState.PASSABLE);
        return tileMap;
    }

    private static ArrayList<Location> generatePlayerSpawns() {
        ArrayList<Location> playerSpawns = new ArrayList<Location>();
        playerSpawns.add(Tile.tileToLocation(1, 1));
        playerSpawns.add(Tile.tileToLocation(1, DEFAULT_Y_DIM - 1));
        playerSpawns.add(Tile.tileToLocation(DEFAULT_X_DIM - 1, 1));
        playerSpawns.add(Tile.tileToLocation(DEFAULT_X_DIM - 1, DEFAULT_Y_DIM - 1));
        return playerSpawns;
    }

    private static ArrayList<Location> generateEnemySpawns() {
        ArrayList<Location> enemySpawns = new ArrayList<Location>();
        enemySpawns.add(Tile.tileToLocation((DEFAULT_X_DIM - 1) / 2, 0));
        enemySpawns.add(Tile.tileToLocation((DEFAULT_X_DIM - 1) / 2, DEFAULT_Y_DIM - 1));
        return enemySpawns;
    }

}
