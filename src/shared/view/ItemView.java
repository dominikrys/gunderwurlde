package shared.view;

import java.io.Serializable;

import shared.lists.AmmoList;
import shared.lists.ItemList;

public class ItemView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected ItemList name;
    protected AmmoList ammoType;
    protected int clipSize;
    protected int ammoInClip;
    protected boolean autoFire;

    public ItemView(ItemList name, AmmoList ammoType, int clipSize, int ammoInClip, boolean autoFire) {
        this.name = name;
        this.ammoType = ammoType;
        this.clipSize = clipSize;
        this.ammoInClip = ammoInClip;
        this.autoFire = autoFire;

    }

    public boolean isAutoFire() {
        return autoFire;
    }

    public ItemList getItemListName() {
        return name;
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
