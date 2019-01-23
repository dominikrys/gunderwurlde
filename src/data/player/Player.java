package data.player;

import java.util.ArrayList;

import data.HasHealth;
import data.HasPose;
import data.IsMovable;
import data.Location;
import data.Pose;
import data.item.Item;
import data.item.weapon.Pistol;

public class Player implements HasPose, HasHealth, IsMovable{
	public static final int DEFAULT_HEALTH = 6;
	public static final int DEFAULT_MOVESPEED = 10;
	public static final int DEFAULT_TEAM = 0;
	public static final int DEFAULT_SCORE = 0;
	
	private static int nextPlayerID = 0;
	
	protected ArrayList<Item> items;
	protected int health;
	protected int maxHealth;
	protected int moveSpeed;
	protected int currentItem;
	protected Pose pose;
	protected int score;
	protected int team;
	protected int playerID;
	
	public Player(Pose pose, int team) {
		this.health = DEFAULT_HEALTH;
		this.maxHealth = health;
		this.moveSpeed = DEFAULT_MOVESPEED;
		this.items = new ArrayList<Item>(){{new Pistol();}};
		this.currentItem = 0;
		this.pose = pose;
		this.score = DEFAULT_SCORE;
		this.team = team;
		this.playerID = nextPlayerID++;
	}
	
	public Player(Location location, int team) {
		this(new Pose(location), team);
	}
	
	public Player(Pose pose) {
		this(pose, DEFAULT_TEAM);
	}
	
	public Player(Location location) {
		this(location, DEFAULT_TEAM);
	}
	
	public int getPlayerID() {
		return playerID;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public boolean setItems(ArrayList<Item> items) {
		if (items.size() >0) {
			this.items = items;
			return true;
		}
		return false;
	}
	
	public boolean addItem(Item itemToAdd) {
		return items.add(itemToAdd);
	}
	
	public boolean removeItem(int itemIndex) {
		try {
			items.remove(itemIndex);
			if (currentItem == itemIndex) previousItem();
			return true;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	
	public Item getCurrentItem() {
		return items.get(currentItem);
	}
	
	public void setCurrentItem(int slot) {
		if (slot < 0) slot = 0;
		else if (slot > items.size()-1) slot = items.size()-1;
		currentItem = slot;
	}
	
	public void nextItem() {
		if (currentItem == items.size()-1) currentItem = 0;
		else currentItem++;
	}
	
	public void previousItem() {
		if (currentItem ==0) currentItem = items.size()-1;
		else currentItem--;
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
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public void changeScore(int value) {
		this.score+=value;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
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
		if (health < 0) health = 0;
		this.health = health;
	}
	
	@Override
	public boolean damage(int amount) {
		if (amount >= health) {
			health = 0;
			return true;
		}
		else {
			health-=amount;
			return false;
		}
	}

	@Override
	public int getMaxHealth() {
		return maxHealth;
	}
	
	@Override
	public void setMaxHealth(int maxHealth) {
		if (maxHealth < 0) maxHealth = 0;
		this.maxHealth = maxHealth;
	}
	
	@Override
	public Pose getPose() {
		return pose;
	}

	@Override
	public void setPose(Pose pose) {
		this.pose = pose;
	}

	@Override
	public Location getLocation() {
		return pose; //will this work?
	}

	@Override
	public void setLocation(Location location) {
		this.pose = new Pose(location);
	}

}
