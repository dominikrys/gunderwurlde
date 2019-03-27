package server.engine.ai.enemyAI;

import server.engine.ai.AIAction;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.entity.projectile.Projectile;
import server.engine.state.item.weapon.gun.*;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Team;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class TheBossAI extends ZombieAI {

    private final long TIME_BETWEEN_ATTACKS;
    private ArrayList<ProjectileGun> gunList = new ArrayList<>();
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
        gunList.add(new PlasmaPistol());
        gunList.add(new Smg());
        gunList.add(new MachineGun());
    }

    @Override
    public AIAction getAction() {
        if(closestPlayer != null) {
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
        }else{
            return AIAction.WAIT;
        }
    }

    @Override
    protected Attack getAttackObj() {
        int attackAngle = getAngle(pose, closestPlayer);
        int weapon = rand.nextInt(gunList.size());
        LinkedList<Projectile> attackProjectiles = new LinkedList<>(gunList.get(weapon).getProjectiles(new Pose(pose, attackAngle), Team.ENEMY, 0));

        weapon = rand.nextInt(gunList.size());
        attackProjectiles.addAll(gunList.get(weapon).getProjectiles(new Pose(pose, attackAngle - 10), Team.ENEMY, 0));
        weapon = rand.nextInt(gunList.size());
        attackProjectiles.addAll(gunList.get(weapon).getProjectiles(new Pose(pose, attackAngle + 10), Team.ENEMY, 0));
        return new ProjectileAttack(attackProjectiles);
    }


}
