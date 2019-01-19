package data.player;

import java.util.ArrayList;

import data.HasPose;
import data.Location;
import data.Pose;
import data.item.Item;
import data.item.weapon.Pistol;

public class Player implements HasPose{
	public static final int DEFAULT_HEALTH = 3;
	public static final int DEAFULT_MOVESPEED = 10;

	protected ArrayList<Item> items;
	protected int health;
	protected int maxHealth;
	protected int movespeed;
	protected int currentItem;
	protected Pose pose;
	
	public Player(Pose pose) {
		this.health = DEFAULT_HEALTH;
		this.maxHealth = health;
		this.movespeed = DEAFULT_MOVESPEED;
		this.items = new ArrayList<Item>(){{new Pistol();}};
		this.currentItem = 0;
		this.pose = pose;
	}
	
	public Player(Location location) {
		this(new Pose(location));
	}
	
	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
	
	public boolean addItem(Item itemToAdd) {
		return items.add(itemToAdd);
	}
	
	public boolean removeItem(int itemIndex) {
		try {
			items.remove(itemIndex);
			return true;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	
	public Item getCurrentItem() {
		return items.get(currentItem);
	}
	
	public boolean setCurrentItem(String itemName) {
		//TODO: implement
		return false;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		if (health < 0) health = 0;
		this.health = health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		if (maxHealth < 0) maxHealth = 0;
		this.maxHealth = maxHealth;
	}

	public int getMovespeed() {
		return movespeed;
	}

	public void setMovespeed(int movespeed) {
		this.movespeed = movespeed;
	}

	public int getCurrentItemIndex() {
		return currentItem;
	}

	public boolean setCurrentItemIndex(int currentItem) {
		if (currentItem>items.size()-1) return false;
		else {
			this.currentItem = currentItem;
			return true;
		}
	}

	@Override
	public Pose getPose() {
		return pose;
	}

	@Override
	public void setPose(Pose pose) {
		this.pose = pose;
	}
	
}
