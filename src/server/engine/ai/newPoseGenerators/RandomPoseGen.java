package server.engine.ai.newPoseGenerators;

import server.engine.ai.enemyAI.EnemyAI;
import server.engine.ai.enemyAI.PoseGeneratorUsingEnemy;
import server.engine.ai.enemyAI.SoldierZombieAI;
import server.engine.state.map.tile.Tile;
import shared.Pose;

import java.util.Random;

import static server.engine.ai.enemyAI.EnemyAI.poseInDistance;

/**
 * Generates a random pose in a specified distance for the starting pose
 */
public class RandomPoseGen extends Thread {
    private final PoseGeneratorUsingEnemy AI;
    private final Pose STARTING_POSE;
    private final int DISTANCE;


    public RandomPoseGen(PoseGeneratorUsingEnemy ai, Pose startingPose, int distance){
        this.AI = ai;
        this.STARTING_POSE = startingPose;
        this.DISTANCE = distance;
    }

    /**
     * Takes a random tile in the distance and if it's not solid, returns it tu the ai
     */
    @Override
    public void run() {
        Pose pose;
        Random rand = new Random();
        do{
            //Go in any direction between 40 and 150 poses
            pose = poseInDistance(STARTING_POSE, rand.nextInt(360), (int) (rand.nextInt(DISTANCE) + DISTANCE * 0.5));
        }while(!EnemyAI.tileNotSolid(Tile.locationToTile(pose), AI.getTileMap()));

        AI.setPoseToGo(pose);
    }
}
