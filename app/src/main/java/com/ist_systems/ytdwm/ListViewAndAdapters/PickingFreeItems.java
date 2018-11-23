package com.ist_systems.ytdwm.ListViewAndAdapters;

import java.util.ArrayList;

/**
 * Created by jmcaceres on 3/5/2018.
 */

public class PickingFreeItems {

    public String DlvItem;
    public String MatNo;
    public String Batch;
    public String ReqdQty;

    private ArrayList<PickingFreeSubItems> subItems = new ArrayList<>();
    private ArrayList<String> arrHU = new ArrayList<>();

    public void setValues(String dlvItem, String matno, String batch, String reqdQty) {
        this.DlvItem = dlvItem;
        this.MatNo = matno;
        this.Batch = batch;
        this.ReqdQty = reqdQty;
    }

    public ArrayList<PickingFreeSubItems> getSubItems() {
        return subItems;
    }

    public void setSubItems(ArrayList<PickingFreeSubItems> pickingDirSubItems) {
        this.subItems = pickingDirSubItems;

        arrHU.clear();
        for (int i = 0; i < subItems.size(); i++) {
            arrHU.add(subItems.get(i).getHU());
        }
    }

    public String getMatNo() {
        return MatNo;
    }

    public String getBatch() {
        return Batch;
    }

    public String getReqdQty() {
        return ReqdQty;
    }

    public ArrayList<String> getHUs() {
        return arrHU;
    }


}
