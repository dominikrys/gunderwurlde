package data.map;

public enum TileList {
	//Add to this for more tiles
	EMPTY(0), GRASS(1), WOOD(2), ;
	
	private int tileID;
	
	private TileList(int id) {
		this.tileID = id;
	}
	
	public int getID() {
		return tileID;
	}
}
