package server.engine.state.map.tile;

import java.io.Serializable;

public class Door implements Serializable{
	private static final long serialVersionUID = 1L;
    protected Tile tileToReturn;
    protected int killsLeft;

    public Door(Tile tileToReturn, int killsLeft) {
        this.tileToReturn = tileToReturn;
        this.killsLeft = killsLeft;
    }

    public Tile getTileToReturn() {
        return tileToReturn;
    }

    public boolean isOpen() {
        return (killsLeft <= 0);
    }

    public void entityKilled() {
        killsLeft--;
    }

}
