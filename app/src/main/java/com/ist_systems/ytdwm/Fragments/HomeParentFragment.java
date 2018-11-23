package com.ist_systems.ytdwm.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ist_systems.ytdwm.R;

public class HomeParentFragment extends Fragment {

    private String sPatternInput = "";

    public HomeParentFragment() {
        // Required empty public constructor
    }

    public static HomeParentFragment newInstance() {
        HomeParentFragment fragment = new HomeParentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_parent, container, false);

        HomeTasksFragment fragment = new HomeTasksFragment();

        FragmentManager fManager = getFragmentManager();
        FragmentTransaction fTransact;
        fTransact = fManager.beginTransaction();
        fTransact.replace(R.id.fmHome, fragment)
                .commit();

        return view;
    }

}
