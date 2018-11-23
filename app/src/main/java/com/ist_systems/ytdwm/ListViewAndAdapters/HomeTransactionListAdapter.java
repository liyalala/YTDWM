package com.ist_systems.ytdwm.ListViewAndAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ist_systems.ytdwm.R;

import java.util.ArrayList;
import java.util.List;

public class HomeTransactionListAdapter extends BaseAdapter {

    Context context;
    TextView tvCtrans, tvCTyp, tvCRemarks, tvCCreatedby, tvCCreatedDt;
    private LayoutInflater inflater;
    private List<HomeTransactionList> homeTransactionLists = new ArrayList<>();


    public HomeTransactionListAdapter(Context _context, List<HomeTransactionList> homeTransactionLists1) {
        this.homeTransactionLists = homeTransactionLists1;
        inflater = LayoutInflater.from(_context);
        context = _context;
    }

    @Override
    public int getCount() {
        return homeTransactionLists.size();
    }

    @Override
    public Object getItem(int position) {
        return homeTransactionLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HomeTransactionList homeTransactionList = (HomeTransactionList) getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_transaction, parent, false);
        }

        TextView tvCTrans = convertView.findViewById(R.id.tvCTrans);
        TextView tvCTyp = convertView.findViewById(R.id.tvCTyp);
        TextView tvCRemarks = convertView.findViewById(R.id.tvCRemarks);
        TextView tvCCreatedBy = convertView.findViewById(R.id.tvCCreatedby);
        TextView tvCCreatedDt = convertView.findViewById(R.id.tvCCreatedDt);

        TextView tvTransNo = convertView.findViewById(R.id.tvtransNo);
        TextView tvTransTyp = convertView.findViewById(R.id.tvtransTyp);
        TextView tvRemarks = convertView.findViewById(R.id.tvRemarks);
        TextView tvCreatedBy = convertView.findViewById(R.id.tvCreatedby);
        TextView tvCreatedDt = convertView.findViewById(R.id.tvCreatedDt);

        tvTransNo.setText(homeTransactionList.TransNo);
        tvTransTyp.setText(homeTransactionList.TransTyp);
        tvRemarks.setText(homeTransactionList.Remarks);
        tvCreatedBy.setText(homeTransactionList.CreatedBy);
        tvCreatedDt.setText(homeTransactionList.CreatedDt);

        if (homeTransactionList.StatusCd.equals("F")) {
            tvTransNo.setTextColor(context.getResources().getColor(R.color.colorRed));
            tvTransTyp.setTextColor(context.getResources().getColor(R.color.colorRed));
            tvRemarks.setTextColor(context.getResources().getColor(R.color.colorRed));
            tvCreatedBy.setTextColor(context.getResources().getColor(R.color.colorRed));
            tvCreatedDt.setTextColor(context.getResources().getColor(R.color.colorRed));
            tvCTrans.setTextColor(context.getResources().getColor(R.color.colorRed));
            tvCTyp.setTextColor(context.getResources().getColor(R.color.colorRed));
            tvCRemarks.setTextColor(context.getResources().getColor(R.color.colorRed));
            tvCCreatedBy.setTextColor(context.getResources().getColor(R.color.colorRed));
            tvCCreatedDt.setTextColor(context.getResources().getColor(R.color.colorRed));

        } else {
            tvTransNo.setTextColor(context.getResources().getColor(R.color.tvDefault));
            tvTransTyp.setTextColor(context.getResources().getColor(R.color.tvDefault));
            tvRemarks.setTextColor(context.getResources().getColor(R.color.tvDefault));
            tvCreatedBy.setTextColor(context.getResources().getColor(R.color.tvDefault));
            tvCreatedDt.setTextColor(context.getResources().getColor(R.color.tvDefault));
            tvCTrans.setTextColor(context.getResources().getColor(R.color.tvDefault));
            tvCTyp.setTextColor(context.getResources().getColor(R.color.tvDefault));
            tvCRemarks.setTextColor(context.getResources().getColor(R.color.tvDefault));
            tvCCreatedBy.setTextColor(context.getResources().getColor(R.color.tvDefault));
            tvCCreatedDt.setTextColor(context.getResources().getColor(R.color.tvDefault));
        }

        if (position % 2 == 1)
            convertView.setBackgroundColor(context.getResources().getColor(R.color.listview1));
        else
            convertView.setBackgroundColor(context.getResources().getColor(R.color.listview));

        return convertView;
    }

    public void setData(List<HomeTransactionList> data) {
        homeTransactionLists.addAll(data);
        notifyDataSetChanged();
    }
}
