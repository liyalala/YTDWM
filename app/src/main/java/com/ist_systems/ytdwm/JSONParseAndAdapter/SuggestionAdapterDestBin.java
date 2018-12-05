package com.ist_systems.ytdwm.JSONParseAndAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ist_systems.ytdwm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SuggestionAdapterDestBin extends ArrayAdapter<String> implements Filterable {

    protected static final String TAG = "SuggestionAdapter";
    private List<String> suggestions;
    public SuggestionAdapterDestBin(Activity context, String nameFilter) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        suggestions = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int index) {
        return suggestions.get(index);
    }


    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                JSONParseDestBin jp=new JSONParseDestBin();
                if (constraint != null) {

                    List<DestBinList> new_suggestions =jp.getParseJsonWCF(constraint.toString());
                    suggestions.clear();

                    for (int i=0;i<new_suggestions.size();i++) {
                        suggestions.add(new_suggestions.get(i).getBinCd());

                        filterResults.values = suggestions;
                        filterResults.count = suggestions.size();

                    }




                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                    results.values = suggestions;
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }



}