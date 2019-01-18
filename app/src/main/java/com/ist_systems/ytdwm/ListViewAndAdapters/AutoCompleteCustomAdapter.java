package com.ist_systems.ytdwm.ListViewAndAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.ist_systems.ytdwm.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteCustomAdapter extends ArrayAdapter<AutoCompleteView> {
    Context context;
    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    private Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((AutoCompleteView) resultValue).strGeneric;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (AutoCompleteView AutoCompleteView : tempItems) {
                    if (AutoCompleteView.strGeneric.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(AutoCompleteView);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<AutoCompleteView> filterList = (ArrayList<AutoCompleteView>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (AutoCompleteView AutoCompleteView : filterList) {
                    add(AutoCompleteView);
                    notifyDataSetChanged();
                }
            }
        }
    };

    private List<AutoCompleteView> items, tempItems, suggestions;

    public AutoCompleteCustomAdapter(Context context, int dumm, List<AutoCompleteView> items) {
        super(context, dumm, items);
        this.context = context;
        this.items = items;
        tempItems = new ArrayList<AutoCompleteView>(items); // this makes the difference.
        suggestions = new ArrayList<AutoCompleteView>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autocomplete_item, parent, false);
        }
        AutoCompleteView AutoCompleteView = items.get(position);
        if (AutoCompleteView != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null)
                lblName.setText(AutoCompleteView.strGeneric);
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }
}
