package com.ist_systems.ytdwm.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ist_systems.ytdwm.R;

public class InbDlvParentFragment extends Fragment {

    private String sPatternInput = "";

    public InbDlvParentFragment() {
        // Required empty public constructor
    }

    public static InbDlvParentFragment newInstance() {
        InbDlvParentFragment fragment = new InbDlvParentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbdlv_parent, container, false);

        InbDlvFragment fragment = new InbDlvFragment();

        FragmentManager fManager = getFragmentManager();
        FragmentTransaction fTransact;
        fTransact = fManager.beginTransaction();
        fTransact.replace(R.id.fmInbound, fragment)
                .commit();

        return view;
    }

}
