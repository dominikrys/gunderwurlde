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

/**
 * Class responsible for AStar algorithm
 */
public class AStar extends Thread {

    /**
     * Cost of travel from one noe to the other
     */
    private final double COST_OF_TRAVEL;
    /**
     * A matrix with all of the distances form the startPose to the endPose
     */
    private double[][] realDist;
    /**
     * The map
     */
    private final Tile[][] tiles;
    /**
     * A pose from which to start calculations
     */
    private final Pose endPose;

    /**
     * A pose at which end calculations
     */
    private final Pose startPose;

    /**
     * The AI object that requested the AStar
     */
    private final AStarUsingEnemy ai;
    /**
     * A Pair from which to start calculations
     */
    private Pair<Integer, Integer> enemyTile;
    /**
     * A Pair from which to start calculations
     */
    private Pair<Integer, Integer> playerTile;

    /**
     * Constructor
     *
     * @param ai  The AI object that requested the AStar
     * @param cost_of_travel A matrix with all of the distances form the startPose to the endPose
     * @param tiles The map
     * @param startPose A pose from which to start calculations
     * @param endPose A pose at which end calculations
     */
    public AStar(AStarUsingEnemy ai, double cost_of_travel, Tile[][] tiles, Pose startPose, Pose endPose) {
        COST_OF_TRAVEL = cost_of_travel;
        this.tiles = tiles;
        this.startPose = startPose;
        this.endPose = endPose;
        this.ai = ai;
    }

    /**
     * Starts the calculations, if the path was created, it is further processed,
     * otherwise, null is returned.
     */
    public void run() {
        ai.setAStarProcessing(true);

        enemyTile = PoseToPairOfTileCoords(startPose);
        playerTile = PoseToPairOfTileCoords(endPose);

        LinkedList<Node> AStartPath = null;
        try {
            AStartPath = aStar(enemyTile, playerTile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (AStartPath != null) {
            ai.setTilePath(generatePoseDirectionPath(addNodesForCorners(AStartPath)));
        } else {
            ai.setTilePath(null);
        }
        ai.setAStarProcessing(false);
    }

    /**
     * Uses the node path returned by AStar to add nodes to walk around corners of the walls.
     *
     * @param nodePath The node path returned form AStar
     * @return
     */
    private LinkedList<Node> addNodesForCorners(LinkedList<Node> nodePath) {
        Node lastNode = nodePath.peek();
        Node currentNode;
        Node nodeToAdd;

        for (int i = 1; i < nodePath.size(); i++) {
            currentNode = nodePath.get(i);

            //jei skersai
            if ((Math.abs(lastNode.getY() - currentNode.getY()) != 0) &&
                    (Math.abs(lastNode.getX() - currentNode.getX()) != 0)) {
                nodeToAdd = findNodeToAddToWalkAroundSolidTile(lastNode, currentNode);

                //Jei rado noda tai pridek i sarasa
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

        if ((node1.getX() + 1 == node2.getX()) && (node1.getY() - 1 == node2.getY())) {
            //  2
            //1
            nodeToAdd = takeNotSolidToTheRight(node1, nodeToAdd);
        } else if ((node2.getX() + 1 == node1.getX()) && (node2.getY() - 1 == node1.getY())) {
            //  1
            //2
            nodeToAdd = takeNotSolidToTheRight(node2, nodeToAdd);
        } else if ((node1.getX() + 1 == node2.getX()) && (node1.getY() + 1 == node2.getY())) {
            //1
            //  2
            nodeToAdd = takeNotSolidToTheLeft(node2, nodeToAdd);
        } else if ((node2.getX() + 1 == node1.getX()) && (node2.getY() + 1 == node1.getY())) {
            //2
            //  1
            nodeToAdd = takeNotSolidToTheLeft(node1, nodeToAdd);
        }

        return nodeToAdd;
    }

    //TODO figure out a better name
    private Node takeNotSolidToTheLeft(Node node, Node nodeToAdd) {
        int[] tile = {node.getIntX(), node.getIntY() - 1};

        if (!EnemyAI.tileNotSolid(tile, tiles)) {
            //1 *
            //  2
            nodeToAdd = new Node(node.getX() - 1, node.getY());
        } else {
            tile[0] = (int) node.getX() - 1;
            tile[1] = (int) node.getY();

            if (!EnemyAI.tileNotSolid(tile, tiles)) {
                //1
                //* 2
                nodeToAdd = new Node(node.getX(), node.getY() - 1);
            }
        }
        return nodeToAdd;
    }

    private Node takeNotSolidToTheRight(Node node, Node nodeToAdd) {
        int[] tile = {node.getIntX(), (int) node.getY() - 1};

        if (!EnemyAI.tileNotSolid(tile, tiles)) {
            //* 1
            //2
            nodeToAdd = new Node(node.getX() + 1, node.getY());
        } else {
            tile[0] = (int) node.getX() + 1;
            tile[1] = (int) node.getY();

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
        return new Pair<>(tileCoords[0], tileCoords[1]); //x and y
    }

    private LinkedList<Node> generateNodePath(PriorityQueue<Node> path) {
        List<Node> list = new ArrayList<>(path);
        LinkedList<Node> finalPath = new LinkedList<>();
        LinkedList<Node> possibleNodesToAdd = new LinkedList<>();
        Node currentNode = null;

        for (Node n : path) {
            if (n.getCoordinates().equals(playerTile))
                currentNode = n;
        }

        if (currentNode == null) {
            System.out.println("This shouldn't ever happen. AStar finished but didn't return the full path.");
            return null;
        }

        finalPath.add(currentNode);
        while (!currentNode.getCoordinates().equals(enemyTile)) {
            for (Node node : list) {
                if (node.getCostToGo() == currentNode.getCostToGo() - 1 && Node.nodesAdjacent(node, currentNode)) {
                    possibleNodesToAdd.add(node);
                }
            }
            if (possibleNodesToAdd.size() != 0) {
                currentNode = findBestNodeToAdd(possibleNodesToAdd, currentNode);
                finalPath.add(currentNode);
                possibleNodesToAdd.clear();
            } else {
                System.out.println("no possible nodes to add");
                break;
            }
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

    /**
     * This checks for change in direction in the path and creates a new pose list
     * that the ai can use to generate force objects.
     *
     * @param nodePath the final node path with the corners added
     * @return
     */
    private LinkedList<Pose> generatePoseDirectionPath(LinkedList<Node> nodePath) {

        if (nodePath == null || nodePath.size() == 0) {
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
    private LinkedList<Node> aStar(Pair<Integer, Integer> startCoords, Pair<Integer, Integer> endCoords) throws IOException {
        // Straight line distances from every coords to end coords
        realDist = calcRealDist(endCoords);
        // LinkedHashSet to store nodes after they have been expanded
        LinkedHashSet<Pair<Integer, Integer>> closedCoords = new LinkedHashSet<>();
        // To store newly opened nodes
        PriorityQueue<Node> newNodes;
        PriorityQueue<Node> closedNodes = new PriorityQueue<>();
        // To store every opened node
        PriorityQueue<Node> opened = openNodes(startCoords, 0d);

        closedCoords.add(startCoords);
        closedNodes.add(new Node(new Pose(startCoords.getKey(), startCoords.getValue()), 0, realDist[startCoords.getValue()][startCoords.getKey()]));

        // A* finishes only when the end node is expanded
        while (!closedCoords.contains(endCoords)) try {
                        newNodes = openNodes(opened.peek().getCoordinates(), opened.peek().getCostToGo());
            // Add the coordinates of expanded node to the closedCoords list and remove it from the opened queue
            closedCoords.add(opened.peek().getCoordinates());
            closedNodes.add(opened.poll());

            for (Node n : newNodes) {
                // Only add nodes to the open queue if they are not already there and the have not been expanded yet
                if ((!closedCoords.contains(n.getCoordinates())) && (!opened.contains(n))) {
                    opened.add(n);
                }
            }
        } catch (NullPointerException e) {
//            appendToLog("Nowhere to go for the enemy!");
            System.out.println("Nowhere to go for the enemy!");
            e.printStackTrace();
            return null;
        }
        return generateNodePath(closedNodes);
    }

    private double[][] calcRealDist(Pair<Integer, Integer> endCoords) {
        // Make a new equal sized 2D array
        double[][] realDist = new double[tiles.length][tiles[0].length];

        // For every node, calculate the straight line distance to the end coords using Pythagoras theorem
        for (int i = 0; i < tiles.length; ++i) {
            for (int j = 0; j < tiles[i].length; ++j) {
                realDist[i][j] = sqrt(pow(j - endCoords.getKey(), 2) + pow(i - endCoords.getValue(), 2));

            }

        }

        return realDist;
    }

    private PriorityQueue<Node> openNodes(Pair<Integer, Integer> nodeLoc, double costToGo) {
        // Nodes in the PriorityQueue are ordered by costLeft + costToGo
        PriorityQueue<Node> newNodes = new PriorityQueue<>(8);
        int topNodes = nodeLoc.getValue() - 1; //y - 1
        int leftNodes = nodeLoc.getKey() - 1;  //x - 1

        // Try to add nodes around the given node if they are not walls
        // i = y
        // j = x
        for (int i = topNodes; i < topNodes + 3; i++) {
            for (int j = leftNodes; j < leftNodes + 3; j++) {
                try {
                    int[] tile = {j, i}; // because it's reversed in the tileNotSolid

                    if (EnemyAI.tileNotSolid(tile, tiles) && (!((i == nodeLoc.getValue()) && (j == nodeLoc.getKey())))) {
                        newNodes.add(new Node(new Pose(j, i), costToGo + COST_OF_TRAVEL, realDist[i][j]));
                    }
                } catch (Exception e) {
                    // Will catch an exception if it tries to look for nodes that are outside the map bounds
                }
            }
        }

        return newNodes;
    }
}