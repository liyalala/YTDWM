package com.ist_systems.ytdwm.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ist_systems.ytdwm.ListViewAndAdapters.SearchList;
import com.ist_systems.ytdwm.ListViewAndAdapters.SearchListAdapter;
import com.ist_systems.ytdwm.ListViewAndAdapters.SearchListLoader;
import com.ist_systems.ytdwm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<List<SearchList>> {

    public View view;
    ListView searchList;
    EditText etSearch;
    SearchListAdapter searchListAdapter;
    String SearchTyp;
    String SearchSubTyp;
    String SearchVal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_dialog, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle args = getArguments();
        SearchTyp = args.getString("SearchTyp");

        searchList = view.findViewById(R.id.lvSearch);
        etSearch = view.findViewById(R.id.etSearchVal);

        searchListAdapter = new SearchListAdapter(getActivity(), new ArrayList<SearchList>(), SearchTyp);
        searchList.setAdapter(searchListAdapter);
        getLoaderManager().destroyLoader(1);
        getLoaderManager().initLoader(1, null, this).forceLoad();
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String strTranNo = ((TextView) view.findViewById(R.id.tvTranNo)).getText().toString();
                etSearch.setText(strTranNo);

                Intent intent = new Intent();
                intent.putExtra("searchKey", strTranNo);
                getTargetFragment().onActivityResult(
                        getTargetRequestCode(), Activity.RESULT_OK, intent);

                dismiss();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = etSearch.getText().toString().toLowerCase(Locale.getDefault());
                searchListAdapter.filter(text, SearchSubTyp);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });

        String strHint = getResources().getString(R.string.hintSearch);
        if (SearchTyp.equals("ID") || SearchTyp.equals("PA")) {
            SearchSubTyp = "ContNo";
            etSearch.setHint(strHint.concat(" " + getResources().getString(R.string.txtContNo)));
        } else if (SearchTyp.equals("OD") || SearchTyp.equals("PK") || SearchTyp.equals("OI")) {
            SearchSubTyp = "ERPIONo";
            etSearch.setHint(strHint.concat(" " + getResources().getString(R.string.txtERPIO)));
        }

        return view;
    }

    @Override
    public Loader<List<SearchList>> onCreateLoader(int id, Bundle args) {
        return new SearchListLoader(getActivity(), SearchTyp);
    }

    @Override
    public void onLoadFinished(Loader<List<SearchList>> loader, List<SearchList> data) {
        searchListAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<SearchList>> loader) {
        searchListAdapter.setData(new ArrayList<SearchList>());
    }
}
