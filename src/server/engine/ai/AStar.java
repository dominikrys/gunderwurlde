package server.engine.ai;

import javafx.util.Pair;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.TileState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class AStar extends Thread {

    private final double COST_OF_TRAVEL;
    private final int MAX_OPENED_NODES = 31;
    private final Tile[][] tiles;
    private final Pose endPose;
    private final Pose startPose;
    private final SniperAI myEnemy;

    protected AStar(SniperAI myEnemy, double cost_of_travel, Tile[][] tiles, Pose startPose, Pose endPose) {
        COST_OF_TRAVEL = cost_of_travel;
        this.tiles = tiles;
        this.startPose = startPose;
        this.endPose = endPose;
        this.myEnemy = myEnemy;
    }

    public void run() {
//        try {
//            myEnemy.setPosePath(aStar(startPose, endPose));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private Pair<Integer, Integer> poseToPairOfTileCoords(Pose Pose) {
        int[] tileCoords = Tile.locationToTile(Pose);
        return new Pair<>(tileCoords[1], tileCoords[0]); //y and x
    }

    //TODO implement this
    private LinkedHashSet<Pose> removeLeafs(LinkedHashSet<Pose> paths) {
        return null;
    }

    // Coordinates are y x
    //TODO maybe use LinkedHashSet?
    protected LinkedHashSet<Pose> aStar(Pose startingPose, Pose endingPose) throws IOException {
        // LinkedHashSet to store nodes after they have been expanded
        LinkedHashSet<Pose> closed = new LinkedHashSet<>();
        // To store newly opened nodes
        PriorityQueue<Node> newNodes;
        // To store every opened node
        PriorityQueue<Node> opened = openNodes(startingPose, 0d);

        startWriting("Start coords: " + startingPose);
        appendToLog("End coords: " + endingPose);


        appendToLog("init nodes");
        printOut(opened);

//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //TODO do I need this?
        // You cannot expand start node
        closed.add(startingPose);
        int counter = 0;
        // A* finishes only when the end node is expanded
        while (!closed.contains(endingPose)) try {
            counter++;

            appendToLog("\nNode to expand: " + opened.peek() + "\n");

            newNodes = openNodes(opened.peek().getPose(), opened.peek().getCostToGo());
            // Add the coordinates of expanded node to the closed list and remove it from the opened queue
            closed.add(opened.poll().getPose());

            if (counter % 1000 == 0) {
                appendToLog(opened.peek() + "\n" + counter + "\n" + opened.size() + "\n" + closed.size());
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            appendToLog("\nNewly added nodes");
            for (Node n : newNodes) {
                // Only add nodes to the open queue if they are not already there and the have not been expanded yet
                if ((!closed.contains(n.getPose())) && (!opened.contains(n))) {
                    opened.add(n);
                    appendToLog(n.toString());
                }
            }
            if (opened.size() > 30) {
                opened = cutQueue(opened);
            }

            printOut(opened);

//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        } catch (NullPointerException e) {
            appendToLog("Nowhere to go for the enemy!");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

        // Shorten the path by finding shortcuts
        //closed  = removeLeafs(closed);


        return closed;
    }

    private PriorityQueue<Node> cutQueue(PriorityQueue<Node> opened) {
        List l = new ArrayList(opened);
        List<Node> cutArray = new ArrayList<>(l.subList(0, MAX_OPENED_NODES));

        return new PriorityQueue(cutArray);
    }

    private void printOut(PriorityQueue<Node> queue) throws IOException {
        PriorityQueue<Node> queueToPrint = new PriorityQueue<>(queue);

        appendToLog("\nOpen nodes");
        while (queueToPrint.size() != 0) {
            appendToLog(queueToPrint.poll().toString());
        }
    }

    private double calcRealDist(Pose startPose) {
        return sqrt(pow(startPose.getY() - endPose.getY(), 2) + pow(endPose.getX() - startPose.getX(), 2));
    }

    private PriorityQueue<Node> openNodes(Pose poseLoc, double costToGo) {
        // Nodes in the PriorityQueue are ordered by costLeft + costToGo
        PriorityQueue<Node> openedNodes = new PriorityQueue<>(8);

        //might be conversion problems with this
        int topNodes = (int) poseLoc.getY() - 1;
        int leftNodes = (int) poseLoc.getX() - 1;

        // Try to add nodes around the given node if they are not walls
        // i = y
        // j = x
        for (int i = topNodes; i < topNodes + 3; i++) {
            for (int j = leftNodes; j < leftNodes + 3; j++) {
                try {
                    Pair<Integer, Integer> tile = poseToPairOfTileCoords(new Pose(j, i));
                    if (tiles[tile.getKey()][tile.getValue()].getState() != TileState.SOLID && (!((i == poseLoc.getY()) && (j == poseLoc.getX())))) {
                        openedNodes.add(new Node(new Pose(j, i), costToGo + COST_OF_TRAVEL, calcRealDist(new Pose(j, i))));
                    }
                } catch (Exception e) {
                    // Will catch an exception if it tries to look for nodes that are outside the map bounds
                }
            }
        }

        return openedNodes;
    }

    private void startWriting(String str)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("src/server/engine/ai/AStar.log"));
        writer.write(str);
        writer.close();
    }

    public void appendToLog(String str)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("src/server/engine/ai/AStar.log", true));
        writer.append(' ');
        writer.append(str);

        writer.close();
    }
}
