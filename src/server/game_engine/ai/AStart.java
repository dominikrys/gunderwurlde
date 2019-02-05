package server.game_engine.ai;


import javafx.util.Pair;

import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class AStart {

    private static final int COST_OF_TRAVEL = 1;

    public static void main(String[] args) {
        //init
        int[][] tiles = {
                {1, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 1, 0, 0, 0, 1, 1, 0, 0},
                {0, 0, 1, 0, 0, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0}
        };

        Pair<Integer, Integer> playerLoc = new Pair<Integer, Integer>(0, 1); //x and y
        Pair<Integer, Integer> enemLoc = new Pair<Integer, Integer>(5, 5);


        List<Pair<Integer, Integer>> path = aStar(tiles, playerLoc, enemLoc);
        System.out.println("\n\n\n");
        for (Pair<Integer, Integer> pair: path) {
            System.out.println(pair);
        }
    }

    private static List<Pair<Integer, Integer>> aStar(
            int[][] tiles, Pair<Integer, Integer> playerLoc, Pair<Integer, Integer> enemLoc) {

        double[][] realDist = calcRealDist(tiles, playerLoc);
        List<Pair<Integer, Integer>> closed = new ArrayList();
        PriorityQueue<Node> newNodes = null;
        PriorityQueue<Node> opened = openNodes(tiles, enemLoc, 0d, realDist);

        closed.add(enemLoc);

        while(!closed.contains(playerLoc)) {
            Node nodeToOpen = opened.peek();
            newNodes = openNodes(tiles, nodeToOpen.getCoordinates(), nodeToOpen.getCostToGo(), realDist);
            closed.add(opened.poll().getCoordinates());

            for (Node n: newNodes) {
                if((!closed.contains(n.getCoordinates()) && (!opened.contains(n))))
                    opened.add(n);
            }
        }

        return closed;
    }

    private static double[][] calcRealDist(int[][] tiles, Pair<Integer, Integer> playerLoc) {
        double[][] realDist = new double[tiles.length][tiles[0].length];

        System.out.println("\n\nReal (straight line) distances form every node to player: ");
        for (int i = 0; i < tiles.length; ++i) {
            System.out.println();
            for (int j = 0; j < tiles[i].length; ++j) {
                realDist[i][j] = sqrt(pow(j - playerLoc.getValue(), 2) + pow(i - playerLoc.getKey(), 2));
                System.out.print(String.format("%.1f ", realDist[i][j]));
            }
        }

        return realDist;
    }

    private static PriorityQueue<Node> openNodes(int[][] tiles, Pair<Integer, Integer> nodeLoc, double costToGo, double[][] realDist) {
        PriorityQueue<Node> initNodes = new PriorityQueue<>(8, (Node o1, Node o2) -> {
            if (o1.getSum() < o2.getSum())
                return -1;
            if (o1.getSum() > o2.getSum())
                return 1;
            return 0;
        });


        int topNodes = nodeLoc.getKey() - 1;
        int leftNodes = nodeLoc.getValue() - 1;

        for (int i = topNodes; i < topNodes + 3; i++) {
            for (int j = leftNodes; j < leftNodes + 3; j++) {
                try {
                    if (tiles[i][j] != 1 && (!((i == nodeLoc.getKey()) && (j == nodeLoc.getValue())))) {
                        initNodes.add(new Node(new Pair<>(i, j), costToGo + COST_OF_TRAVEL, realDist[i][j]));
                    }
                } catch (Exception e) {
                }
            }
        }

        return initNodes;
    }
}
