package server.game_engine;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import client.GameRenderer;
import client.data.GameView;
import data.entity.player.Teams;
import data.map.MapList;
import javafx.application.Application;
import javafx.stage.Stage;
import server.request.ClientRequests;

public class TestEngine extends Application implements HasEngine {
    private ProcessGameState engine;
    private GameRenderer rend;
    private GameView view;
    private ClientRequests requests;

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
    public void requestClientRequests() {
        if (requests != null) engine.setClientRequests(requests);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Random rand = new Random();
        this.engine = new ProcessGameState(this, MapList.MEADOW, "Bob");
        stage.setResizable(false);
        engine.start();
        engine.addPlayer("Bob2", Teams.RED);
        engine.addPlayer("Bob3", Teams.RED);
        engine.addPlayer("Bob4", Teams.RED);
        boolean firstRender = true;
        
        requests = new ClientRequests(4);
        for (int i = 0; i < 1000; i++) {
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
            Thread.sleep(17);
            
            if (firstRender && view != null) {
                stage.show();
                rend = new GameRenderer(stage, view, 0);
                firstRender = false;
                rend.run();
                System.out.println("Timer started");
                startTheTimer();
            }
        }
        engine.handlerClosing();       
    }

    private void startTheTimer() {
        new Thread() {
            public void run() {
                final AtomicBoolean a = new AtomicBoolean(true);
                Timer t = new Timer();
                t.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        if (a.get()) {
                            rend.updateGameView(view);
                            a.set(false);
                        } else {
                            rend.updateGameView(view);
                            a.set(true);
                        }

                    }

                }, 0, 1000);
            }
        }.start();
    }

}
