package com.ist_systems.ytdwm.ListViewAndAdapters;

/**
 * Created by jmcaceres on 03/26/2017.
 */

public class PutAway {
    public String OuterPkg;
    public String HU;
    public String Bin;
    public String HLHU;

    public PutAway(String outerPkg, String hu, String bin, String hlhu) {
        this.OuterPkg = outerPkg;
        this.HU = hu;
        this.Bin = bin;
        this.HLHU = hlhu;
    }

    public String getBin() {
        return Bin;
    }

    public String getOuterPkg() {
        return OuterPkg;
    }
}
