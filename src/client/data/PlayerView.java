package client.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import data.Pose;
import data.entity.item.weapon.gun.AmmoList;

public class PlayerView extends EntityView {
    protected int health;
    protected int maxHealth;
    protected int moveSpeed;
    protected ArrayList<ItemView> items;
    protected int currentItemIndex;
    protected int score;
    protected String name;
    protected LinkedHashMap<AmmoList, Integer> ammo;
    protected final int id;

    public PlayerView(Pose pose, int size, int health, int maxHealth, int moveSpeed, ArrayList<ItemView> items, int currentItemIndex, int score, String name,
            LinkedHashMap<AmmoList, Integer> ammo, int playerID) {
        super(pose, size);
        this.pathToGraphic = "file:assets/img/mobs/player.png";
        this.health = health;
        this.maxHealth = health;
        this.moveSpeed = moveSpeed;
        this.items = items;
        this.currentItemIndex = currentItemIndex;
        this.score = score;
        this.name = name;
        this.ammo = ammo;
        this.id = playerID;
    }
    
    public int getID() {
        return id;
    }

    public LinkedHashMap<AmmoList, Integer> getAmmo() {
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
    
    public int getMoveSpeed() {
    	return moveSpeed;
    }

    public ItemView getCurrentItem() {
        return items.get(currentItemIndex);
    }

    public ArrayList<ItemView> getItems() {
        return items;
    }

}
