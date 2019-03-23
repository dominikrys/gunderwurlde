package server.engine.ai.enemyAI;

import shared.Pose;

import java.util.LinkedList;

public abstract class AStarUsingEnemy extends EnemyAI {
    LinkedList<Pose> posePath;
    boolean AStartProcessing;

    protected AStarUsingEnemy(long attackDelay) {
        super(attackDelay);
    }

    public abstract void setTilePath(LinkedList<Pose> aStar);

}
