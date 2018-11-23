package com.ist_systems.ytdwm.ListViewAndAdapters;

/**
 * Created by jmcaceres on 3/5/2018.
 */

public class PickingFreeSubItems {

    public String DlvItem;
    public String Bin;
    public String HU;
    public String PickQty;
    public String PickingHU;
    private String sequence = "";

    public void setValues(String dlvItem, String bin, String hu, String pickQty, String pickingHU) {
        this.DlvItem = dlvItem;
        this.Bin = bin;
        this.HU = hu;
        this.PickQty = pickQty;
        this.PickingHU = pickingHU;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getBin() {
        return Bin;
    }

    public String getHU() {
        return HU;
    }

    public String getPickingHU() {
        return PickingHU;
    }

    public String getPickQty() {
        return PickQty;
    }


}
