package client.inputhandler;

import client.data.entity.EntityView;

public abstract class Action {
	protected EntityView performer;
	
	public Action(EntityView performer) {
		this.performer = performer;
	}
}
