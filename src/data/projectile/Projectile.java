package data.projectile;

import data.HasPose;
import data.Pose;

public abstract class Projectile implements HasPose {
	protected int speed;
	protected int damage;
	protected ProjectileType projectileType;
	protected Pose pose;
	
	Projectile(int speed, int damage, ProjectileType projectileType, Pose pose) {
		this.speed = speed;
		this.damage = damage;
		this.projectileType = projectileType;
		this.pose = pose;
	}
	
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getDamage() {
		return damage;
	}
	public void setDamage(int damage) {
		this.damage = damage;
	}

	public ProjectileType getProjectileType() {
		return projectileType;
	}

	public void setProjectileType(ProjectileType projectileType) {
		this.projectileType = projectileType;
	}
	
	@Override
	public Pose getPose() {
		return pose;
	}

	@Override
	public void setPose(Pose pose) {
		this.pose = pose;
	}
	
}
