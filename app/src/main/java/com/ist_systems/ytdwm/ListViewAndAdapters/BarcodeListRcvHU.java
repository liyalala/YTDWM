package com.ist_systems.ytdwm.ListViewAndAdapters;

/**
 * Created by jmcaceres on 04/11/2017.
 */

public class BarcodeListRcvHU {

    private String OuterPkg;
    private String HU;

    public BarcodeListRcvHU(String outerPkg, String hu) {
        super();
        this.OuterPkg = outerPkg;
        this.HU = hu;
    }

    public String getOuterPkg() {
        return OuterPkg;
    }

    public String getHU() {
        return HU;
    }

    public void setHU(String hu) {
        this.HU = hu;
    }
}
