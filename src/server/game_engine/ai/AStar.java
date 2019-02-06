package server.game_engine.ai;


import javafx.util.Pair;

import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class AStar {

    private static final double COST_OF_TRAVEL = 0.8;
    private static double[][] realDist;
    private static final int[][] tiles = {
            {1, 1, 0, 0, 0, 0, 1, 0, 0},
            {0, 1, 1, 0, 0, 1, 1, 0, 0},
            {0, 0, 1, 0, 0, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 1, 1, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0}
    };

    public static void main(String[] args) {
        Pair<Integer, Integer> playerLoc = new Pair<Integer, Integer>(2, 7); //y and x
        Pair<Integer, Integer> enemLoc = new Pair<Integer, Integer>(1, 0);

        ArrayList<Pair<Integer, Integer>> path = aStar(enemLoc, playerLoc);

        for (Pair<Integer, Integer> pair: path) {
            System.out.println(pair);
        }
    }

    private static ArrayList<Pair<Integer, Integer>> removeLeafs(ArrayList<Pair<Integer, Integer>> path){
        Pair<Integer,Integer> biggestDist = path.get(0);

        // Find a node that has the biggest distance to the final end node.
        // At this point realDist array still have values for the last A* search
        for (Pair<Integer, Integer> coord : path) {
            if(realDist[biggestDist.getKey()][biggestDist.getValue()] < realDist[coord.getKey()][coord.getValue()]){
                biggestDist = coord;
            }
        }

        // If the path is not straight to the end node, try to find shortcuts for it
        if(biggestDist != path.get(0)){
            ArrayList<Pair<Integer, Integer>> shortCut = aStar(path.get(0), biggestDist);

            ArrayList<Pair<Integer, Integer>> shorterPath = new ArrayList<>();

            // Add the starting "shortcut" node and the rest of the path
            for (int i = 0; i < shortCut.size(); i++) {
                shorterPath.add(shortCut.get(i));
            }
            shorterPath.addAll(path.subList(path.indexOf(biggestDist) + 1, path.size()));

            return shorterPath;
        }

        // If unable to find shortcuts, return the original path
        return path;
    }

    private static ArrayList<Pair<Integer, Integer>> aStar(Pair<Integer, Integer> startCoords, Pair<Integer, Integer> endCoords) {
        // Straight line distances from every coords to end coords
        realDist = calcRealDist(endCoords);
        // Array list to store nodes after they have been expanded
        ArrayList<Pair<Integer, Integer>> closed = new ArrayList<>();
        // To store newly opened nodes
        PriorityQueue<Node> newNodes = null;
        // To store every opened node
        PriorityQueue<Node> opened = openNodes(startCoords, 0d);

        System.out.println("init nodes");
        for (Node node : opened) {
            System.out.println(node);
        }
        // You cannot expand start node
        closed.add(startCoords);

        // A* finishes only when the end node is expanded
        while(!closed.contains(endCoords)) {
            System.out.println("\nNode to expand: " + opened.peek() + "\n");
            newNodes = openNodes(opened.peek().getCoordinates(), opened.peek().getCostToGo());
            // Add the coordinates of expanded node to the closed list and remove it from the opened queue
            closed.add(opened.poll().getCoordinates());

            System.out.println("\nNewly added nodes");
            for (Node n: newNodes) {
                // Only add nodes to the open queue if they are not already there and the have not been expanded yet
                if((!closed.contains(n.getCoordinates())) && (!opened.contains(n))) {
                    opened.add(n);
                    System.out.println(n);
                }
            }

            printOut(opened);
        }

        // Shorten the path by finding shortcuts
        //closed  = removeLeafs(closed);


//        Set<Pair<Integer, Integer>> set = new HashSet<>(closed);
//        closed.clear();
//        closed.addAll(set);

        return closed;
    }

    private static void printOut(PriorityQueue<Node> queue){
        PriorityQueue<Node> queueToPrint = new PriorityQueue<Node>(queue);

        System.out.println("\nOpen nodes");
        while(queueToPrint.size() != 0){
            System.out.println(queueToPrint.poll());
        }
    }

    private static double[][] calcRealDist(Pair<Integer, Integer> endCoords) {
        // Make a new equal sized 2D array
        double[][] realDist = new double[tiles.length][tiles[0].length];

        // For every node, calculate the straight line distance to the end coords using Pythagoras theorem
        for (int i = 0; i < tiles.length; ++i) {
            for (int j = 0; j < tiles[i].length; ++j) {
                realDist[i][j] = sqrt(pow(j - endCoords.getValue(), 2) + pow(i - endCoords.getKey(), 2));
            }
        }

        return realDist;
    }

    private static PriorityQueue<Node> openNodes(Pair<Integer, Integer> nodeLoc, double costToGo) {
        // Nodes in the PriorityQueue are ordered by costLeft + costToGo
        PriorityQueue<Node> initNodes = new PriorityQueue<>(8);


        int topNodes = nodeLoc.getKey() - 1;
        int leftNodes = nodeLoc.getValue() - 1;

        // Try to add nodes around the given node if they are not walls
        for (int i = topNodes; i < topNodes + 3; i++) {
            for (int j = leftNodes; j < leftNodes + 3; j++) {
                try {
                    if (tiles[i][j] != 1 && (!((i == nodeLoc.getKey()) && (j == nodeLoc.getValue())))) {
                        initNodes.add(new Node(new Pair<>(i, j), costToGo + COST_OF_TRAVEL, realDist[i][j]));
                    }
                } catch (Exception e) {
                    // Will catch an exception if it tries to look for nodes that are outside the map bounds
                }
            }
        }

        return initNodes;
    }
}
