package client.data;

import java.io.Serializable;

import data.Constants;
import data.Pose;

public abstract class EntityView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Pose pose;
    protected int size;
    protected String pathToGraphic;

    protected EntityView(Pose pose, int size) {
        int radius = size / 2;
        this.pose = new Pose(pose.getX() - radius, pose.getY() - radius, pose.getDirection());
        this.pathToGraphic = Constants.DEFAULT_GRAPHIC_PATH;
        this.size = 1; // TODO sort out scaling
    }

    public Pose getPose() {
        return pose;
    }

    public int getSize() {
        return size;
    }

    public String getPathToGraphic() {
        return pathToGraphic;
    }
}
