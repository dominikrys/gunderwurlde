package server.game_engine;

import java.util.Random;

import client.Renderer;
import client.data.GameView;
import data.entity.player.Teams;
import data.map.MapList;
import javafx.application.Application;
import javafx.stage.Stage;
import server.request.ClientRequests;

public class TestEngine extends Application implements HasEngine {
    private ProcessGameState engine;
    private Renderer rend;
    private GameView view;

    public static void main(String[] args) throws Exception {
        launch(args);
    }



    @Override
    public void updateGameView(GameView view) {
        this.view = view;
    }

    @Override
    public void removePlayer(int playerID) {
        // TODO Auto-generated method stub

    }

    @Override
    public void start(Stage stage) throws Exception {
        Random rand = new Random();
        this.engine = new ProcessGameState(this, MapList.MEADOW, "Bob");
        stage.setResizable(false);
        this.rend = new Renderer(stage);
        engine.start();
        engine.addPlayer("Bob2", Teams.RED);
        engine.addPlayer("Bob3", Teams.RED);
        engine.addPlayer("Bob4", Teams.RED);
        
        ClientRequests requests = new ClientRequests(2);
        for (int i=0;i<1000;i++) {
            requests.playerRequestFacing(0,rand.nextInt(360));
            requests.playerRequestMovement(0, rand.nextInt(360));
            requests.playerRequestShoot(0);
            requests.playerRequestFacing(1, rand.nextInt(360));
            requests.playerRequestMovement(1, rand.nextInt(360));
            requests.playerRequestShoot(1);
            requests.playerRequestFacing(2, rand.nextInt(360));
            requests.playerRequestMovement(2, rand.nextInt(360));
            requests.playerRequestShoot(2);
            requests.playerRequestFacing(3, rand.nextInt(360));
            requests.playerRequestMovement(3, rand.nextInt(360));
            requests.playerRequestShoot(3);
            engine.setClientRequests(requests);
            Thread.sleep(17);
            if (view != null) rend.renderGameView(view, 0);
            else System.out.println("View is null");
        }

        engine.handlerClosing();       
    }

}
