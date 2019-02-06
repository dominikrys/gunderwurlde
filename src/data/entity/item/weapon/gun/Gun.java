package data.entity.item.weapon.gun;

import data.entity.item.Limited;
import data.entity.item.weapon.Weapon;
import data.entity.projectile.ProjectileList;

public abstract class Gun extends Weapon implements Limited {

    protected int clipSize;
    protected int ammoInClip;
    protected int reloadTime;
    protected long reloadStartTime;
    protected boolean reloading;
    protected int ammoPerShot;
    protected int spread;
    protected ProjectileList projectileType;
    protected AmmoList ammoType;
    protected GunList gunName;
    protected int shootCoolDown; //effectively fire rate
    protected long lastShootTime;

    Gun(GunList gunName, int clipSize, int reloadTime, int ammoPerShot, ProjectileList projectileType,
            AmmoList ammoType, int spread, int coolDown) {
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
        return ammoPerShot;
    }

    public GunList getGunName() {
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

    public ProjectileList getProjectileType() {
        return projectileType;
    }

    public void setProjectileType(ProjectileList projectileType) {
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
