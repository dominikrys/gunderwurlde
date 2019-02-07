package server.game_engine.ai;

import data.map.Meadow;
import data.map.tile.Tile;
import data.map.tile.TileState;
import data.map.tile.TileTypes;
import javafx.util.Pair;

import java.util.ArrayList;

public class AStarTest {
    public static void main(String[] args) {

        Meadow map = new Meadow();
        final Tile[][] tiles = map.getTileMap();


        final double COST_OF_TRAVEL = 0.8;

        Pair<Integer, Integer> playerLoc = new Pair<>(5, 3); //y and x
        Pair<Integer, Integer> enemLoc = new Pair<>(25, 15);
        AStar aStar = new AStar(COST_OF_TRAVEL, tiles);

        ArrayList<Pair<Integer, Integer>> path = aStar.aStar(enemLoc, playerLoc);

        for (Pair<Integer, Integer> pair : path) {
            System.out.println(pair);
        }
//        TileTypes tileType = TileTypes.GRASS;
//        TileState tileState = TileState.PASSABLE;

//        Tile tile1 = new Tile(TileTypes.GRASS, TileState.PASSABLE);
//        Tile tile2 = new Tile(TileTypes.WOOD, TileState.PASSABLE);
//
//        System.out.println(tile1.getState() == tile2.getState());
    }
}
