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

    protected void addHelper(DirectionalValue a) {
        double[] result = Physics.combineComponents(Physics.getComponents(this.direction, this.value), Physics.getComponents(a.direction, a.value));
        result = Physics.fromComponents(result[0], result[1]);
        this.direction = (int) result[0];
        this.value = result[1];
    }

}
