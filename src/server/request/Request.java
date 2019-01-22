package server.request;

import java.util.Optional;

import data.Pose;

public class Request {
	protected Optional<Pose> pose;
	protected Optional<Integer> selectItem;
	protected boolean shoot;
	protected boolean reload;
	
	public Request() {
		this.shoot = false;
		this.reload = false;
	}
	
	public boolean poseExists() {
		return pose.isPresent();
	}

	public Pose getPose() {
		return pose.get(); //can throw exception if used incorrectly
	}

	public void setPose(Pose pose) {
		this.pose = Optional.of(pose);
	}
	
	public boolean selectItemExists() {
		return selectItem.isPresent();
	}

	public Integer getSelectItem() {
		return selectItem.get(); //can throw exception if used incorrectly
	}

	public void setSelectItem(Integer selectWeapon) {
		this.selectItem = Optional.of(selectWeapon);
	}

	public boolean requestShoot() {
		return shoot;
	}

	public void setShoot(boolean shoot) {
		this.shoot = shoot;
	}

	public boolean requestReload() {
		return reload;
	}

	public void setReload(boolean reload) {
		this.reload = reload;
	}
		
}
