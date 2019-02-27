package server.engine.state.map.tile;

public class Door {
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
