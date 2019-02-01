package client.data;

import data.Constants;
import data.Pose;

public abstract class EntityView {
    protected Pose pose;
    protected double size;
    protected String pathToGraphic;

    protected EntityView(Pose pose, double size) {
        this.pose = pose;
        this.pathToGraphic = Constants.DEFAULT_GRAPHIC_PATH;
        this.size = size;
    }

    public Pose getPose() {
        return pose;
    }

    public double getSize() {
        return size;
    }

    public String getPathToGraphic() {
        return pathToGraphic;
    }
}
