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
 * Created by jmcaceres on 03/25/2017.
 */

public class HomeTaskListAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater;
    private List<HomeTaskList> homeTaskLists = new ArrayList<>();

    public HomeTaskListAdapter(Context _context, List<HomeTaskList> homeTaskLists1) {
        this.homeTaskLists = homeTaskLists1;
        inflater = LayoutInflater.from(_context);
        context = _context;
    }

    @Override
    public int getCount() {
        return homeTaskLists.size();
    }

    @Override
    public Object getItem(int position) {
        return homeTaskLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HomeTaskList homeTaskList = (HomeTaskList) getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_tasklist, parent, false);
        }

        TextView tvMonth = convertView.findViewById(R.id.tvTLMonth);
        TextView tvDay = convertView.findViewById(R.id.tvTLDay);
        TextView tvTask = convertView.findViewById(R.id.tvTLTask);
        TextView tvTranNo = convertView.findViewById(R.id.tvTLTranNo);
        TextView tvTag = convertView.findViewById(R.id.tvTLTag);
        TextView tvContNo = convertView.findViewById(R.id.tvTLContNo);
        TextView tvVessel = convertView.findViewById(R.id.tvTLVessel);

        tvMonth.setText(homeTaskList.Month);
        tvDay.setText(homeTaskList.Day);
        tvTask.setText(homeTaskList.Task);
        tvTranNo.setText(homeTaskList.TranNo);
        tvTag.setText(homeTaskList.Tag);
        tvContNo.setText(homeTaskList.ContNo);
        tvVessel.setText(homeTaskList.Vessel);

        if (position % 2 == 1)
            convertView.setBackgroundColor(context.getResources().getColor(R.color.listview));
        else
            convertView.setBackgroundColor(context.getResources().getColor(R.color.listview1));

        return convertView;
    }

    public void setData(List<HomeTaskList> data) {
        homeTaskLists.addAll(data);
        notifyDataSetChanged();
    }
}
