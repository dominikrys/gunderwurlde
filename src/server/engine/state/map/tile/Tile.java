package server.engine.state.map.tile;

import java.util.HashSet;
import java.util.LinkedHashSet;

import shared.Constants;
import shared.Location;
import shared.lists.TileState;
import shared.lists.TileTypes;

public class Tile {
    public static final int TILE_SIZE = Constants.TILE_SIZE;

    // Type of tile
    protected TileTypes tileType;

    // State of tile, e.g. solid or passable
    protected TileState tileState;
    
    protected HashSet<Integer> itemDropsOnTile;
    protected HashSet<Integer> enemiesOnTile;
    protected HashSet<Integer> playersOnTile;
    protected LinkedHashSet<Integer> zoneTriggers;

    public Tile(TileTypes tileType, TileState tileState) {
        this.tileType = tileType;
        this.tileState = tileState;
        this.itemDropsOnTile = new HashSet<>();
        this.enemiesOnTile = new HashSet<>();
        this.playersOnTile = new HashSet<>();
        this.zoneTriggers = new LinkedHashSet<>();
    }
    
    public void addTrigger(int zoneID) {
        zoneTriggers.add(zoneID);
    }

    public boolean hasTriggers() {
        return !zoneTriggers.isEmpty();
    }

    public LinkedHashSet<Integer> triggered() {
        LinkedHashSet<Integer> triggers = zoneTriggers;
        zoneTriggers = new LinkedHashSet<>();
        return triggers;
    }

    public void clearOnTile() {
        this.itemDropsOnTile = new HashSet<>();
        this.enemiesOnTile = new HashSet<>();
        this.playersOnTile = new HashSet<>();
    }

    public HashSet<Integer> getPlayersOnTile() {
        return playersOnTile;
    }

    public void addPlayer(int playerID) {
        playersOnTile.add(playerID);
    }

    public boolean removePlayer(int playerID) {
        return playersOnTile.remove(playerID);
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
        int[] i = { ((int) (location.getX() - 1) / TILE_SIZE), ((int) (location.getY() - 1) / TILE_SIZE) };
        return i;
    }

    public Tile getCopy() {
        return new Tile(this.tileType, this.tileState);
    }

}
