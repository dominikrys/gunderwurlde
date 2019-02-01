package client.data;

import data.entity.item.weapon.gun.AmmoList;
import data.entity.item.weapon.gun.GunList;

public class GunView extends ItemView {
    protected int clipSize;
    protected int ammoInClip;
    AmmoList ammoType;

    public GunView(GunList name, int clipSize, int ammoInClip, AmmoList ammoType) {
        super(name.toItemList());
        this.clipSize = clipSize;
        this.ammoInClip = ammoInClip;
        this.ammoType = ammoType;
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
