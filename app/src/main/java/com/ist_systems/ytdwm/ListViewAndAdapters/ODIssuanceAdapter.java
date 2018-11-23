package com.ist_systems.ytdwm.ListViewAndAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ist_systems.ytdwm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jmcaceres on 3/2/2018.
 */

public class ODIssuanceAdapter extends BaseAdapter {

    public ArrayList<String> searchHU = new ArrayList<>();

    private LayoutInflater inflater;
    private List<ODIssuance1> odissuance = new ArrayList<>();
    private ArrayList<ODIssuance1> arraylist;
    private Boolean onDeleteMode;
    private Context _context;

    public ODIssuanceAdapter(Context context, List<ODIssuance1> odissuance1, Boolean ondeletemode) {
        this.odissuance = odissuance1;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(odissuance1);
        this.onDeleteMode = ondeletemode;
        this._context = context;

        /*for (int i = 0; i < odissuance1.size(); i++) {
            if (Double.parseDouble(odissuance1.get(i).getIssQtyFld()) > 0) {
                searchHU.add(odissuance1.get(i).getHUID());
            }
        }*/
    }

    @Override
    public int getCount() {
        return odissuance.size();
    }

    @Override
    public Object getItem(int i) {
        return odissuance.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ODIssuance1 issuance1 = (ODIssuance1) getItem(position);

        if (view == null) {
            view = inflater.inflate(R.layout.listview_odissuance1, viewGroup, false);
        }

        if (position % 2 == 1)
            view.setBackgroundColor(_context.getResources().getColor(R.color.listview1));
        else
            view.setBackgroundColor(_context.getResources().getColor(R.color.listview));

        TextView tvRollNo = view.findViewById(R.id.tvRollNo);
        TextView tvHU = view.findViewById(R.id.tvHU);
        TextView tvMatNo = view.findViewById(R.id.tvMatNo);
        TextView tvBatch = view.findViewById(R.id.tvBatch);
        TextView tvDyeLot = view.findViewById(R.id.tvDyeLot);
        TextView tvFabTon = view.findViewById(R.id.tvFabTon);
        TextView tvReqdQty = view.findViewById(R.id.tvReqdQty);
        TextView tvIssQty = view.findViewById(R.id.tvIssQty);

        tvRollNo.setText(issuance1.PkgNo);
        tvHU.setText(issuance1.HUID);
        tvMatNo.setText(issuance1.MatNo);
        tvBatch.setText(issuance1.Batch);
        tvDyeLot.setText(issuance1.VendorLot);
        tvFabTon.setText(issuance1.FabToning);
        tvReqdQty.setText(issuance1.ReqdQty);
        tvIssQty.setText("0");

        if (searchHU.contains(issuance1.HUID)) {
            view.setBackgroundColor(_context.getResources().getColor(R.color.selected));
            tvIssQty.setText(issuance1.IssQty);
            tvIssQty.setVisibility(View.VISIBLE);
        } else {
            tvIssQty.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    public void filter(String charText, Boolean ondeletemode) {
        charText = charText.toLowerCase(Locale.getDefault());
        this.onDeleteMode = ondeletemode;
        boolean check = false;

        if (charText.length() >= 10) {
            Log.e("YTLog " + getClass().getSimpleName(), charText);

            for (ODIssuance1 wp : arraylist) {

                // if at least one record found, trigger getView and add search string to searchHU.
                if (wp.getHUID().toLowerCase(Locale.getDefault()).contains(charText)) {
                    if (!onDeleteMode) {
                        if (!searchHU.contains(charText)) {
                            searchHU.add(charText);
                        }
                    } else {
                        searchHU.remove(charText);
                        wp.setIssQty(wp.getOrigIssQty()); // reset IssQty
                    }

                    check = true;
                }
            }

            if (check) {
                odissuance.clear();
                odissuance.addAll(arraylist);
            }
        }

        notifyDataSetChanged();
    }

    public boolean filter1(String charText, Boolean ondeletemode) {
        charText = charText.toLowerCase(Locale.getDefault());
        this.onDeleteMode = ondeletemode;
        boolean check = false;

        if (charText.length() >= 10) {
            Log.e("YTLog " + getClass().getSimpleName(), charText);

            for (ODIssuance1 wp : arraylist) {

                // if at least one record found, trigger getView and add search string to searchHU.
                if (wp.getHUID().toLowerCase(Locale.getDefault()).equals(charText)) {
                    if (!onDeleteMode) {
                        if (!searchHU.contains(charText)) {
                            searchHU.add(charText);
                        }
                    } else {
                        searchHU.remove(charText);
                        wp.setIssQty(wp.getOrigIssQty()); // reset IssQty
                    }

                    check = true;
                }
            }

            if (check) {
                odissuance.clear();
                odissuance.addAll(arraylist);
            }
        }

        notifyDataSetChanged();
        return check;
    }

    public int getTotalIssued() {
        return searchHU.size();
    }
}
