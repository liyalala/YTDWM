package com.ist_systems.ytdwm.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ist_systems.ytdwm.ListViewAndAdapters.IDPOSummary;
import com.ist_systems.ytdwm.ListViewAndAdapters.IDPOSummaryAdapter;
import com.ist_systems.ytdwm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InbDlvPOSummFragment extends Fragment {

    static JSONArray jPOSumm;

    IDPOSummaryAdapter idpoSummaryAdapter;
    ListView lvIDPOSumm;

    public InbDlvPOSummFragment() {
        // Required empty public constructor
    }

    public static InbDlvPOSummFragment newInstance(JSONArray jsonPOSumm) {
        InbDlvPOSummFragment fragment = new InbDlvPOSummFragment();
        jPOSumm = jsonPOSumm;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbdlv_posumm, container, false);

        lvIDPOSumm = view.findViewById(R.id.lvIDPOSumm);

        List<IDPOSummary> idpoSummaryList = new ArrayList<>();
        String strPONo, strPOLn, strMatNo, strBatch, strPOQty, strDlvQty, strUOM;
        try {
            if (jPOSumm != null) {
                Log.e("YTLog " + this.getClass().getSimpleName(), jPOSumm.toString());
                for (int i = 0; i < jPOSumm.length(); i++) {
                    JSONObject jObj = jPOSumm.getJSONObject(i);
                    strPONo = jObj.getString("PODocNo");
                    strPOLn = jObj.getString("PODocItem");
                    strMatNo = jObj.getString("MatNo");
                    strBatch = jObj.getString("Batch");
                    strPOQty = jObj.getString("OrderQty");
                    strDlvQty = jObj.getString("TotDlvQty");
                    strUOM = jObj.getString("OrderUOM");

                    idpoSummaryList.add(new IDPOSummary(strPONo, strPOLn, strMatNo, strBatch, strPOQty, strDlvQty, strUOM));
                }
            }
        } catch (JSONException e) {
            Log.e("YTLog " + this.getClass().getSimpleName(), e.toString());
        }

        idpoSummaryAdapter = new IDPOSummaryAdapter(getActivity(), idpoSummaryList);
        lvIDPOSumm.setAdapter(idpoSummaryAdapter);

        return view;
    }

}
