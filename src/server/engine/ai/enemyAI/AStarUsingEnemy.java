package server.engine.ai.enemyAI;

import server.engine.ai.aStar.AStar;
import server.engine.state.physics.Force;
import shared.Pose;

import java.util.LinkedList;

/**
 * Class that enemies that use AStar must extend. It has all of the needed methods.
 */
public abstract class AStarUsingEnemy extends PoseGeneratorUsingEnemy {
    /**
     * The pose path returned by AStar thread
     */
    LinkedList<Pose> posePath;
    /**
     * A boolean to know the state of the AStar thread
     */
    private boolean AStartProcessing;
    /**
     * A boolean to know if the enemy is currently moving
     */
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

    /**
     * Checks if has a pose path to generate force objects and does so or
     * otherwise starts AStar thread to generate the pose path.
     *
     * @param poseGen
     * @return
     */
    protected Force generateMovementForce(Thread poseGen) {
        if (posePath == null) {
            if (!getAStarProcessing()) {
                if(poseToGo != null) {
                    new AStar(this, 1, tileMap, pose, poseToGo).start();
                    poseToGo = null;
                }else if (!isProcessing()){
                    poseGen.start();
                }
            }
        } else {
            Force angle = getForceFromPath();
            if (angle != null) return angle;
        }
        return new Force(pose.getDirection(), 0);
    }

    /**
     * Takes the generated pose path and created force objects for the enemy
     * @return
     */
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
