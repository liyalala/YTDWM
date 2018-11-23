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

public class IDPendingHUAdapter extends BaseAdapter {
    Context context1;
    private LayoutInflater inflater;
    private List<IDPendingHU> idPOPending = new ArrayList<>();

    public IDPendingHUAdapter(Context context, List<IDPendingHU> idpoPending) {
        this.idPOPending = idpoPending;
        inflater = LayoutInflater.from(context);
        context1 = context;
    }

    @Override
    public int getCount() {
        return idPOPending.size();
    }

    @Override
    public Object getItem(int position) {
        return idPOPending.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IDPendingHU idpopending = (IDPendingHU) getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_idpendinghu, parent, false);
        }

        TextView tvHU = convertView.findViewById(R.id.tvHU);
        TextView tvPkgNo = convertView.findViewById(R.id.tvIDPkgNo);
        TextView tvQty = convertView.findViewById(R.id.tvIDDlvQty);
        TextView tvUOM = convertView.findViewById(R.id.tvUOM);

        tvHU.setText(idpopending.IntHUID);
        tvPkgNo.setText(idpopending.PkgNo);
        tvQty.setText(idpopending.DlvQty);
        tvUOM.setText(idpopending.EntryUOM);

        return convertView;
    }

    public void setData(List<IDPendingHU> data) {
        idPOPending.addAll(data);
        notifyDataSetChanged();
    }
}
