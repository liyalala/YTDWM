package com.ist_systems.ytdwm.ListViewAndAdapters;

/**
 * Created by jmcaceres on 04/26/2017.
 */

public class IDDetails {
    public String PONo;
    public String MatNo;
    public String Batch;
    public String DlvQty;
    public String UOM;
    public String VendorLot;
    public String PkgNo;

    public IDDetails(String pono, String matno, String batch, String dlvqty, String uom, String vendorLot, String pkgno) {
        this.PONo = pono;
        this.MatNo = matno;
        this.Batch = batch;
        this.DlvQty = dlvqty;
        this.UOM = uom;
        this.VendorLot = vendorLot;
        this.PkgNo = pkgno;
    }
}
