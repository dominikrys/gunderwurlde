package shared.view.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;

import shared.Pose;
import shared.lists.ActionList;
import shared.lists.AmmoList;
import shared.lists.EntityList;
import shared.lists.EntityStatus;
import shared.lists.Team;
import shared.view.ItemView;

public class PlayerView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final int id;

    protected EnumMap<AmmoList, Integer> ammo;
    protected ArrayList<ItemView> items;
    protected String name;
    protected Team team;
    protected ActionList currentAction;
    protected int health;
    protected int maxHealth;
    protected int currentItemIndex;
    protected int score;
    protected int moveSpeed;;
    protected boolean takenDamage;
    protected boolean moving;
    protected boolean paused;

    public PlayerView(Pose pose, int sizeScaleFactor, int health, int maxHealth, ArrayList<ItemView> items, int currentItemIndex, int score,
                      String name, EnumMap<AmmoList, Integer> ammo, int playerID, Team team, boolean cloaked, EntityStatus status,
                      ActionList currentAction, boolean takenDamage, boolean moving, boolean paused) {
        super(pose, sizeScaleFactor, EntityList.PLAYER, cloaked, status);
        this.health = health;
        this.maxHealth = maxHealth;
        this.items = items;
        this.currentItemIndex = currentItemIndex;
        this.score = score;
        this.name = name;
        this.ammo = ammo;
        this.id = playerID;
        this.team = team;
        this.takenDamage = takenDamage;
        this.moving = moving;
        this.currentAction = currentAction;
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
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

    public int getID() {
        return id;
    }

    public EnumMap<AmmoList, Integer> getAmmo() {
        return ammo;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public int getCurrentItemIndex() {
        return currentItemIndex;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public ItemView getCurrentItem() {
        return items.get(currentItemIndex);
    }

    public ArrayList<ItemView> getItems() {
        return items;
    }

    public Team getTeam() {
        return team;
    }

}
