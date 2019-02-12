package server.engine;

import shared.view.GameView;

public interface HasEngine {
    public void updateGameView(GameView view);
    public void removePlayer(int playerID);    
    public void requestClientRequests();
}
