package server.engine.state.entity.player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;

import server.engine.state.entity.Entity;
import server.engine.state.entity.ItemDrop;
import server.engine.state.entity.LivingEntity;
import server.engine.state.item.Item;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.item.weapon.gun.MachineGun;
import server.engine.state.item.weapon.gun.Pistol;
import server.engine.state.item.weapon.gun.PlasmaPistol;
import shared.lists.AmmoList;
import shared.lists.EntityList;
import shared.lists.Team;

public class Player extends LivingEntity {
    public static final int DEFAULT_HEALTH = 20;
    public static final double DEFAULT_MOVEMENT_FORCE = 18;
    public static final int DEFAULT_SCORE = 0;
    public static final int DEFAULT_ITEM_CAP = 3;
    public static final int DEFAULT_SIZE = EntityList.PLAYER.getSize() / 2;
    public static final double DEFAULT_MASS = 2;

    private static final EnumMap<AmmoList, Integer> DEFAULT_MAX_AMMO = new EnumMap<>(AmmoList.class);

    static {
        DEFAULT_MAX_AMMO.put(AmmoList.BASIC_AMMO, 300);
        DEFAULT_MAX_AMMO.put(AmmoList.SHOTGUN_ROUND, 120);
    }

    protected static EnumMap<Team, Integer> teamScore = new EnumMap<>(Team.class);
    protected final Team team;
    protected final String name;

    protected ArrayList<Item> items;
    protected EnumMap<AmmoList, Integer> ammo;
    protected EnumMap<AmmoList, Integer> maxAmmo;
    protected int currentItem;
    protected int maxItems;

    private boolean paused;

    public Player(Team team, String name) {
        super(DEFAULT_HEALTH, DEFAULT_MOVEMENT_FORCE, EntityList.PLAYER, DEFAULT_SIZE, DEFAULT_MASS);
        this.items = new ArrayList<Item>();
        items.add(new Pistol());
        items.add(new PlasmaPistol()); // TODO remove testing only
        items.add(new MachineGun()); // TODO remove testing only
        this.maxItems = DEFAULT_ITEM_CAP;
        this.currentItem = 0;
        this.team = team;
        changeScore(team, DEFAULT_SCORE);
        this.name = name;
        this.maxAmmo = DEFAULT_MAX_AMMO;
        this.ammo = new EnumMap<>(AmmoList.class);
        this.ammo.put(AmmoList.BASIC_AMMO, 120);
        this.ammo.put(AmmoList.SHOTGUN_ROUND, 20); // TODO remove testing only
        this.paused = false;
    }

    public static void changeScore(Team team, int value) {
        if (teamScore.containsKey(team))
            teamScore.put(team, teamScore.get(team) + value);
        else
            teamScore.put(team, value);
    }

    public static int getScore(Team team) {
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

    public void setAmmo(AmmoList type, int amount) { // ignores maxammo
        ammo.put(type, amount);
    }

    public int addAmmo(AmmoList ammoType, int amountAvailable) {
        int amount = ammo.getOrDefault(ammoType, 0);
        int maxVal = maxAmmo.getOrDefault(ammoType, -1);
        int amountTaken = amountAvailable;

        if (maxVal != -1 && amount + amountAvailable > maxVal) {
            amountTaken = maxVal - amount;
            amount = maxVal;
        } else {
            amount += amountAvailable;
        }

        ammo.put(ammoType, amount);
        return amountTaken;
    }

    public EnumMap<AmmoList, Integer> getAmmoList() {
        return ammo;
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
            slot = items.size() - 1;
        else if (slot > items.size() - 1)
            slot = items.size() - 1;
        if (slot != currentItem) {
            if (items.get(currentItem) instanceof Gun)
                ((Gun) items.get(currentItem)).cancelReload();
            currentItem = slot;
        }
    }

    public int getScore() {
        return teamScore.get(team);
    }

    @Override
    public Team getTeam() {
        return team;
    }

    public String getName() {
        return name;
    }

    @Override
    public Entity makeCopy() {
        return new Player(team, name);
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public LinkedList<ItemDrop> getDrops() {
        LinkedList<ItemDrop> itemsToDrop = new LinkedList<>();
        items.stream().forEach((i) -> itemsToDrop.add(toItemDrop(i, 1)));
        return itemsToDrop;
    }
}
