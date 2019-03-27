package server.engine.ai.enemyAI;

import org.junit.BeforeClass;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.entity.enemy.Zombie;
import shared.Pose;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class EnemyAITest {

    private static EnemyAI enemy;

    @BeforeClass
    public static void setup() {
        enemy = new ZombieAI();
        HashSet<Pose> playerPoses = new HashSet<Pose>(Arrays.asList(
                new Pose(300, 300, 90),
                new Pose(200, 200, 180),
                new Pose(100, 100, 270)
        ));

        enemy.setInfo(new Zombie(), playerPoses);
    }

    @org.junit.Test
    public void getAttacks() {

    }

    @org.junit.Test
    public void getTileMap() {
    }

    @org.junit.Test
    public void getActionState() {
    }

    @org.junit.Test
    public void getMovementForce() {
    }

    @org.junit.Test
    public void getDistToPlayer() {
    }

    @org.junit.Test
    public void tileNotSolid() {
    }

    @org.junit.Test
    public void testSetInfo() {
        Enemy newEnemy = new Zombie();
        HashSet<Pose> newPlayerPoses = new HashSet<Pose>(Arrays.asList(
                new Pose(300, 300, 90),
                new Pose(200, 200, 180)
        ));
        enemy.setInfo(newEnemy, newPlayerPoses);

        assertEquals(enemy.enemy, newEnemy);
        assertEquals(enemy.getPlayerPoses(), newPlayerPoses);
    }

    @org.junit.Test
    public void transposeMatrix() {
    }

    @org.junit.Test
    public void getAngle() {
    }

    @org.junit.Test
    public void getForceFromAttack() {
    }

    @org.junit.Test
    public void pathUnobstructed() {
    }

    @org.junit.Test
    public void poseInDistance() {
    }

    @org.junit.Test
    public void getUpdatedEnemy() {
    }

    @org.junit.Test
    public void isProcessing() {
    }

    @org.junit.Test
    public void setProcessing() {
    }

    @org.junit.Test
    public void setTileMap() {
    }


}