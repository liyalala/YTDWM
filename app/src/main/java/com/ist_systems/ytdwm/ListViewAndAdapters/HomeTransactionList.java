package com.ist_systems.ytdwm.ListViewAndAdapters;

public class HomeTransactionList {

    public String TransNo;
    public String TransTyp;
    public String Remarks;
    public String CreatedBy;
    public String CreatedDt;
    public String StatusCd;

    public HomeTransactionList(String transNo, String transTyp, String remarks, String createdBy, String createdDt, String statusCd) {
        this.TransNo = transNo;
        this.TransTyp = transTyp;
        this.Remarks = remarks;
        this.CreatedBy = createdBy;
        this.CreatedDt = createdDt;
        this.StatusCd = statusCd;
    }
}
