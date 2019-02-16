package server.engine.state.map;

import java.util.HashMap;
import java.util.LinkedHashSet;

import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.lists.MapList;
import shared.lists.Teams;

public class GameMap {
    protected final int DEFAULT_X_DIM;
    protected final int DEFAULT_Y_DIM;
    protected Tile[][] tileMap;
    protected HashMap<Teams, Location> teamSpawns;
    protected LinkedHashSet<Location> enemySpawns;
    protected LinkedHashSet<Round> rounds;
    protected MapList mapName;

    GameMap(int xDim, int yDim, Tile[][] tileMap, HashMap<Teams, Location> teamSpawns, LinkedHashSet<Location> enemySpawns, LinkedHashSet<Round> rounds,
            MapList mapName) {
        this.DEFAULT_X_DIM = xDim;
        this.DEFAULT_Y_DIM = yDim;
        this.tileMap = tileMap;
        this.teamSpawns = teamSpawns;
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

    public Tile[][] getTileMap() {
        return tileMap;
    }

    public HashMap<Teams, Location> getTeamSpawns() {
        return teamSpawns;
    }

    public void setPlayerSpawns(HashMap<Teams, Location> teamSpawns) {
        this.teamSpawns = teamSpawns;
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
