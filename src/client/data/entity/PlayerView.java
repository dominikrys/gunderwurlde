package client.data.entity;

import client.data.ItemView;
import data.Pose;
import data.entity.EntityList;
import data.entity.item.weapon.gun.AmmoList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PlayerView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final int id;
    protected int health;
    protected int maxHealth;
    protected ArrayList<ItemView> items;
    protected int currentItemIndex;
    protected int score;
    protected String name;
    protected LinkedHashMap<AmmoList, Integer> ammo;

    public PlayerView(Pose pose, int size, int health, int maxHealth, ArrayList<ItemView> items, int currentItemIndex, int score,
                      String name, LinkedHashMap<AmmoList, Integer> ammo, int playerID) {
        super(pose, size, EntityList.PLAYER);
        this.health = health;
        this.maxHealth = health;
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

    public ItemView getCurrentItem() {
        return items.get(currentItemIndex);
    }

    public ArrayList<ItemView> getItems() {
        return items;
    }

}
