package server.engine.state.physics;

public interface HasPhysics {
    public abstract Velocity getVelocity();

    public abstract void setVelocity(Velocity v);

    public abstract Force getResultantForce();

    public abstract void addNewForce(Force f);
}
