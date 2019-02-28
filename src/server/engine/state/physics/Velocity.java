package server.engine.state.physics;

public final class Velocity extends DirectionalValue {

    public Velocity(int direction, double speed) {
        super(direction, speed);
    }

    public Velocity() {
        super();
    }

    public double getSpeed() {
        return value;
    }

}
