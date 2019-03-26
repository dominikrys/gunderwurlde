package mapeditor;

import java.io.Serializable;
import java.util.HashMap;

import server.engine.state.map.tile.Tile;
import shared.lists.Team;

public class MapSave implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String mapName;
	private HashMap<Team, int[]> teamSpawns;
	private int mapWidth;
	private int mapHeight;
	private Tile[][] mapTiles;
	private HashMap<String, ZoneSettings> zoneMap;
	
	public MapSave(String mapName, HashMap<Team, int[]> teamSpawns, int mapWidth, int mapHeight, Tile[][] mapTiles, HashMap<String, ZoneSettings> zoneMap) {
		this.mapName = mapName;
		this.teamSpawns = teamSpawns;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.mapTiles = mapTiles;
		this.zoneMap = zoneMap;
	}
	
	public String getMapName() {
		return this.mapName;
	}
	
	public HashMap<Team, int[]> getTeamSpawns() {
		return this.teamSpawns;
	}
	
	public int getMapWidth() {
		return this.mapWidth;
	}
	
	public int getMapHeight() {
		return this.mapHeight;
	}
	
	public Tile[][] getMapTiles() {
		return this.mapTiles;
	}
	
	public HashMap<String, ZoneSettings> getZoneMap() {
		return this.zoneMap;
	}

}
