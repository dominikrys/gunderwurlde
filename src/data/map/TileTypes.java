package data.map;

public enum TileTypes {
    // Add to this for more tiles
    EMPTY(0), GRASS(1), WOOD(2),
    ;
    //TODO: potentially remove below methods as these integers are never used directly - this just causes confusion
    //+ remove integers from above declaration
    private int tileID;

    private TileTypes(int id) {
        this.tileID = id;
    }

    public int getID() {
        return tileID;
    }
}
