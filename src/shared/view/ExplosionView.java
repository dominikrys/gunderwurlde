package shared.view;

import java.io.Serializable;

import shared.Location;

public class ExplosionView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final Location location;
    protected final double size;

    public ExplosionView(Location location, double size) {
        this.location = location;
        this.size = size;
    }

    public Location getLocation() {
        return location;
    }

    public double getSize() {
        return size;
    }

}
