package data.entity.item.weapon;

import data.entity.item.Limited;
import data.entity.projectile.ProjectileList;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public abstract class Gun extends Weapon implements Limited {

    protected int maxAmmo;
    protected int currentAmmo; // includes ammoInClip
    protected int clipSize;
    protected int ammoInClip;
    protected int reloadTime; // in seconds
    protected LocalTime reloadStartTime;
    protected boolean reloading;
    protected int ammoPerShot;
    protected ProjectileList projectileType;
    protected AmmoList ammoType;

    Gun(GunList gunName, int maxAmmo, int clipSize, int reloadTime, int ammoPerShot, ProjectileList projectileType,
            AmmoList ammoType) {
        super(gunName);
        this.maxAmmo = maxAmmo;
        this.clipSize = clipSize;
        this.reloadTime = reloadTime;
        this.ammoPerShot = ammoPerShot;
        this.projectileType = projectileType;
        this.ammoType = ammoType;
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

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
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

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public boolean isReloading() {
        return reloading;
    }

    @Override
    public void replenish() {
        ammoInClip = clipSize;
        currentAmmo = maxAmmo;
    }

    @Override
    public void replenish(int amount) { // allows overmax ammo
        currentAmmo += amount;
    }

    @Override
    public void empty() {
        currentAmmo = 0;
        ammoInClip = 0;
    }

    @Override
    public void empty(int amount) {
        if (amount >= currentAmmo)
            currentAmmo = 0;
        else
            currentAmmo -= amount;
        if (currentAmmo < ammoInClip)
            ammoInClip = currentAmmo;
    }

    public boolean shoot() { // This method doesn't create the projectile ProcessGameState is responsible for
                             // this.
        if (ammoInClip > ammoPerShot) {
            if (reloading)
                reloading = false;
            ammoInClip -= ammoPerShot;
            currentAmmo -= ammoPerShot;
            return true;
        }
        return false;
    }

    public boolean attemptReload() {
        if (!reloading) {
            if (ammoInClip < clipSize && currentAmmo > 0) {
                reloadStartTime = LocalTime.now();
                reloading = true;
                return true;
            }
        }
        return false;
    }

    public boolean reload() {
        if (reloading && (reloadStartTime.until(LocalTime.now(), ChronoUnit.SECONDS)) >= reloadTime) {
            if (currentAmmo >= clipSize)
                ammoInClip = clipSize;
            else
                ammoInClip = currentAmmo;
            reloading = false;
            return true;
        }
        return false;
    }
}
