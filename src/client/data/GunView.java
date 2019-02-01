package client.data;

import data.entity.item.weapon.gun.GunList;

public class GunView extends ItemView {
    protected int clipSize;
    protected int ammoInClip;

    public GunView(GunList name, int clipSize, int ammoInClip) {
        super(name.toItemList());
    }

    public int getClipSize() {
        return clipSize;
    }

    public int getAmmoInClip() {
        return ammoInClip;
    }

}
