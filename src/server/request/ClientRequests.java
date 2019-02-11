package server.request;

import java.util.LinkedHashMap;


public class ClientRequests {
    protected LinkedHashMap<Integer, Request> playerRequests;

    public ClientRequests(int numOfPlayers) {
        this.playerRequests = new LinkedHashMap<>();
        for (int i = 0; i < numOfPlayers; i++) {
            playerRequests.put(i, new Request());
        }
    }

    public LinkedHashMap<Integer, Request> getPlayerRequests() {
        return playerRequests;
    }

    public boolean updateRequest(int playerID, Request request) {
        if (playerRequests.containsKey(playerID)) {
            Request updatedRequest = playerRequests.get(playerID);

            if (request.getLeave()) {
                updatedRequest.requestLeave();
            } else {
                if (request.facingExists())
                    updatedRequest.setFacing(request.getFacing());
                if (request.movementExists())
                    updatedRequest.setMovementDirection(request.getMovementDirection());
                if (request.selectItemAtExists())
                    updatedRequest.setSelectItem(request.getSelectItemAt());
                if (request.getReload())
                    updatedRequest.requestReload();
                if (request.getDrop())
                    updatedRequest.requestDrop();
                if (request.getShoot())
                    updatedRequest.requestShoot();
            }

            playerRequests.put(playerID, updatedRequest);
            return true;
        } else
            return false;
    }

    // TODO remove individual methods below if requests are sent by client

    public boolean playerRequestMovement(int playerID, int direction) {
        if (playerRequests.containsKey(playerID)) {
            playerRequests.get(playerID).setMovementDirection(direction);
            return true;
        } else
            return false;
    }
    
    public boolean playerRequestFacing(int playerID, int direction) {
        if (playerRequests.containsKey(playerID)) {
            playerRequests.get(playerID).setFacing(direction);
            return true;
        } else
            return false;
    }

    public boolean playerRequestSelectItem(int playerID, int itemPosition) {
        if (playerRequests.containsKey(playerID)) {
            playerRequests.get(playerID).setSelectItem(itemPosition);
            ;
            return true;
        } else
            return false;
    }

    public boolean playerRequestShoot(int playerID) {
        if (playerRequests.containsKey(playerID)) {
            playerRequests.get(playerID).requestShoot();
            return true;
        } else
            return false;
    }

    public boolean playerRequestReload(int playerID) {
        if (playerRequests.containsKey(playerID)) {
            playerRequests.get(playerID).requestReload();
            return true;
        } else
            return false;
    }

    public boolean playerRequestLeave(int playerID) {
        if (playerRequests.containsKey(playerID)) {
            playerRequests.get(playerID).requestLeave();
            return true;
        } else
            return false;
    }
}
