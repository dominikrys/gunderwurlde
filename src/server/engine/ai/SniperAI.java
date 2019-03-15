package server.engine.ai;

import javafx.util.Pair;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.item.weapon.gun.SniperRifle;
import server.engine.state.map.tile.Tile;
import server.engine.state.physics.Force;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Teams;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class SniperAI extends EnemyAI {

    private final int RANGE_TO_RUN_AWAY;
    Gun gun = new SniperRifle();
    private Pose poseToGo;
    private boolean runningAway;
    private boolean inPositionToAttack = false;
    private boolean playerAiming = false;
    private long beginAttackTime;
    private LinkedHashSet<Pair<Integer, Integer>> posePath;

    public SniperAI(int rangeToRunAway){
        super();
        this.RANGE_TO_RUN_AWAY = rangeToRunAway;
    }

    @Override
    public AIAction getAction() {
        if (attacking) {
            return attackController();
        }else if(inPositionToAttack){
            attacking = true;
            beginAttackTime = System.currentTimeMillis();
            return attackController();
        }else{
            return AIAction.MOVE;
        }
    }

    public AIAction attackController() {
        //Check if still in range or if player is not aiming at us
        if(!(inRangeToRun() && playerAiming)){
            return AIAction.ATTACK;
            //if aiming, run away
        }else if(playerAiming){
            //Run away

            //if in range for a few secs, move away to safer location
        }else{


        }
        return AIAction.WAIT;
    }

    private boolean inRangeToRun() {
        return false;
    }


    @Override
    protected Attack getAttackObj() {
        int angle = getAngle(pose, closestPlayer);
        return new ProjectileAttack(gun.getShotProjectiles(new Pose(pose, angle), Teams.ENEMY));
    }

    @Override
    protected Force generateMovementForce() {
        if(runAway()){
            if(posePath == null)
                new AStarForTiles(this, 0.8, tileMap, pose, (Pose) Tile.tileToLocation(25, 15)).run();
        }else{
            if(posePath == null) {
                System.out.println();
                Pose endPose = new Pose(Tile.tileToLocation(25, 4));
                new AStarForTiles(this, 1, tileMap, pose, endPose).run();
            }else{
//                int[] endTile= Tile.locationToTile((Pose)posePath.toArray()[ posePath.size()-1 ]);
//                System.out.println(endTile[0] + " " + endTile[1]);
            }
            //calc pos and move using aStart
        }
        return null;
    }

    private boolean runAway() {
        return false;
    }

    synchronized void setPoseToGo(Pose pose) {
        poseToGo = pose;
        setProcessing(false);
    }

    synchronized void setTilePath(LinkedHashSet<Pair<Integer, Integer>> aStar) {
        for (Pair<Integer, Integer> pair :
                aStar) {
            System.out.println(pair.getKey() + " " + pair.getValue());
        }
        posePath = aStar;
    }

}
