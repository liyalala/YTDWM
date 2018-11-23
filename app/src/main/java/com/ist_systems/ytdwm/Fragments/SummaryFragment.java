package com.ist_systems.ytdwm.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ist_systems.ytdwm.ListViewAndAdapters.BarcodeListPutAway;
import com.ist_systems.ytdwm.ListViewAndAdapters.PickingDirItems;
import com.ist_systems.ytdwm.ListViewAndAdapters.StockInquiry;
import com.ist_systems.ytdwm.ListViewAndAdapters.Summary;
import com.ist_systems.ytdwm.ListViewAndAdapters.SummaryAdapter;
import com.ist_systems.ytdwm.R;

import java.util.ArrayList;
import java.util.List;


public class SummaryFragment extends DialogFragment {

    static List<BarcodeListPutAway> lPutAway;
    static List<StockInquiry> lStockInquiry;
    static List<PickingDirItems> lPickingItems;
    static String ArrayName;
    public View view;
    SQLiteDatabase SQLiteDatabase;
    Cursor cursor;
    ListView lvSummary;
    SummaryAdapter summaryAdapter;
    private List<Summary> ListSumm = new ArrayList<>();

    public static SummaryFragment newInstance(List<BarcodeListPutAway> putAway, String arrayName) {
        SummaryFragment fragment = new SummaryFragment();
        lPutAway = putAway;
        ArrayName = arrayName;
        return fragment;
    }

    public static SummaryFragment newInstance(List<StockInquiry> stockInquiries, String arrayName, String sType) {
        SummaryFragment fragment = new SummaryFragment();
        lStockInquiry = stockInquiries;
        ArrayName = arrayName;
        return fragment;
    }

    public static SummaryFragment newInstance(List<PickingDirItems> stockInquiries, String arrayName, Boolean bType) {
        SummaryFragment fragment = new SummaryFragment();
        lPickingItems = stockInquiries;
        ArrayName = arrayName;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary, container, false);

        getDialog().setTitle(getResources().getString(R.string.txtSummary));
        SQLiteDatabase = getContext().openOrCreateDatabase("YTDWMDB", Context.MODE_PRIVATE, null);

        lvSummary = view.findViewById(R.id.lvSummary);

        switch (ArrayName) {
            case "PutAway":
                ListSumm = setARPutAway(lPutAway);
                break;
            case "StockInquiry":
                ListSumm = setARStockInquiry(lStockInquiry);
                break;
            case "Picking":
                ListSumm = setARPickingInquiry(lPickingItems);
                break;
        }

        summaryAdapter = new SummaryAdapter(getContext(), ListSumm);
        lvSummary.setAdapter(summaryAdapter);

        return view;
    }

    public List<Summary> setARPutAway(List<BarcodeListPutAway> summary) {
        List<Summary> newList = new ArrayList<>();
        String strRecId = java.util.UUID.randomUUID().toString();

        for (BarcodeListPutAway putAway : summary) {
            String strInsert = "INSERT INTO Summary (RecId, BinCd, OuterPkg, HU, NewOuterPkg) VALUES ('"
                    + strRecId + "', '"
                    + putAway.getBinCd() + "', '"
                    + putAway.getOuterPkg() + "', '"
                    + putAway.getHU() + "', '"
                    + putAway.getHLHUID() + "')";

            SQLiteDatabase.execSQL(strInsert);
        }

        /*String strSQl = "SELECT DISTINCT BinCd, OuterPkg, COUNT(HU) HU FROM Summary WHERE RecId = '" + strRecId
                            + "' AND IFNULL(NewOuterPkg, '') = '' AND BinCd <> '' GROUP BY BinCd, OuterPkg";
        strSQl += " UNION ALL ";
        strSQl += "SELECT DISTINCT BinCd, NewOuterPkg, COUNT(HU) HU FROM Summary WHERE RecId = '" + strRecId
                            + "' AND IFNULL(NewOuterPkg, '') <> '' AND BinCd <> '' GROUP BY BinCd, OuterPkg";*/

        String strSQl = "SELECT DISTINCT BinCd, NewOuterPkg, COUNT(HU) HU FROM Summary WHERE RecId = '" + strRecId
                + "' AND BinCd <> '' GROUP BY BinCd, NewOuterPkg";
        cursor = SQLiteDatabase.rawQuery(strSQl, null);

        String bin, outerpkg, cnt;
        if (cursor.moveToFirst()) {
            do {
                bin = cursor.getString(0);
                outerpkg = cursor.getString(1);
                cnt = cursor.getString(2);

                Log.e("YTLog " + this.getClass().getSimpleName(), bin + " - " + outerpkg + " - " + cnt);

                newList.add(new Summary(bin, outerpkg, cnt));
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        strSQl = "DELETE FROM Summary WHERE RecId = '" + strRecId + "'";
        SQLiteDatabase.execSQL(strSQl);

        return newList;
    }

    public List<Summary> setARStockInquiry(List<StockInquiry> summary) {
        List<Summary> newList = new ArrayList<>();

        String strRecId = java.util.UUID.randomUUID().toString();

        for (StockInquiry stockInquiry : summary) {
            String strInsert = "INSERT INTO Summary (RecId, BinCd, OuterPkg, HU) VALUES ('"
                    + strRecId + "', '"
                    + stockInquiry.getBinCd() + "', '"
                    + stockInquiry.getHLHUID() + "', '"
                    + stockInquiry.getHUID() + "')";

            SQLiteDatabase.execSQL(strInsert);
        }

        String strSQl = "SELECT DISTINCT BinCd, OuterPkg, COUNT(HU) HU FROM Summary WHERE RecId = '" + strRecId + "' GROUP BY BinCd, OuterPkg";
        cursor = SQLiteDatabase.rawQuery(strSQl, null);

        String bin, outerpkg, cnt;
        if (cursor.moveToFirst()) {
            do {
                bin = cursor.getString(0);
                outerpkg = cursor.getString(1);
                cnt = cursor.getString(2);

                Log.e("YTLog " + this.getClass().getSimpleName(), bin + " - " + outerpkg + " - " + cnt);

                newList.add(new Summary(bin, outerpkg, cnt));
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        strSQl = "DELETE FROM Summary WHERE RecId = '" + strRecId + "'";
        SQLiteDatabase.execSQL(strSQl);

        return newList;
    }

    public List<Summary> setARPickingInquiry(List<PickingDirItems> summary) {
        List<Summary> newList = new ArrayList<>();

        String strRecId = java.util.UUID.randomUUID().toString();

        for (PickingDirItems pickingDirItems : summary) {
            if (Double.parseDouble(pickingDirItems.getPickQty()) > 0) {
                String strInsert = "INSERT INTO Summary (RecId, BinCd, OuterPkg, HU) VALUES ('"
                        + strRecId + "', '"
                        + pickingDirItems.getBin() + "', '"
                        + pickingDirItems.getOutPkg() + "', '"
                        + pickingDirItems.getHUID() + "')";

                SQLiteDatabase.execSQL(strInsert);
            }
        }

        String strSQl = "SELECT DISTINCT BinCd, OuterPkg, COUNT(HU) HU FROM Summary WHERE RecId = '" + strRecId + "' GROUP BY BinCd, OuterPkg";
        cursor = SQLiteDatabase.rawQuery(strSQl, null);

        String bin, outerpkg, cnt;
        if (cursor.moveToFirst()) {
            do {
                bin = cursor.getString(0);
                outerpkg = cursor.getString(1);
                cnt = cursor.getString(2);

                Log.e("YTLog " + this.getClass().getSimpleName(), bin + " - " + outerpkg + " - " + cnt);

                newList.add(new Summary(bin, outerpkg, cnt));
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        strSQl = "DELETE FROM Summary WHERE RecId = '" + strRecId + "'";
        SQLiteDatabase.execSQL(strSQl);

        return newList;
    }
}
