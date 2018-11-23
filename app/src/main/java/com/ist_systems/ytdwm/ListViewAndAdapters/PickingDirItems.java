package com.ist_systems.ytdwm.ListViewAndAdapters;

/**
 * Created by jmcaceres on 3/2/2018.
 */

public class PickingDirItems {
    public String Bin;
    public String HLHU;
    public String HU;
    public String MatNo;
    public String Batch;
    public String ReqdQty;
    public String PickQty;
    public String PickingHU;
    public String RollNo;
    public String DyeLot;
    public String IsValidHLHUID;
    public String FabToning;
    public String SourceHU;

    public PickingDirItems(String bin, String hlhu, String hu, String matno, String batch, String reqdQty, String pickQty, String pickingHU) {
        this.Bin = bin;
        this.HLHU = hlhu;
        this.HU = hu;
        this.MatNo = matno;
        this.Batch = batch;
        this.ReqdQty = reqdQty;
        this.PickQty = pickQty;
        this.PickingHU = pickingHU;
    }

    public PickingDirItems(String bin, String hlhu, String hu, String matno, String batch, String reqdQty, String pickQty,
                           String pickingHU, String rollNo, String dyeLot, String isValidHLHUID, String fabToning, String sourceHU) {
        this.Bin = bin;
        this.HLHU = hlhu;
        this.HU = hu;
        this.MatNo = matno;
        this.Batch = batch;
        this.ReqdQty = reqdQty;
        this.PickQty = pickQty;
        this.PickingHU = pickingHU;
        this.RollNo = rollNo;
        this.DyeLot = dyeLot;
        this.IsValidHLHUID = isValidHLHUID;
        this.FabToning = fabToning;
        this.SourceHU = sourceHU;
    }

    public String getBin() {
        return Bin;
    }

    public String getHUID() {
        return HU;
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

    public String getPickQty() {
        return PickQty;
    }

    public String getOutPkg() {
        return HLHU;
    }

    public String getIsValidHLHUID() {
        return IsValidHLHUID;
    }

    public void setPickQty(String pickQty) {
        this.PickQty = pickQty;
    }
}
