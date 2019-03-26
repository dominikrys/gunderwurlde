package shared;

public class Line {
    protected Location start;
    protected Location end;
    protected int direction;
    protected double length;

    public Line(Location start, Location end) {
        this.start = start;
        this.end = end;
        double xComp = end.getX() - start.getX();
        double yComp = end.getY() - start.getY();
        length = Math.sqrt(Math.pow(xComp, 2) + Math.pow(yComp, 2));
        direction = (int) Math.round(Math.toDegrees(Math.atan(yComp / xComp)));

        if (xComp < 0)
            direction += 180;
        else if (yComp < 0)
            direction += 360;
    }

    public Line(Location start, int direction, double length) {
        this.start = start;
        this.direction = direction;
        this.length = length;
        double directionInRadians = Math.toRadians(direction);
        double xComp = length * Math.cos(directionInRadians);
        double yComp = length * Math.sin(directionInRadians);
        this.end = new Location(start.getX() + xComp, start.getY() + yComp);
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }

    public int getDirection() {
        return direction;
    }

    public double getLength() {
        return length;
    }


}
