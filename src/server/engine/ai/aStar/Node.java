package server.engine.ai.aStar;


import javafx.util.Pair;
import shared.Pose;


/**
 * A class used to store data by AStar
 */
public class Node implements Comparable<Node> {
    /**
     * The pose of the node
     */
    private Pose pose;
    /**
     * Cost to go to get to this note from the initial node
     */
    private double costToGo = 0;
    /**
     * Cost left to go to the final node
     */
    private double costLeft = 0;
    /**
     * costToGo + costLeft. Used in AStar calculations
     */
    private double sum = 0;

    public Node(Pair<Integer, Integer> tile){
        this.pose = new Pose(tile.getKey(), tile.getValue());
    }

    public Node(double x, double y){
        this.pose = new Pose(x, y);
    }

    public Node(Pose pose, double costToGo, double costLeft) {
        this.pose = pose;
        this.costToGo += costToGo;
        this.costLeft = costLeft;
        sum = costLeft + costToGo;
    }

    public static boolean nodesAdjacent(Node node1, Node node2) {
        return Math.abs(node1.getCoordinates().getValue()-node2.getCoordinates().getValue()) <= 1 &&
                Math.abs(node1.getCoordinates().getKey()-node2.getCoordinates().getKey()) <= 1;
    }

    public double getX(){
        return pose.getX();
    }

    public double getY(){
        return pose.getY();
    }

    public int getIntX(){
        return (int) pose.getX();
    }

    public int getIntY(){
        return (int) pose.getY();
    }

    public double getSum() {
        return sum;
    }

    public Pair<Integer, Integer> getCoordinates() {
        return new Pair<>((int)pose.getX(), (int)pose.getY());
    }

    public double getCostToGo() {
        return costToGo;
    }

    protected Pose getPose(){
        return pose;
    }


    @Override
    public String toString() {
        return "Coords: x:" + pose.getX() + ", y:" + pose.getY()
                + " Sum: " + String.format("%.3f", costToGo) + " + " + String.format("%.3f", costLeft) + " = " + String.format("%.3f", sum);
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Node or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Node)) {
            return false;
        }

        // typecast o to Node so that we can compare data members
        Node c = (Node) o;

        // Compare the data members and return accordingly
        return (this.sum == c.getSum() && this.pose.equals(c.getPose()) && this.costToGo == c.getCostToGo());
    }

    @Override
    public int compareTo(Node o) {
        if (this.getSum() < o.getSum())
            return -1;
        if (this.getSum() > o.getSum())
            return 1;
        return 0;
    }
}
