package server.engine.ai.enemyAI;

import server.engine.ai.AIAction;
import server.engine.ai.newPoseGenerators.RandomPoseGen;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.ProjectileGun;
import server.engine.state.item.weapon.gun.RocketLauncher;
import server.engine.state.physics.Force;
import shared.Pose;
import shared.lists.Team;

import java.util.Random;

/**
 * The AI for Shotgun midget enemy
 */
public class SoldierZombieAI extends AStarUsingEnemy {

    private final int RANGE_TO_SHOOT;
    private final int RATE_OF_FIRE;
    private final int DISTANCE_TO_MOVE;
    private Random rand = new Random();
    private ProjectileGun gun = new RocketLauncher();

    public SoldierZombieAI(int rangeToShoot, int rateOfFire, int distanceToMove) {
        super(SHORT_DELAY);
        this.RANGE_TO_SHOOT = rangeToShoot;
        this.RATE_OF_FIRE = rateOfFire;
        this.DISTANCE_TO_MOVE = distanceToMove;
    }

    /**
     * Randomly decides to move in any direction and if the player is in range, shoots him
     * @return
     */
    @Override
    public AIAction getAction() {
        int distToPlayer;
        if (closestPlayer != null) {
            distToPlayer = getDistToPlayer(closestPlayer);
        } else {
            distToPlayer = RANGE_TO_SHOOT + 1;
        }

        if (attacking) {                                //If attacking, continue to attack
            return AIAction.ATTACK;
        } else if (distToPlayer < RANGE_TO_SHOOT
                && pathUnobstructed(pose, closestPlayer, tileMap)) {
            int decision = rand.nextInt(100);
            //Will decide whether to attack based on the RATE_OF_FIRE
            if (decision <= RATE_OF_FIRE && decision >= 2) {
                attacking = true;
                beginAttackTime = System.currentTimeMillis();
                return AIAction.ATTACK;
            } else if (decision < 2 && decision > 0) {
                return AIAction.MOVE;
            } else {
                return AIAction.WAIT;
            }
        } else if (!movementFinished) {                    //If moving, continue to move
            return AIAction.MOVE;

        } else if (distToPlayer >= RANGE_TO_SHOOT) {
            //1 in 50 change it will decide to move
            if (rand.nextInt(50) == 0) {
                return AIAction.MOVE;
            } else {
                return AIAction.WAIT;
            }
        }

        return AIAction.WAIT;
    }

    protected Force generateMovementForce() {
        return generateMovementForce(new RandomPoseGen(this, pose, DISTANCE_TO_MOVE));
    }

    @Override
    protected Attack getAttackObj() {
        int angle = getAngle(pose, closestPlayer);
        return new ProjectileAttack(gun.getProjectiles(new Pose(pose, angle), Team.ENEMY, 0));
    }

}
