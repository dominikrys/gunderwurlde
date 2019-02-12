package client.input;

import client.GameHandler;
import shared.view.entity.EntityView;

public abstract class Action {
    protected GameHandler handler;
    protected EntityView performer;

    public Action(GameHandler handler, EntityView performer) {
        this.handler = handler;
        this.performer = performer;
    }
}
