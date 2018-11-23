package com.ist_systems.ytdwm.ListViewAndAdapters;

public class ODIssuance1 {

    public String PkgNo;
    public String HUID;
    public String MatNo;
    public String Batch;
    public String VendorLot;
    public String FabToning;
    public String ReqdQty;
    public String OrigIssQty;
    public String IssQty;

    public ODIssuance1(String pkgno, String huid, String matno, String batch, String vendorLot, String fabToning, String reqdQty, String issQty) {
        this.PkgNo = pkgno;
        this.HUID = huid;
        this.MatNo = matno;
        this.Batch = batch;
        this.VendorLot = vendorLot;
        this.FabToning = fabToning;
        this.ReqdQty = reqdQty;
        this.OrigIssQty = issQty;
        this.IssQty = issQty;
    }

    public String getHUID() {
        return this.HUID;
    }

    public String getOrigIssQty() {
        return this.OrigIssQty;
    }

    public String getIssQtyFld() {
        return this.IssQty;
    }

    public void setIssQty(String strNewQty) {
        this.IssQty = strNewQty;
    }
}
