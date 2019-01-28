package data.map;

import java.util.ArrayList;

import data.Location;
import data.map.tile.Tile;

public abstract class GameMap {
    protected final int DEFAULT_X_DIM;
    protected final int DEFAULT_Y_DIM;
    protected Tile[][] tileMap;
    protected ArrayList<Location> playerSpawns;
    protected ArrayList<Location> enemySpawns;

    GameMap(int xDim, int yDim, Tile[][] tileMap, ArrayList<Location> playerSpawns, ArrayList<Location> enemySpawns) {
        this.DEFAULT_X_DIM = xDim;
        this.DEFAULT_Y_DIM = yDim;
        this.tileMap = tileMap;
        this.playerSpawns = playerSpawns;
        this.enemySpawns = enemySpawns;
    }

    public int getXDim() {
        return DEFAULT_X_DIM;
    }

    public int getYDim() {
        return DEFAULT_Y_DIM;
    }

    public Tile[][] getTileMap() {
        return tileMap;
    }

    public void setTile(Tile tile, int x, int y) { // Used for dynamically changing tiles
        tileMap[x][y] = tile;
    }

    public ArrayList<Location> getPlayerSpawns() {
        return playerSpawns;
    }

    public void setPlayerSpawns(ArrayList<Location> playerSpawns) {
        this.playerSpawns = playerSpawns;
    }

    public ArrayList<Location> getEnemySpawns() {
        return enemySpawns;
    }

    public void setEnemySpawns(ArrayList<Location> enemySpawns) {
        this.enemySpawns = enemySpawns;
    }

}
