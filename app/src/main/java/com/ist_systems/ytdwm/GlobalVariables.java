package com.ist_systems.ytdwm;

/**
 * Created by jmcaceres on 03/24/2017.
 */

public class GlobalVariables {

    public static String gblUserID;
    public static String gblUserPW;
    public static String gblPattern;
    public static String gblURL;
    public static String gblFolerPath;

    public static String gblDlvNo;
    public static String gblDlvStatus;
    public static String gblDlvStatusCd;
    public static String gblContVessel;

    public static String gblTONo;
    public static String gblTOTyp;
    public static String gblMatGrp;
    public static String gblTask;
    public static String gblDeviceName;

    public static int gblTimeOut;
    public static int gblReadTime;
    public static int gblBuffer;

    public static int gblSQLiteVersion;

    public static String GetUserLogs() {
        String strSQL = "CREATE TABLE IF NOT EXISTS UserLog (";
        strSQL += "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ";
        strSQL += "UserId VARCHAR, ";
        strSQL += "UserPattern INTEGER);";

        return strSQL;
    }

    public static String GetSummary() {
        String strSQL = "CREATE TABLE IF NOT EXISTS Summary (";
        strSQL += "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ";
        strSQL += "RecId VARCHAR, ";
        strSQL += "BinCd VARCHAR, ";
        strSQL += "OuterPkg VARCHAR, ";
        strSQL += "HU VARCHAR, ";
        strSQL += "NewOuterPkg VARCHAR); ";

        return strSQL;
    }

    public void InitVariables(String deviceName) {
        gblTimeOut = 30000;
        gblReadTime = 30000;
        gblBuffer = 16384;
        gblDeviceName = deviceName;
        gblSQLiteVersion = 3;
    }
}
