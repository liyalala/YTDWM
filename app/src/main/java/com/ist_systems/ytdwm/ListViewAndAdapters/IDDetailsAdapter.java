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
 * Created by jmcaceres on 04/26/2017.
 */

public class IDDetailsAdapter extends BaseAdapter {

    Context context1;
    private LayoutInflater inflater;
    private List<IDDetails> idDetailsList = new ArrayList<>();

    public IDDetailsAdapter(Context context, List<IDDetails> idDetailsList1) {
        this.idDetailsList = idDetailsList1;
        inflater = LayoutInflater.from(context);
        context1 = context;
    }

    @Override
    public int getCount() {
        return idDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return idDetailsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IDDetails idDetails = (IDDetails) getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_iddetails1, parent, false);
        }

        if (position % 2 == 1)
            convertView.setBackgroundColor(context1.getResources().getColor(R.color.listview));
        else
            convertView.setBackgroundColor(context1.getResources().getColor(R.color.listview1));

        TextView tvPONo = convertView.findViewById(R.id.tvIDPONo);
        TextView tvMatNo = convertView.findViewById(R.id.tvIDMatNo);
        TextView tvBatch = convertView.findViewById(R.id.tvIDBatch);
        TextView tvDlvQty = convertView.findViewById(R.id.tvIDDlvQty);
        TextView tvUOM = convertView.findViewById(R.id.tvIDUOM);
        TextView tvVendorLot = convertView.findViewById(R.id.tvVendorLot);
        TextView tvPKgNo = convertView.findViewById(R.id.tvIDPkgNo);

        tvPONo.setText(idDetails.PONo);
        tvMatNo.setText(idDetails.MatNo);
        tvBatch.setText(idDetails.Batch);
        tvDlvQty.setText(idDetails.DlvQty);
        tvUOM.setText(idDetails.UOM);
        tvVendorLot.setText(idDetails.VendorLot);
        tvPKgNo.setText(idDetails.PkgNo);

        return convertView;
    }
}
