package server.engine.state.physics;

import server.engine.state.map.tile.Tile;

public class Physics {
    private static double MASS_PER_SIZE = 1;
    private static int TIME_PER_SECOND = 1000;

    public static Force getFrictionalForce(double frictionCoefficient, int size, int directionOfVelocity) {
        double force = getFrictionalForce(frictionCoefficient, getMass(size));
        int direction = directionOfVelocity - 180;
        if (direction < 0)
            direction += 360;
        return new Force(direction, force);
    }

    private static double getFrictionalForce(double frictionCoefficient, double mass) {
        return frictionCoefficient * mass * 100;
    }

    public static Force getDragForce(double fluidDensity, Velocity velocity, int size) {
        double force = getDragForce(fluidDensity, normalise(velocity.getSpeed()), normalise(size));
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
        System.out.println(Math.atan(yComp / xComp));
        System.out.println(yComp + " " + xComp);
        double[] result = { direction, value };
        return result;
    }

    public static Force getNewForce(Force f1, Force f2) {
        double[] result = combineComponents(getComponents(f1.getDirection(), f1.getForce()), getComponents(f2.getDirection(), f2.getForce()));
        result = fromComponents(result[0], result[1]);
        return new Force((int) result[0], result[1]);
    }

    public static Force getForce(int acceleration, int direction, int size) {
        return new Force(direction, acceleration * getMass(size));
    }

    public static double getAcceleration(Force f, int size) {
        return f.getForce() / getMass(size);
    }

    private static double[] combineComponents(double[] c1, double[] c2) {
        double[] result = { c1[0] + c2[0], c1[1] + c2[1] };
        return result;
    }

    private static double normaliseTime(long time) {
        return time / (double) TIME_PER_SECOND;
    }

    private static double normalise(double val) {
        return val / Tile.TILE_SIZE;
    }

    private static double getMass(int size) {
        return normalise(size) * MASS_PER_SIZE;
    }

}
