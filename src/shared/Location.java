package shared;

import java.io.Serializable;

public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    protected double x;
    protected double y;

    public Location(double x, double y) {
        assert (x >= 0 && y >= 0);
        this.x = x;
        this.y = y;
    }

    public boolean setX(double x) {
        if (x < 0)
            return false;
        else {
            this.x = x;
            return true;
        }
    }

    public boolean setY(double y) {
        if (y < 0)
            return false;
        else {
            this.y = y;
            return true;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public static Location calculateNewLocation(Location l, int direction, double distanceMoved) {
        double directionInRadians = Math.toRadians(direction);
        double x_dist = (double) distanceMoved * Math.cos(directionInRadians);
        double y_dist = (double) distanceMoved * Math.sin(directionInRadians);

        // x_dist = Math.round(x_dist);
        // y_dist = Math.round(y_dist);

        double x = l.getX() + x_dist;
        double y = l.getY() + y_dist;

        if (x < 0) x = 0;
        if (y < 0) y = 0;

        return new Location(x, y);
    }
}
