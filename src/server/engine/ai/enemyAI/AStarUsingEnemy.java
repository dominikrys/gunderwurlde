package server.engine.ai.enemyAI;

import shared.Pose;

import java.util.LinkedList;

public abstract class AStarUsingEnemy extends PoseGeneratorUsingEnemy {
    LinkedList<Pose> posePath;
    private boolean AStartProcessing;

    protected AStarUsingEnemy(long attackDelay) {
        super(attackDelay);
    }

    public synchronized void setTilePath(LinkedList<Pose> aStar){
        posePath = aStar;
        AStartProcessing = false;
    }

    synchronized void setAStarProcessing(boolean processing){
        this.AStartProcessing = processing;
    }

    synchronized boolean getAStarProcessing(){
        return AStartProcessing;
    }

}
