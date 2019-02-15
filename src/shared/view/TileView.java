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

    protected EntityList entityName; // TODO is this needed?

    private boolean wasHit;

    public TileView(TileTypes tileType, TileState tileState) {
        this(tileType, tileState, false);
    }

    public TileView(TileTypes tileType, TileState tileState, boolean wasHit) {
        this.tileType = tileType;
        this.tileState = tileState;
        this.entityName = EntityList.DEFAULT;
        this.wasHit = wasHit;
    }

    public TileTypes getTileType() {
        return tileType;
    }

    public TileState getTileState() {
        return tileState;
    }

    public boolean wasHit() {
        return wasHit;
    }
}
