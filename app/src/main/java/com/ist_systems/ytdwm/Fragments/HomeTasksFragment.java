package com.ist_systems.ytdwm.Fragments;


import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ist_systems.ytdwm.Activities.InbDlvPutAway1Activity;
import com.ist_systems.ytdwm.Activities.InbDlvPutAwayPerContActivity;
import com.ist_systems.ytdwm.Activities.InbDlvRcvHUActivity;
import com.ist_systems.ytdwm.Activities.InbDlvRcvHUPerContActivity;
import com.ist_systems.ytdwm.Activities.InbDlvViewActivity;
import com.ist_systems.ytdwm.Activities.OutDlvIssuance1Activity;
import com.ist_systems.ytdwm.Activities.OutDlvIssuanceActivity;
import com.ist_systems.ytdwm.Activities.OutDlvPickAccDirActivity;
import com.ist_systems.ytdwm.Activities.OutDlvPickAccFreeActivity;
import com.ist_systems.ytdwm.Activities.OutDlvPickFabDirActivity;
import com.ist_systems.ytdwm.Activities.OutDlvPickFabFreeActivity;
import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.ListViewAndAdapters.HomeTaskList;
import com.ist_systems.ytdwm.ListViewAndAdapters.HomeTaskListAdapter;
import com.ist_systems.ytdwm.ListViewAndAdapters.HomeTaskListLoader;
import com.ist_systems.ytdwm.R;

import java.util.ArrayList;
import java.util.List;

public class HomeTasksFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<HomeTaskList>> {

    Button btTransactions;
    Button btChangePattern;
    ListView homeTaskList;
    HomeTaskListAdapter homeTaskListAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    public HomeTasksFragment() {
        // Required empty public constructor
    }

    public static HomeTasksFragment newInstance() {
        HomeTasksFragment fragment = new HomeTasksFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home_tasks, container, false);

        btTransactions = view.findViewById(R.id.btHFTrans);
        btChangePattern = view.findViewById(R.id.btHFPattern);
        homeTaskList = view.findViewById(R.id.lvTaskList);
        swipeRefreshLayout = view.findViewById(R.id.swipeTaskListRefresh);

        homeTaskListAdapter = new HomeTaskListAdapter(getActivity(), new ArrayList<HomeTaskList>());
        homeTaskList.setAdapter(homeTaskListAdapter);
        //callRefresh();
        homeTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strTranNo = ((TextView) view.findViewById(R.id.tvTLTranNo)).getText().toString();
                String strTag = ((TextView) view.findViewById(R.id.tvTLTag)).getText().toString();
                String strTaskOrg = ((TextView) view.findViewById(R.id.tvTLTask)).getText().toString();
                String strTask = strTaskOrg.toUpperCase();

                Intent i = null;
                switch (strTag) {
                    case "IDRcv":
                        i = new Intent(getActivity(), InbDlvRcvHUActivity.class);
                        break;
                    case "IDRcv1":
                        i = new Intent(getActivity(), InbDlvRcvHUPerContActivity.class);
                        break;
                    case "IDPost":
                        i = new Intent(getActivity(), InbDlvViewActivity.class);
                        break;
                    case "PutAway":
                        //i = new Intent(getActivity(), InbDlvPutAwayActivity.class);
                        i = new Intent(getActivity(), InbDlvPutAway1Activity.class);
                        break;
                    case "PutAway1":
                        i = new Intent(getActivity(), InbDlvPutAwayPerContActivity.class);
                        break;
                    case "Picking":
                        if (strTask.contains("FAB") && strTask.contains("FREE"))
                            i = new Intent(getActivity(), OutDlvPickFabFreeActivity.class);
                        else if (strTask.contains("FAB") && strTask.contains("DIRECT"))
                            i = new Intent(getActivity(), OutDlvPickFabDirActivity.class);
                        else if (strTask.contains("ACC") && strTask.contains("FREE"))
                            i = new Intent(getActivity(), OutDlvPickAccFreeActivity.class);
                        else if (strTask.contains("ACC") && strTask.contains("DIRECT"))
                            i = new Intent(getActivity(), OutDlvPickAccDirActivity.class);
                        break;
                    case "ODIssuance":
                        //i = new Intent(getActivity(), OutDlvIssuanceActivity.class);
                        i = new Intent(getActivity(), OutDlvIssuance1Activity.class);
                        break;
                }
                if (i != null) {

                    switch (strTag) {
                        case "PutAway":
                        case "Picking":
                            GlobalVariables.gblTONo = strTranNo;
                            break;
                        case "IDRcv1":
                        case "PutAway1":
                            GlobalVariables.gblContVessel = strTranNo;
                            break;
                        default:
                            GlobalVariables.gblDlvNo = strTranNo;
                    }

                    GlobalVariables.gblTask = strTaskOrg;
                    i.putExtra("TranNo", strTranNo);
                    startActivity(i);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeTaskListAdapter = new HomeTaskListAdapter(getActivity(), new ArrayList<HomeTaskList>());
                homeTaskList.setAdapter(homeTaskListAdapter);
                callRefresh();

                swipeRefreshLayout.setRefreshing(false);
            }

            public void onFailure(Throwable e) {
                Log.d("YTLog " + this.getClass().getSimpleName(), "Fetch data error: " + e.toString());
            }
        });

        btTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeTransactionsFragment fragment = new HomeTransactionsFragment();

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

        return view;
    }

    @Override
    public void onResume() {
        homeTaskListAdapter = new HomeTaskListAdapter(getActivity(), new ArrayList<HomeTaskList>());
        homeTaskList.setAdapter(homeTaskListAdapter);

        getLoaderManager().destroyLoader(1);
        getLoaderManager().initLoader(1, null, this).forceLoad();
        super.onResume();
    }

    @Override
    public Loader<List<HomeTaskList>> onCreateLoader(int i, Bundle bundle) {
        return new HomeTaskListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<HomeTaskList>> loader, List<HomeTaskList> homeTaskLists) {
        homeTaskListAdapter.setData(homeTaskLists);
    }

    @Override
    public void onLoaderReset(Loader<List<HomeTaskList>> loader) {
        homeTaskListAdapter.setData(new ArrayList<HomeTaskList>());
    }

    public void callRefresh() {
        getLoaderManager().destroyLoader(1);
        getLoaderManager().initLoader(1, null, this).forceLoad();
    }
}
