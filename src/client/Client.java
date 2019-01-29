package client;

import data.GameState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client {
    protected GameState gameState;
    protected String playerName;
    protected int playerID;

    private ClientSender sender;
    private ClientReceiver receiver;
    private RendererController renderer;
    private inputController input;
    private inputChecker inChecker;
    //private audioController audio;
    private Socket server;


    public Client() {
        renderer = new RendererController(this);
    }
    
    /*
    Methods called by renderer:
    */

    public void setPlayerName(String name) {
        this.playerName = name;
        sendPlayerName();
    }

    public void joinServer(Socket server) {
        this.server = server;
        tryConnection();
    }

    public void createServer() { //will need to include params i just can't be bothered to guess.

    }
    
    /*
    Methods called by receiver:
    */

    public void setID(int id) {
        this.playerID = id;
        renderer.setID(id);
    }

    public void updateGameState(GameState gameState) {
        if (this.gameState == null) {
            this.gameState = gameState;
            input = new inputController(this); //assuming input controller is only for ingame input???
        }
        renderer.renderGame(this.gameState);
    }

    public void close() {
        sender.close();
        //whatever you wanna do here to close the client, System.exit(0)?
    }

    /*
    Request methods to be called by input controller:
    They are to be checked and if it passes return true after submitting the request to the sender to handle.
    */
    public void requestToFace(int direction) {
        sender.requestToFace(direction);
    }

    public boolean requestToMove(int direction) {
        if (inChecker.checkMove(direction)) {
            sender.requestMove(direction);
            return true;
        } else return false;
    }

    public boolean requestToChangeItem(int newCurrentItem) {
        if (inChecker.checkItemChange(newCurrentItem)) {
            sender.requestItemChange(newCurrentItem);
            return true;
        } else return false;
    }

    public boolean requestNextItem() {
        if (inChecker.checkOtherItem()) {
            sender.requestNextItem();
            return true;
        } else return false;
    }

    public boolean requestPreviousItem() {
        if (inChecker.checkOtherItem()) {
            sender.requestPreviousItem();
            return true;
        } else return false;
    }

    public boolean requestToReload() {
        if (inChecker.checkHasAmmo()) {
            sender.requestReload();
            return true;
        } else return false;
    }

    public void requestToLeave() {
        sender.requestLeave();
    }
    
    /*
    Other methods that call the sender:
    */

    private void sendName() {
        sender.sendName(this.playerName);
    }
    
    /*
    Misc:
    */

    private void tryConnection() {

        try {
            receiver = new ClientReceiver(this, new BufferedReader(new InputStreamReader(server.getInputStream())));
            receiver.start();
            sender = new ClientSender(new PrintStream(server.getOutputStream()));
        } catch (IOException e) {
            //insert whatever you wanna do here
        }


    }
}
