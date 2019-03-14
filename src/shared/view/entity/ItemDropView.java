package shared.view.entity;

import shared.Pose;
import shared.lists.EntityList;
import shared.lists.EntityStatus;

import java.io.Serializable;

public class ItemDropView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int timeLeft;

    public ItemDropView(Pose pose, int sizeScaleFactor, EntityList name, boolean cloaked, EntityStatus status, int timeLeft) {
        super(pose, sizeScaleFactor, name, cloaked, status);
        this.timeLeft = timeLeft;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

}
