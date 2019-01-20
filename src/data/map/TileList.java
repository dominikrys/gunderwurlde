package data.map;

public enum TileList {
	//Add to this for more tiles
	GRASS(0), WOOD(1);
	
	private int tileID;
	
	private TileList(int id) {
		this.tileID = id;
	}
	
	public int getID() {
		return tileID;
	}
}
