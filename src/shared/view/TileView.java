package shared.view;

import shared.Constants;
import shared.lists.EntityList;
import shared.lists.TileState;
import shared.lists.TileType;

import java.io.Serializable;

public class TileView implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int TILE_SIZE = Constants.TILE_SIZE;

    // Type of tile
    protected TileType tileType;

    // State of tile, e.g. solid or passable
    protected TileState tileState;

    protected EntityList entityName; // TODO is this needed?

    private boolean wasHit;

    public TileView(TileType tileType, TileState tileState) {
        this(tileType, tileState, false);
    }

    public TileView(TileType tileType, TileState tileState, boolean wasHit) {
        this.tileType = tileType;
        this.tileState = tileState;
        this.entityName = EntityList.DEFAULT;
        this.wasHit = wasHit;
    }

    public TileType getTileType() {
        return tileType;
    }

    public TileState getTileState() {
        return tileState;
    }

    public boolean wasHit() {
        return wasHit;
    }
}
