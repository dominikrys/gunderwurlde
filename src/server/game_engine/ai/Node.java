package server.game_engine.ai;

import javafx.util.Pair;

import java.util.PriorityQueue;

public class Node implements Comparable<Node> {
    private Pair<Integer, Integer> coordinates; // y x
    private double costToGo = 0;
    private double costLeft;
    private double sum;

    public Node(Pair<Integer, Integer> coordinates, double costToGo, double costLeft) {
        this.coordinates = coordinates;
        this.costToGo += costToGo;
        this.costLeft = costLeft;
        sum = costLeft + costToGo;
    }

    public double getSum() {
        return sum;
    }

    public Pair<Integer, Integer> getCoordinates() {
        return coordinates;
    }

    public double getCostToGo() {
        return costToGo;
    }


    @Override
    public String toString() {
        return "Coords: " + coordinates.getKey() + " " + coordinates.getValue()
                + "\nSum: " + costToGo + " + " + String.format("%.3f", costLeft) + " = " + String.format("%.3f", sum);
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
        return (this.sum == c.getSum() && this.coordinates.equals(c.getCoordinates()) && this.costToGo == c.getCostToGo());
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


