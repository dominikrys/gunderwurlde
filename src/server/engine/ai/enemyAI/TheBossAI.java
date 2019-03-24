package server.engine.ai.enemyAI;

import server.engine.ai.AIAction;
import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.*;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Team;

import java.util.ArrayList;
import java.util.Random;

public class TheBossAI extends ZombieAI {

    private final long TIME_BETWEEN_ATTACKS;
    private ArrayList<Gun> gunList = new ArrayList<>();
    private long lastAttack;
    private Random rand;

    public TheBossAI(long timeBetweenAttacks) {
        super();
        randomizePath = false;
        this.TIME_BETWEEN_ATTACKS = timeBetweenAttacks;
        rand = new Random();
        gunList.add(new FireGun());
        gunList.add(new CrystalLauncher());
        gunList.add(new IceGun());
        gunList.add(new RingOfDeath());
    }

    @Override
    public AIAction getAction() {
        long now = System.currentTimeMillis();
        if (attacking) {
            return AIAction.ATTACK;
        } else if (now - lastAttack >= TIME_BETWEEN_ATTACKS) {
            this.actionState = ActionList.ATTACKING;
            attacking = true;
            lastAttack = System.currentTimeMillis();
            attackLocation = closestPlayer; // Prevents teleporting attacks onto the player
            return AIAction.ATTACK;
        }
        return AIAction.MOVE;
    }

    @Override
    protected Attack getAttackObj() {
        int attackAngle = getAngle(pose, closestPlayer);
        int weapon = rand.nextInt(gunList.size());
        return new ProjectileAttack(gunList.get(weapon).getShotProjectiles(new Pose(pose, attackAngle), Team.ENEMY));
    }


}
