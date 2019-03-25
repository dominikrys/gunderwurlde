package shared.view;

import java.io.Serializable;

import shared.lists.AmmoList;
import shared.lists.ItemList;
import shared.lists.ItemType;

public class GunView extends ItemView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected AmmoList ammoType;
    protected int clipSize;
    protected int ammoInClip;
    protected boolean autoFire;
    protected int reloadTime;

    public GunView(ItemList name, AmmoList ammoType, int clipSize, int ammoInClip, boolean autoFire, int reloadTime) {
        super(name, ItemType.GUN);
        this.ammoType = ammoType;
        this.clipSize = clipSize;
        this.ammoInClip = ammoInClip;
        this.autoFire = autoFire;
        this.reloadTime = reloadTime;

    }

    public int getReloadTime() { // total reload time, not time left till reload!
        return reloadTime;
    }

    public boolean isAutoFire() {
        return autoFire;
    }

    public AmmoList getAmmoType() {
        return ammoType;
    }

    public int getClipSize() {
        return clipSize;
    }

    public int getAmmoInClip() {
        return ammoInClip;
    }
}
