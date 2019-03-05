package server.engine.ai;

import server.engine.state.item.weapon.gun.GrenadeLauncher;

public class GrenaderAI extends SoldierZombieAI {

    public GrenaderAI(int rangeToShoot, int rateOfFire) {
        super(rangeToShoot, rateOfFire, new GrenadeLauncher());
    }

}
