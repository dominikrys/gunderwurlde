package data;

public class Pose extends Location {
    protected int direction;
    
    public Pose() { //workaround for template enemies
        this(0, 0, 0);
    }

    public Pose(Location location) {
        this(location, 0);
    }

    public Pose(int x, int y) {
        this(x, y, 0);
    }

    public Pose(Location location, int direction) {
        this(location.getX(), location.getY(), direction);
    }

    public Pose(int x, int y, int direction) {
        super(x, y);
        setDirection(direction);
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = normaliseDirection(direction);
    }
    
    public static int normaliseDirection(int direction) {
        if (direction > 360)
            direction = direction % 360;
        else if (direction < 0)
            direction = 360 + (direction % -360);
        return direction;
    }

    @Override
    public int hashCode() {
        return x * y * (direction + 7);
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
        return (this.direction == c.getDirection() && this.x == c.getX() && this.y == c.getY());
    }

    @Override
    public String toString(){
        return "Coordinates: " + x + ", " + y + "; Direction: " + direction;
    }
}
