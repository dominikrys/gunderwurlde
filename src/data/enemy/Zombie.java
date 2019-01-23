package data.enemy;

import data.HasContactDamage;
import data.Pose;

public class Zombie extends Enemy implements HasContactDamage {
	public static final int DEFAULT_HEALTH = 2;
	public static final int DEFAULT_MOVESPEED = 6;
	
	protected int contactDamage = 1;
	
	Zombie(Pose pose) {
		this(DEFAULT_HEALTH, DEFAULT_MOVESPEED, pose);
	}

	Zombie(int maxHealth, int moveSpeed, Pose pose) {
		super(maxHealth, moveSpeed, pose, EnemyList.ZOMBIE);
	}

	@Override
	public int getContactDamage() {
		return contactDamage;
	}

	@Override
	public void setContactDamage(int contactDamage) {
		this.contactDamage = contactDamage;
	}
	
}
