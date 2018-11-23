package com.ist_systems.ytdwm.ListViewAndAdapters;

/**
 * Created by jmcaceres on 3/22/2018.
 */

public class SearchList {

    public String TranNo;
    public String Typ;
    public String Status;
    public String PlantCd;
    public String SLoc;
    public String ContNo;
    public String Vessel;
    public String CreatedBy;
    public String CreatedDt;
    public String ActDt;
    public String ReqdDt;
    public String ERPIONo;
    public String DlvNo;
    public String RsvNo;

    public SearchList(String tranNo, String typ, String status, String plantCd, String sloc,
                      String contNo, String vessel, String createdBy, String createdDt, String actDt,
                      String reqdDt, String erpiono, String dlvno, String rsvno) {
        this.TranNo = tranNo;
        this.Typ = typ;
        this.Status = status;
        this.PlantCd = plantCd;
        this.SLoc = sloc;
        this.ContNo = contNo;
        this.Vessel = vessel;
        this.CreatedBy = createdBy;
        this.CreatedDt = createdDt;
        this.ActDt = actDt;
        this.ReqdDt = reqdDt;
        this.ERPIONo = erpiono;
        this.DlvNo = dlvno;
        this.RsvNo = rsvno;
    }

    public String getTranNo() {
        return TranNo;
    }

    public String getContNo() {
        return ContNo;
    }

    public String getERPIONo() {
        return ERPIONo;
    }

}
