package server.game_engine.data;

public class ProjectileChange extends Change {
	protected boolean poseChange;
	protected boolean typeChange;
	protected boolean speedChange;
	protected boolean damageChange;
	
	public ProjectileChange() {
		this.poseChange = false;
		this.typeChange = false;
		this.speedChange = false;
		this.damageChange = false;
	}
	
	public ProjectileChange(ChangeType type) {
		this.type = type;
		switch (type) {
		case FULL_CHANGE:
			this.speedChange = true;
			this.damageChange = true;
		case NEW:
			this.typeChange = true;
		case BASIC_CHANGE:
			this.poseChange = true;
			break;
		case NONE:
			break;
		case REMOVED:
			break;
		}
	}
	

	public boolean isPoseChange() {
		return poseChange;
	}

	public void poseChanged() {
		if (type == ChangeType.NONE) type = ChangeType.BASIC_CHANGE;
		this.poseChange = true;
	}

	public boolean isTypeChange() {
		return typeChange;
	}

	public void typeChanged() {
		if (type == ChangeType.NONE|| type == ChangeType.BASIC_CHANGE) type = ChangeType.NEW;
		this.typeChange = true;
	}

	public boolean isSpeedChange() {
		return speedChange;
	}

	public void speedChanged() {
		if (type != ChangeType.FULL_CHANGE) type = ChangeType.FULL_CHANGE;
		this.speedChange = true;
	}

	public boolean isDamageChange() {
		return damageChange;
	}

	public void damageChanged() {
		if (type != ChangeType.FULL_CHANGE) type = ChangeType.FULL_CHANGE;
		this.damageChange = true;
	}
	
	

}
