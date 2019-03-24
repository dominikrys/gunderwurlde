package shared.view;

import java.io.Serializable;

import shared.Constants;
import shared.lists.TileState;
import shared.lists.Tile;

public class TileView implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int TILE_SIZE = Constants.TILE_SIZE;

    // Type of tile
    protected Tile tile;

    // State of tile, e.g. solid or passable
    protected TileState tileState;

    private boolean wasHit;

    public TileView(Tile tile, TileState tileState) {
        this(tile, tileState, false);
    }

    public TileView(Tile tile, TileState tileState, boolean wasHit) {
        this.tile = tile;
        this.tileState = tileState;
        this.wasHit = wasHit;
    }

    public Tile getTile() {
        return tile;
    }

    public TileState getTileState() {
        return tileState;
    }

    public boolean wasHit() {
        return wasHit;
    }
}
