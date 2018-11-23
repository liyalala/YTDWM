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

public class IDPOSummaryAdapter extends BaseAdapter {
    Context context1;
    private LayoutInflater inflater;
    private List<IDPOSummary> idpoSummaries = new ArrayList<>();

    public IDPOSummaryAdapter(Context context, List<IDPOSummary> idpoSummaries1) {
        this.idpoSummaries = idpoSummaries1;
        inflater = LayoutInflater.from(context);
        context1 = context;
    }

    @Override
    public int getCount() {
        return idpoSummaries.size();
    }

    @Override
    public Object getItem(int position) {
        return idpoSummaries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String strPONo;
        IDPOSummary idpoSummary = (IDPOSummary) getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_idposummary1, parent, false);
        }

        if (position % 2 == 1)
            convertView.setBackgroundColor(context1.getResources().getColor(R.color.listview1));
        else
            convertView.setBackgroundColor(context1.getResources().getColor(R.color.listview));

        TextView tvPONo = convertView.findViewById(R.id.tvIDPONo);
        TextView tvMatNo = convertView.findViewById(R.id.tvIDMatNo);
        TextView tvBatch = convertView.findViewById(R.id.tvIDBatch);
        TextView tvPOQty = convertView.findViewById(R.id.tvIDPOQty);
        TextView tvDlvQty = convertView.findViewById(R.id.tvIDDlvQty);
        TextView tvUOM = convertView.findViewById(R.id.tvIDUOM);
        TextView tvPOUOM = convertView.findViewById(R.id.tvIDPOUOM);

        strPONo = idpoSummary.PONo + "-" + idpoSummary.POLn;
        tvPONo.setText(strPONo);
        tvMatNo.setText(idpoSummary.MatNo);
        tvBatch.setText(idpoSummary.Batch);
        tvPOQty.setText(idpoSummary.POQty);
        tvDlvQty.setText(idpoSummary.DlvQty);
        tvUOM.setText(idpoSummary.UOM);
        tvPOUOM.setText(idpoSummary.UOM);

        return convertView;
    }
}
