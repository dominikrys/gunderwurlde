package server.engine.state.physics;

import shared.Location;

public interface HasPhysics {
    public abstract Velocity getVelocity();

    public abstract void setVelocity(Velocity v);

    public abstract Force getResultantForce();

    public abstract void addNewForce(Force f);

    public abstract Location getLocation();

    public abstract void setLocation(Location location);

    public abstract int getSize();

    public abstract double getMass();
}
