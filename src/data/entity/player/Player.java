package data.entity.player;

import data.HasHealth;
import data.IsMovable;
import data.entity.Entity;
import data.entity.HasID;
import data.entity.item.Item;
import data.entity.item.weapon.gun.AmmoList;
import data.entity.item.weapon.gun.Pistol;
import data.map.tile.Tile;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Player extends Entity implements HasHealth, IsMovable, HasID {
    public static final int DEFAULT_HEALTH = 20;
    public static final int DEFAULT_MOVESPEED = Tile.TILE_SIZE;
    public static final int DEFAULT_SCORE = 0;
    public static final int DEFAULT_SIZE = 1;

    private static int nextPlayerID = 0;

    protected final int playerID;
    protected final Teams team;
    protected final String name;

    protected ArrayList<Item> items;
    protected LinkedHashMap<AmmoList, Integer> ammo;
    protected int health;
    protected int maxHealth;
    protected int moveSpeed;
    protected int currentItem;
    protected int score;

    public Player(Teams team, String name) {
        super(DEFAULT_SIZE);
        this.health = DEFAULT_HEALTH;
        this.maxHealth = health;
        this.moveSpeed = DEFAULT_MOVESPEED;
        this.items = new ArrayList<Item>() {
            private static final long serialVersionUID = 1L;

            {
                new Pistol();
            }
        };
        this.currentItem = 0;
        this.score = DEFAULT_SCORE;
        this.team = team;
        this.name = name;
        this.playerID = nextPlayerID++;
    }

    @Override
    public int getID() {
        return playerID;
    }

    public int getAmmo(AmmoList type) {
        if (ammo.containsKey(type)) {
            return ammo.get(type);
        } else {
            return 0;
        }
    }

    public void setAmmo(AmmoList type, int amount) {
        ammo.put(type, amount);
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public boolean setItems(ArrayList<Item> items) {
        if (items.size() > 0) {
            this.items = items;
            return true;
        }
        return false;
    }

    public void addItem(Item itemToAdd) {
        items.add(itemToAdd);
    }

    public boolean removeItem(int itemIndex) {
        try {
            items.remove(itemIndex);
            if (currentItem == itemIndex)
                previousItem();
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public Item getCurrentItem() {
        return items.get(currentItem);
    }

    public void setCurrentItem(Item item) {
        this.items.set(currentItem, item);
    }

    public void nextItem() {
        if (currentItem == items.size() - 1)
            currentItem = 0;
        else
            currentItem++;
    }

    public void previousItem() {
        if (currentItem == 0)
            currentItem = items.size() - 1;
        else
            currentItem--;
    }

    public int getCurrentItemIndex() {
        return currentItem;
    }

    public void setCurrentItemIndex(int slot) {
        if (slot < 0)
            slot = 0;
        else if (slot > items.size() - 1)
            slot = items.size() - 1;
        currentItem = slot;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void changeScore(int value) {
        this.score += value;
    }

    public Teams getTeam() {
        return team;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getMoveSpeed() {
        return moveSpeed;
    }

    @Override
    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        if (health < 0)
            health = 0;
        this.health = health;
    }

    @Override
    public boolean damage(int amount) {
        if (amount >= health) {
            health = 0;
            return true;
        } else {
            health -= amount;
            return false;
        }
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        if (maxHealth < 0)
            maxHealth = 0;
        this.maxHealth = maxHealth;
    }

    public LinkedHashMap<AmmoList, Integer> getAmmoList() {
        return ammo;
    }

}
