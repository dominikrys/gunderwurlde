package server.engine.ai;

import java.util.LinkedList;

import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.AttackType;
import server.engine.state.map.Meadow;
import server.engine.state.map.tile.Tile;
import shared.Constants;
import shared.Location;
import shared.Pose;
import shared.lists.TileState;

public class ZombieAI extends EnemyAI {
    private static long DEFAULT_DELAY = 380;

    private long attackDelay;
    private long beginAttackTime;
    private boolean attacking;
    private Location attackLocation;

    public ZombieAI() {
        super();
        this.beginAttackTime = System.currentTimeMillis();
        this.attackDelay = DEFAULT_DELAY;
        this.attacking = false;
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        if ((now - beginAttackTime) >= attackDelay) {
            attacks.add(new AoeAttack(attackLocation, 24, AttackType.AOE, 1));
            attacking = false;
        }
        return attacks;
    }

    @Override
    public AIAction getAction() {
        if (attacking) {
            return AIAction.ATTACK;
        } else if (getDistToPlayer(closestPlayer) >= Constants.TILE_SIZE) {
            return AIAction.MOVE;
        } else if (getDistToPlayer(closestPlayer) < Constants.TILE_SIZE) {
            attacking = true;
            beginAttackTime = System.currentTimeMillis();
            attackLocation = closestPlayer;
            return AIAction.ATTACK;
        }
        return AIAction.WAIT;
    }

    @Override
    protected Pose generateNextPose(double maxDistanceMoved, Pose closestPlayer) {
        int[] tile = Tile.locationToTile(pose);

        if (tile[0] == 0 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2) {
            return new Pose(pose.getX() + 0.1, pose.getY(), 90);
        }

        if (tile[0] == Meadow.DEFAULT_X_DIM - 1 && tile[1] == (Meadow.DEFAULT_Y_DIM - 2) / 2) {
            return new Pose(pose.getX() - 0.1, pose.getY(), 270);
        }

        for (double i = 0.1; i < maxDistanceMoved; i += 0.1) {
            pose = poseByAngle(getAngle(pose, closestPlayer), pose);
        }

        return pose;
    }

    private Pose poseByAngle(double angle, Pose enemy) {

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
