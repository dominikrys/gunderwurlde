package inputhandler;

import client.data.EntityView;

public abstract class Action {
	protected EntityView performer;
	
	public Action(EntityView performer) {
		this.performer = performer;
	}
}
