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

public class PickingDirItemsAdapter extends BaseAdapter {

    public ArrayList<String> searchHU = new ArrayList<>();

    private LayoutInflater inflater;
    private List<PickingDirItems> pickingList = new ArrayList<>();
    private ArrayList<PickingDirItems> arraylist;
    private Boolean onDeleteMode;
    private Context _context;

    public PickingDirItemsAdapter(Context context, List<PickingDirItems> pickingList1, Boolean ondeletemode) {
        this.pickingList = pickingList1;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(pickingList1);
        this.onDeleteMode = ondeletemode;
        this._context = context;

        for (int i = 0; i < pickingList1.size(); i++) {
            if (Double.parseDouble(pickingList1.get(i).getPickQty()) > 0) {
                searchHU.add(pickingList1.get(i).getHUID());
            }
        }
    }

    @Override
    public int getCount() {
        return pickingList.size();
    }

    @Override
    public Object getItem(int i) {
        return pickingList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        PickingDirItems picking = (PickingDirItems) getItem(position);

        if (view == null) {
            view = inflater.inflate(R.layout.listview_pickdiritems1, viewGroup, false);
        }

        if (position % 2 == 1)
            view.setBackgroundColor(_context.getResources().getColor(R.color.listview1));
        else
            view.setBackgroundColor(_context.getResources().getColor(R.color.listview));

        TextView tvBin = view.findViewById(R.id.tvPickBin);
        TextView tvHLHU = view.findViewById(R.id.tvHLHU);
        TextView tvHU = view.findViewById(R.id.tvHU);
        TextView tvMatNo = view.findViewById(R.id.tvMatNo);
        TextView tvBatch = view.findViewById(R.id.tvBatch);
        TextView tvRollNo = view.findViewById(R.id.tvRollNo);
        TextView tvDyeLot = view.findViewById(R.id.tvDyeLot);
        TextView tvFabTon = view.findViewById(R.id.tvFabTon);
        TextView tvReqdQty = view.findViewById(R.id.tvReqdQty);
        TextView tvPickQty = view.findViewById(R.id.tvPickQty);
        TextView tvSourceHU = view.findViewById(R.id.tvSourceHU);

        tvBin.setText(picking.Bin);
        tvHLHU.setText(picking.HLHU);
        tvHU.setText(picking.HU);
        tvMatNo.setText(picking.MatNo);
        tvBatch.setText(picking.Batch);
        tvRollNo.setText(picking.RollNo);
        tvDyeLot.setText(picking.DyeLot);
        tvFabTon.setText(picking.FabToning);
        tvReqdQty.setText(picking.ReqdQty);
        tvSourceHU.setText(picking.SourceHU);

        /*int color;
        if (searchHU.contains(picking.HU)) {
            color = ContextCompat.getColor(_context, R.color.selected);
            tvPickQty.setText(picking.ReqdQty);
            tvPickQty.setVisibility(View.VISIBLE);
        } else {
            color = ContextCompat.getColor(_context, R.color.notSelected);
            tvPickQty.setVisibility(View.INVISIBLE);
        }

        tvBin.setBackgroundColor(color);
        tvHU.setBackgroundColor(color);
        tvMatNo.setBackgroundColor(color);
        tvBatch.setBackgroundColor(color);
        tvRollNo.setBackgroundColor(color);
        tvDyeLot.setBackgroundColor(color);
        tvFabTon.setBackgroundColor(color);
        tvReqdQty.setBackgroundColor(color);
        tvPickQty.setBackgroundColor(color);*/

        if (searchHU.contains(picking.HU)) {
            view.setBackgroundColor(_context.getResources().getColor(R.color.selected));
            tvPickQty.setText(picking.ReqdQty);
            tvPickQty.setVisibility(View.VISIBLE);

            picking.setPickQty(picking.ReqdQty);
        } else {
            tvPickQty.setVisibility(View.INVISIBLE);

            picking.setPickQty("0");
        }

        return view;
    }

    public void filter(String charText, Boolean ondeletemode) {
        charText = charText.toLowerCase(Locale.getDefault());
        this.onDeleteMode = ondeletemode;
        boolean check = false;

        if (charText.length() >= 10) {
            Log.e("YTLog " + getClass().getSimpleName(), charText);

            for (PickingDirItems wp : arraylist) {

                // if at least one record found, trigger getView and add search string to searchHU.
                if (wp.getHUID().toLowerCase(Locale.getDefault()).contains(charText)) {
                    if (!onDeleteMode) {
                        if (!searchHU.contains(charText)) {
                            searchHU.add(charText);
                        }
                    } else {
                        searchHU.remove(charText);
                    }

                    check = true;
                }

                if (wp.getIsValidHLHUID().equals("1") && wp.getOutPkg().toLowerCase(Locale.getDefault()).contains(charText)) {
                    if (!onDeleteMode) {
                        if (!searchHU.contains(charText)) {
                            searchHU.add(wp.getHUID());
                        }
                    } else {
                        searchHU.remove(wp.getHUID());
                    }

                    check = true;
                }
            }

            if (check) {
                pickingList.clear();
                pickingList.addAll(arraylist);
            }
        }
        notifyDataSetChanged();
    }

    public int getTotalPicked() {
        return searchHU.size();
    }
}
