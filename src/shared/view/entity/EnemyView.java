package shared.view.entity;

import java.io.Serializable;

import shared.Pose;
import shared.lists.ActionList;
import shared.lists.EntityList;
import shared.lists.Status;

public class EnemyView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected ActionList currentAction;
    protected boolean takenDamage;
    protected boolean moving;

    public EnemyView(Pose pose, int size, EntityList name, boolean cloaked, Status status, ActionList currentAction, boolean takenDamage, boolean moving) {
        super(pose, size, name, cloaked, status);
        this.takenDamage = takenDamage;
        this.moving = moving;
        this.currentAction = currentAction;
    }

    public ActionList getCurrentAction() {
        return currentAction;
    }

    public boolean hasTakenDamage() {
        return takenDamage;
    }

    public boolean isMoving() {
        return moving;
    }

}
