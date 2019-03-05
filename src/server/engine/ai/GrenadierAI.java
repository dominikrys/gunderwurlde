package server.engine.ai;

import server.engine.state.item.weapon.gun.GrenadeLauncher;

public class GrenadierAI extends SoldierZombieAI {

    public GrenadierAI(int rangeToShoot, int rateOfFire) {
        super(rangeToShoot, rateOfFire, new GrenadeLauncher());
    }

}
