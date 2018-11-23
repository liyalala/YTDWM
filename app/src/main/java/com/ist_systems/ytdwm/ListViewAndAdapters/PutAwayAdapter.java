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
 * Created by jmcaceres on 03/26/2017.
 */

public class PutAwayAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<PutAway> putAwayList = new ArrayList<>();

    public PutAwayAdapter(Context context, List<PutAway> putAwayList1) {
        this.putAwayList = putAwayList1;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return putAwayList.size();
    }

    @Override
    public Object getItem(int position) {
        return putAwayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PutAway putAway = (PutAway) getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_putaway, parent, false);
        }

        TextView tvOuterPkg = convertView.findViewById(R.id.tvIDOuterPkg);
        TextView tvHU = convertView.findViewById(R.id.tvIDHU);
        TextView tvBin = convertView.findViewById(R.id.tvIDBin);
        TextView tvHLHUI = convertView.findViewById(R.id.tvHLHUID);

        tvOuterPkg.setText(putAway.OuterPkg);
        tvHU.setText(putAway.HU);
        tvBin.setText(putAway.Bin);
        tvHLHUI.setText(putAway.HLHU);

        return convertView;
    }
}
