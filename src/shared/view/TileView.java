package shared.view;

import java.io.Serializable;

import shared.Constants;
import shared.lists.TileState;
import shared.lists.TileType;

public class TileView implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int TILE_SIZE = Constants.TILE_SIZE;

    // Type of tile
    protected TileType tileType;

    // State of tile, e.g. solid or passable
    protected TileState tileState;

    private boolean wasHit;

    public TileView(TileType tileType, TileState tileState) {
        this(tileType, tileState, false);
    }

    public TileView(TileType tileType, TileState tileState, boolean wasHit) {
        this.tileType = tileType;
        this.tileState = tileState;
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
