package server;

import client.data.GameView;
import data.map.MapList;
import server.game_engine.HasEngine;
import server.game_engine.ProcessGameState;
import server.request.ClientRequests;

public class Server implements HasEngine {
    protected ClientRequests clientRequests;
    protected ProcessGameState engine;
    
    public Server(MapList mapName, String hostName) {
        this.engine = new ProcessGameState(this, mapName, hostName);
    }

    @Override
    public void removePlayer(int playerID) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void updateGameView(GameView view) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void requestClientRequests() {
        if (clientRequests != null) engine.setClientRequests(clientRequests);
    }

}
