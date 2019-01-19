package data.item.weapon;

import data.item.projectile.ProjectileType;

public class Pistol extends Gun {
	public static final int DEFAULT_AMMO_AMOUNT = 180;
	public static final ProjectileType DEFAULT_AMMO_TYPE = ProjectileType.SMALLBULLET;
	

	public Pistol() {
		super("Pistol", 0);
		this.maxAmmo = DEFAULT_AMMO_AMOUNT;
		this.currentAmmo = maxAmmo;
		this.projectileType = DEFAULT_AMMO_TYPE;
	}

	@Override
	public void replenish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replenish(int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void empty() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void empty(int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean shoot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reload() {
		// TODO Auto-generated method stub
		return false;
	}

}
