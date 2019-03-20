package server.engine.ai.enemyAI;

import server.engine.ai.AIAction;
import server.engine.ai.aStar.AStar;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.item.weapon.gun.SniperRifle;
import server.engine.state.map.tile.Tile;
import server.engine.state.physics.Force;
import shared.Pose;
import shared.lists.Team;

import java.util.LinkedList;

public class SniperAI extends AStarUsingEnemy {

    private final int RANGE_TO_RUN_AWAY;
    Gun gun = new SniperRifle();
    private Pose poseToGo;
    private boolean runningAway;
    private boolean inPositionToAttack = false;
    private boolean playerAiming = false;
    private long beginAttackTime;
    private LinkedList<Pose> posePath;
    private boolean AStartProcessing = false;

    public SniperAI(int rangeToRunAway) {
        super();
        this.RANGE_TO_RUN_AWAY = rangeToRunAway;
    }

    @Override
    public AIAction getAction() {
        if (attacking) {
            return attackController();
        } else if (inPositionToAttack) {
            attacking = true;
            beginAttackTime = System.currentTimeMillis();
            return attackController();
        } else {
            return AIAction.MOVE;
        }
    }

    public AIAction attackController() {
        //Check if still in range or if player is not aiming at us
        if (!(inRangeToRun() && playerAiming)) {
            return AIAction.ATTACK;
            //if aiming, run away
        } else if (playerAiming) {
            //Run away

            //if in range for a few secs, move away to safer location
        } else {


        }
        return AIAction.WAIT;
    }

    private boolean inRangeToRun() {
        return false;
    }


    @Override
    protected Attack getAttackObj() {
        int angle = getAngle(pose, closestPlayer);
        return new ProjectileAttack(gun.getShotProjectiles(new Pose(pose, angle), Team.ENEMY));
    }

    @Override
    protected Force generateMovementForce() {
        if (runAway()) {
            if (posePath == null && !AStartProcessing)
                new AStar(this, 0.8, tileMap, pose, (Pose) Tile.tileToLocation(25, 15)).run();
        } else {
            if (posePath == null) {
                if (!AStartProcessing) {
                    (new AStar(this, 1, tileMap, pose, closestPlayer)).start();
                    AStartProcessing = true;
                } else {
                    //cloak and move somewhere?
                }
            } else {
//                System.out.println("pose to go: " + posePath.peekLast());
                if (!Pose.compareLocation(pose, posePath.peekLast(), 5)) {
                    int angle = getAngle(pose, posePath.peekLast());
                    return new Force(angle, maxMovementForce);
                } else {
                    posePath.pollLast();
                    if (posePath.size() == 0) {
                        posePath = null;
                    }
                }
            }
        }
        return new Force(pose.getDirection(), 0);
    }

    private boolean runAway() {
        return false;
    }

    public  void setTilePath(LinkedList<Pose> aStar){
        System.out.println("Returned direction poses: ");
        for (Pose pose : aStar) {
            System.out.println(Tile.locationToTile(pose)[0] + " " + Tile.locationToTile(pose)[1]);
        }

        posePath = aStar;
        AStartProcessing = false;
    }

}
