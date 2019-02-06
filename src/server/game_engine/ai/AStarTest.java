package server.game_engine.ai;

import javafx.util.Pair;

import java.util.ArrayList;

public class AStarTest {
    public static void main(String[] args) {
        final int[][] tiles = {
                {1, 1, 0, 0, 0, 0, 1, 0, 0},
                {0, 1, 1, 0, 0, 1, 1, 0, 0},
                {0, 0, 1, 0, 0, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0}
        };

        final double COST_OF_TRAVEL = 0.8;

        Pair<Integer, Integer> playerLoc = new Pair<>(5, 3); //y and x
        Pair<Integer, Integer> enemLoc = new Pair<>(5, 1);
        AStar aStar = new AStar(COST_OF_TRAVEL, tiles);

        ArrayList<Pair<Integer, Integer>> path = aStar.aStar(enemLoc, playerLoc);

        for (Pair<Integer, Integer> pair : path) {
            System.out.println(pair);
        }
    }
}
