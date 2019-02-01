package client.data;

import data.Constants;
import data.map.tile.TileState;
import data.map.tile.TileTypes;

public class TileView {
    public static final int TILE_SIZE = Constants.TILE_SIZE;

    // Type of tile
    protected TileTypes tileType;

    // State of tile, e.g. solid or passable
    protected TileState tileState;

    protected String pathToGraphic;

    public TileView(TileTypes tileType, TileState tileState) {
        this.tileType = tileType;
        this.tileState = tileState;
        this.pathToGraphic = Constants.DEFAULT_GRAPHIC_PATH;
    }

    public TileTypes getTileType() {
        return tileType;
    }

    public TileState getTileState() {
        return tileState;
    }

    public String getPathToGraphic() {
        switch (tileType) {
        case GRASS:
            return "file:assets/img/tiles/grass.png";
        case WOOD:
            return "file:assets/img/tiles/wood.png";
        default:
            return pathToGraphic;
        }
    }

}
