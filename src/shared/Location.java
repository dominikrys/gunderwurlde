package shared;

import java.io.Serializable;

public class Location implements Serializable {
    protected int x;
    protected int y;

    public Location(int x, int y) {
        assert (x >= 0 && y >= 0);
        this.x = x;
        this.y = y;
    }

    public boolean setX(int x) {
        if (x < 0)
            return false;
        else {
            this.x = x;
            return true;
        }
    }

    public boolean setY(int y) {
        if (y < 0)
            return false;
        else {
            this.y = y;
            return true;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static Location calculateNewLocation(Location l, int direction, int distance) {
        double directionInRadians = Math.toRadians(direction);
        double x_dist = (double) distance * Math.sin(directionInRadians);
        double y_dist = (double) -distance * Math.cos(directionInRadians);

        x_dist = Math.round(x_dist);
        y_dist = Math.round(y_dist);

        int x = l.getX() + (int) x_dist;
        int y = l.getY() + (int) y_dist;

        if (x < 0) x = 0;
        if (y < 0) y = 0;

        return new Location(x, y);
    }
}
