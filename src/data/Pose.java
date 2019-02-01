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
}
