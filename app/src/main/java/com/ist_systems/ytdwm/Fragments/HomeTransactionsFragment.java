package com.ist_systems.ytdwm.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ist_systems.ytdwm.ListViewAndAdapters.HomeTaskList;
import com.ist_systems.ytdwm.ListViewAndAdapters.HomeTaskListAdapter;
import com.ist_systems.ytdwm.ListViewAndAdapters.HomeTransactionList;
import com.ist_systems.ytdwm.ListViewAndAdapters.HomeTransactionListAdapter;
import com.ist_systems.ytdwm.ListViewAndAdapters.HomeTransactionListLoader;
import com.ist_systems.ytdwm.R;

import java.util.ArrayList;
import java.util.List;

public class HomeTransactionsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<HomeTransactionList>>{

    Button btTasks;
    Button btChangePattern;
    ListView transactionList;
    HomeTransactionListAdapter homeTransactionListAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    public HomeTransactionsFragment() {
        // Required empty public constructor
    }

    public static HomeTransactionsFragment newInstance() {
        HomeTransactionsFragment fragment = new HomeTransactionsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_transactions, container, false);

        btTasks = view.findViewById(R.id.btHFTaskList);
        btChangePattern = view.findViewById(R.id.btHFPattern);
        transactionList = view.findViewById(R.id.lvTransactionList);
        swipeRefreshLayout = view.findViewById(R.id.swipeTaskListRefresh);

        btTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeTasksFragment fragment = new HomeTasksFragment();

                FragmentManager fManager = getFragmentManager();
                FragmentTransaction fTransact;
                fTransact = fManager.beginTransaction();
                fTransact.replace(R.id.fmHome, fragment)
                        .commit();
            }
        });

        btChangePattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeChangePattern fragment = new HomeChangePattern();

                FragmentManager fManager = getFragmentManager();
                FragmentTransaction fTransact;
                fTransact = fManager.beginTransaction();
                fTransact.replace(R.id.fmHome, fragment)
                        .commit();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeTransactionListAdapter = new HomeTransactionListAdapter(getActivity(), new ArrayList<HomeTransactionList>());
                transactionList.setAdapter(homeTransactionListAdapter);
                callRefresh();

                swipeRefreshLayout.setRefreshing(false);
            }

            public void onFailure(Throwable e) {
                Log.d("YTLog " + this.getClass().getSimpleName(), "Fetch data error: " + e.toString());
            }
        });

        homeTransactionListAdapter = new HomeTransactionListAdapter(getActivity(), new ArrayList<HomeTransactionList>());
        transactionList.setAdapter(homeTransactionListAdapter);

        return view;
    }

    @Override
    public void onResume() {
        homeTransactionListAdapter = new HomeTransactionListAdapter(getActivity(), new ArrayList<HomeTransactionList>());
        transactionList.setAdapter(homeTransactionListAdapter);

        callRefresh();
        super.onResume();
    }

    @Override
    public Loader<List<HomeTransactionList>> onCreateLoader(int i, Bundle bundle) {
        return new HomeTransactionListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<HomeTransactionList>> loader, List<HomeTransactionList> transactionList) {
        homeTransactionListAdapter.setData(transactionList);
    }

    @Override
    public void onLoaderReset(Loader<List<HomeTransactionList>> loader) {
        homeTransactionListAdapter.setData(new ArrayList<HomeTransactionList>());
    }

    public void callRefresh() {
        getLoaderManager().destroyLoader(2);
        getLoaderManager().initLoader(2, null, this).forceLoad();
    }
}
