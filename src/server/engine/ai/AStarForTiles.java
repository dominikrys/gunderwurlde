package server.engine.ai;

import javafx.util.Pair;
import server.engine.state.map.tile.Tile;
import shared.Pose;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class AStarForTiles extends Thread {

    private final double COST_OF_TRAVEL;
    private double[][] realDist;
    private final Tile[][] tiles;
    private final Pose endPose;
    private final Pose startPose;
    private final SniperAI myEnemy;

    protected AStarForTiles(SniperAI myEnemy, double cost_of_travel, Tile[][] tiles, Pose startPose, Pose endPose) {
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
        Pair<Integer, Integer> enemTile = PoseToPairOfTileCoords(startPose);
        Pair<Integer, Integer> playerTile = PoseToPairOfTileCoords(endPose);

        for (Tile[] arr :
                this.tiles) {

            System.out.println(Arrays.toString(arr));
        }

        try {
            myEnemy.setTilePath(generatePoseDirectionPath(generateNodePath(aStar(enemTile, playerTile), enemTile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pair<Integer, Integer> PoseToPairOfTileCoords(Pose Pose) {
        int[] tileCoords = Tile.locationToTile(Pose);
        return new Pair<>(tileCoords[1], tileCoords[0]); //y and x
    }

    private LinkedList<Node> generateNodePath(PriorityQueue<Node> path, Pair<Integer, Integer> enemTile) {
        System.out.println(path.size());
        Node currentNode = path.poll();
        List<Node> list = new ArrayList<>(path);
        LinkedList<Node> finalPath = new LinkedList<>();
        LinkedList<Node> possibleNodesToAdd = new LinkedList<>();
//        Pair<Integer, Integer> testCoord = new Pair<>(10, 10);
//        Node testNode = new Node(new Pose(4,25), 0, 0);
//        System.out.println(enemTile);
//        System.out.println(testCoord);
        finalPath.add(currentNode);
//        if(Node.nodesAdjacent(currentNode,testNode)){
//            System.out.println("labas");
//        }else{
//            System.out.println("as krabas");
//        }
//        System.out.println(currentNode);
//        System.out.println(list.size());
        while (!currentNode.getCoordinates().equals(enemTile)) {
            for (Node node : list) {
//                System.out.println("node " + node);
//                System.out.println("currentNode " + currentNode);
                if (node.getCostToGo() == currentNode.getCostToGo() - 1 && Node.nodesAdjacent(node, currentNode)) {
                    possibleNodesToAdd.add(node);
                }
            }
            currentNode = findBestNodeToAdd(possibleNodesToAdd, currentNode);
            finalPath.add(currentNode);
            possibleNodesToAdd.clear();
        }
        for (Node n :
                finalPath) {
            System.out.println(n);
        }

        return finalPath;
    }

    private Node findBestNodeToAdd(LinkedList<Node> possibleNodesToAdd, Node currentNode) {
        Node bestNodeToAdd = null;

        for (Node node : possibleNodesToAdd) {
            if (((Math.abs(node.getX() - currentNode.getX()) == 1) &&
                    (Math.abs(node.getY() - currentNode.getY()) != 1)) ||
                    ((Math.abs(node.getX() - currentNode.getX()) != 1)
                            && (Math.abs(node.getY() - currentNode.getY()) == 1))) {
                bestNodeToAdd = node;
            }
        }

        if(bestNodeToAdd == null){
            bestNodeToAdd = possibleNodesToAdd.removeFirst();
        }

        return bestNodeToAdd;
    }

    //TODO make tihs better
    private LinkedList<Pose> generatePoseDirectionPath(LinkedList<Node> nodePath) {
        LinkedList<Pose> finalDirectionPath = new LinkedList<>();
        Node currentNode = nodePath.poll();
        Pose currentPose = new Pose(Tile.tileToLocation((int) currentNode.getX(), (int) currentNode.getY()));
        int currentAngle = -1;
        int nextAngle;
        Node nextNode;
        Pose nextPose;
        Node finalnode = nodePath.peekLast();
//        System.out.println("final node " + finalnode);

        while (nodePath.size() != 0) {
            nextNode = nodePath.pop();
            nextPose = new Pose(Tile.tileToLocation((int) nextNode.getX(), (int) nextNode.getY()));
            nextAngle = EnemyAI.getAngle(currentPose, nextPose);

            if (nextAngle != currentAngle) {
                finalDirectionPath.add(currentPose);
            }

            currentPose = nextPose;
            currentAngle = nextAngle;
        }
//        System.out.println("final node" + nodePath.pop());
        finalDirectionPath.add(new Pose(Tile.tileToLocation((int) finalnode.getX(), (int) finalnode.getY())));
        finalDirectionPath.poll();

        return finalDirectionPath;
    }

    // Coordinates are y x
    protected PriorityQueue<Node> aStar(Pair<Integer, Integer> startCoords, Pair<Integer, Integer> endCoords) throws IOException {
        // Straight line distances from every coords to end coords
        realDist = calcRealDist(endCoords);
        // LinkedHashSet to store nodes after they have been expanded
        LinkedHashSet<Pair<Integer, Integer>> closedCoords = new LinkedHashSet<>();
        // To store newly opened nodes
        PriorityQueue<Node> newNodes;
        PriorityQueue<Node> closedNodes = new PriorityQueue<>();
        // To store every opened node
        PriorityQueue<Node> opened = openNodes(startCoords, 0d);

//        startWriting("Start coords: " + startCoords);
//        appendToLog("End coords: " + endCoords);


//        appendToLog("\ninit nodes");
//        printOut(opened);
//
//        try {
//            TimeUnit.SECONDS.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //TODO do I need this?
        // You cannot expand start node
        closedCoords.add(startCoords);
        System.out.println(startPose);
        closedNodes.add(new Node(new Pose(startCoords.getValue(), startCoords.getKey()), 0, realDist[startCoords.getValue()][startCoords.getKey()]));

        // A* finishes only when the end node is expanded
        while (!closedCoords.contains(endCoords)) try {

            appendToLog("\nNode to expand: " + opened.peek() + "\n");

            newNodes = openNodes(opened.peek().getCoordinates(), opened.peek().getCostToGo());
            // Add the coordinates of expanded node to the closedCoords list and remove it from the opened queue
            closedCoords.add(opened.peek().getCoordinates());
            closedNodes.add(opened.poll());
//            appendToLog("\nNewly added nodes");
            for (Node n : newNodes) {
                // Only add nodes to the open queue if they are not already there and the have not been expanded yet
                if ((!closedCoords.contains(n.getCoordinates())) && (!opened.contains(n))) {
                    opened.add(n);
//                    appendToLog("\n" + n.toString());
                }
            }

//            printOut(opened);
        } catch (NullPointerException e) {
            appendToLog("Nowhere to go for the enemy!");
            System.out.println("Nowhere to go for the enemy!");
            e.printStackTrace();
            break;
        }

        // Shorten the path by finding shortcuts
//        closedCoords  = removeLeafs(closedCoords);
//        System.out.println("Path Found!");

        return closedNodes;
    }

    private void printOut(PriorityQueue<Node> queue) throws IOException {
        PriorityQueue<Node> queueToPrint = new PriorityQueue<>(queue);

        appendToLog("\n\nOpen nodes");
        while (queueToPrint.size() != 0) {
            appendToLog("\n" + queueToPrint.poll().toString());
        }
    }

    //TODO I don't need to generate every value for every point in the array
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
        // i = y
        // j = x
        for (int i = topNodes; i < topNodes + 3; i++) {
            for (int j = leftNodes; j < leftNodes + 3; j++) {
                try {
                    int[] tile = {i, j};
//                    if(!EnemyAI.tileNotSolid(tile, tiles))
//                        System.out.println("Tile:" + j + ", " + i + "is solid");
                    if (EnemyAI.tileNotSolid(tile, tiles) && (!((i == nodeLoc.getKey()) && (j == nodeLoc.getValue())))) {
                        initNodes.add(new Node(new Pose(j, i), costToGo + COST_OF_TRAVEL, realDist[i][j]));
                    }
                } catch (Exception e) {
//                    System.out.println("Tile: " + j + "," + i + " is solid");
                    // Will catch an exception if it tries to look for nodes that are outside the map bounds
                }
            }
        }

        return initNodes;
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
