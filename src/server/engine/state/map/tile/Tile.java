package server.engine.state.map.tile;

import java.util.LinkedHashSet;

import shared.Constants;
import shared.Location;
import shared.lists.TileList;
import shared.lists.TileState;

public class Tile {
    public static final int TILE_SIZE = Constants.TILE_SIZE;
    public static final double DEFAULT_DENSITY = 200;

    // Type of tileList
    protected TileList tileList;

    // State of tileList, e.g. solid or passable
    protected TileState tileState;

    protected LinkedHashSet<Integer> itemDropsOnTile;
    protected LinkedHashSet<Integer> enemiesOnTile;
    protected LinkedHashSet<Integer> playersOnTile;
    protected LinkedHashSet<Integer> entitiesOnTile;
    protected LinkedHashSet<Integer> zoneTriggers;
    protected double frictionCoefficient;
    protected double bounceCoefficient;
    protected double density;

    public Tile(TileList tileList, TileState tileState, double frictionCoefficient, double bounceCoefficient, double density) {
        this.tileList = tileList;
        this.tileState = tileState;
        this.itemDropsOnTile = new LinkedHashSet<>();
        this.enemiesOnTile = new LinkedHashSet<>();
        this.playersOnTile = new LinkedHashSet<>();
        this.entitiesOnTile = new LinkedHashSet<>();
        this.zoneTriggers = new LinkedHashSet<>();
        this.bounceCoefficient = bounceCoefficient;
        this.frictionCoefficient = frictionCoefficient;
        this.density = density;
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
        entitiesOnTile.add(playerID);
    }

    public boolean removePlayer(int playerID) {
        if (playersOnTile.remove(playerID)) {
            entitiesOnTile.remove(playerID);
            return true;
        }
        return false;
    }
    
    public LinkedHashSet<Integer> getEnemiesOnTile() {
        return enemiesOnTile;
    }

    public void addEnemy(int enemyID) {
        enemiesOnTile.add(enemyID);
        entitiesOnTile.add(enemyID);
    }
    
    public boolean removeEnemy(int enemyID) {
        if (enemiesOnTile.remove(enemyID)) {
            entitiesOnTile.remove(enemyID);
            return true;
        }
        return false;
    }

    public LinkedHashSet<Integer> getEntitiesOnTile() {
        return entitiesOnTile;
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
    
    public TileList getType() {
        return tileList;
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
        return new Tile(this.tileList, this.tileState, this.frictionCoefficient, this.bounceCoefficient, this.density);
    }

    public double getDensity() {
        return DEFAULT_DENSITY;
    }

    @Override
    public String toString() {
        if (tileList == TileList.WOOD) {
            return "W";
        } else if (tileList == TileList.GRASS) {
            return "G";
        } else if (tileList == TileList.DOOR) {
            return "D";
        }
        return "New state added";
    }

}
