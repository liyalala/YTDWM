package com.ist_systems.ytdwm.ListViewAndAdapters;

/**
 * Created by jmcaceres on 05/08/2017.
 */

public class BarcodeListPutAway {
    private String BinCd;
    private String HU;
    private String OuterPkg;
    private String HLHUID;

    public BarcodeListPutAway(String bincd, String hu, String outerpkg, String hlhuid) {
        super();
        this.BinCd = bincd;
        this.HU = hu;
        this.OuterPkg = outerpkg;
        this.HLHUID = hlhuid;
    }

    public String getBinCd() {
        return this.BinCd;
    }

    public void setBinCd(String strbin) {
        this.BinCd = strbin;
    }

    public String getHU() {
        return this.HU;
    }

    public void setHU(String strHU) {
        this.HU = strHU;
    }

    public String getOuterPkg() {
        return OuterPkg;
    }

    public void setOuterPkg(String strouterpkg) {
        this.OuterPkg = strouterpkg;
    }

    public String getHLHUID() {
        return HLHUID;
    }

    public void setHLHUID(String strhlhuid) {
        this.HLHUID = strhlhuid;
    }
}
