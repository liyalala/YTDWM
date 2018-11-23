package com.ist_systems.ytdwm.ListViewAndAdapters;

/**
 * Created by jmcaceres on 04/26/2017.
 */

public class IDPOSummary {
    public String PONo;
    public String POLn;
    public String MatNo;
    public String Batch;
    public String POQty;
    public String DlvQty;
    public String UOM;

    public IDPOSummary(String pono, String poln, String matno, String batch, String poqty, String dlvqty, String uom) {
        this.PONo = pono;
        this.POLn = poln;
        this.MatNo = matno;
        this.Batch = batch;
        this.POQty = poqty;
        this.DlvQty = dlvqty;
        this.UOM = uom;
    }
}
