package server.game_engine.ai;

import data.Pose;
import data.map.tile.Tile;
import data.map.tile.TileState;
import javafx.util.Pair;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class AStar extends Thread{

    private final double COST_OF_TRAVEL;
    private double[][] realDist;
    private final Tile[][] tiles;
    private final Pose endPose;
    private final Pose startPose;
    private final EnemyAI myEnemy;

    protected AStar(EnemyAI myEnemy, double cost_of_travel, Tile[][] tiles, Pose startPose, Pose endPose){
        COST_OF_TRAVEL = cost_of_travel;
        this.tiles = tiles;
        this.startPose = startPose;
        this.endPose = endPose;
        this.myEnemy = myEnemy;
    }

//    protected AStar(double cost_of_travel, Tile[][] tiles, Pose startPose, Pose endPose){
//        COST_OF_TRAVEL = cost_of_travel;
//        this.tiles = tiles;
//        this.startPose = startPose;
//        this.endPose = endPose;
//    }

    public void run() {
        System.out.println("run");

        Pair<Integer, Integer> playerLoc = PoseToPairOfTileCoords(endPose);
        Pair<Integer, Integer> enemLoc = new Pair<>(startPose.getY(), startPose.getX());

        myEnemy.setPath(aStar(enemLoc, playerLoc));
    }

    private Pair<Integer, Integer> PoseToPairOfTileCoords(Pose Pose) {
        int[] tileCoords = Tile.locationToTile(Pose);
        return new Pair<>(tileCoords[1], tileCoords[0]); //y and x
    }

    private ArrayList<Pair<Integer, Integer>> removeLeafs(ArrayList<Pair<Integer, Integer>> paths){
        Pair<Integer,Integer> biggestDist = paths.get(0);

        // Construct a new list from the set constucted from elements
        // of the original list
        List<Pair<Integer, Integer>> path = paths.stream()
                .distinct()
                .collect(Collectors.toList());

        // Find a node that has the biggest distance to the final end node.
        // At this point realDist array still have values for the last A* search

        //TODO fix this
//        for (Pair<Integer, Integer> coord : path) {
//            if(realDist[biggestDist.getKey()][biggestDist.getValue()] < realDist[coord.getKey()][coord.getValue()]){
//                biggestDist = coord;
//            }
//        }
//
//        // If the path is not straight to the end node, try to find shortcuts for it
//        if(biggestDist != path.get(0)){
//            ArrayList<Pair<Integer, Integer>> shortCut = aStar(path.get(0), biggestDist);
//
//            ArrayList<Pair<Integer, Integer>> shorterPath = new ArrayList<>();
//
//            // Add the starting "shortcut" node and the rest of the path
//            shorterPath.addAll(shortCut);
//            shorterPath.addAll(path.subList(path.indexOf(biggestDist) + 1, path.size()));
//
//            return shorterPath;
//        }

        // If unable to find shortcuts, return the original path
        return (ArrayList<Pair<Integer, Integer>>) path;
    }
    // Coordinates are y x
    //TODO maybe use LinkedHashSet?
    protected ArrayList<Pair<Integer, Integer>> aStar(Pair<Integer, Integer> startCoords, Pair<Integer, Integer> endCoords) {
        // Straight line distances from every coords to end coords
        realDist = calcRealDist(endCoords);
        // Array list to store nodes after they have been expanded
        ArrayList<Pair<Integer, Integer>> closed = new ArrayList<>();
        // To store newly opened nodes
        PriorityQueue<Node> newNodes;
        // To store every opened node
        PriorityQueue<Node> opened = openNodes(startCoords, 0d);

        System.out.println("Start coords: " + startCoords);
        System.out.println("End coords: " + endCoords);


        System.out.println("init nodes");
        for (Node node : opened) {
            System.out.println(node);
        }

        try {
            TimeUnit.SECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // You cannot expand start node
        closed.add(startCoords);

        // A* finishes only when the end node is expanded
        while(!closed.contains(endCoords)) try {

           // System.out.println("\nNode to expand: " + opened.peek() + "\n");

            newNodes = openNodes(opened.peek().getCoordinates(), opened.peek().getCostToGo());
            // Add the coordinates of expanded node to the closed list and remove it from the opened queue
            closed.add(opened.poll().getCoordinates());

            //System.out.println("\nNewly added nodes");
            for (Node n : newNodes) {
                // Only add nodes to the open queue if they are not already there and the have not been expanded yet
                if ((!closed.contains(n.getCoordinates())) && (!opened.contains(n))) {
                    opened.add(n);
                    //System.out.println(n);
                }
            }

            //printOut(opened);
        } catch (NullPointerException e) {
            System.out.println("Nowhere to go for the enemy!");
            e.printStackTrace();
        }

        // Shorten the path by finding shortcuts
        closed  = removeLeafs(closed);


        return closed;
    }

    private void printOut(PriorityQueue<Node> queue){
        PriorityQueue<Node> queueToPrint = new PriorityQueue<>(queue);

        System.out.println("\nOpen nodes");
        while(queueToPrint.size() != 0){
            System.out.println(queueToPrint.poll());
        }
    }

    private double[][] calcRealDist(Pair<Integer, Integer> endCoords) {
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

    private PriorityQueue<Node> openNodes(Pair<Integer, Integer> nodeLoc, double costToGo) {
        // Nodes in the PriorityQueue are ordered by costLeft + costToGo
        PriorityQueue<Node> initNodes = new PriorityQueue<>(8);


        int topNodes = nodeLoc.getKey() - 1;
        int leftNodes = nodeLoc.getValue() - 1;

        // Try to add nodes around the given node if they are not walls
        for (int i = topNodes; i < topNodes + 3; i++) {
            for (int j = leftNodes; j < leftNodes + 3; j++) {
                try {
                    if (tiles[i][j].getState() != TileState.SOLID && (!((i == nodeLoc.getKey()) && (j == nodeLoc.getValue())))) {
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
