package data.map;

import java.util.LinkedHashSet;
import java.util.TreeSet;

import data.Location;
import data.entity.enemy.Enemy;
import data.entity.enemy.Zombie;
import data.map.GameMap;
import data.map.tile.Tile;
import data.map.tile.TileState;
import data.map.tile.TileTypes;

public class MeadowTest extends GameMap {
    public static final int DEFAULT_X_DIM = 32;
    public static final int DEFAULT_Y_DIM = 22;

    public MeadowTest() {
        super(DEFAULT_X_DIM, DEFAULT_Y_DIM, generateTileMap(), generatePlayerSpawns(), generateEnemySpawns(), generateRounds(), MapList.MEADOW);
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

    private static LinkedHashSet<Location> generatePlayerSpawns() {
        LinkedHashSet<Location> playerSpawns = new LinkedHashSet<Location>();
        playerSpawns.add(Tile.tileToLocation(15, 15));
        return playerSpawns;
    }

    private static LinkedHashSet<Location> generateEnemySpawns() {
        LinkedHashSet<Location> enemySpawns = new LinkedHashSet<Location>();
        enemySpawns.add(Tile.tileToLocation(25, 10));
        return enemySpawns;
    }

    private static LinkedHashSet<Round> generateRounds() {
        LinkedHashSet<Round> rounds = new LinkedHashSet<>();
        TreeSet<Wave> waves = new TreeSet<>();

        // Simple rounds just for testings
        // Round 1
        Enemy enemyType = new Zombie();
        waves.add(new Wave(0, 0, enemyType, 1, 1));
        Round round = new Round(waves, false);
        rounds.add(round);


        return rounds;
    }

}
