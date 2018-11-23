package com.ist_systems.ytdwm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "UserLog";
    private static final String KEY_ID = "ID";
    private static final String KEY_UserId = "UserId";
    private static final String KEY_UserPattern = "UserPattern";
    private static String DATABASE_NAME = "YTDWMDB";

    public SQLiteHelper(Context context) {
        //super(context, DATABASE_NAME, null, 2);
        super(context, DATABASE_NAME, null, GlobalVariables.gblSQLiteVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String strSQL = "CREATE TABLE " + TABLE_NAME + " (";
        strSQL += KEY_ID + " ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,";
        strSQL += KEY_UserId + "VARCHAR,";
        strSQL += KEY_UserPattern + " INTEGER,";
        sqLiteDatabase.execSQL(strSQL);

        strSQL = "CREATE TABLE IF NOT EXISTS Summary (";
        strSQL += "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ";
        strSQL += "RecId VARCHAR, ";
        strSQL += "BinCd VARCHAR, ";
        strSQL += "OuterPkg VARCHAR, ";
        strSQL += "HU VARCHAR, ";
        strSQL += "NewOuterPkg VARCHAR);";
        sqLiteDatabase.execSQL(strSQL);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int NewVersion) { //recreate table if new version is available
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS Summary");
        onCreate(db);
    }
}
