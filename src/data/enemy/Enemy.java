package data.enemy;

import java.util.LinkedHashMap;

import data.HasHealth;
import data.HasPose;
import data.IsMovable;
import data.Pose;
import data.item.Item;

public abstract class Enemy implements HasHealth, HasPose, IsMovable{
	protected int health;
	protected int maxHealth;
	protected int moveSpeed;
	protected Pose pose;
	protected LinkedHashMap<Item,Double> drops;
	
	Enemy(int maxHealth, int moveSpeed, Pose pose, LinkedHashMap<Item,Double> drops) {
		this.maxHealth = maxHealth;
		this.health = maxHealth;
		this.moveSpeed = moveSpeed;
		this.pose = pose;
		this.drops = drops;
	}
	
	Enemy(int maxHealth, int moveSpeed, Pose pose) {
		this(maxHealth, moveSpeed, pose, new LinkedHashMap<Item,Double>());
	}
	
	public LinkedHashMap<Item, Double> getDrops() {
		return drops;
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
	
}
