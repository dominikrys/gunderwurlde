package data.item.weapon;

import data.item.ItemType;
import data.item.Limited;
import data.item.projectile.ProjectileType;

abstract class Gun extends Weapon implements Limited {
	
	Gun(String itemName, int itemID) {
		super(itemName, itemID, ItemType.GUN);
	}
	
	protected int maxAmmo;
	protected int currentAmmo;
	protected ProjectileType projectileType;
	public abstract boolean shoot();
	public abstract boolean reload();
}
