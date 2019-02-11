package server.game_engine.ai;

import data.Pose;

public class ZombieAI extends EnemyAI {

    public ZombieAI() {
        super();
    }

    @Override
    public Attack getAttack() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Pose generateNextPose(int maxDistanceMoved){
        Pose playerPose = getPlayerPoses().iterator().next();
        for (int i = 0; i < maxDistanceMoved; i++) {
            pose = newPose(playerPose, pose);
        }

        return pose;
    }

    private Pose newPose(Pose player, Pose enemy){
        double angle = getAngle(enemy, player);

        if(angle > 337.5 || angle <= 22.5){
            return new Pose(enemy.getX() + 1, enemy.getY(), (int )angle + 180);
        } else if (angle > 22.5 && angle <= 67.5){
            return new Pose(enemy.getX() + 1, enemy.getY() + 1, (int )angle + 180);
        } else if (angle > 67.5 && angle <= 112.5){
            return new Pose(enemy.getX(), enemy.getY() + 1, (int )angle + 180);
        } else if (angle > 112.5 && angle <= 157.5){
            return new Pose(enemy.getX() - 1, enemy.getY() + 1, (int )angle + 180);
        } else if (angle > 157.5 && angle <= 202.5){
            return  new Pose(enemy.getX() - 1, enemy.getY(), (int )angle + 180);
        } else if (angle > 202.5 && angle <= 247.5){
            return new Pose(enemy.getX() - 1, enemy.getY() - 1, (int )angle + 180);
        } else if (angle > 247.5 && angle <= 292.5){
            return new Pose(enemy.getX(), enemy.getY() - 1, (int )angle + 180);
        } else if (angle > 292.5 && angle <= 337.5){
            return new Pose(enemy.getX() + 1, enemy.getY() - 1, (int )angle + 180);
        }

        return enemy;
    }

    private static double getAngle(Pose enemy, Pose player) {
        double angle = Math.toDegrees(Math.atan2(player.getY() - enemy.getY(), player.getX() - enemy.getX()));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }




}
