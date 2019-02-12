package shared.view;

import java.io.Serializable;

import shared.Constants;
import shared.lists.EntityList;
import shared.lists.TileState;
import shared.lists.TileTypes;

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
