package server.engine.state.map.tile;

import shared.Constants;
import shared.Location;
import shared.lists.TileState;
import shared.lists.TileType;

import java.util.LinkedHashSet;

public class Tile {
    public static final int TILE_SIZE = Constants.TILE_SIZE;
    public static final double DEFAULT_DENSITY = 200;

    // Type of tile
    protected TileType tileType;

    // State of tile, e.g. solid or passable
    protected TileState tileState;

    protected LinkedHashSet<Integer> itemDropsOnTile;
    protected LinkedHashSet<Integer> enemiesOnTile;
    protected LinkedHashSet<Integer> playersOnTile;
    protected LinkedHashSet<Integer> zoneTriggers;
    protected double frictionCoefficient;
    protected double bounceCoefficient;

    public Tile(TileType tileType, TileState tileState, double value) {
        this.tileType = tileType;
        this.tileState = tileState;
        this.itemDropsOnTile = new LinkedHashSet<>();
        this.enemiesOnTile = new LinkedHashSet<>();
        this.playersOnTile = new LinkedHashSet<>();
        this.zoneTriggers = new LinkedHashSet<>();
        if (tileState == TileState.SOLID) {
            this.bounceCoefficient = value;
            this.frictionCoefficient = 0;
        } else {
            this.frictionCoefficient = value;
            this.bounceCoefficient = 0;
        }
    }

    public void addTrigger(int zoneID) {
        zoneTriggers.add(zoneID);
    }

    public boolean hasTriggers() {
        return !zoneTriggers.isEmpty();
    }

    public LinkedHashSet<Integer> triggered() {
        return zoneTriggers;
    }

    public boolean removeTrigger(int zoneID) {
        return zoneTriggers.remove(zoneID);
    }

    public void clearOnTile() {
        this.itemDropsOnTile = new LinkedHashSet<>();
        this.enemiesOnTile = new LinkedHashSet<>();
        this.playersOnTile = new LinkedHashSet<>();
    }

    public LinkedHashSet<Integer> getPlayersOnTile() {
        return playersOnTile;
    }

    public void addPlayer(int playerID) {
        playersOnTile.add(playerID);
    }

    public boolean removePlayer(int playerID) {
        return playersOnTile.remove(playerID);
    }

    public LinkedHashSet<Integer> getItemDropsOnTile() {
        return itemDropsOnTile;
    }

    public void addItemDrop(int itemID) {
        this.itemDropsOnTile.add(itemID);
    }

    public boolean removeItemDrop(int itemID) {
        return itemDropsOnTile.remove(itemID);
    }

    public LinkedHashSet<Integer> getEnemiesOnTile() {
        return enemiesOnTile;
    }

    public void addEnemy(int enemyID) {
        this.enemiesOnTile.add(enemyID);
    }

    public boolean removeEnemy(int enemyID) {
        return enemiesOnTile.remove(enemyID);
    }

    public TileType getType() {
        return tileType;
    }

    public TileState getState() {
        return tileState;
    }

    public double getFrictionCoefficient() {
        return frictionCoefficient;
    }

    public double getBounceCoefficient() {
        return bounceCoefficient;
    }

    public static Location tileToLocation(int x, int y) {
        int tileMid = TILE_SIZE / 2;
        return new Location((x * TILE_SIZE) + tileMid, (y * TILE_SIZE) + tileMid);
    }

    public static int[] locationToTile(Location location) {
        int[] i = new int[2];
        try {
            i[0] = ((int) (location.getX() - 1) / TILE_SIZE);
            i[1] = ((int) (location.getY() - 1) / TILE_SIZE);
        } catch (Exception e) {
            int[] j = {5, 5};
            return j;
        }
        return i;
    }

    public Tile getCopy() {
        return new Tile(this.tileType, this.tileState, this.bounceCoefficient + this.frictionCoefficient);
    }

    public double getDensity() {
        return DEFAULT_DENSITY;
    }

    @Override
    public String toString() {
        if (tileType == TileType.WOOD) {
            return "W";
        } else if (tileType == TileType.GRASS) {
            return "G";
        } else if (tileType == TileType.DOOR) {
            return "D";
        }
        return "New state added";
    }

}
