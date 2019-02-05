package data.map;

import java.util.LinkedHashSet;

import data.Location;
import data.map.tile.Tile;

public abstract class GameMap {
    protected final int DEFAULT_X_DIM;
    protected final int DEFAULT_Y_DIM;
    protected static Tile[][] tileMap;
    protected LinkedHashSet<Location> playerSpawns;
    protected LinkedHashSet<Location> enemySpawns;
    protected LinkedHashSet<Round> rounds;
    protected MapList mapName;

    GameMap(int xDim, int yDim, Tile[][] tileMap, LinkedHashSet<Location> playerSpawns, LinkedHashSet<Location> enemySpawns, LinkedHashSet<Round> rounds, MapList mapName) {
        this.DEFAULT_X_DIM = xDim;
        this.DEFAULT_Y_DIM = yDim;
        this.tileMap = tileMap;
        this.playerSpawns = playerSpawns;
        this.enemySpawns = enemySpawns;
        this.rounds = rounds;
        this.mapName = mapName;
    }
    
    public MapList getMapName( ) {
        return mapName;
    }

    public LinkedHashSet<Round> getRounds() {
        return rounds;
    }

    public int getXDim() {
        return DEFAULT_X_DIM;
    }

    public int getYDim() {
        return DEFAULT_Y_DIM;
    }

    public static Tile[][] getTileMap() {
        return tileMap;
    }

    public LinkedHashSet<Location> getPlayerSpawns() {
        return playerSpawns;
    }

    public void setPlayerSpawns(LinkedHashSet<Location> playerSpawns) {
        this.playerSpawns = playerSpawns;
    }

    public LinkedHashSet<Location> getEnemySpawns() {
        return enemySpawns;
    }

    public void setEnemySpawns(LinkedHashSet<Location> enemySpawns) {
        this.enemySpawns = enemySpawns;
    }

    public void setTileMap(Tile[][] tileMap) {
        this.tileMap = tileMap;
    }

}
