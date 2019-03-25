package server.engine.ai.newPoseGenerators;

import server.engine.ai.enemyAI.PoseGeneratorUsingEnemy;
import server.engine.state.map.tile.Tile;
import shared.Pose;

import java.util.concurrent.ThreadLocalRandom;

import static server.engine.ai.enemyAI.EnemyAI.*;

public class PoseAroundPlayerGen extends Thread {

    private PoseGeneratorUsingEnemy ai;
    private Pose player;
    private Pose enemy;
    private int distanceToPlayer;
    private boolean attacking;

    public PoseAroundPlayerGen(PoseGeneratorUsingEnemy ai, int distanceToPlayer, boolean attacking, Pose player, Pose enemy){
        this.ai = ai;
        this.distanceToPlayer = distanceToPlayer;
        this.attacking = attacking;
        this.player = player;
        this.enemy = enemy;
    }

    @Override
    public void run() {
        ai.setProcessing(true);
        if(attacking){
            findPoseAroundPlayer();
        }else{
            findPoseAwayFromEnemy();
        }
    }

    private void findPoseAroundPlayer() {
        Pose positionToAttack;
        int expandedRange = 5;
        int angle = getAngle(player, enemy);
        do {
            positionToAttack = poseInDistance(player,
                    ThreadLocalRandom.current().nextInt(angle - expandedRange, angle + expandedRange),
                    ThreadLocalRandom.current().nextInt(
                            distanceToPlayer, distanceToPlayer + expandedRange));
            expandedRange += 5;
        } while ((!pathUnobstructed(positionToAttack, player, ai.getTileMap()))
                || (!tileNotSolid(Tile.locationToTile(positionToAttack), ai.getTileMap())));

        ai.setPoseToGo(positionToAttack);
        ai.setProcessing(false);
    }

    private void findPoseAwayFromEnemy(){
        Pose poseToTeleportAway;
        int decreasedRange = 5;
        distanceToPlayer += 300;
        int angle = getAngle(player, enemy);
        do {
            poseToTeleportAway = poseInDistance(player,
                    ThreadLocalRandom.current().nextInt(angle - decreasedRange, angle + decreasedRange),
                    ThreadLocalRandom.current().nextInt(
                            distanceToPlayer - decreasedRange, distanceToPlayer + decreasedRange));
            decreasedRange += 5;

        } while (!tileNotSolid(Tile.locationToTile(poseToTeleportAway), ai.getTileMap()));

        ai.setPoseToGo(poseToTeleportAway);
        ai.setProcessing(false);
    }

}
