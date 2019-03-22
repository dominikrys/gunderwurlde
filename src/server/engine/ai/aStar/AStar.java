package server.engine.ai.aStar;

import javafx.util.Pair;
import server.engine.ai.enemyAI.AStarUsingEnemy;
import server.engine.ai.enemyAI.EnemyAI;
import server.engine.state.map.tile.Tile;
import shared.Pose;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class AStar extends Thread {

    private final double COST_OF_TRAVEL;
    private double[][] realDist;
    private final Tile[][] tiles;
    private final Pose endPose;
    private final Pose startPose;
    private final AStarUsingEnemy myEnemy;
    private Pair<Integer, Integer> enemyTile;
    private Pair<Integer, Integer> playerTile;

    public AStar(AStarUsingEnemy myEnemy, double cost_of_travel, Tile[][] tiles, Pose startPose, Pose endPose) {
        COST_OF_TRAVEL = cost_of_travel;
        this.tiles = tiles;
        this.startPose = startPose;
        this.endPose = endPose;
        this.myEnemy = myEnemy;
    }

    public void run() {
        enemyTile = PoseToPairOfTileCoords(startPose);
        playerTile = PoseToPairOfTileCoords(endPose);

//        System.out.println("\n\n\n\n" + findNodeToAddToWalkAroundSolidTile(new Node(19, 13), new Node(18, 14)) +
//                "\n\n\n\n");
//        for (Tile[] arr :
//                this.tiles) {
//            System.out.println(Arrays.toString(arr));
//        }

        myEnemy.setTilePath(generatePoseDirectionPath(addNodesForCorners(aStar(enemyTile, playerTile))));

    }

    private LinkedList<Node> addNodesForCorners(LinkedList<Node> nodePath) {
        Node lastNode = nodePath.peekLast();
        Node currentNode;
        Node nodeToAdd;
        for (int i = 1; i < nodePath.size(); i++) {
            currentNode = nodePath.get(i);
            if ((Math.abs(lastNode.getY() - currentNode.getY()) != 0) &&
                    (Math.abs(lastNode.getX() - currentNode.getX()) != 0)) {
                nodeToAdd = findNodeToAddToWalkAroundSolidTile(lastNode, currentNode);
                if (nodeToAdd != null) {
                    nodePath.add(i, nodeToAdd);
                }
            }
            lastNode = currentNode;
        }
        return nodePath;
    }

    private Node findNodeToAddToWalkAroundSolidTile(Node node1, Node node2) {
        Node nodeToAdd = null;
//
//        System.out.println((node1.getIntX() + 1 == node2.getIntY()) && (node1.getIntY() + 1 == node2.getIntY()));
//
//        System.out.println("fist stm: " + (node1.getIntX() + 1 == node2.getIntX()) + " 2nd stm: "
//                + (node1.getIntY() + 1 == node2.getIntY()));

        if ((node1.getX() + 1 == node2.getX()) && (node1.getY() - 1 == node2.getY())) {
            //  2
            //1
            nodeToAdd = takeNotSolid(node1, nodeToAdd);
        } else if ((node2.getX() + 1 == node1.getX()) && (node2.getY() - 1 == node1.getY())) {
            //  1
            //2
            nodeToAdd = takeNotSolid(node2, nodeToAdd);
        } else if ((node1.getX() + 1 == node2.getX()) && (node1.getY() + 1 == node2.getY())) {
            //1
            //  2
            nodeToAdd = takeNotSolid2(node2, nodeToAdd);
        } else if ((node2.getX() + 1 == node1.getX()) && (node2.getY() + 1 == node1.getY())) {
            //2
            //  1
            nodeToAdd = takeNotSolid2(node1, nodeToAdd);
        }

        return nodeToAdd;
    }
    //TODO figure out a better name
    private Node takeNotSolid2(Node node, Node nodeToAdd) {
//        System.out.println(node);
        int[] tile = {node.getIntY() - 1, node.getIntX()};
//        System.out.println("upper right - Tile[0]: " + tile[0] + " Tile[1]: " + tile[1]);
        if (!EnemyAI.tileNotSolid(tile, tiles)) {
            //1 *
            //  2
            nodeToAdd = new Node(node.getX() - 1, node.getY());
        } else {
            tile[0] = (int) node.getY();
            tile[1] = (int) node.getX() - 1;
//            System.out.println("lower left Tile[0]: " + tile[0] + " Tile[1]: " + tile[1]);
            if (!EnemyAI.tileNotSolid(tile, tiles)) {
                //1
                //* 2
                nodeToAdd = new Node(node.getX(), node.getY() - 1);
            }
        }
        return nodeToAdd;
    }

    private Node takeNotSolid(Node node, Node nodeToAdd) {
        int[] tile = {(int) node.getY() - 1, (int) node.getX()};
        if (!EnemyAI.tileNotSolid(tile, tiles)) {
            //* 1
            //2
            nodeToAdd = new Node(node.getX() + 1, node.getY());
        } else {
            tile[0] = (int) node.getY();
            tile[1] = (int) node.getX() + 1;
            if (!EnemyAI.tileNotSolid(tile, tiles)) {
                //  1
                //2 *
                nodeToAdd = new Node(node.getX(), node.getY() - 1);
            }
        }
        return nodeToAdd;
    }

    private Pair<Integer, Integer> PoseToPairOfTileCoords(Pose Pose) {
        int[] tileCoords = Tile.locationToTile(Pose);
        return new Pair<>(tileCoords[1], tileCoords[0]); //y and x
    }

    private LinkedList<Node> generateNodePath(PriorityQueue<Node> path) {
//        System.out.println(path.size());
        List<Node> list = new ArrayList<>(path);
        LinkedList<Node> finalPath = new LinkedList<>();
        LinkedList<Node> possibleNodesToAdd = new LinkedList<>();
        Node currentNode = null;

        for (Node n : path) {
            if (n.getCoordinates().equals(playerTile))
                currentNode = n;
        }

        if (currentNode == null) {
            return null;
        }
        finalPath.add(currentNode);

        while (!currentNode.getCoordinates().equals(enemyTile)) {
            for (Node node : list) {
                if (node.getCostToGo() == currentNode.getCostToGo() - 1 && Node.nodesAdjacent(node, currentNode)) {
                    possibleNodesToAdd.add(node);
                }
            }
            currentNode = findBestNodeToAdd(possibleNodesToAdd, currentNode);
            finalPath.add(currentNode);
            possibleNodesToAdd.clear();
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

        if (bestNodeToAdd == null) {
            bestNodeToAdd = possibleNodesToAdd.removeFirst();
        }

        return bestNodeToAdd;
    }

    //TODO make this better
    private LinkedList<Pose> generatePoseDirectionPath(LinkedList<Node> nodePath) {

        if(nodePath == null || nodePath.size() == 0){
            return null;
        }

        LinkedList<Pose> finalDirectionPath = new LinkedList<>();
        Node currentNode = nodePath.peek();
        Pose currentPose = new Pose(Tile.tileToLocation((int) currentNode.getX(), (int) currentNode.getY()));
        int currentAngle = -1;
        int nextAngle;
        Node nextNode;
        Pose nextPose;
        Node finalNode = nodePath.peekLast();

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

        finalDirectionPath.add(new Pose(Tile.tileToLocation((int) finalNode.getX(), (int) finalNode.getY())));
        finalDirectionPath.pollLast();

        return finalDirectionPath;
    }

    // Coordinates are y x in the Pairs
    private LinkedList<Node> aStar(Pair<Integer, Integer> startCoords, Pair<Integer, Integer> endCoords){
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
            closedNodes.add(new Node(new Pose(startCoords.getValue(), startCoords.getKey()), 0, realDist[startCoords.getValue()][startCoords.getKey()]));
        // A* finishes only when the end node is expanded
        while (!closedCoords.contains(endCoords)) try {

//            appendToLog("\nNode to expand: " + opened.peek() + "\n");

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
//            appendToLog("Nowhere to go for the enemy!");
            System.out.println("Nowhere to go for the enemy!");
            e.printStackTrace();
            break;
        }

        // Shorten the path by finding shortcuts
//        closedCoords  = removeLeafs(closedCoords);
//        System.out.println("Path Found!");

        return generateNodePath(closedNodes);
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
        BufferedWriter writer = new BufferedWriter(new FileWriter("server/engine/ai/aStar/AStar.log"));
        writer.write(str);
        writer.close();
    }

    private void appendToLog(String str)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("server/engine/ai/aStar/AStar.log", true));
        writer.append(' ');
        writer.append(str);

        writer.close();
    }
}