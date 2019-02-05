package data.map.tile;

import java.util.HashSet;

import data.Constants;
import data.Location;

public class Tile {
    public static final int TILE_SIZE = Constants.TILE_SIZE;

    // Type of tile
    protected TileTypes tileType;

    // State of tile, e.g. solid or passable
    protected TileState tileState;
    
    protected HashSet<Integer> itemDropsOnTile;
    protected HashSet<Integer> enemiesOnTile;
    // TODO maybe players ontile needed?

    public Tile(TileTypes tileType, TileState tileState) {
        this.tileType = tileType;
        this.tileState = tileState;
        this.itemDropsOnTile = new HashSet<>();
        this.enemiesOnTile = new HashSet<>();      
    }
    
    public void clearOnTile() {
        this.itemDropsOnTile = new HashSet<>();
        this.enemiesOnTile = new HashSet<>();
    }

    public HashSet<Integer> getItemDropsOnTile() {
        return itemDropsOnTile;
    }

    public void addItemDrop(int itemID) {
        this.itemDropsOnTile.add(itemID);
    }
    
    public boolean removeItemDrop(int itemID) {
        return itemDropsOnTile.remove(itemID);
    }

    public HashSet<Integer> getEnemiesOnTile() {
        return enemiesOnTile;
    }

    public void addEnemy(int enemyID) {
        this.enemiesOnTile.add(enemyID);
    }
    
    public boolean removeEnemy(int enemyID) {
        return enemiesOnTile.remove(enemyID);
    }

    public TileTypes getType() {
        return tileType;
    }

    public TileState getState() {
        return tileState;
    }

    public static Location tileToLocation(int x, int y) {
        int tileMid = TILE_SIZE / 2;
        return new Location((x * TILE_SIZE) + tileMid, (y * TILE_SIZE) + tileMid);
    }

    public static int[] locationToTile(Location location) {
        int[] i = { ((location.getX() - 1) / TILE_SIZE), ((location.getY() - 1) / TILE_SIZE) };
        return i;
    }

}
