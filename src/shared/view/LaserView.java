package shared.view;

import java.io.Serializable;

import shared.Location;

public class LaserView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final Location start;
    protected final Location end;
    protected final double width;

    public LaserView(Location start, Location end, double width) {
        this.start = start;
        this.end = end;
        this.width = width;
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }

    public double getWidth() {
        return width;
    }

}
