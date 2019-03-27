package server.engine.state.item.weapon.gun;

import java.util.LinkedList;

import server.engine.state.entity.projectile.Projectile;
import server.engine.state.item.CreatesProjectiles;
import shared.Pose;
import shared.lists.AmmoList;
import shared.lists.ItemList;
import shared.lists.Team;

public abstract class ProjectileGun extends Gun implements CreatesProjectiles {

    protected Projectile projectile;

    ProjectileGun(ItemList gunName, int clipSize, int reloadTime, int ammoPerShot, Projectile projectile,
            AmmoList ammoType, int spread, int coolDown, int projectilesPerShot, int accuracy) {
        super(gunName, clipSize, reloadTime, ammoPerShot, ammoType, spread, coolDown, projectilesPerShot, accuracy);
        this.projectile = projectile;
    }

    @Override
    public LinkedList<Projectile> getProjectiles(Pose gunPose, Team team, int desiredDistance) {
        LinkedList<Projectile> shotProjectiles = new LinkedList<>();
        LinkedList<Pose> bulletPoses = getShotPoses(gunPose);

        for (Pose p : bulletPoses) {
            Projectile proj = projectile.createFor(p, team);
            shotProjectiles.add(proj);
        }

        return shotProjectiles;
    }
}
