package com.ist_systems.ytdwm.ListViewAndAdapters;

/**
 * Created by jmcaceres on 5/22/2018.
 */

public class StockInquiry {

    public String MatNo;
    public String Batch;
    public String Color;
    public String HLHUID;
    public String HLHUID1;
    public String HUID;
    public String HUID1;
    public String VendorLot;
    public String FabToning;
    public String AvailQty;
    public String Bin;
    public String StorAreaCd;

    public StockInquiry(String matno, String batch, String color, String hlhuid, String hlhuid1, String huid, String huid1,
                        String vendorLot, String fabToning, String availQty, String bin, String storeAreaCd) {
        this.MatNo = matno;
        this.Batch = batch;
        this.Color = color;
        this.HLHUID = hlhuid;
        this.HLHUID1 = hlhuid1;
        this.HUID = huid;
        this.HUID1 = huid1;
        this.VendorLot = vendorLot;
        this.FabToning = fabToning;
        this.AvailQty = availQty;
        this.Bin = bin;
        this.StorAreaCd = storeAreaCd;
    }

    public String getBinCd() {
        return Bin;
    }

    public String getHLHUID() {
        return HLHUID;
    }

    public String getHUID() {
        return HUID;
    }

}
