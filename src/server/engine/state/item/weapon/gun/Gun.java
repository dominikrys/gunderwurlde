package server.engine.state.item.weapon.gun;

import java.util.LinkedList;
import java.util.Random;

import server.engine.state.item.Limited;
import server.engine.state.item.weapon.Weapon;
import shared.Pose;
import shared.lists.AmmoList;
import shared.lists.ItemList;

/**
 * Class for Guns.
 * 
 * @author Richard
 *
 */
public abstract class Gun extends Weapon implements Limited {
    private static Random random = new Random();

    protected int clipSize;
    protected int ammoInClip;
    protected int reloadTime;
    protected long reloadStartTime;
    protected boolean reloading;
    protected int ammoPerShot;
    protected int spread;
    protected AmmoList ammoType;
    protected ItemList gunName;
    protected int shootCoolDown; //effectively fire rate
    protected long lastShootTime;
    protected int outputPerShot;
    protected int accuracy;

    Gun(ItemList gunName, int clipSize, int reloadTime, int ammoPerShot, AmmoList ammoType, int spread, int coolDown, int outputPerShot, int accuracy) {
        super(gunName);
        this.gunName = gunName;
        this.clipSize = clipSize;
        this.ammoInClip = clipSize;
        this.reloadTime = reloadTime;
        this.ammoPerShot = ammoPerShot;
        this.ammoType = ammoType;
        this.shootCoolDown = coolDown;
        this.lastShootTime = 0;
        this.spread = spread;
        this.outputPerShot = outputPerShot;
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

    public int getOutputPerShot() {
        return outputPerShot;
    }

    public void setOutputPerShot(int amount) {
        if (amount < 0)
            amount = 0;
        this.outputPerShot = amount;
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

    protected LinkedList<Pose> getShotPoses(Pose gunPose) {
        int spacing = 0;
        if (outputPerShot > 1)
            spacing = (2 * spread) / (outputPerShot - 1);

        LinkedList<Pose> poses = new LinkedList<>();

        int nextDirection = gunPose.getDirection() - spread;
        for (int i = 0; i < outputPerShot; i++) {
            int direction = nextDirection;
            if (accuracy != 0)
                direction += (random.nextInt(accuracy) - (accuracy / 2));
            poses.add(new Pose(gunPose, direction));
            nextDirection += spacing;
        }

        return poses;
    }

}
