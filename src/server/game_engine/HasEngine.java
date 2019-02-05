package server.game_engine;

import client.data.GameView;
import server.request.ClientRequests;
import server.request.Request;

public interface HasEngine {
    public void updateGameView(GameView view);
    public void removePlayer(int playerID);
    public void sendClientRequest(ClientRequests request);
}
