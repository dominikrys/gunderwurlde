package server.engine.state.physics;

public final class Force extends DirectionalValue {

    public Force(int direction, double force) {
        super(direction, force);
    }

    public Force() {
        super();
    }

    public double getForce() {
        return value;
    }

}
