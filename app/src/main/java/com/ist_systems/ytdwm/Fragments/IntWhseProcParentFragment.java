package com.ist_systems.ytdwm.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ist_systems.ytdwm.Activities.IntWhseBinTransActivity;
import com.ist_systems.ytdwm.Activities.IntWhseStocksInqMaterialActivity;
import com.ist_systems.ytdwm.Activities.IntWhseStocksInquiryActivity;
import com.ist_systems.ytdwm.R;

public class IntWhseProcParentFragment extends Fragment {

    EditText etBinCd;
    Button btBinTrans;
    Button btBinInq;
    Button btBinInqMatNo;

    public IntWhseProcParentFragment() {
        // Required empty public constructor
    }

    public static IntWhseProcParentFragment newInstance() {
        IntWhseProcParentFragment fragment = new IntWhseProcParentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intwhse_parent, container, false);

        etBinCd = view.findViewById(R.id.etBinCd);
        btBinTrans = view.findViewById(R.id.btBinTrans);
        btBinInq = view.findViewById(R.id.btBinInq);
        btBinInqMatNo = view.findViewById(R.id.btBinInqMatNo);

        btBinInq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), IntWhseStocksInquiryActivity.class);
                startActivity(i);
            }
        });

        btBinInqMatNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), IntWhseStocksInqMaterialActivity.class);
                startActivity(i);
            }
        });

        btBinTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), IntWhseBinTransActivity.class);
                startActivity(i);
            }
        });


        return view;
    }
}
