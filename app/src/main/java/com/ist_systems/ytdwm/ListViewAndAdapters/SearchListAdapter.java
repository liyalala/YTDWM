package com.ist_systems.ytdwm.ListViewAndAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ist_systems.ytdwm.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jmcaceres on 3/22/2018.
 */

public class SearchListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<SearchList> searchLists = new ArrayList<>();
    private ArrayList<SearchList> arraylist;
    private String SearchTyp;
    private Context context1;

    public SearchListAdapter(Context context, List<SearchList> searchLists1, String searchTyp) {
        inflater = LayoutInflater.from(context);
        this.searchLists = searchLists1;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(searchLists1);
        this.SearchTyp = searchTyp;
        this.context1 = context;
    }

    @Override
    public int getCount() {
        return searchLists.size();
    }

    @Override
    public Object getItem(int i) {
        return searchLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        SearchList searchList = (SearchList) getItem(i);
        if (view == null) {
            view = inflater.inflate(R.layout.listview_search, viewGroup, false);
        }

        TextView tvTranNo = view.findViewById(R.id.tvTranNo);
        TextView tvActDt = view.findViewById(R.id.tvActDt);
        TextView tvActDtLbl = view.findViewById(R.id.tvActDtLbl);
        TextView tvCont = view.findViewById(R.id.tvContNo);
        TextView tvVessel = view.findViewById(R.id.tvVessel);
        TextView tvContLbl = view.findViewById(R.id.tvContNoLbl);
        TextView tvVesselLbl = view.findViewById(R.id.tvVesselLbl);
        TextView tvStat = view.findViewById(R.id.tvStatus);
        TextView tvTranLbl = view.findViewById(R.id.tvTransLbl);
        TextView tvIONoLbl = view.findViewById(R.id.tvIONoLbl);
        TextView tvIONo = view.findViewById(R.id.tvIONo);
        TextView tvDlvNoLbl = view.findViewById(R.id.tvDlvNoLbl);
        TextView tvRsvNoLbl = view.findViewById(R.id.tvRsvNoLbl);
        TextView tvDlvNo = view.findViewById(R.id.tvDlvNo);
        TextView tvRsvNo = view.findViewById(R.id.tvRsvNo);

        tvTranNo.setText(searchList.TranNo);
        tvCont.setText(searchList.ContNo);
        tvVessel.setText(searchList.Vessel);
        tvStat.setText(searchList.Status);
        tvIONo.setText(searchList.ERPIONo);
        tvDlvNo.setText(searchList.DlvNo);
        tvRsvNo.setText(searchList.RsvNo);

        if (SearchTyp != null) {
            if (SearchTyp.equals("OD") || SearchTyp.equals("PK") || SearchTyp.equals("OI")) {
                tvCont.setVisibility(View.GONE);
                tvVessel.setVisibility(View.GONE);
                tvContLbl.setVisibility(View.GONE);
                tvVesselLbl.setVisibility(View.GONE);
                tvRsvNoLbl.setVisibility(View.VISIBLE);
                tvRsvNo.setVisibility(View.VISIBLE);

                if (SearchTyp.equals("PK")) {
                    tvDlvNoLbl.setVisibility(View.VISIBLE);
                    tvDlvNo.setVisibility(View.VISIBLE);
                }

                tvActDt.setText(searchList.ReqdDt);
                tvActDtLbl.setText(context1.getResources().getString(R.string.txtReqdDt));
            } else {
                tvIONoLbl.setVisibility(View.GONE);
                tvIONo.setVisibility(View.GONE);

                tvActDt.setText(searchList.ActDt);
                tvActDtLbl.setText(context1.getResources().getString(R.string.txtActDt));
            }

            if (SearchTyp.equals("ID") || SearchTyp.equals("OD") || SearchTyp.equals("OI")) {
                tvTranLbl.setText(context1.getResources().getString(R.string.txtDlvNo1));
            } else {
                tvTranLbl.setText(context1.getResources().getString(R.string.txtTONo1));
            }
        }

        return view;
    }

    public void setData(List<SearchList> data) {
        searchLists.addAll(data);
        arraylist.addAll(data);
        notifyDataSetChanged();
    }

    public void filter(String charText, String subType) {
        charText = charText.toLowerCase(Locale.getDefault());

        searchLists.clear();
        if (charText.length() == 0) {
            searchLists.addAll(arraylist);
        } else {
            Log.e("YTLog " + getClass().getSimpleName(), charText);

            for (SearchList sl : arraylist) {
                switch (subType) {
                    case "TranNo":
                        if (sl.getTranNo().toLowerCase(Locale.getDefault()).contains(charText)) {
                            searchLists.add(sl);
                        }
                        break;
                    case "ContNo":
                        if (sl.getContNo().toLowerCase(Locale.getDefault()).contains(charText)) {
                            searchLists.add(sl);
                        }
                        break;
                    case "ERPIONo":
                        if (sl.getERPIONo().toLowerCase(Locale.getDefault()).contains(charText)) {
                            searchLists.add(sl);
                        }
                        break;
                }
            }
        }

        notifyDataSetChanged();
    }
}
