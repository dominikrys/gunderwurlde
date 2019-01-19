package data.ai;

import data.HasHealth;
import data.HasPose;
import data.IsMovable;
import data.Pose;

public abstract class Enemy implements HasHealth, HasPose, IsMovable{
	protected int health;
	protected int maxHealth;
	protected int moveSpeed;
	protected Pose pose;
	
	Enemy(int maxHealth, int moveSpeed, Pose pose) {
		this.maxHealth = maxHealth;
		this.health = maxHealth;
		this.moveSpeed = moveSpeed;
		this.pose = pose;
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
