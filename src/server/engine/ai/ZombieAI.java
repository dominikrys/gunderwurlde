package server.engine.ai;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.AttackType;
import server.engine.state.map.Meadow;
import server.engine.state.map.tile.Tile;
import shared.Pose;
import shared.lists.TileState;

public class ZombieAI extends EnemyAI {

    public ZombieAI() {
        super();
    }

    @Override
    public Attack getAttack() {
        return new Attack(pose, 6, AttackType.AOE, 400); // TODO sort out to be infront of zombie
    }

    @Override
    protected Pose generateNextPose(double maxDistanceMoved) {
        Pose playerPose = getPlayerPoses().iterator().next();
        int[] tile = Tile.locationToTile(pose);
        if(tile[0] == 0 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2){
            return new Pose(pose.getX() + 0.1, pose.getY());
        }

        if(tile[0] == Meadow.DEFAULT_X_DIM - 1 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2){
            return new Pose(pose.getX() - 0.1, pose.getY());
        }

        for (double i = 0.1; i < maxDistanceMoved; i += 0.1) {
            pose = newPose(playerPose, pose);
        }

        return pose;
    }

    private Pose newPose(Pose player, Pose enemy) {


        double angle = getAngle(enemy, player);

        if (angle > 337.5 || angle <= 22.5) {
            int[] tile = Tile.locationToTile(new Pose(enemy.getX() + 1, enemy.getY()));
            if (tileNotSolid(tile))
                return new Pose(enemy.getX() + 0.1, enemy.getY(), (int) angle + 90);
        } else if (angle > 22.5 && angle <= 67.5) {

            int[] tile = Tile.locationToTile(new Pose(enemy.getX() + 0.1, enemy.getY() + 0.1));
            if (tileNotSolid(tile))
                return new Pose(enemy.getX() + 0.1, enemy.getY() + 0.1, (int) angle + 90);
        } else if (angle > 67.5 && angle <= 112.5) {

            int[] tile = Tile.locationToTile(new Pose(enemy.getX(), enemy.getY() + 0.1));
            if (tileNotSolid(tile))
                return new Pose(enemy.getX(), enemy.getY() + 0.1, (int) angle + 90);
        } else if (angle > 112.5 && angle <= 157.5) {

            int[] tile = Tile.locationToTile(new Pose(enemy.getX() - 0.1, enemy.getY() + 0.1));
            if (tileNotSolid(tile))
                return new Pose(enemy.getX() - 0.1, enemy.getY() + 0.1, (int) angle + 90);
        } else if (angle > 157.5 && angle <= 202.5) {

            int[] tile = Tile.locationToTile(new Pose(enemy.getX() - 0.1, enemy.getY()));
            if (tileNotSolid(tile))
                return new Pose(enemy.getX() - 0.1, enemy.getY(), (int) angle + 90);
        } else if (angle > 202.5 && angle <= 247.5) {

            int[] tile = Tile.locationToTile(new Pose(enemy.getX() - 0.1, enemy.getY() - 0.1));
            if (tileNotSolid(tile))
                return new Pose(enemy.getX() - 0.1, enemy.getY() - 0.1, (int) angle + 90);
        } else if (angle > 247.5 && angle <= 292.5) {

            int[] tile = Tile.locationToTile(new Pose(enemy.getX(), enemy.getY() - 0.1));
            if (tileNotSolid(tile))
                return new Pose(enemy.getX(), enemy.getY() - 0.1, (int) angle + 90);

        } else if (angle > 292.5 && angle <= 337.5) {
            int[] tile = Tile.locationToTile(new Pose(enemy.getX() + 0.1, enemy.getY() - 0.1));
            if (tileNotSolid(tile))
                return new Pose(enemy.getX() + 0.1, enemy.getY() - 0.1, (int) angle + 90);

        }

        return enemy;
    }

    private boolean tileNotSolid(int[] tile) {
        return tileMap[tile[0]][tile[1]].getState() != TileState.SOLID;
    }

    private static double getAngle(Pose enemy, Pose player) {
        double angle = Math.toDegrees(Math.atan2(player.getY() - enemy.getY(), player.getX() - enemy.getX()));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }


}
