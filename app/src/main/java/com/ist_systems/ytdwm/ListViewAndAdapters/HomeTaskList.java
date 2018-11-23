package com.ist_systems.ytdwm.ListViewAndAdapters;

/**
 * Created by jmcaceres on 03/25/2017.
 */

public class HomeTaskList {
    public String TranNo;
    public String Month;
    public String Day;
    public String Tag;
    public String Task;
    public String ContNo;
    public String Vessel;

    public HomeTaskList(String tranno, String month, String day, String tag, String task, String contNo, String vessel) {
        this.TranNo = tranno;
        this.Month = month;
        this.Day = day;
        this.Tag = tag;
        this.Task = task;
        this.ContNo = contNo;
        this.Vessel = vessel;
    }
}
