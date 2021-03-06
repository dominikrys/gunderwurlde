package server.engine.state.map;

import java.util.EnumMap;
import java.util.LinkedHashMap;

import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.lists.MapList;
import shared.lists.Team;

/**
 * Class for the Game Map with the Tiles and Zones.
 * 
 * @author Richard
 *
 */
public class GameMap {
    protected final int DEFAULT_X_DIM;
    protected final int DEFAULT_Y_DIM;
    protected Tile[][] tileMap;
    protected EnumMap<Team, Location> teamSpawns;
    protected MapList mapName;
    protected LinkedHashMap<Integer, Zone> zones;

    /**
     * Construct a Game map with the given parameters.
     * 
     * @param xDim
     * @param yDim
     * @param tileMap
     * @param teamSpawns
     * @param zones
     * @param mapName
     */
    GameMap(int xDim, int yDim, Tile[][] tileMap, EnumMap<Team, Location> teamSpawns, LinkedHashMap<Integer, Zone> zones, MapList mapName) {
        this.DEFAULT_X_DIM = xDim;
        this.DEFAULT_Y_DIM = yDim;
        this.tileMap = tileMap;
        this.teamSpawns = teamSpawns;
        this.zones = zones;
        this.mapName = mapName;

        for (Zone z : zones.values()) {
            int zoneID = z.getId();
            for (int[] trigger : z.getTriggers()) {
                this.tileMap[trigger[0]][trigger[1]].addTrigger(zoneID);
            }
        }
    }
    
    public MapList getMapName( ) {
        return mapName;
    }

    public LinkedHashMap<Integer, Zone> getZones() {
        return zones;
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

    public EnumMap<Team, Location> getTeamSpawns() {
        return teamSpawns;
    }

    public void setPlayerSpawns(EnumMap<Team, Location> teamSpawns) {
        this.teamSpawns = teamSpawns;
    }

    public void setTileMap(Tile[][] tileMap) {
        this.tileMap = tileMap;
    }

}
