package server.engine.state.item.weapon.gun;

import java.util.LinkedList;
import java.util.Random;

import server.engine.state.entity.projectile.Projectile;
import server.engine.state.item.CreatesProjectiles;
import shared.Pose;
import shared.lists.AmmoList;
import shared.lists.ItemList;
import shared.lists.Team;

public abstract class ProjectileGun extends Gun implements CreatesProjectiles {

    private static Random random = new Random();

    protected Projectile projectile;

    ProjectileGun(ItemList gunName, int clipSize, int reloadTime, int ammoPerShot, Projectile projectile,
            AmmoList ammoType, int spread, int coolDown, int projectilesPerShot, int accuracy) {
        super(gunName, clipSize, reloadTime, ammoPerShot, ammoType, spread, coolDown, projectilesPerShot, accuracy);
        this.projectile = projectile;
    }

    @Override
    public LinkedList<Projectile> getProjectiles(Pose gunPose, Team team) {
        LinkedList<Projectile> shotProjectiles = new LinkedList<>();

        int bulletSpacing = 0;
        if (outputPerShot > 1)
            bulletSpacing = (2 * spread) / (outputPerShot - 1);

        LinkedList<Pose> bulletPoses = new LinkedList<>();

        int nextDirection = gunPose.getDirection() - spread;
        for (int i = 0; i < outputPerShot; i++) {
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
