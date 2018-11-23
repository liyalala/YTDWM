package com.ist_systems.ytdwm.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;

import com.ist_systems.ytdwm.ListViewAndAdapters.IDPendingHU;
import com.ist_systems.ytdwm.ListViewAndAdapters.IDPendingHUAdapter;
import com.ist_systems.ytdwm.ListViewAndAdapters.IDPendingHULoader;
import com.ist_systems.ytdwm.R;

import java.util.ArrayList;
import java.util.List;

public class InbDlvViewPendingHUFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<List<IDPendingHU>> {
    static String strDlvNo;
    static String strModule;
    public View view;
    ListView pendingHU;
    IDPendingHUAdapter idPendingHUAdapter;

    public static InbDlvViewPendingHUFragment newInstance(String DlvNo, String strmodule) {
        InbDlvViewPendingHUFragment fragment = new InbDlvViewPendingHUFragment();
        strDlvNo = DlvNo;
        strModule = strmodule;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inbdlv_viewpendinghu, container, false);

        getDialog().setTitle(getResources().getString(R.string.txtPendingHU));
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        pendingHU = view.findViewById(R.id.lvPendingHU);

        idPendingHUAdapter = new IDPendingHUAdapter(getActivity(), new ArrayList<IDPendingHU>());
        pendingHU.setAdapter(idPendingHUAdapter);
        getLoaderManager().destroyLoader(1);
        getLoaderManager().initLoader(1, null, this).forceLoad();

        return view;
    }

    @Override
    public Loader<List<IDPendingHU>> onCreateLoader(int id, Bundle args) {
        return new IDPendingHULoader(getActivity(), strDlvNo, strModule);
    }

    @Override
    public void onLoadFinished(Loader<List<IDPendingHU>> loader, List<IDPendingHU> data) {
        idPendingHUAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<IDPendingHU>> loader) {
        idPendingHUAdapter.setData(new ArrayList<IDPendingHU>());
    }
}
