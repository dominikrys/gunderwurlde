package client.input;

import client.Client;
import shared.view.entity.EntityView;

/**
 * Action class. This is the abstract class of all player actions.
 *
 * @author Mak Hong Lun Timothy
 */
public abstract class Action {
	/**
     * Client handler for sending requests
     */
    protected Client handler;
    /**
     * Performer of the action
     */
    protected EntityView performer;

    /**
     * Constructor
     *
     * @param handler  Client handler
     * @param performer Performer of action
     */
    public Action(Client handler, EntityView performer) {
        this.handler = handler;
        this.performer = performer;
    }
}
