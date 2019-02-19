package server.engine.ai;

import server.engine.state.map.tile.Tile;
import shared.Pose;

import java.util.Random;

public class GenerateSoldierPath extends Thread {
    private SoldierZombieAI ai;
    private Pose startingPose;

    GenerateSoldierPath(SoldierZombieAI ai, Pose startingPose){
        this.ai = ai;
        this.startingPose = startingPose;
    }

    @Override
    public void run() {
        Pose pose;
        Random rand = new Random();
        do{
            //Go in any direction between 10 and 100 poses
            pose = poseInDistance(startingPose, rand.nextInt(360), rand.nextInt(100) + 10);
        }while(!EnemyAI.tileNotSolid(Tile.locationToTile(pose), ai.tileMap));

        ai.setPoseToGo(pose);
    }

    // vec = dist * (cos(angle)i + sin(angle)j)
    private Pose poseInDistance(Pose startingPose, int angleToPose, int distanceToPose) {
        double vecI = Math.cos(angleToPose) * distanceToPose;
        double vecJ = Math.sin(angleToPose) * distanceToPose;

        return new Pose((int) vecI + startingPose.getX(), (int) vecJ + startingPose.getY());
    }
}
