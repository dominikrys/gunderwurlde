package server.engine.ai;

import server.engine.state.map.tile.Tile;
import shared.Pose;

import java.util.Random;

import static server.engine.ai.EnemyAI.poseInDistance;

public class SniperAttackPosesGenerator extends Thread {
    private SniperAI ai;
    private Pose startingPose;

    SniperAttackPosesGenerator(SniperAI ai, Pose startingPose){
        this.ai = ai;
        this.startingPose = startingPose;
    }

    @Override
    public void run() {
        Pose pose;
//        Random rand = new Random();
        do{

            pose =
        }while(!EnemyAI.tileNotSolid(Tile.locationToTile(pose), ai.tileMap));

        ai.setPoseToGo(pose);
    }

}
