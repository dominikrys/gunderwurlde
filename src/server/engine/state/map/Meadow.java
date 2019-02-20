package server.engine.state.map;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.TreeSet;

import server.engine.state.entity.enemy.Enemy;
import server.engine.state.entity.enemy.RunnerZombie;
import server.engine.state.entity.enemy.SoldierZombie;
import server.engine.state.entity.enemy.Zombie;
import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.lists.MapList;
import shared.lists.Teams;
import shared.lists.TileState;
import shared.lists.TileTypes;

public class Meadow extends GameMap {
    public static final int DEFAULT_X_DIM = 32;
    public static final int DEFAULT_Y_DIM = 22;

    public Meadow() {
        super(DEFAULT_X_DIM, DEFAULT_Y_DIM, generateTileMap(), generateTeamSpawns(), generateEnemySpawns(), generateRounds(), MapList.MEADOW);
    }

    public static Tile[][] generateTileMap() {
        Tile[][] tileMap = new Tile[DEFAULT_X_DIM][DEFAULT_Y_DIM];

        for (int x = 0; x < DEFAULT_X_DIM; x++) {
            tileMap[x][0] = new Tile(TileTypes.WOOD, TileState.SOLID);
            for (int y = 1; y < DEFAULT_Y_DIM; y++) {
                if (x == 0 || x == DEFAULT_X_DIM - 1)
                    tileMap[x][y] = new Tile(TileTypes.WOOD, TileState.SOLID);
                else
                    tileMap[x][y] = new Tile(TileTypes.GRASS, TileState.PASSABLE);
            }
            tileMap[x][DEFAULT_Y_DIM - 1] = new Tile(TileTypes.WOOD, TileState.SOLID);
        }
        tileMap[0][(DEFAULT_Y_DIM - 1) / 2] = new Tile(TileTypes.GRASS, TileState.PASSABLE);
        tileMap[DEFAULT_X_DIM - 1][(DEFAULT_Y_DIM - 1) / 2] = new Tile(TileTypes.GRASS, TileState.PASSABLE);
        return tileMap;
    }

    private static HashMap<Teams, Location> generateTeamSpawns() {
        HashMap<Teams, Location> teamSpawns = new HashMap<>();
        teamSpawns.put(Teams.RED, Tile.tileToLocation(1, 1));
        teamSpawns.put(Teams.BLUE, Tile.tileToLocation(1, DEFAULT_Y_DIM - 2));
        teamSpawns.put(Teams.GREEN, Tile.tileToLocation(DEFAULT_X_DIM - 2, 1));
        teamSpawns.put(Teams.YELLOW, Tile.tileToLocation(DEFAULT_X_DIM - 2, DEFAULT_Y_DIM - 2));
        return teamSpawns;
    }

    private static LinkedHashSet<Location> generateEnemySpawns() {
        LinkedHashSet<Location> enemySpawns = new LinkedHashSet<Location>();
        enemySpawns.add(Tile.tileToLocation(0, (DEFAULT_Y_DIM - 2) / 2));
        enemySpawns.add(Tile.tileToLocation(DEFAULT_X_DIM - 1, (DEFAULT_Y_DIM - 2) / 2));
        return enemySpawns;
    }

    public static LinkedHashSet<Round> generateRounds() {
        LinkedHashSet<Round> rounds = new LinkedHashSet<>();
        TreeSet<Wave> waves = new TreeSet<>();

        // Simple rounds just for testings
        // Round 1
        Enemy enemyType = new SoldierZombie(400, 4);
//        Enemy enemyType = new RunnerZombie();
        //enemyType.setMoveSpeed(Zombie.DEFAULT_MOVESPEED * 2);
        waves.add(new Wave(0, 2000, enemyType, 1, 1));
//        waves.add(new Wave(28000, 1600, enemyType, 2, 30));
//        waves.add(new Wave(60000, 1000, enemyType, 2, 40));
//        waves.add(new Wave(82000, 1000, enemyType, 2, 2));
        Round round = new Round(waves, false);
        rounds.add(round);

//        // Round 2
//        waves = new TreeSet<>();
//        enemyType = new Zombie();
//        waves.add(new Wave(0, 1000, enemyType, 2, 50));
//        enemyType.setMoveSpeed(Zombie.DEFAULT_MOVESPEED * 2);
//        waves.add(new Wave(20000, 500, enemyType, 2, 6));
//        enemyType = new Zombie();
//        enemyType.setHealth(Zombie.DEFAULT_HEALTH + 1);
//        waves.add(new Wave(25000, 2000, enemyType, 2, 10));
//        enemyType = new Zombie();
//        enemyType.setMoveSpeed(Zombie.DEFAULT_MOVESPEED * 2);
//        waves.add(new Wave(27000, 500, enemyType, 1, 20));
//        enemyType.setHealth(Zombie.DEFAULT_HEALTH * 4);
//        enemyType.setMoveSpeed((int) Math.ceil(Zombie.DEFAULT_MOVESPEED * 2.5));
//        waves.add(new Wave(40000, 1000, enemyType, 2, 2));
//        enemyType = new Zombie();
//        waves.add(new Wave(50000, 1000, enemyType, 2, 100));
//        round = new Round(waves, false);
//        rounds.add(round);

        return rounds;
    }

}
