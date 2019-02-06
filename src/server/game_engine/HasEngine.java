package server.game_engine;

import client.data.entity.GameView;

public interface HasEngine {
    public void updateGameView(GameView view);
    public void removePlayer(int playerID);    
    public void requestClientRequests();
}
