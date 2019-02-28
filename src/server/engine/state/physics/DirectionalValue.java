package server.engine.state.physics;

public abstract class DirectionalValue {
    protected int direction;
    protected double value;

    public DirectionalValue(int direction, double value) {
        this.direction = direction;
        this.value = value;
    }

    public DirectionalValue() {
        this.direction = 0;
        this.value = 0;
    }

    public int getDirection() {
        return direction;
    }

}
