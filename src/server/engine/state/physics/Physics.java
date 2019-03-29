package server.engine.state.physics;

import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.lists.TileState;

/**
 * Physics class for all the Physics in the game.
 * 
 * @author Richard
 *
 */
public class Physics {
    /**
     * Value used for friction calculations.
     */
    public static int GRAVITY = 100;

    /**
     * Convertion between the time units used to seconds.
     */
    private static int TIME_PER_SECOND = 1000;
    /**
     * Bounce coefficient for entities (non-tiles).
     */
    private static double OBJECT_BOUNCE = 0.9;

    /**
     * Process a collision between two physics objects.
     * 
     * @param e1
     * @param e2
     * @param tileMap
     */
    public static void objectCollision(HasPhysics e1, HasPhysics e2, Tile[][] tileMap) {
        double e1Mass = e1.getMass();
        double e2Mass = e1.getMass();

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

        Location e1Loc = e1.getLocation();
        Location e2Loc = e2.getLocation();
        double dist = e1.getSize() + e2.getSize() + 1;

        if (e1Mass < e2Mass) {
            int direction = (int) fromComponents(e1Loc.getX() - e2Loc.getX(), e1Loc.getY() - e2Loc.getY())[0];
            double[] newLoc = getComponents(direction, dist);
            Location newLocation = new Location(e2Loc.getX() + newLoc[0], e2Loc.getY() + newLoc[1]);
            int[] tileCords = Tile.locationToTile(newLocation);
            if (tileCords[0] >= 0 && tileCords[1] >= 0 && tileCords[0] < tileMap.length && tileCords[1] < tileMap[0].length
                    && tileMap[tileCords[0]][tileCords[1]].getState() != TileState.SOLID)
                e1.setLocation(newLocation);
        } else {
            int direction = (int) fromComponents(e2Loc.getX() - e1Loc.getX(), e2Loc.getY() - e1Loc.getY())[0];
            double[] newLoc = getComponents(direction, dist);
            Location newLocation = new Location(e1Loc.getX() + newLoc[0], e1Loc.getY() + newLoc[1]);
            int[] tileCords = Tile.locationToTile(newLocation);
            if (tileCords[0] >= 0 && tileCords[1] >= 0 && tileCords[0] < tileMap.length && tileCords[1] < tileMap[0].length
                    && tileMap[tileCords[0]][tileCords[1]].getState() != TileState.SOLID)
                e2.setLocation(newLocation);
        }
    }

    /**
     * Gets a new velocity from a momentum conserved collision.
     * 
     * @param e1Velocity
     * @param e2Velocity
     * @param e1Mass
     * @param e2Mass
     * @return
     */
    private static double getNewVelocity(double e1Velocity, double e2Velocity, double e1Mass, double e2Mass) { // for e1
        return ((e1Mass * e1Velocity) + (e2Mass * e2Velocity) + (e2Mass * OBJECT_BOUNCE * (e2Velocity - e1Velocity))) / (e1Mass + e2Mass);
    }

    /**
     * Process a collision between a physics object and a Tile.
     * 
     * @param e
     *            the Entity colliding.
     * @param tileLoc
     * @param tileBounce
     */
    public static void tileCollision(HasPhysics e, Location tileLoc, double tileBounce) {
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
        } else {
            if (xDiff < 0) {
                normal = 0;
                loc = new Location(tileLoc.getX() + gapSize, loc.getY());
            } else {
                normal = 180;
                loc = new Location(tileLoc.getX() - gapSize, loc.getY());
            }
        }

        e.setLocation(loc);

        int newDirection = normal + (normal - currentVelocity.getDirection()) - 180;
        if (newDirection < 0)
            newDirection += 360;
        currentVelocity = new Velocity(newDirection, currentVelocity.getSpeed() * tileBounce);
        e.setVelocity(currentVelocity);
    }

    /**
     * Calculates the fictional force with direction for the given parameters.
     * 
     * @param frictionCoefficient
     * @param mass
     * @param directionOfVelocity
     * @return Force due to friction opposing velocity.
     */
    public static Force getFrictionalForce(double frictionCoefficient, double mass, int directionOfVelocity) {
        double force = getFrictionalForce(frictionCoefficient, mass);
        int direction = directionOfVelocity - 180;
        if (direction < 0)
            direction += 360;
        return new Force(direction, force);
    }

    /**
     * Basic friction calculation.
     * 
     * @param frictionCoefficient
     * @param mass
     * @return Force due to friction.
     */
    private static double getFrictionalForce(double frictionCoefficient, double mass) {
        return frictionCoefficient * mass * GRAVITY;
    }

    /**
     * Calculates the drag force with direction.
     * 
     * @param fluidDensity
     * @param velocity
     * @param size
     * @return Force due to drag and direction opposing velocity.
     */
    public static Force getDragForce(double fluidDensity, Velocity velocity, int size) {
        double force = getDragForce(fluidDensity, normalise(velocity.getSpeed()), normaliseSize(size));
        int direction = velocity.getDirection() - 180;
        if (direction < 0)
            direction += 360;
        return new Force(direction, force);
    }

    /**
     * Basic drag calculation.
     * 
     * @param fluidDensity
     * @param speed
     * @param surfaceArea
     * @return Force due to drag.
     */
    private static double getDragForce(double fluidDensity, double speed, double surfaceArea) {
        return 0.5 * Math.pow(speed, 2) * surfaceArea * fluidDensity;
    }

    /**
     * Calculates the new velocity from the original given the provided acceleration
     * and time.
     * 
     * @param acceleration
     * @param velocity
     * @param direction
     * @param time
     * @return
     */
    public static Velocity getNewVelocity(double acceleration, Velocity velocity, int direction, long time) {
        double changeInSpeed = acceleration * normaliseTime(time);
        double[] changeInSpeedComponents = getComponents(direction, changeInSpeed);
        double[] velocityComponents = getComponents(velocity.getDirection(), velocity.getSpeed());

        double[] result = combineComponents(changeInSpeedComponents, velocityComponents);
        result = fromComponents(result[0], result[1]);
        return new Velocity((int) result[0], result[1]);
    }

    /**
     * Gets the X and Y comonents from a direction and value.
     * 
     * @param direction
     * @param value
     * @return X & Y components
     */
    static double[] getComponents(int direction, double value) { // TODO use Line instead?
        double directionInRadians = Math.toRadians(direction);
        double[] components = { (double) value * Math.cos(directionInRadians), (double) value * Math.sin(directionInRadians) };
        return components;
    }

    /**
     * Gets the direction and value from X and Y components.
     * 
     * @param xComp
     * @param yComp
     * @return Direction and Value.
     */
    public static double[] fromComponents(double xComp, double yComp) { // TODO use Line instead?
        double value = Math.sqrt(Math.pow(xComp, 2) + Math.pow(yComp, 2));
        int direction = (int) Math.round(Math.toDegrees(Math.atan(yComp / xComp)));

        if (xComp < 0)
            direction += 180;
        else if (yComp < 0)
            direction += 360;

        double[] result = { direction, value };
        return result;
    }

    /**
     * Gets a force from F=ma with a given direction.
     * 
     * @param acceleration
     * @param direction
     * @param mass
     * @return Force
     */
    public static Force getForce(double acceleration, int direction, double mass) {
        return new Force(direction, acceleration * mass);
    }

    /**
     * Gets the accelerate from a force.
     * 
     * @param f
     *            Given force.
     * @param mass
     * @return Acceleration
     */
    public static double getAcceleration(Force f, double mass) {
        return f.getForce() / mass;
    }

    /**
     * Combines two X & Y components together.
     * 
     * @param c1
     * @param c2
     * @return Combined result.
     */
    static double[] combineComponents(double[] c1, double[] c2) {
        double[] result = { c1[0] + c2[0], c1[1] + c2[1] };
        return result;
    }

    /**
     * Normalises time using the TIME_PER_SECOND
     * 
     * @param time
     * @return normalised time
     */
    public static double normaliseTime(long time) {
        return time / (double) TIME_PER_SECOND;
    }

    /**
     * Normalises size to use diameter instead of radius.
     * 
     * @param size
     *            as radius
     * @return size as diameter
     */
    private static double normaliseSize(int size) {
        return normalise(size * 2);
    }

    /**
     * Normalise distances based off the Tile Size.
     * 
     * @param val
     * @return normalised distance.
     */
    private static double normalise(double val) {
        return val / Tile.TILE_SIZE;
    }

}
