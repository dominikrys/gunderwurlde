package server.request;

import java.util.Optional;

import data.Pose;

public class Request {
    protected Optional<Integer> movementDir;
    protected Optional<Integer> facing;
    protected Optional<Integer> selectItem;
    protected boolean shoot;
    protected boolean reload;
    protected boolean leave;

    public Request() {
        this.shoot = false;
        this.reload = false;
        this.leave = false;
        this.movementDir = Optional.empty();
        this.facing = Optional.empty();
        this.selectItem = Optional.empty();
    }

    public boolean movementExists() {
        return movementDir.isPresent();
    }

    public int getMovementDirection() {
        return movementDir.get(); // can throw exception if used incorrectly
    }

    public void setMovementDirection(int direction) {
        this.movementDir = Optional.of(Pose.normaliseDirection(direction));
    }
    
    public boolean facingExists() {
        return facing.isPresent();
    }
    
    public int getFacing() {
        return facing.get(); // can throw exception if used incorrectly
    }

    public void setFacing(int direction) {
        this.facing = Optional.of(Pose.normaliseDirection(direction));
    }

    public boolean selectItemExists() {
        return selectItem.isPresent();
    }

    public Integer getSelectItem() {
        return selectItem.get(); // can throw exception if used incorrectly
    }

    public void setSelectItem(Integer selectWeapon) {
        this.selectItem = Optional.of(selectWeapon);
    }

    public void requestShoot() {
        this.shoot = true;
    }

    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

    public boolean getShoot() {
        return shoot;
    }

    public void requestReload() {
        this.reload = true;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public boolean getReload() {
        return reload;
    }

    public void requestLeave() {
        this.leave = true;
    }

    public boolean getLeave() {
        return leave;
    }

}
