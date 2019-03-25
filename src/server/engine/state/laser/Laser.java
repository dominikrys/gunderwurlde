package server.engine.state.laser;

import shared.Line;
import shared.Location;

public class Laser extends Line {
    protected double size;
    protected int damage;

    public Laser(Location start, Location end, double size, int damage) {
        super(start, end);
        this.size = size;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

}
