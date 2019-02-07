package client.data;

import java.io.Serializable;

import data.Constants;
import data.entity.EntityList;
import data.map.tile.TileState;
import data.map.tile.TileTypes;

public class TileView implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int TILE_SIZE = Constants.TILE_SIZE;

    // Type of tile
    protected TileTypes tileType;

    // State of tile, e.g. solid or passable
    protected TileState tileState;

    protected EntityList entityName;

    public TileView(TileTypes tileType, TileState tileState) {
        this.tileType = tileType;
        this.tileState = tileState;
        this.entityName = EntityList.DEFAULT;
    }

    public TileTypes getTileType() {
        return tileType;
    }

    public TileState getTileState() {
        return tileState;
    }


}
