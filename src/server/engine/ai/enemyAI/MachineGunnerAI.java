package server.engine.ai.enemyAI;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.item.weapon.gun.Smg;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Team;

import java.util.LinkedList;

public class MachineGunnerAI extends ZombieAI {

    //Probably needs a better name
    //The angle the enemy shoots
    private final int TURN_RATE;
    private final int ATTACK_WIDTH;
    private final int BULLETS_PER_ATTACK;
    private boolean addToAngle = false;
    private boolean shootingPathUnobstructed = false;
    private int startOfAttackAngle;
    private int attackAngle;
    private int bulletsShotInThisAttack = 0;
    private boolean delayPast;
    private Gun smg = new Smg();
    private boolean isInAttackPosition = false;
    private int currentAndStartAngDiff;

    public MachineGunnerAI(int attackWidth, int bulletsPerAttack, int turnRate) {
        super(Constants.TILE_SIZE * 10, LONG_DELAY);
        this.ATTACK_WIDTH = attackWidth;
        this.BULLETS_PER_ATTACK = bulletsPerAttack;
        this.TURN_RATE = turnRate;
        randomizePath = false;
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();
        delayPast = (now - beginAttackTime) >= attackDelay;

        if (delayPast && shootingPathUnobstructed && isInAttackPosition) {
            if (bulletsShotInThisAttack != BULLETS_PER_ATTACK) {
                attacks.add(new ProjectileAttack(smg.getProjectiles(
                        new Pose(pose, attackAngle), Team.ENEMY)));

                bulletsShotInThisAttack++;

                if (attackAngle == startOfAttackAngle + ATTACK_WIDTH) {
                    addToAngle = false;
                } else if (attackAngle == startOfAttackAngle) {
                    addToAngle = true;
                }

                if(addToAngle){
                    attackAngle++;
                }else{
                    attackAngle--;
                }

            } else {
                this.actionState = ActionList.NONE;
                bulletsShotInThisAttack = 0;
                attacking = false;
                shootingPathUnobstructed = false;
                isInAttackPosition = false;
            }
        } else {
            startOfAttackAngle = Pose.normaliseDirection(getAngle(pose, closestPlayer) - ATTACK_WIDTH / 2);
            currentAndStartAngDiff = Pose.getDifferenceBetweenAngles(startOfAttackAngle, pose.getDirection());
            attackAngle = startOfAttackAngle;
            shootingPathUnobstructed = pathUnobstructed(pose, closestPlayer, tileMap);
        }

        return attacks;
    }

    @Override
    public Force getForceFromAttack(double maxMovementForce) {
        if (delayPast && isInAttackPosition) {
            return new Force(attackAngle, 0);
        } else {
            if(currentAndStartAngDiff > 0){
                return new Force(pose.getDirection() + TURN_RATE, 0);
            }else if(currentAndStartAngDiff < 0){
                return new Force(pose.getDirection() - TURN_RATE, 0);
            }else {
                isInAttackPosition = true;
                return new Force(pose.getDirection(), 0);
            }
        }
    }
}
