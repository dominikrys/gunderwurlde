package shared.view;

import java.io.Serializable;

import shared.Constants;
import shared.lists.TileList;
import shared.lists.TileState;

public class TileView implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int TILE_SIZE = Constants.TILE_SIZE;

    // Type of tileList
    protected TileList tileList;

    // State of tileList, e.g. solid or passable
    protected TileState tileState;

    private boolean wasHit;

    public TileView(TileList tileList, TileState tileState) {
        this(tileList, tileState, false);
    }

    public TileView(TileList tileList, TileState tileState, boolean wasHit) {
        this.tileList = tileList;
        this.tileState = tileState;
        this.wasHit = wasHit;
    }

    public TileList getTileList() {
        return tileList;
    }

    public TileState getTileState() {
        return tileState;
    }

    public boolean wasHit() {
        return wasHit;
    }
}
