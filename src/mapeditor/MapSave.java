package mapeditor;

import java.io.Serializable;
import java.util.HashMap;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import server.engine.state.map.tile.Tile;
import shared.lists.Team;
import shared.lists.TileList;

public class MapSave implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String mapName;
	private HashMap<Team, int[]> teamSpawns;
	private int mapWidth;
	private int mapHeight;
	private Tile[][] mapTiles;
	private HashMap<String, ZoneSettings> zoneMap;
	private HashMap<String, TileList> doors;
	
	public MapSave(String mapName, HashMap<Team, int[]> teamSpawns, int mapWidth, int mapHeight, Tile[][] mapTiles, HashMap<String, ZoneSettings> zoneMap, HashMap<String, TileList> doors) {
		this.mapName = mapName;
		this.teamSpawns = teamSpawns;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.mapTiles = mapTiles;
		this.zoneMap = zoneMap;
		this.doors = doors;
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
	
	public HashMap<String, TileList> getDoors() {
		return this.doors;
	}

}
