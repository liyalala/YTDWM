package com.ist_systems.ytdwm.ListViewAndAdapters;

public class ODIssuance {

    public String HUID;
    public String IssQty;

    public ODIssuance(String huid, String issQty) {
        this.HUID = huid;
        this.IssQty = issQty;
    }

    public String getHUID() {
        return this.HUID;
    }

    public String getIssQty() {
        return this.IssQty;
    }

    public void setIssQty(String strNewQty) {
        this.IssQty = strNewQty;
    }
}
