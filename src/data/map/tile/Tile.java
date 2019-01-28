package data.map.tile;

import data.Constants;
import data.Location;

public class Tile {
    public static final int TILE_SIZE = Constants.TILE_SIZE;

    // Type of tile
    protected TileTypes tileType;

    // State of file - solid or not
    protected TileState tileState;

    public Tile(TileTypes tileType, TileState tileState) {
        this.tileType = tileType;
        this.tileState = tileState;
    }

    public TileTypes getType() {
        return tileType;
    }

    public TileState getState() {
        return tileState;
    }

    public static Location tileToLocation(int x, int y) {
        int tileMid = TILE_SIZE / 2;
        return new Location((x * TILE_SIZE) + tileMid, (y * TILE_SIZE) + tileMid);
    }

    public static int[] locationToTile(Location location) {
        int[] i = { ((location.getX() - 1) / TILE_SIZE), ((location.getY() - 1) / TILE_SIZE) };
        return i;
    }

}
