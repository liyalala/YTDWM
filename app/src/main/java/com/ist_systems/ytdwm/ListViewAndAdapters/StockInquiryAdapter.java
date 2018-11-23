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
 * Created by jmcaceres on 5/22/2018.
 */

public class StockInquiryAdapter extends BaseAdapter {
    Context context1;
    String ScanType;
    private LayoutInflater inflater;
    private List<StockInquiry> binInquiries = new ArrayList<>();
    private ArrayList<StockInquiry> arraylist;

    public StockInquiryAdapter(Context context, List<StockInquiry> whseStocks1, String scantype) {
        inflater = LayoutInflater.from(context);
        this.binInquiries = whseStocks1;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(whseStocks1);
        ScanType = scantype;
        context1 = context;
    }

    @Override
    public int getCount() {
        return binInquiries.size();
    }

    @Override
    public Object getItem(int position) {
        return binInquiries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StockInquiry binInquiry = (StockInquiry) getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_bininquiry1, parent, false);
        }

        if (position % 2 == 1)
            convertView.setBackgroundColor(context1.getResources().getColor(R.color.listview));
        else
            convertView.setBackgroundColor(context1.getResources().getColor(R.color.listview1));

        TextView tvMatNo = convertView.findViewById(R.id.tvMatNo);
        TextView tvColor = convertView.findViewById(R.id.tvColor);
        TextView tvBatch = convertView.findViewById(R.id.tvBatch);
        TextView tvHLHUID = convertView.findViewById(R.id.tvHLHUID);
        TextView tvHLHUID1 = convertView.findViewById(R.id.tvHLHUID1);
        TextView tvHUID = convertView.findViewById(R.id.tvHUID);
        TextView tvHUID1 = convertView.findViewById(R.id.tvRollNo);
        TextView tvVendorLot = convertView.findViewById(R.id.tvDyeLot);
        TextView tvFabTon = convertView.findViewById(R.id.tvFabTon);
        TextView tvAvail = convertView.findViewById(R.id.tvAvailQty);
        TextView tvBinLb = convertView.findViewById(R.id.tvBinLb);
        TextView tvStorAreaCdLb = convertView.findViewById(R.id.tvStorAreaCdLb);
        TextView tvBin = convertView.findViewById(R.id.tvBin);
        TextView tvStorAreaCd = convertView.findViewById(R.id.tvStorAreaCd);

        tvMatNo.setText(binInquiry.MatNo);
        tvColor.setText(binInquiry.Color);
        tvBatch.setText(binInquiry.Batch);
        tvHLHUID.setText(binInquiry.HLHUID);
        tvHLHUID1.setText(binInquiry.HLHUID1);
        tvHUID.setText(binInquiry.HUID);
        tvHUID1.setText(binInquiry.HUID1);
        tvVendorLot.setText(binInquiry.VendorLot);
        tvFabTon.setText(binInquiry.FabToning);
        tvAvail.setText(binInquiry.AvailQty);
        tvBin.setText(binInquiry.Bin);
        tvStorAreaCd.setText(binInquiry.StorAreaCd);

        switch (ScanType) {
            case "Bin":
                tvBin.setVisibility(View.GONE);
                tvStorAreaCd.setVisibility(View.GONE);
                tvBinLb.setVisibility(View.GONE);
                tvStorAreaCdLb.setVisibility(View.GONE);
                break;
        }

        return convertView;
    }

    public void setData(List<StockInquiry> data) {
        binInquiries.addAll(data);
        arraylist.addAll(data);
        notifyDataSetChanged();
    }
}
