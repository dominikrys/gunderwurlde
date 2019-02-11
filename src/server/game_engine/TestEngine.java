package server.game_engine;

import java.util.Random;

import client.GameRenderer;
import client.data.entity.GameView;
import data.map.MapList;
import javafx.application.Application;
import javafx.stage.Stage;
import server.request.ClientRequests;
import java.util.concurrent.TimeUnit;

public class TestEngine extends Application implements HasEngine {
    private static final int LOOPS = 1000;
    private ProcessGameState engine;
    private GameRenderer rend;
    private GameView view;
    private ClientRequests requests;
    private boolean firstRender;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void updateGameView(GameView view) {
        this.view = view;
        if (!firstRender)
            rend.updateGameView(this.view);
    }

    @Override
    public void removePlayer(int playerID) {
        // TODO Auto-generated method stub

    }

    @Override
    public void requestClientRequests() {
        if (requests != null)
            engine.setClientRequests(requests);
    }

    @Override
    public void start(Stage stage) throws Exception {
        firstRender = true;
        this.engine = new ProcessGameState(this, MapList.MEADOWTEST, "Bob");
        stage.setResizable(true);
        engine.start();
        requests = new ClientRequests(1);
        loopDeDoop(stage);
        while (firstRender) {
            if (view != null) {
                stage.show();
                rend = new GameRenderer(stage, view, 0);
                firstRender = false;
                rend.run();
                System.out.println("Renderer started");
            } else {
                //System.out.println("View is null");
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

    private void loopDeDoop(Stage stage) {
        Random rand = new Random();
        new Thread() {
            public void run() {
                for (int i = 0; i < LOOPS; i++) {
//                    requests.playerRequestFacing(0, rand.nextInt(360));
//                    requests.playerRequestMovement(0, rand.nextInt(360));
//                    requests.playerRequestShoot(0);
//                    requests.playerRequestFacing(1, rand.nextInt(360));
//                    requests.playerRequestMovement(1, rand.nextInt(360));
//                    requests.playerRequestShoot(1);
//                    requests.playerRequestFacing(2, rand.nextInt(360));
//                    requests.playerRequestMovement(2, rand.nextInt(360));
//                    requests.playerRequestShoot(2);
//                    requests.playerRequestFacing(3, rand.nextInt(360));
//                    requests.playerRequestMovement(3, rand.nextInt(360));
//                    requests.playerRequestShoot(3);
                    try {
                        Thread.sleep(17);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                engine.handlerClosing();
            }
        }.start();
    }

}
