package client.input;

import client.Client;
import client.GameHandler;
import shared.view.entity.EntityView;

public abstract class Action {
    protected Client handler;
    protected EntityView performer;

    public Action(Client handler, EntityView performer) {
        this.handler = handler;
        this.performer = performer;
    }
}
