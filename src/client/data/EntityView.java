package client.data;

import java.io.Serializable;

import data.Pose;

public abstract class EntityView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Pose pose;
    protected int sizeScaleFactor;

    protected EntityView(Pose pose, int sizeScaleFactor) {
        int radius = sizeScaleFactor / 2;
        this.pose = new Pose(pose.getX() - radius, pose.getY() - radius, pose.getDirection());
        this.sizeScaleFactor = 1; // TODO sort out scaling
    }

    public Pose getPose() {
        return pose;
    }

    public int getSizeScaleFactor() {
        return sizeScaleFactor;
    }

}
