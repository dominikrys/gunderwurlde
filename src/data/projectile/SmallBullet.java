package data.projectile;

import data.Pose;

public class SmallBullet extends Projectile {
	public static int DEFAULT_SPEED = 60;
	public static int DEFAULT_DAMAGE = 1;

	public SmallBullet(Pose pose) {
		super(DEFAULT_SPEED, DEFAULT_DAMAGE, ProjectileType.SMALLBULLET, pose);
	}
	
	public SmallBullet(int speed, int damage, Pose pose) {
		super(speed, damage, ProjectileType.SMALLBULLET, pose);
	}
	
}
