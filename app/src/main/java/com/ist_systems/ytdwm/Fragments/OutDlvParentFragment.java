package com.ist_systems.ytdwm.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ist_systems.ytdwm.R;

public class OutDlvParentFragment extends Fragment {

    public OutDlvParentFragment() {
        // Required empty public constructor
    }

    public static OutDlvParentFragment newInstance() {
        OutDlvParentFragment fragment = new OutDlvParentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outdlv_parent, container, false);

        OutDlvPickingFragment fragment = new OutDlvPickingFragment();

        FragmentManager fManager = getFragmentManager();
        FragmentTransaction fTransact;
        fTransact = fManager.beginTransaction();
        fTransact.replace(R.id.fmOutbound, fragment)
                .commit();

        return view;
    }

}
