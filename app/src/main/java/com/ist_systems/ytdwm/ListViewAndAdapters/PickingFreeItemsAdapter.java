package com.ist_systems.ytdwm.ListViewAndAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ist_systems.ytdwm.R;

import java.util.ArrayList;

/**
 * Created by jmcaceres on 3/5/2018.
 */

public class PickingFreeItemsAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<PickingFreeItems> pickingDirItems;
    private ArrayList<String> arrHU;

    public PickingFreeItemsAdapter(Context context, ArrayList<PickingFreeItems> pickingDirItems1) {
        this.context = context;
        this.pickingDirItems = pickingDirItems1;
        this.arrHU = new ArrayList<>();
    }

    @Override
    public int getGroupCount() {
        return pickingDirItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<PickingFreeSubItems> productList = pickingDirItems.get(groupPosition).getSubItems();
        return productList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return pickingDirItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<PickingFreeSubItems> subItems = pickingDirItems.get(groupPosition).getSubItems();
        return subItems.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
        PickingFreeItems pickItems = (PickingFreeItems) getGroup(groupPosition);

        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (inf != null) {
                view = inf.inflate(R.layout.listview_pickfreeitems, parent, false);

                TextView MatNo = view.findViewById(R.id.tvMatNo);
                TextView Batch = view.findViewById(R.id.tvBatch);
                TextView ReqdQty = view.findViewById(R.id.tvReqdQty);

                MatNo.setText(pickItems.getMatNo());
                Batch.setText(pickItems.getBatch());
                ReqdQty.setText(pickItems.getReqdQty());
            }
        }

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {
        PickingFreeSubItems subItems = (PickingFreeSubItems) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (infalInflater != null) {
                view = infalInflater.inflate(R.layout.listview_pickfreesubitems, parent, false);

                TextView BinCd = view.findViewById(R.id.tvPickBin);
                TextView HUID = view.findViewById(R.id.tvHU);
                TextView PickQty = view.findViewById(R.id.tvPickQty);

                BinCd.setText(subItems.getBin());
                HUID.setText(subItems.getHU());
                PickQty.setText(subItems.getPickQty());

                arrHU.add(subItems.getHU());
            }
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public ArrayList<String> getArrSubHU() {
        return arrHU;
    }
}
