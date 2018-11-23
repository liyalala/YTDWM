package com.ist_systems.ytdwm.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ist_systems.ytdwm.ListViewAndAdapters.IDDetails;
import com.ist_systems.ytdwm.ListViewAndAdapters.IDDetailsAdapter;
import com.ist_systems.ytdwm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InbDlvDetailsFragment extends Fragment {

    static JSONArray jDlvDet;

    IDDetailsAdapter idDetailsAdapter;
    ListView lvIDDetails;

    public InbDlvDetailsFragment() {
        // Required empty public constructor
    }

    public static InbDlvDetailsFragment newInstance(JSONArray jsonDlvDet) {
        InbDlvDetailsFragment fragment = new InbDlvDetailsFragment();
        jDlvDet = jsonDlvDet;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbdlv_details, container, false);

        lvIDDetails = view.findViewById(R.id.lvIDDetails);

        List<IDDetails> idDetailsList = new ArrayList<IDDetails>();
        String strPONo = "", strMatNo = "", strBatch = "", strDlvQty = "", strUOM = "", strVendorLot = "", strPkgNo = "";

        try {
            if (jDlvDet != null) {
                Log.e("YTLog " + this.getClass().getSimpleName(), jDlvDet.toString());
                for (int i = 0; i < jDlvDet.length(); i++) {
                    JSONObject jObj = jDlvDet.getJSONObject(i);
                    strPONo = jObj.getString("PODocNo");
                    strMatNo = jObj.getString("MatNo");
                    strBatch = jObj.getString("Batch");
                    strDlvQty = jObj.getString("DlvQtyEntUOM");
                    strUOM = jObj.getString("EntryUOM");
                    strVendorLot = jObj.getString("VendorLot");
                    strPkgNo = jObj.getString("PkgNo");

                    idDetailsList.add(new IDDetails(strPONo, strMatNo, strBatch, strDlvQty, strUOM, strVendorLot, strPkgNo));
                }
            }
        } catch (JSONException e) {
            Log.e("YTLog " + this.getClass().getSimpleName(), e.toString());
        }

        idDetailsAdapter = new IDDetailsAdapter(getActivity(), idDetailsList);
        lvIDDetails.setAdapter(idDetailsAdapter);

        return view;
    }

}
