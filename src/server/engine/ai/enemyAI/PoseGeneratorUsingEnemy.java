package server.engine.ai.enemyAI;


import shared.Pose;

/**
 * A class neeed for every enemy AI that uses pose generators
 */
public abstract class PoseGeneratorUsingEnemy extends EnemyAI {
    Pose poseToGo;

    PoseGeneratorUsingEnemy(long attackDelay) {
        super(attackDelay);
    }

    public synchronized void setPoseToGo(Pose pose) {
        poseToGo = pose;
        setProcessing(false);
    }
}
