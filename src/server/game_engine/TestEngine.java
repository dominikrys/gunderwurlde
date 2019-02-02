package server.game_engine;

import client.data.GameView;
import data.map.MapList;
import server.request.ClientRequests;

public class TestEngine implements HasEngine {
    private ProcessGameState engine;
    
    public static void main(String[] args) throws Exception{
        new TestEngine();
    }
    
    public TestEngine() throws Exception {
        this.engine = new ProcessGameState(this, MapList.MEADOW, "Bob");
        engine.start();
        ClientRequests requests = new ClientRequests(1);
        requests.playerRequestFacing(0, 160);
        requests.playerRequestShoot(0);
        requests.playerRequestMovement(0, 90);     
        engine.setClientRequests(requests);
        Thread.sleep(1000);
        engine.serverClosing();
    }

    @Override
    public void updateGameView(GameView view) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removePlayer(int playerID) {
        // TODO Auto-generated method stub
        
    }

}
