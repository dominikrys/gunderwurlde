package client.inputhandler;

import client.ClientHandler;
import client.data.entity.EntityView;

public abstract class Action {
	protected ClientHandler handler;
	protected EntityView performer;
	
	public Action(ClientHandler handler, EntityView performer) {
		this.handler = handler;
		this.performer = performer;
	}
}
