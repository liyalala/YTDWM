package com.ist_systems.ytdwm.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.ist_systems.ytdwm.Activities.OutDlvIssuance1Activity;
import com.ist_systems.ytdwm.Activities.OutDlvIssuanceActivity;
import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.JSONParseAndAdapter.SuggestionAdapterDestBin;
import com.ist_systems.ytdwm.JSONParseAndAdapter.SuggestionAdapterODIssuanceDlvNo;
import com.ist_systems.ytdwm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


public class OutDlvIssuanceFragment extends Fragment {

    Button btIssue;
    Button btPick;
    Button btReject;
    AutoCompleteTextView actDlvNo;
    ImageButton imgSearch;

    AlertDialog alrtLog;
    ProgressDialog dlDialog;

    public OutDlvIssuanceFragment() {
        // Required empty public constructor
    }

    public static OutDlvPickingFragment newInstance() {
        OutDlvPickingFragment fragment = new OutDlvPickingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outdlv_issuance, container, false);

        actDlvNo = view.findViewById(R.id.tvODDlvNoSearch);
        btIssue = view.findViewById(R.id.btODIssue);
        btPick = view.findViewById(R.id.btODPick);
        btReject = view.findViewById(R.id.btODReject);
        imgSearch = view.findViewById(R.id.imgSearch);

        actDlvNo.setAdapter(new SuggestionAdapterODIssuanceDlvNo(getActivity(),actDlvNo.getText().toString()));
        actDlvNo.setThreshold(2);

        btPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutDlvPickingFragment fragment = new OutDlvPickingFragment();

                FragmentManager fManager = getFragmentManager();
                FragmentTransaction fTransact;
                fTransact = fManager.beginTransaction();
                fTransact.replace(R.id.fmOutbound, fragment)
                        .commit();
            }
        });

        btReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutDlvRejectFragment fragment = new OutDlvRejectFragment();

                FragmentManager fManager = getFragmentManager();
                FragmentTransaction fTransact;
                fTransact = fManager.beginTransaction();
                fTransact.replace(R.id.fmOutbound, fragment)
                        .commit();
            }
        });

        btIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strDlvNo = actDlvNo.getText().toString();
                if (strDlvNo.length() == 0) {
                    alrtLog = new AlertDialog.Builder(getActivity()).setMessage("Please enter Delivery No.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {

                    GlobalVariables.gblDlvNo = strDlvNo;

                    Log.e("YTLog " + this.getClass().getSimpleName(), GlobalVariables.gblDlvNo);
                    new PHPCheckDlvNo().execute();

                    /*if ((new CheckNetwork(getActivity())).isConnectingToInternet()) {
                        GlobalVariables.gblTONo = strTONo;
                        new PHPCheckTONo().execute();
                    } else {
                        alrtLog = new AlertDialog.Builder(getActivity()).setMessage("Network Connection failed.")
                                .setNegativeButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        })
                                .show();
                    }*/
                }
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        actDlvNo.setText(data.getStringExtra("searchKey"));
                    }
                }
                break;
        }
    }

    public void ShowDialog() {
        Bundle args = new Bundle();
        args.putString("SearchTyp", "OI");

        FragmentManager fManager = getFragmentManager();
        SearchDialogFragment search = new SearchDialogFragment();
        search.setArguments(args);
        search.setTargetFragment(this, 1);
        search.show(fManager, "Search Dialog");
    }

    private class PHPCheckDlvNo extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "CheckODForIssuance.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("DlvNo", GlobalVariables.gblDlvNo);
                    String message = jsonObject.toString();

                    OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
                    os.write(message.getBytes());
                    os.flush();

                    /*InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));
                    StringBuilder sb = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }

                    in.close();*/

                    InputStream is = urlConnection.getInputStream();
                    Reader reader = new InputStreamReader(is);
                    char[] buf = new char[GlobalVariables.gblBuffer];
                    int read;
                    StringBuffer sb = new StringBuffer();

                    while ((read = reader.read(buf)) > 0) {
                        sb.append(buf, 0, read);
                    }

                    is.close();
                    responseString = sb.toString();
                } catch (Exception e) {
                    Log.e("YTLog " + this.getClass().getSimpleName(), e.toString());

                    bError = true;
                    strMsg = e.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("YTLog " + this.getClass().getSimpleName(), e.toString());

                bError = true;
                strMsg = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dlDialog = ProgressDialog.show(getActivity(), "Please wait", "Searching...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(getActivity()).setMessage(strMsg)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
            } else {
                Log.e("YTLog " + this.getClass().getSimpleName(), resString);
                if (resString != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(resString);
                        if (!jsonResponse.getString("ODSearch").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("ODSearch");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                            GlobalVariables.gblDlvNo = jsonChildNode.optString("DlvNo");
                            GlobalVariables.gblDlvStatus = jsonChildNode.optString("LongText");
                            GlobalVariables.gblDlvStatusCd = jsonChildNode.optString("DlvStatusCd");

                            if (!GlobalVariables.gblDlvNo.equals("")) {
                                actDlvNo.setText("");
                                GlobalVariables.gblTask = "Dlv " + GlobalVariables.gblDlvNo + " - " + GlobalVariables.gblDlvStatus + ";";

                                //Intent i = new Intent(getActivity().getApplicationContext(), OutDlvIssuanceActivity.class);
                                Intent i = new Intent(getActivity().getApplicationContext(), OutDlvIssuance1Activity.class);
                                startActivity(i);
                            } else {
                                alrtLog = new AlertDialog.Builder(getActivity()).setMessage("No Data Found.")
                                        .setNegativeButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                    }
                                                })
                                        .show();
                            }
                        } else {
                            alrtLog = new AlertDialog.Builder(getActivity()).setMessage("No Data Found.")
                                    .setNegativeButton("Ok",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                }
                                            })
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            dlDialog.dismiss();
        }
    }
}
