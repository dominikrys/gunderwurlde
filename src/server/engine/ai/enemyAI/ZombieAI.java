package server.engine.ai.enemyAI;

import server.engine.ai.AIAction;
import java.util.Random;
import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.Location;
import shared.lists.ActionList;
import shared.lists.Team;

/**
 * The AI for Shotgun midget enemy
 */
public class ZombieAI extends EnemyAI {

    private boolean turnLeft;
    private int stepsUntilNormPath = 0;
    boolean randomizePath = true;
    final int DISTANCE_TO_PLAYER_FOR_ATTACK;
    protected Location attackLocation;

    public ZombieAI() {
        super(SHORT_DELAY);
        this.DISTANCE_TO_PLAYER_FOR_ATTACK = Constants.TILE_SIZE;
        this.beginAttackTime = System.currentTimeMillis();
        this.attacking = false;
    }

    public ZombieAI(int distanceToPlayerForAttack, long delay) {
        super(delay);
        this.DISTANCE_TO_PLAYER_FOR_ATTACK = distanceToPlayerForAttack;
        this.beginAttackTime = System.currentTimeMillis();
        this.attacking = false;
    }

    /**
     * Tries to move towards the player and if in range to attack, attacks
     * @return
     */
    @Override
    public AIAction getAction() {
        if(closestPlayer != null) {
            if (attacking) {
                return AIAction.ATTACK;
            } else if (getDistToPlayer(closestPlayer) >= DISTANCE_TO_PLAYER_FOR_ATTACK) {
                return AIAction.MOVE;
            } else if (getDistToPlayer(closestPlayer) < DISTANCE_TO_PLAYER_FOR_ATTACK) {
                this.actionState = ActionList.ATTACKING;
                attacking = true;
                beginAttackTime = System.currentTimeMillis();
                attackLocation = closestPlayer; // Prevents teleporting attacks onto the player
                return AIAction.ATTACK;
            }
            return AIAction.WAIT;
        }else{
            return AIAction.UPDATE;
        }
    }

    protected Force generateMovementForce(){
        int angleToMove = getAngle(pose, closestPlayer);
        if(randomizePath) {
            return new Force((int) randomizePath(angleToMove), maxMovementForce);
        }else{
            return new Force(angleToMove, maxMovementForce);
        }
    }

    /**
     * Has a chance to turn away from the straight path
     *
     * @param angle
     * @return
     */
    private double randomizePath(double angle) {
        Random rand = new Random();
        //change of moving from direct path
        int r = rand.nextInt(500);

        if (stepsUntilNormPath == 0) {
            if (r == 1) {
                turnLeft = true;
                //How much to move to a side
                stepsUntilNormPath = rand.nextInt(100) + 20;
            } else if (r == 0) {
                turnLeft = false;
                stepsUntilNormPath = rand.nextInt(100) + 20;
            } else {
                return angle;
            }
        } else {
            if (turnLeft) {
                angle -= 50;
            } else {
                angle += 50;
            }
            stepsUntilNormPath--;
        }

        return angle;
    }

    @Override
    protected Attack getAttackObj() {
        return new AoeAttack(closestPlayer, 24, 1, Team.ENEMY);
    }
}
