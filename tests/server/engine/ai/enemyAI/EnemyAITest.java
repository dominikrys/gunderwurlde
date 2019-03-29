package server.engine.ai.enemyAI;

import org.junit.BeforeClass;
import server.engine.ai.enemyAI.EnemyAI;
import server.engine.ai.enemyAI.ZombieAI;
import server.engine.state.entity.enemy.Enemy;
import server.engine.state.entity.enemy.Zombie;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.TileList;
import shared.lists.TileState;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

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
    public void setTileMap() {
        Tile t1 = new Tile(TileList.WOOD, TileState.SOLID,
                1,1,1);
        Tile t2 = new Tile(TileList.GRASS, TileState.PASSABLE,
                1,1,1);

        Tile [][] tileMap = {
                {t1,t1,t1},
                {t2,t2,t2}
        };

        Tile [][] tileMapTransposed = {
                {t1,t2},
                {t1,t2},
                {t1,t2}
        };

        EnemyAI.setTileMap(tileMap);
        assertArrayEquals(tileMapTransposed, enemy.getTileMap());
    }

    @org.junit.Test
    public void tileNotSolid() {
        Tile t1 = new Tile(TileList.WOOD, TileState.SOLID,
                1,1,1);
        Tile t2 = new Tile(TileList.GRASS, TileState.PASSABLE,
                1,1,1);

        Tile [][] tileMap = {
                {t1,t1,t1},
                {t2,t2,t2}
        };

        EnemyAI.setTileMap(tileMap);
        int [] tile = {0,0};
        assertFalse(EnemyAI.tileNotSolid(tile, enemy.getTileMap()));
        int [] tile2 = {0,1};
        assertFalse(EnemyAI.tileNotSolid(tile2, enemy.getTileMap()));
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
        Tile t1 = new Tile(TileList.WOOD, TileState.SOLID,
                1,1,1);
        Tile t2 = new Tile(TileList.GRASS, TileState.PASSABLE,
                1,1,1);

        Tile [][] tileMap = {
                {t1,t1,t1},
                {t2,t2,t2}
        };

        Tile [][] tileMapTransposed = {
                {t1,t2},
                {t1,t2},
                {t1,t2}
        };

        Tile [][] testMap =  EnemyAI.transposeMatrix(tileMap);

        assertArrayEquals(tileMapTransposed, testMap);
    }

    @org.junit.Test
    public void getAngle() {
        Pose p1 = new Pose(10,10);
        Pose p2 = new Pose(11,10);
        Pose p3 = new Pose(11,11);
        Pose p4 = new Pose(10,11);
        Pose p5 = new Pose(9,11);
        Pose p6 = new Pose(9,10);
        Pose p7 = new Pose(9,9);
        Pose p8 = new Pose(10,9);
        Pose p9 = new Pose(11,9);

        assertTrue(EnemyAI.getAngle(p1, p2) == 0);
        assertTrue(EnemyAI.getAngle(p1, p3) == 45);
        assertTrue(EnemyAI.getAngle(p1, p4) == 90);
        assertTrue(EnemyAI.getAngle(p1, p5) == 135);
        assertTrue(EnemyAI.getAngle(p1, p6) == 180);
        assertTrue(EnemyAI.getAngle(p1, p7) == 225);
        assertTrue(EnemyAI.getAngle(p1, p8) == 270);
        assertTrue(EnemyAI.getAngle(p1, p9) == 315);

    }

}