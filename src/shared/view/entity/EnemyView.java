package shared.view.entity;

import shared.Pose;
import shared.lists.ActionList;
import shared.lists.EntityList;
import shared.lists.EntityStatus;

import java.io.Serializable;

public class EnemyView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected ActionList currentAction;
    protected boolean takenDamage;
    protected boolean moving;
    protected int health;
    protected int maxHealth;
    protected int ID;

    public EnemyView(Pose pose, int size, EntityList name, boolean cloaked, EntityStatus status, ActionList currentAction, boolean takenDamage, boolean moving, int health, int maxHealth, int ID) {
        super(pose, size, name, cloaked, status);
        this.takenDamage = takenDamage;
        this.moving = moving;
        this.currentAction = currentAction;
        this.health = health;
        this.maxHealth = maxHealth;
        this.ID = ID;
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

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getID() {
        return ID;
    }
}
