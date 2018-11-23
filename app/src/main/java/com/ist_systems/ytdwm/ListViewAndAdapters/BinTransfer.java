package com.ist_systems.ytdwm.ListViewAndAdapters;

/**
 * Created by jmcaceres on 5/23/2018.
 */

public class BinTransfer {

    public String DestBin;
    public String HLHUID;
    public String HUID;

    public BinTransfer(String destBin, String hlhuid, String huid) {
        this.DestBin = destBin;
        this.HLHUID = hlhuid;
        this.HUID = huid;
    }

    public String getDestBin() {
        return this.DestBin;
    }

    public String getHLHUID() {
        return this.HLHUID;
    }

    public String getHUID() {
        return this.HUID;
    }
}
