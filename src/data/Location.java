package data;

public class Location {
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

        if (Math.abs(x_dist % 1) < 0.01) {
            x_dist = (int) (x_dist / 1);
        }

        if (Math.abs(y_dist % 1) < 0.01) {
            y_dist = (int) (y_dist / 1);
        }

        if (x_dist < 0)
            x_dist = Math.floor(x_dist);
        else
            x_dist = Math.ceil(x_dist);

        if (y_dist < 0)
            y_dist = Math.floor(y_dist);
        else
            y_dist = Math.ceil(y_dist);

        int x = l.getX() + (int) x_dist;
        int y = l.getY() + (int) y_dist;

        if (x < 0) x = 0;
        if (y < 0) y = 0;

        return new Location(x, y);
    }
}
