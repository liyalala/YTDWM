package com.ist_systems.ytdwm.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ist_systems.ytdwm.Activities.OutDlvIssuance1Activity;
import com.ist_systems.ytdwm.Activities.OutDlvIssuanceActivity;
import com.ist_systems.ytdwm.R;


public class OutDlvIssNewQtyFragment extends DialogFragment {

    static String strOldQty;
    public View view;
    EditText etNewQty;
    Button btSubmit;

    public static OutDlvIssNewQtyFragment newInstance(String OldQty) {
        OutDlvIssNewQtyFragment fragment = new OutDlvIssNewQtyFragment();
        strOldQty = OldQty;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_outdlv_issqty, container, false);

        etNewQty = view.findViewById(R.id.tvNewQty);
        btSubmit = view.findViewById(R.id.btSubmit);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double dOldQty, dNewQty;

                String strNewQty = etNewQty.getText().toString();
                dOldQty = Double.parseDouble(strOldQty);
                dNewQty = Double.parseDouble(strNewQty);

                if (dNewQty > dOldQty) {
                    Toast.makeText(getActivity(), "Qty greater than Old Qty.", Toast.LENGTH_LONG).show();
                } else {
                    //OutDlvIssuanceActivity callingActivity = (OutDlvIssuanceActivity) getActivity();
                    OutDlvIssuance1Activity callingActivity = (OutDlvIssuance1Activity) getActivity();
                    callingActivity.UpdateQty(strNewQty);

                    dismiss();
                }
            }
        });

        return view;
    }
}
