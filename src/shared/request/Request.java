package shared.request;

import shared.Pose;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int movementDir;
    protected int facing;
    protected int selectItemAt;
    protected boolean shoot;
    protected boolean reload;
    protected boolean leave;
    protected boolean drop;

    public Request() {
        this.shoot = false;
        this.reload = false;
        this.leave = false;
        this.drop = false;
        this.movementDir = -1;
        this.facing = -1;
        this.selectItemAt = -1;
    }

    public void requestDrop() {
        this.drop = true;
    }

    public boolean getDrop() {
        return drop;
    }

    public boolean movementExists() {
        return (movementDir != -1);
    }

    public int getMovementDirection() {
        return movementDir;
    }

    public void setMovementDirection(int direction) {
        this.movementDir = Pose.normaliseDirection(direction);
    }
    
    public boolean facingExists() {
        return (facing != -1);
    }
    
    public int getFacing() {
        return facing;
    }

    public void setFacing(int direction) {
        this.facing = Pose.normaliseDirection(direction);
    }

    public boolean selectItemAtExists() {
        return (selectItemAt != -1);
    }

    public Integer getSelectItemAt() {
        return selectItemAt;
    }

    public void setSelectItem(Integer itemIndex) {
        this.selectItemAt = itemIndex;
    }

    public void requestShoot() {
        this.shoot = true;
    }

    public boolean getShoot() {
        return shoot;
    }

    public void requestReload() {
        this.reload = true;
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
