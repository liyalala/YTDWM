package com.ist_systems.ytdwm.ListViewAndAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ist_systems.ytdwm.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmcaceres on 3/22/2018.
 */

public class SummaryAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Summary> summary = new ArrayList<>();

    public SummaryAdapter(Context context, List<Summary> summaries) {
        inflater = LayoutInflater.from(context);
        this.summary = summaries;
    }

    @Override
    public int getCount() {
        return summary.size();
    }

    @Override
    public Object getItem(int i) {
        return summary.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Summary summList = (Summary) getItem(i);
        if (view == null) {
            view = inflater.inflate(R.layout.listview_summary, viewGroup, false);
        }

        TextView tvBin = view.findViewById(R.id.tvBin);
        TextView tvOuterPkg = view.findViewById(R.id.tvOuterPkg);
        TextView tvHUCount = view.findViewById(R.id.tvHU);

        tvBin.setText(summList.Bin);
        tvOuterPkg.setText(summList.OuterPkg);
        tvHUCount.setText(summList.HU);

        return view;
    }

    public void setData(List<Summary> data) {
        summary.addAll(data);
        notifyDataSetChanged();
    }
}
