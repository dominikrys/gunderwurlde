package server.engine.state.entity.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import server.engine.state.entity.Entity;
import server.engine.state.entity.HasHealth;
import server.engine.state.entity.HasID;
import server.engine.state.entity.IsMovable;
import server.engine.state.item.Item;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.item.weapon.gun.Pistol;
import server.engine.state.item.weapon.gun.Shotgun;
import server.engine.state.map.tile.Tile;
import shared.lists.ActionList;
import shared.lists.AmmoList;
import shared.lists.EntityList;
import shared.lists.Teams;

public class Player extends Entity implements HasHealth, IsMovable, HasID {
    public static final int DEFAULT_HEALTH = 20;
    public static final int DEFAULT_MOVESPEED = Tile.TILE_SIZE * 4;
    public static final int DEFAULT_SCORE = 0;
    public static final int DEFAULT_ITEM_CAP = 3;
    public static final int DEFAULT_SIZE = (Tile.TILE_SIZE - 6) / 2;

    private static int nextPlayerID = 0;
    protected static LinkedHashMap<Teams, Integer> teamScore = new LinkedHashMap<>();

    protected final int playerID;
    protected final Teams team;
    protected final String name;

    protected ArrayList<Item> items;
    protected LinkedHashMap<AmmoList, Integer> ammo; // TODO add cap to ammo stored
    protected ActionList currentAction;
    protected int health;
    protected int maxHealth;
    protected int moveSpeed;
    protected int currentItem;
    protected int maxItems;
    protected boolean takenDamage;
    protected boolean moving;

    public Player(Teams team, String name) {
        super(DEFAULT_SIZE, EntityList.PLAYER);
        this.health = DEFAULT_HEALTH;
        this.maxHealth = health;
        this.moveSpeed = DEFAULT_MOVESPEED;
        this.items = new ArrayList<Item>();
        items.add(new Pistol());
        items.add(new Shotgun()); // TODO remove testing only
        this.maxItems = DEFAULT_ITEM_CAP;
        this.currentItem = 0;
        this.team = team;
        changeScore(team, DEFAULT_SCORE);
        this.name = name;
        this.ammo = new LinkedHashMap<>();
        this.ammo.put(AmmoList.BASIC_AMMO, 120);
        this.ammo.put(AmmoList.SHOTGUN_ROUND, 20); // TODO remove testing only
        this.playerID = nextPlayerID++;
        this.takenDamage = false;
        this.moving = false;
        this.currentAction = ActionList.NONE;
    }

    public ActionList getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(ActionList currentAction) {
        this.currentAction = currentAction;
    }

    public static void changeScore(Teams team, int value) {
        if (teamScore.containsKey(team))
            teamScore.put(team, teamScore.get(team) + value);
        else
            teamScore.put(team, value);
    }

    public static int getScore(Teams team) {
        if (teamScore.containsKey(team))
            return teamScore.get(team);
        else
            return 0;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(int maxItems) {
        if (maxItems < 1)
            maxItems = 1;
        this.maxItems = maxItems;
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
            if (currentItem >= itemIndex)
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
        if (slot != currentItem) {
            if (items.get(currentItem) instanceof Gun)
                ((Gun) items.get(currentItem)).cancelReload();
            currentItem = slot;
        }
    }

    public int getScore() {
        if (teamScore.containsKey(team))
            return teamScore.get(team);
        else
            return 0;
    }

    public Teams getTeam() {
        return team;
    }

    public String getName() {
        return name;
    }

    public LinkedHashMap<AmmoList, Integer> getAmmoList() {
        return ammo;
    }

    @Override
    public int getID() {
        return playerID;
    }

    @Override
    public boolean hasTakenDamage() {
        return takenDamage;
    }

    @Override
    public void setTakenDamage(boolean takenDamage) {
        this.takenDamage = takenDamage;
    }

    @Override
    public boolean isMoving() {
        return moving;
    }

    @Override
    public void setMoving(boolean moving) {
        this.moving = moving;
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

}
