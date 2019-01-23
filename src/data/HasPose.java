package data;

public interface HasPose extends HasLocation{
	public abstract Pose getPose();
	public abstract void setPose(Pose pose);
}
