package data.map;

import data.Location;

public class Tile {	
	public static final int TILE_SIZE = 16;
	
	protected TileType tileType;
	protected TileList tileID;
	
	Tile(TileList tileID, TileType tileType) {
		this.tileType = tileType;
		this.tileID = tileID;
	}
	
	public int getID() {
		return tileID.getID();
	}
	
	public TileType getType() {
		return tileType;
	}
	
	public static Location tileToLocation(int x, int y) {
		int tileMid = TILE_SIZE/2;
		return new Location((x*TILE_SIZE)+tileMid, (y*TILE_SIZE)+tileMid);
	}
	
}
