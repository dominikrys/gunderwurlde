package server.engine.ai.newPoseGenerators;

import server.engine.ai.enemyAI.EnemyAI;
import server.engine.ai.enemyAI.SoldierZombieAI;
import server.engine.state.map.tile.Tile;
import shared.Pose;

import java.util.Random;

import static server.engine.ai.enemyAI.EnemyAI.poseInDistance;

public class RandomPoseGen extends Thread {
    private SoldierZombieAI ai;
    private Pose startingPose;

    public RandomPoseGen(SoldierZombieAI ai, Pose startingPose){
        this.ai = ai;
        this.startingPose = startingPose;
    }

    @Override
    public void run() {
        Pose pose;
        Random rand = new Random();
        do{
            //Go in any direction between 40 and 150 poses
            pose = poseInDistance(startingPose, rand.nextInt(360), rand.nextInt(150) + 40);
        }while(!EnemyAI.tileNotSolid(Tile.locationToTile(pose), ai.getTileMap()));

        ai.setPoseToGo(pose);
    }
}
