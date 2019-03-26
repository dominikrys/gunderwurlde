package server.engine.ai.enemyAI;

import server.engine.ai.aStar.AStar;
import server.engine.state.physics.Force;
import shared.Pose;

import java.util.LinkedList;

public abstract class AStarUsingEnemy extends PoseGeneratorUsingEnemy {
    LinkedList<Pose> posePath;
    private boolean AStartProcessing;
    boolean movementFinished = true;

    protected AStarUsingEnemy(long attackDelay) {
        super(attackDelay);
    }

    public synchronized void setTilePath(LinkedList<Pose> aStar){
        posePath = aStar;
        AStartProcessing = false;
    }

    public synchronized void setAStarProcessing(boolean processing){
        this.AStartProcessing = processing;
    }

    synchronized boolean getAStarProcessing(){
        return AStartProcessing;
    }

    protected Force generateMovementForce(Thread poseGen) {
        if (posePath == null) {
            if (!getAStarProcessing()) {
                if(poseToGo != null) {
//                    Pose posetogoo = new Pose(Tile.tileToLocation(11, 11));
                    new AStar(this, 1, tileMap, pose, poseToGo).start();
                    poseToGo = null;
                }else if (!isProcessing()){
//                    (new PoseAroundPlayerGen(this, RANGE_TO_RUN_AWAY, true,closestPlayer, pose)).start();
                    poseGen.start();
                }
            }
        } else {
            Force angle = getForceFromPath();
            if (angle != null) return angle;
        }
        return new Force(pose.getDirection(), 0);
    }

    private Force getForceFromPath() {
        movementFinished = false;
        if (!Pose.compareLocation(pose, posePath.peekLast(), 5)) {
            int angle = getAngle(pose, posePath.peekLast());
            return new Force(angle, maxMovementForce);
        } else {
            posePath.pollLast();
            if (posePath.size() == 0) {
                posePath = null;
                movementFinished = true;
            }
        }
        return null;
    }

}
