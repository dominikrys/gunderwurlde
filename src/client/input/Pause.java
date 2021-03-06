package client.input;

import client.Client;
import shared.view.entity.PlayerView;

public class Pause extends Action {

    private Client handler;

    public Pause(Client handler, PlayerView playerView) {
        super(handler, playerView);
        this.handler = handler;
    }

    public void pause() {
        handler.send(CommandList.PAUSED);
    }

    public void resume() {
        handler.send(CommandList.RESUME);
    }
}
