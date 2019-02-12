package server.engine.ai;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.HashSet;

import server.engine.state.map.tile.Tile;
import shared.Constants;
import shared.Pose;

public abstract class EnemyAI {

    protected Pose pose;
    private int size;
    protected HashSet<Pose> playerPoses;
    protected Tile[][] tileMap;
    private boolean isProcessing = false;
    private AIAction aiAction = AIAction.WAIT;

    protected EnemyAI() {
    }

    public abstract Attack getAttack();

    protected abstract Pose generateNextPose(double maxDistanceMoved);

    public synchronized Pose getNewPose(double maxDistanceMoved) {
        return generateNextPose(maxDistanceMoved);
    }

    private int getDistToPlayer(Pose player) {
        return (int) sqrt(pow(pose.getY() - player.getY(), 2) + pow(pose.getX() - player.getX(), 2));
    }


    public AIAction getAction() {
        if(getDistToPlayer(getPlayerPoses().iterator().next()) >= Constants.TILE_SIZE){
            return AIAction.MOVE;
        }else if (getDistToPlayer(getPlayerPoses().iterator().next()) < Constants.TILE_SIZE){
            return AIAction.WAIT;
        }
        return AIAction.WAIT;
    }

    protected HashSet<Pose> getPlayerPoses() {
        return playerPoses;
    }

    public void setInfo(Pose pose, int size, HashSet<Pose> playerPoses, Tile[][] tileMap) {
        this.pose = pose;
        this.size = size;
        this.playerPoses = playerPoses;
        this.tileMap = tileMap;
    }

    public boolean isProcessing() {
        return isProcessing;
    }

}
