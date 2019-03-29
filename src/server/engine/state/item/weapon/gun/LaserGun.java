package server.engine.state.item.weapon.gun;

import java.util.LinkedList;

import server.engine.state.item.CreatesLasers;
import server.engine.state.laser.Laser;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.AmmoList;
import shared.lists.ItemList;
import shared.lists.Team;

/**
 * 
 * @author Richard
 *
 */
public abstract class LaserGun extends Gun implements CreatesLasers {
    protected Laser laser;

    LaserGun(ItemList gunName, int clipSize, int reloadTime, int ammoPerShot, AmmoList ammoType, int spread, int coolDown, int projectilesPerShot, int accuracy,
            Laser laser) {
        super(gunName, clipSize, reloadTime, ammoPerShot, ammoType, spread, coolDown, projectilesPerShot, accuracy);
        this.laser = laser;
    }

    @Override
    public LinkedList<Laser> getLasers(Pose gunPose, Team team, Tile[][] tileMap) {
        LinkedList<Laser> shotLasers = new LinkedList<>();
        LinkedList<Pose> laserPoses = getShotPoses(gunPose);

        for (Pose p : laserPoses) {
            Laser l = Laser.DrawLaser(p, tileMap, laser, team);
            shotLasers.add(l);
        }

        return shotLasers;
    }
}
