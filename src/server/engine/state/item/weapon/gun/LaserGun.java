package server.engine.state.item.weapon.gun;

import java.util.LinkedList;
import java.util.Random;

import server.engine.state.item.CreatesLasers;
import server.engine.state.laser.Laser;
import shared.Pose;
import shared.lists.AmmoList;
import shared.lists.ItemList;
import shared.lists.Team;

public abstract class LaserGun extends Gun implements CreatesLasers {

    private static Random random = new Random();

    protected Laser laser;

    LaserGun(ItemList gunName, int clipSize, int reloadTime, int ammoPerShot, AmmoList ammoType, int spread, int coolDown, int projectilesPerShot, int accuracy,
            Laser laser) {
        super(gunName, clipSize, reloadTime, ammoPerShot, ammoType, spread, coolDown, projectilesPerShot, accuracy);
        this.laser = laser;
    }

    @Override
    public LinkedList<Laser> getLasers(Pose gunPose, Team team) {
        LinkedList<Laser> shotLasers = new LinkedList<>();
        // TODO

        return shotLasers;
    }
}
