package server.engine.state.item.weapon.gun;

import java.util.LinkedList;

import server.engine.state.entity.projectile.Projectile;
import server.engine.state.entity.projectile.SmallBullet;
import shared.Pose;
import shared.lists.AmmoList;
import shared.lists.ItemList;
import shared.lists.Team;

/**
 * 
 * @author Richard
 *
 */
public class BuckshotShotgun extends Shotgun {
    public static final ItemList NAME = ItemList.BUCKSHOT_SHOTGUN;
    public static final int DEFAULT_CLIP_SIZE = 2;
    public static final int DEFAULT_RELOAD_TIME = 500;
    public static final int DEFAULT_AMMO_PER_SHOT = 2;
    public static final int DEFAULT_PROJECTILES_PER_SHOT = 12;
    public static final Projectile DEFAULT_PROJECTILE = new SmallBullet();
    public static final AmmoList DEFAULT_AMMO_TYPE = AmmoList.SHOTGUN_ROUND;
    public static final int DEFAULT_SPREAD = 30;
    public static final int DEFAULT_COOL_DOWN = 600;
    public static final int DEFAULT_ACCURACY = 8;
    
    protected int ammoUsed;

    public BuckshotShotgun() {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE, DEFAULT_AMMO_TYPE, DEFAULT_SPREAD, DEFAULT_COOL_DOWN, DEFAULT_PROJECTILES_PER_SHOT, DEFAULT_ACCURACY);
    }

    public BuckshotShotgun(int projectilesPerShot, int spread, int coolDown) {
        super(NAME, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_AMMO_PER_SHOT,
                DEFAULT_PROJECTILE, DEFAULT_AMMO_TYPE, spread, coolDown, projectilesPerShot, DEFAULT_ACCURACY);
    }
    
    @Override
    public boolean shoot(int amountAvailable) {
        long now = System.currentTimeMillis();
        if (ammoInClip != 0 && (now - lastShootTime) >= shootCoolDown) {
            if (reloading)
                reloading = false;
            ammoUsed = ammoInClip;
            ammoInClip = 0;
            lastShootTime = now;
            return true;
        } else if (ammoInClip == 0) {
            attemptReload(amountAvailable);
        }
        return false;
    }

    @Override
    public LinkedList<Projectile> getProjectiles(Pose gunPose, Team team, int desiredDistance) {
        int oldProjectsPileShot = outputPerShot;
        outputPerShot = (int) (outputPerShot * (Double.valueOf(ammoUsed) / ammoPerShot));
        LinkedList<Projectile> shotProjectiles = super.getProjectiles(gunPose, team, desiredDistance);
        outputPerShot = oldProjectsPileShot;
        return shotProjectiles;
    }


}
