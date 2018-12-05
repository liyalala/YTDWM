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

import com.ist_systems.ytdwm.Activities.InbDlvRcvHUActivity;
import com.ist_systems.ytdwm.Activities.InbDlvViewActivity;
import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.JSONParseAndAdapter.SuggestionAdapterIDDlvNo;
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

public class InbDlvFragment extends Fragment {

    Button btPutAway;
    Button btView;
    Button btRcvHU;
    AutoCompleteTextView actDlvNo;
    ImageButton imgSearch;

    AlertDialog alrtLog;
    ProgressDialog dlDialog;

    String strCaller;

    public InbDlvFragment() {
        // Required empty public constructor
    }

    public static InbDlvFragment newInstance() {
        InbDlvFragment fragment = new InbDlvFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbdlv, container, false);

        actDlvNo = view.findViewById(R.id.tvIDDlvNoSearch);
        btPutAway = view.findViewById(R.id.btIDPutAway);
        btView = view.findViewById(R.id.btIDView);
        btRcvHU = view.findViewById(R.id.btIDRcvHU);
        imgSearch = view.findViewById(R.id.imgSearch);

        actDlvNo.setAdapter(new SuggestionAdapterIDDlvNo(getActivity(),actDlvNo.getText().toString()));
        actDlvNo.setThreshold(2);

        btPutAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InbDlvPutAwayFragment fragment = new InbDlvPutAwayFragment();

                FragmentManager fManager = getFragmentManager();
                FragmentTransaction fTransact;
                fTransact = fManager.beginTransaction();
                fTransact.replace(R.id.fmInbound, fragment)
                        .commit();
            }
        });

        btView.setOnClickListener(new View.OnClickListener() {
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
                    strCaller = "View";
                    new PHPCheckDlvNo().execute();

                    /*if ((new CheckNetwork(getActivity())).isConnectingToInternet()) {
                        GlobalVariables.gblDlvNo = strDlvNo;
                        strCaller = "View";
                        new PHPCheckDlvNo().execute();
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

        btRcvHU.setOnClickListener(new View.OnClickListener() {
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
                    strCaller = "RcvHU";
                    new PHPCheckDlvNo().execute();

                    /*if ((new CheckNetwork(getActivity())).isConnectingToInternet()) {
                        GlobalVariables.gblDlvNo = strDlvNo;
                        strCaller = "RcvHU";
                        new PHPCheckDlvNo().execute();
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

                /*Intent i = new Intent(getActivity().getApplicationContext(), InbDlvRcvHUActivity.class);
                startActivity(i);*/
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
        args.putString("SearchTyp", "ID");

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
                URL url = new URL(GlobalVariables.gblURL + "CheckIDDlvNo.php");
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
                        if (!jsonResponse.getString("IDSearch").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("IDSearch");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                            GlobalVariables.gblDlvNo = jsonChildNode.optString("DlvNo");
                            GlobalVariables.gblDlvStatus = jsonChildNode.optString("DlvStatus");
                            GlobalVariables.gblDlvStatusCd = jsonChildNode.optString("DlvStatusCd");

                            actDlvNo.setText("");
                            if (strCaller.equals("View")) {
                                Intent i = new Intent(getActivity().getApplicationContext(), InbDlvViewActivity.class);
                                startActivity(i);
                            } else {
                                if (!GlobalVariables.gblDlvStatusCd.equals("01")) {
                                    alrtLog = new AlertDialog.Builder(getActivity()).setMessage("Status should be For RF Receiving.")
                                            .setNegativeButton("Ok",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                        }
                                                    })
                                            .show();
                                } else {
                                    Intent i = new Intent(getActivity().getApplicationContext(), InbDlvRcvHUActivity.class);
                                    startActivity(i);
                                }
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
