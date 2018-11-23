package com.ist_systems.ytdwm.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ist_systems.ytdwm.R;


public class IntWhseBinInquiryFragment extends Fragment {

    public IntWhseBinInquiryFragment() {
        // Required empty public constructor
    }

    public static IntWhseBinInquiryFragment newInstance() {
        IntWhseBinInquiryFragment fragment = new IntWhseBinInquiryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_biniquiry, container, false);


        return view;
    }
}
