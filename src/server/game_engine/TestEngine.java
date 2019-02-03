package server.game_engine;

import java.util.Random;

import client.data.GameView;
import data.entity.player.Teams;
import data.map.MapList;
import server.request.ClientRequests;

public class TestEngine implements HasEngine {
    private ProcessGameState engine;

    public static void main(String[] args) throws Exception {
        new TestEngine();
    }

    public TestEngine() throws Exception {
        Random rand = new Random();
        this.engine = new ProcessGameState(this, MapList.MEADOW, "Bob");
        engine.start();
        engine.addPlayer("Bob2", Teams.RED);
        engine.addPlayer("Bob3", Teams.RED);
        engine.addPlayer("Bob4", Teams.RED);
        
        ClientRequests requests = new ClientRequests(2);
        for (int i=0;i<1000;i++) {
            requests.playerRequestFacing(0,rand.nextInt(360));
            requests.playerRequestMovement(0, rand.nextInt(360));
            requests.playerRequestFacing(1, rand.nextInt(360));
            requests.playerRequestMovement(1, rand.nextInt(360));
            requests.playerRequestFacing(2, rand.nextInt(360));
            requests.playerRequestMovement(2, rand.nextInt(360));
            requests.playerRequestFacing(3, rand.nextInt(360));
            requests.playerRequestMovement(3, rand.nextInt(360));
            engine.setClientRequests(requests);
            Thread.sleep(17);
        }

        engine.handlerClosing();
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
