package com.ist_systems.ytdwm.JSONParseAndAdapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class SuggestionAdapterODTONo extends ArrayAdapter<String> {

    private List<String> suggestions;
    public SuggestionAdapterODTONo(Activity context, String nameFilter) {
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
                JSONParseODTONo jp=new JSONParseODTONo();
                suggestions.clear();
                //constraint = constraint.toString().trim().toLowerCase();
                List<ODTONoList> new_suggestions = jp.getParseJsonWCF(constraint.toString().trim().toLowerCase());
                suggestions.clear();
                for (int i = 0; i < new_suggestions.size(); i++) {
                    //suggestions.add(new_suggestions.get(i).getBatch());
                    if (new_suggestions.get(i).getTONo().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(new_suggestions.get(i).getTONo());
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