package server.engine.state.item.weapon.gun;

import server.engine.state.item.Limited;
import server.engine.state.item.weapon.Weapon;
import shared.lists.AmmoList;
import shared.lists.EntityList;
import shared.lists.ItemList;

public abstract class Gun extends Weapon implements Limited {

    protected int clipSize;
    protected int ammoInClip;
    protected int reloadTime;
    protected long reloadStartTime;
    protected boolean reloading;
    protected int ammoPerShot;
    protected int spread;
    protected EntityList projectileType;
    protected AmmoList ammoType;
    protected ItemList gunName;
    protected int shootCoolDown; //effectively fire rate
    protected long lastShootTime;
    protected int projectilesPerShot;

    Gun(ItemList gunName, int clipSize, int reloadTime, int ammoPerShot, EntityList projectileType,
            AmmoList ammoType, int spread, int coolDown, int projectilesPerShot) {
        super(gunName);
        this.gunName = gunName;
        this.clipSize = clipSize;
        this.ammoInClip = clipSize;
        this.reloadTime = reloadTime;
        this.ammoPerShot = ammoPerShot;
        this.projectileType = projectileType;
        this.ammoType = ammoType;
        this.shootCoolDown = coolDown;
        this.lastShootTime = 0;
        this.spread = spread;
        this.projectilesPerShot = projectilesPerShot;
    }


    public int getSpread() {
        return spread;
    }

    public void setSpread(int spread) {
        if (spread < 0) spread = -spread;
        if (spread > 360) spread = 360;
        this.spread = spread;
    }

    public int getProjectilesPerShot() {
        return projectilesPerShot;
    }

    public void setProjectilesPerShot(int amount) {
        if (amount < 0)
            amount = 0;
        this.projectilesPerShot = amount;
    }

    public ItemList getGunName() {
        return gunName;
    }

    public int getAmmoInClip() {
        return ammoInClip;
    }

    public AmmoList getAmmoType() {
        return ammoType;
    }

    public void setAmmoType(AmmoList ammoType) {
        this.ammoType = ammoType;
    }

    public int getClipSize() {
        return clipSize;
    }

    public EntityList getProjectileType() {
        return projectileType;
    }

    public void setProjectileType(EntityList projectileType) {
        this.projectileType = projectileType;
    }

    public boolean isReloading() {
        return reloading;
    }

    @Override
    public void replenish() {
        ammoInClip = clipSize;
    }

    @Override
    public void replenish(int amount) { // allows overmax ammo
        ammoInClip += amount;
    }

    @Override
    public void empty() {
        ammoInClip = 0;
    }

    @Override
    public void empty(int amount) {
        if (amount >= ammoInClip)
            ammoInClip = 0;
        else
            ammoInClip -= amount;
    }

    public boolean shoot(int amountAvailable) { // This method doesn't create the projectile
        long now = System.currentTimeMillis();
        if (ammoInClip >= ammoPerShot && (now - lastShootTime) >= shootCoolDown) {
            if (reloading)
                reloading = false;
            ammoInClip -= ammoPerShot;
            lastShootTime = now;
            return true;
        } else if (ammoInClip < ammoPerShot) {
            attemptReload(amountAvailable);
        }
        return false;
    }

    public boolean attemptReload(int amountAvailable) {
        if (!reloading) {
            if (ammoInClip < clipSize && amountAvailable > 0) {
                reloadStartTime = System.currentTimeMillis();
                reloading = true;
                return true;
            }
        }
        return false;
    }

    public int reload(int amountAvailable) {
        int amountTaken = 0;
        if (reloading && (System.currentTimeMillis() - reloadStartTime) >= reloadTime) {
            if ((amountAvailable + ammoInClip) >= clipSize) {
                amountTaken = clipSize - ammoInClip;
                ammoInClip = clipSize;
            } else {
                ammoInClip += amountAvailable;
                amountTaken = amountAvailable;
            }
            reloading = false;
        }
        return amountTaken;
    }
}
