package mapeditor;

import java.io.Serializable;
import java.util.HashMap;

import server.engine.state.map.tile.Tile;
import shared.lists.Team;
import shared.lists.TileList;

/**
 * MapSave class. Contains all important settings for a map to save.
 *
 * @author Mak Hong Lun Timothy
 */
public class MapSave implements Serializable{
	/**
     * serialVersionUID - serialVersionUID
     */
	private static final long serialVersionUID = 1L;
	/**
     * mapName - Name of map
     */
	private String mapName;
	/**
     * teamSpawns - HashMap containing all teams and their spawn coordinates
     */
	private HashMap<Team, int[]> teamSpawns;
	/**
     * mapWidth - Width of the map
     */
	private int mapWidth;
	/**
     * mapHeight - Height of the map
     */
	private int mapHeight;
	/**
     * mapTiles - 2D matrix representation of the map
     */
	private Tile[][] mapTiles;
	/**
     * zoneMap - HashMap contain zone names and their settings
     */
	private HashMap<String, ZoneSettings> zoneMap;
	/**
     * doors - HashMap containing info on all door tiles on the map
     */
	private HashMap<String, TileList> doors;
	
	/**
     * Constructor
     *
     * @param mapName Name of map
     * @param teamSpawns HashMap containing all teams and their spawn coordinates
     * @param mapWidth Width of the map
     * @param mapHeight Height of the map
     * @param mapTiles 2D matrix representation of the map
     * @param zoneMap HashMap contain zone names and their settings
     * @param doors HashMap containing info on all door tiles on the map
     */
	public MapSave(String mapName, HashMap<Team, int[]> teamSpawns, int mapWidth, int mapHeight, Tile[][] mapTiles, HashMap<String, ZoneSettings> zoneMap, HashMap<String, TileList> doors) {
		this.mapName = mapName;
		this.teamSpawns = teamSpawns;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.mapTiles = mapTiles;
		this.zoneMap = zoneMap;
		this.doors = doors;
	}
	
	/**
     * Getter for mapName
     */
	public String getMapName() {
		return this.mapName;
	}
	
	/**
     * Getter for teamSpawns
     * 
     * @return teamSpawns
     */
	public HashMap<Team, int[]> getTeamSpawns() {
		return this.teamSpawns;
	}
	
	/**
     * Getter for mapWidth
     * 
     * @return mapWidth
     */
	public int getMapWidth() {
		return this.mapWidth;
	}
	
	/**
     * Getter for mapHeight
     * 
     * @return mapHeight
     */
	public int getMapHeight() {
		return this.mapHeight;
	}
	
	/**
     * Getter for mapTiles
     * 
     * @return mapTiles
     */
	public Tile[][] getMapTiles() {
		return this.mapTiles;
	}
	
	/**
     * Getter for zoneMap
     * 
     * @return zoneMap
     */
	public HashMap<String, ZoneSettings> getZoneMap() {
		return this.zoneMap;
	}
	
	/**
     * Getter for doors
     * 
     * @return doors
     */
	public HashMap<String, TileList> getDoors() {
		return this.doors;
	}

}
