package com.ist_systems.ytdwm.JSONParseAndAdapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
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

    private List<String> suggestions;
    public SuggestionAdapterDestBin(Activity context, String nameFilter) {
        super(context, android.R.layout.select_dialog_item);
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


    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                JSONParseDestBin jp=new JSONParseDestBin();
                suggestions.clear();
                //constraint = constraint.toString().trim().toLowerCase();
                List<DestBinList> new_suggestions = jp.getParseJsonWCF(constraint.toString().trim().toLowerCase());
                suggestions.clear();
                for (int i = 0; i < new_suggestions.size(); i++) {
                    //suggestions.add(new_suggestions.get(i).getBatch());
                    if (new_suggestions.get(i).getBinCd().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(new_suggestions.get(i).getBinCd());
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }


}