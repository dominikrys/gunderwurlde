package server.game_engine.ai;

import data.Pose;
import data.map.MeadowTest;
import data.map.tile.Tile;
import javafx.util.Pair;

import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;

public class Node implements Comparable<Node> {
    private Pose pose;
    private double costToGo = 0;
    private double costLeft;
    private double sum;

    public Node(Pose pose, double costToGo, double costLeft) {
        this.pose = pose;
        this.costToGo += costToGo;
        this.costLeft = costLeft;
        sum = costLeft + costToGo;
    }

    public double getSum() {
        return sum;
    }

    public Pair<Integer, Integer> getCoordinates() {
        return new Pair<Integer, Integer>(pose.getY(), pose.getX());
    }

    public double getCostToGo() {
        return costToGo;
    }

    protected Pose getPose(){
        return pose;
    }


    @Override
    public String toString() {
        return "Coords: " + pose.getY() + " " + pose.getX()
                + "\nSum: " + String.format("%.3f", costToGo) + " + " + String.format("%.3f", costLeft) + " = " + String.format("%.3f", sum);
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

//class Main {
//
//    public static void main(String[] args) {
//
//        //new AStar(this, 1, getTileMap(), getEnemPose(), getPlayerPoses().iterator().next()).start();
////        Node c1 = new Node(new Pose(3,4), 1,1);
////        Node c2 = new Node(new Pose(3,4), 1,1);
////
////        if (c1.equals(c2)) {
////            System.out.println("Equal c");
////        } else {
////            System.out.println("Not Equal c");
////        }
////
////
////        PriorityQueue<Node> test = new PriorityQueue<>(8);
////
////        Node testNode = new Node(new Pose(3,4), 1,1);
////        System.out.println(test.offer(testNode));
////        testNode = new Node(new Pose(3,4), 1,4);
////        System.out.println(test.offer(testNode));
////        testNode = new Node(new Pose(3,4), 1,7);
////        System.out.println(test.offer(testNode));
////
////        if(test.contains(new Node(new Pose(3,4), 1,4))){
////            System.out.println("veikia PQ<Node>");
////        }
//
//        LinkedHashSet<Pose> testP = new LinkedHashSet<>(8);
//
//        Pose testPose = new Pose(3,4, 45);
//        System.out.println(testP.add(testPose));
//        testPose = new Pose(3,5);
//        System.out.println(testP.add(testPose));
//        testPose = new Pose(8,4);
//        System.out.println(testP.add(testPose));
//
//        if(testP.contains(new Pose(3,5))){
//            System.out.println("veikia LHS<Pose>" + testP.size());
//        }
//
////        Pose testPose1 = new Pose(3,4, 45);
////        Pose testPose2 = new Pose(3,4, 45);
////        if(testPose2.equals(testPose2)){
////            System.out.println("equals");
////        }
////
////        for (Pose pose : testP) {
////            System.out.println(pose.getX() + " " + pose.getY());
////        }
//    }
//}
