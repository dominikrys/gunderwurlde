package server.engine.state.physics;

public final class Impulse extends DirectionalValue {

    public Impulse(int direction, double impulse) {
        super(direction, impulse);
    }

    public double getImpulse() {
        return value;
    }

}
