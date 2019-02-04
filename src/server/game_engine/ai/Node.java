package server.game_engine.ai;

import javafx.util.Pair;

public class Node {
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
        return "Coordinates: " + coordinates.getKey() + " - " + coordinates.getValue() + "\n"
                + "Cost to go there : " + costToGo + "\n"
                + "Distance to enemy: " + costLeft + "\n"
                + "Sum: " + sum;
    }

}
