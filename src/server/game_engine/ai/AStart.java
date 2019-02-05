package server.game_engine.ai;


import javafx.util.Pair;

import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class AStart {

    private static final int COST_OF_TRAVEL = 1;
    private static double[][] realDist;
    private static final int[][] tiles = {
            {1, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 1, 0, 0, 0, 1, 1, 0, 0},
            {0, 0, 1, 0, 0, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 1, 1, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0}
    };

    public static void main(String[] args) {
        //init

        Pair<Integer, Integer> playerLoc = new Pair<Integer, Integer>(0, 1); //x and y
        Pair<Integer, Integer> enemLoc = new Pair<Integer, Integer>(5, 5);

        realDist = calcRealDist(playerLoc);

        ArrayList<Pair<Integer, Integer>> path = aStar(enemLoc, playerLoc);
        path  = removeLeafs(path);


        System.out.println("\n\n\n");
        for (Pair<Integer, Integer> pair: path) {
            System.out.println(pair);
        }
    }

    private static ArrayList<Pair<Integer, Integer>> removeLeafs(ArrayList<Pair<Integer, Integer>> path){
        Pair<Integer,Integer> biggestDist = path.get(0);
        Pair<Integer,Integer> peak;

        System.out.println("\n\n");

        for (Pair<Integer, Integer> coord : path) {
            System.out.println("Coords: " + coord + " Dist to player: " + realDist[coord.getKey()][coord.getValue()]);
            if(realDist[biggestDist.getKey()][biggestDist.getValue()] < realDist[coord.getKey()][coord.getValue()]){
                biggestDist = coord;
            }
        }

        peak = biggestDist;
        System.out.println("Peak: " + peak);

        if(peak != path.get(0)){
            System.out.println("ShortCut: ");
            realDist = calcRealDist(peak);
            ArrayList<Pair<Integer, Integer>> shortCut = aStar(path.get(0), peak);

            for (Pair<Integer, Integer> coord : shortCut) {
                System.out.println(coord);
            }
            ArrayList<Pair<Integer, Integer>> shorterPath = new ArrayList<>();

            for (int i = 0; i < shortCut.size(); i++) {
                shorterPath.add(shortCut.get(i));
            }
            shorterPath.addAll(path.subList(path.indexOf(peak) + 1, path.size()));

            return shorterPath;
        }



        return path;
    }

    private static ArrayList<Pair<Integer, Integer>> aStar(Pair<Integer, Integer> startCoords, Pair<Integer, Integer> endCoords) {

        ArrayList<Pair<Integer, Integer>> closed = new ArrayList();
        PriorityQueue<Node> newNodes = null;
        PriorityQueue<Node> opened = openNodes(startCoords, 0d);

        closed.add(startCoords);

        while(!closed.contains(endCoords)) {
            newNodes = openNodes(opened.peek().getCoordinates(), opened.peek().getCostToGo());
            closed.add(opened.poll().getCoordinates());

            for (Node n: newNodes) {
                if((!closed.contains(n.getCoordinates()) && (!opened.contains(n))))
                    opened.add(n);
            }
        }

        return closed;
    }

    private static double[][] calcRealDist(Pair<Integer, Integer> endCoords) {
        double[][] realDist = new double[tiles.length][tiles[0].length];

        System.out.println("\n\nReal (straight line) distances form every node to player: ");
        for (int i = 0; i < tiles.length; ++i) {
            System.out.println();
            for (int j = 0; j < tiles[i].length; ++j) {
                realDist[i][j] = sqrt(pow(j - endCoords.getValue(), 2) + pow(i - endCoords.getKey(), 2));
                System.out.print(String.format("%.1f ", realDist[i][j]));
            }
        }

        return realDist;
    }

    private static PriorityQueue<Node> openNodes(Pair<Integer, Integer> nodeLoc, double costToGo) {
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
