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

        double x = l.getX() + x_dist;
        double y = l.getY() + y_dist;

        if (x < 0) x = 0;
        if (y < 0) y = 0;

        return new Location(x, y);
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Pose or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Pose)) {
            return false;
        }

        // typecast o to Pose so that we can compare data members
        Pose c = (Pose) o;

        // Compare the data members and return accordingly
        return this.x == c.getX() && this.y == c.getY();
    }
}
