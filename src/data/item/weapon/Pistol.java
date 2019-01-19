package data.item.weapon;

import data.item.projectile.ProjectileType;

public class Pistol extends Gun {
	public static final String ITEM_NAME = "Pistol";
	public static final int ITEM_ID = 0;
	public static final int DEFAULT_AMMO_AMOUNT = 120;
	public static final int DEFAULT_CLIP_SIZE = 12;
	public static final int DEFAULT_RELOAD_TIME = 5;
	public static final int DEFAULT_AMMO_PER_SHOT = 1;
	public static final ProjectileType DEFAULT_AMMO_TYPE = ProjectileType.SMALLBULLET;
	

	public Pistol() {
		super(ITEM_NAME, ITEM_ID, DEFAULT_AMMO_AMOUNT, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT, DEFAULT_AMMO_TYPE);
	}

}
