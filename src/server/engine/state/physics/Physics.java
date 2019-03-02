package server.engine.state.physics;

import server.engine.state.map.tile.Tile;
import shared.Location;

public class Physics {
    private static double MASS_PER_SIZE = 2;
    private static int TIME_PER_SECOND = 1000;
    private static int GRAVITY = 100;
    private static double TILE_BOUNCE = 0.7;
    private static double OBJECT_BOUNCE = 0.9;

    public static HasPhysics[] objectCollision(HasPhysics e1, HasPhysics e2) {
        double e1Mass = getMass(e1.getSize());
        double e2Mass = getMass(e2.getSize());
        Velocity e1Velocity = e1.getVelocity();
        Velocity e2Velocity = e2.getVelocity();
        double[] e1VelocityComponents = getComponents(e1Velocity.getDirection(), e1Velocity.getSpeed());
        double[] e2VelocityComponents = getComponents(e2Velocity.getDirection(), e2Velocity.getSpeed());

        double e1NewXvelocity = getNewVelocity(e1VelocityComponents[0], e2VelocityComponents[0], e1Mass, e2Mass);
        double e2NewXvelocity = e1NewXvelocity + (OBJECT_BOUNCE * (e1VelocityComponents[0] - e2VelocityComponents[0]));
        double e1NewYvelocity = getNewVelocity(e1VelocityComponents[1], e2VelocityComponents[1], e1Mass, e2Mass);
        double e2NewYvelocity = e1NewYvelocity + (OBJECT_BOUNCE * (e1VelocityComponents[1] - e2VelocityComponents[1]));

        e1VelocityComponents = fromComponents(e1NewXvelocity, e1NewYvelocity);
        e2VelocityComponents = fromComponents(e2NewXvelocity, e2NewYvelocity);

        e1.setVelocity(new Velocity((int) e1VelocityComponents[0], e1VelocityComponents[1]));
        e2.setVelocity(new Velocity((int) e2VelocityComponents[0], e2VelocityComponents[1]));

        HasPhysics[] result = {e1,e2};
        return result;
    }

    private static double getNewVelocity(double e1Velocity, double e2Velocity, double e1Mass, double e2Mass) { // for e1
        return ((e1Mass * e1Velocity) + (e2Mass * e2Velocity) + (e2Mass * OBJECT_BOUNCE * (e2Velocity - e1Velocity))) / (e1Mass + e2Mass);
    }

    public static HasPhysics tileCollision(HasPhysics e, Location tileLoc) {
        int gapSize = e.getSize() + (Tile.TILE_SIZE / 2) + 1;
        Location loc = e.getLocation();
        double xDiff = tileLoc.getX() - loc.getX();
        double yDiff = tileLoc.getY() - loc.getY();
        Velocity currentVelocity = e.getVelocity();
        int normal;
        if (Math.abs(xDiff) < Math.abs(yDiff)) {
            if (yDiff < 0) {
                normal = 90;
                loc = new Location(loc.getX(), tileLoc.getY() + gapSize);
            } else {
                normal = 270;
                loc = new Location(loc.getX(), tileLoc.getY() - gapSize);
            }
        } else if (Math.abs(xDiff) > Math.abs(yDiff)) {
            if (xDiff < 0) {
                normal = 0;
                loc = new Location(tileLoc.getX() + gapSize, loc.getY());
            } else {
                normal = 180;
                loc = new Location(tileLoc.getX() - gapSize, loc.getY());
            }
        } else {
            System.out.println("Corner hit!");
            // TODO handle
            normal = 0;
        }

        e.setLocation(loc);

        int newDirection = normal + (normal - currentVelocity.getDirection()) - 180;
        if (newDirection < 0)
            newDirection += 360;
        currentVelocity = new Velocity(newDirection, currentVelocity.getSpeed() * TILE_BOUNCE);
        e.setVelocity(currentVelocity);

        return e;
    }

    public static Force getFrictionalForce(double frictionCoefficient, int size, int directionOfVelocity) {
        double force = getFrictionalForce(frictionCoefficient, getMass(size));
        int direction = directionOfVelocity - 180;
        if (direction < 0)
            direction += 360;
        return new Force(direction, force);
    }

    private static double getFrictionalForce(double frictionCoefficient, double mass) {
        return frictionCoefficient * mass * GRAVITY;
    }

    public static Force getDragForce(double fluidDensity, Velocity velocity, int size) {
        double force = getDragForce(fluidDensity, normalise(velocity.getSpeed()), normaliseSize(size));
        int direction = velocity.getDirection() - 180;
        if (direction < 0)
            direction += 360;
        return new Force(direction, force);
    }

    private static double getDragForce(double fluidDensity, double speed, double surfaceArea) {
        return 0.5 * Math.pow(speed, 2) * surfaceArea * fluidDensity;
    }

    //public static Velocity getNewVelocity(Impulse impulse, Velocity velocity, int size) {
       // return velocity;
    //}

    public static Velocity getNewVelocity(double acceleration, Velocity velocity, int direction, long time) {
        double changeInSpeed = acceleration * normaliseTime(time);
        double[] changeInSpeedComponents = getComponents(direction, changeInSpeed);
        double[] velocityComponents = getComponents(velocity.getDirection(), velocity.getSpeed());

        double[] result = combineComponents(changeInSpeedComponents, velocityComponents);
        result = fromComponents(result[0], result[1]);
        return new Velocity((int) result[0], result[1]);
    }

    public static Impulse getImpulse(int direction, double force, long time) {
        return new Impulse(direction, force * normaliseTime(time));
    }

    public static double[] getComponents(int direction, double value) {
        double directionInRadians = Math.toRadians(direction);
        double[] components = { (double) value * Math.cos(directionInRadians), (double) value * Math.sin(directionInRadians) };
        return components;
    }

    public static double[] fromComponents(double xComp, double yComp) {
        double value = Math.sqrt(Math.pow(xComp, 2) + Math.pow(yComp, 2));
        int direction = (int) Math.round(Math.toDegrees(Math.atan(yComp / xComp)));

        if (xComp < 0)
            direction += 180;
        else if (yComp < 0)
            direction += 360;

        double[] result = { direction, value };
        return result;
    }

    public static Force getNewForce(Force f1, Force f2) {
        double[] result = combineComponents(getComponents(f1.getDirection(), f1.getForce()), getComponents(f2.getDirection(), f2.getForce()));
        result = fromComponents(result[0], result[1]);
        return new Force((int) result[0], result[1]);
    }

    public static Force getForce(double acceleration, int direction, int size) {
        return new Force(direction, acceleration * getMass(size));
    }

    public static double getAcceleration(Force f, int size) {
        return f.getForce() / getMass(size);
    }

    private static double[] combineComponents(double[] c1, double[] c2) {
        double[] result = { c1[0] + c2[0], c1[1] + c2[1] };
        return result;
    }

    public static double normaliseTime(long time) {
        return time / (double) TIME_PER_SECOND;
    }

    private static double normaliseSize(int size) {
        return normalise(size * 2);
    }

    private static double normalise(double val) {
        return val / Tile.TILE_SIZE;
    }

    private static double getMass(int size) {
        return normalise(size) * MASS_PER_SIZE;
    }

}
