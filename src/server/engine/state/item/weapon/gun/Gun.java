package server.engine.state.item.weapon.gun;

import server.engine.state.entity.projectile.Projectile;
import server.engine.state.item.Limited;
import server.engine.state.item.weapon.Weapon;
import shared.Pose;
import shared.lists.AmmoList;
import shared.lists.ItemList;
import shared.lists.Team;

import java.util.LinkedList;
import java.util.Random;

public abstract class Gun extends Weapon implements Limited {

    private static Random random = new Random();

    protected int clipSize;
    protected int ammoInClip;
    protected int reloadTime;
    protected long reloadStartTime;
    protected boolean reloading;
    protected int ammoPerShot;
    protected int spread;
    protected Projectile projectile;
    protected AmmoList ammoType;
    protected ItemList gunName;
    protected int shootCoolDown; //effectively fire rate
    protected long lastShootTime;
    protected int projectilesPerShot;
    protected int accuracy;

    Gun(ItemList gunName, int clipSize, int reloadTime, int ammoPerShot, Projectile projectile,
            AmmoList ammoType, int spread, int coolDown, int projectilesPerShot, int accuracy) {
        super(gunName);
        this.gunName = gunName;
        this.clipSize = clipSize;
        this.ammoInClip = clipSize;
        this.reloadTime = reloadTime;
        this.ammoPerShot = ammoPerShot;
        this.projectile = projectile;
        this.ammoType = ammoType;
        this.shootCoolDown = coolDown;
        this.lastShootTime = 0;
        this.spread = spread;
        this.projectilesPerShot = projectilesPerShot;
        this.accuracy = accuracy;
    }

    public boolean isAutoFire() {
        return (shootCoolDown <= 800);
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        if (accuracy < 0) accuracy = -accuracy;
        if (accuracy > 180) accuracy = 180;
        this.accuracy = accuracy;
    }

    public int getSpread() {
        return spread;
    }

    public void setSpread(int spread) {
        if (spread < 0) spread = -spread;
        if (spread > 180) spread = 180;
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

    public Projectile getProjectile() {
        return projectile;
    }

    public void setProjectile(Projectile projectile) {
        this.projectile = projectile;
    }

    public boolean isReloading() {
        return reloading;
    }

    public int getReloadTime() {
        return reloadTime;
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

    public void cancelReload() {
        this.reloading = false;
    }

    public LinkedList<Projectile> getShotProjectiles(Pose gunPose, Team team) {
        LinkedList<Projectile> shotProjectiles = new LinkedList<>();

        int bulletSpacing = 0;
        if (projectilesPerShot > 1)
            bulletSpacing = (2 * spread) / (projectilesPerShot - 1);

        LinkedList<Pose> bulletPoses = new LinkedList<>();

        int nextDirection = gunPose.getDirection() - spread;
        for (int i = 0; i < projectilesPerShot; i++) {
            int direction = nextDirection;
            if (accuracy != 0)
                direction += (random.nextInt(accuracy) - (accuracy / 2));
            bulletPoses.add(new Pose(gunPose, direction));
            nextDirection += bulletSpacing;
        }

        for (Pose p : bulletPoses) {
            Projectile proj = projectile.createFor(p, team);
            shotProjectiles.add(proj);
        }

        return shotProjectiles;
    }
}
