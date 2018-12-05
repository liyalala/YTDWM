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

import com.ist_systems.ytdwm.Activities.InbDlvPutAway1Activity;
import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.JSONParseAndAdapter.SuggestionAdapterTONo;
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

public class InbDlvPutAwayFragment extends Fragment {

    Button btDlv;
    Button btPutAway;
    ImageButton imgSearch;
    AutoCompleteTextView actIDTONoSearch;

    AlertDialog alrtLog;
    ProgressDialog dlDialog;

    String strTONo1 = "";

    public InbDlvPutAwayFragment() {
        // Required empty public constructor
    }

    public static InbDlvPutAwayFragment newInstance() {
        InbDlvPutAwayFragment fragment = new InbDlvPutAwayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbdlv_putaway, container, false);

        btDlv = view.findViewById(R.id.btIDDelivery);
        actIDTONoSearch = view.findViewById(R.id.actIDTONoSearch);
        btPutAway = view.findViewById(R.id.btIDPutAway1);
        imgSearch = view.findViewById(R.id.imgSearch);

        actIDTONoSearch.setAdapter(new SuggestionAdapterTONo(getActivity(),actIDTONoSearch.getText().toString()));
        actIDTONoSearch.setThreshold(2);

        btDlv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InbDlvFragment fragment = new InbDlvFragment();

                FragmentManager fManager = getFragmentManager();
                FragmentTransaction fTransact;
                fTransact = fManager.beginTransaction();
                fTransact.replace(R.id.fmInbound, fragment)
                        .commit();
            }
        });

        btPutAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strTONo = actIDTONoSearch.getText().toString();
                if (strTONo.length() == 0) {
                    alrtLog = new AlertDialog.Builder(getActivity()).setMessage("Please enter Transfer Order No.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {
                    strTONo1 = strTONo;
                    new PHPCheckTONo().execute();

                    /*if ((new CheckNetwork(getActivity())).isConnectingToInternet()) {
                        strTONo1 = strTONo;
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

                /*Intent i = new Intent(getActivity().getApplicationContext(), InbDlvPutAwayActivity.class);
                i.putExtra("TranNo", strTONo);
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
                        actIDTONoSearch.setText(data.getStringExtra("searchKey"));
                    }
                }
                break;
        }
    }

    public void ShowDialog() {
        Bundle args = new Bundle();
        args.putString("SearchTyp", "PA");

        FragmentManager fManager = getFragmentManager();
        SearchDialogFragment search = new SearchDialogFragment();
        search.setArguments(args);
        search.setTargetFragment(this, 1);
        search.show(fManager, "Search Dialog");
    }

    private class PHPCheckTONo extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "CheckIDTONo.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("TONo", strTONo1);
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
                alrtLog = new AlertDialog.Builder(getActivity()).setMessage(strMsg)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
            } else {
                if (resString != null) {
                    Log.e("YTLog " + this.getClass().getSimpleName(), resString);

                    try {
                        JSONObject jsonResponse = new JSONObject(resString);
                        if (!jsonResponse.getString("TOSearch").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("TOSearch");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                            strTONo1 = jsonChildNode.optString("TONo");
                            //String strIsConfirm = jsonChildNode.optString("IsConfirm");

                        /*if(strIsConfirm.equals("1")) {
                            alrtLog = new AlertDialog.Builder(getActivity()).setMessage("TO is already confirmed.")
                                    .setNegativeButton("Ok",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                }
                                            })
                                    .show();
                        } else {
                            strTONo1 = jsonChildNode.optString("TONo");
                            actIDTONoSearch.setText("");
                            Intent i = new Intent(getActivity().getApplicationContext(), InbDlvPutAwayActivity.class);
                            i.putExtra("TranNo", strTONo1);
                            startActivity(i);
                        }*/

                            actIDTONoSearch.setText("");
                            //Intent i = new Intent(getActivity().getApplicationContext(), InbDlvPutAwayActivity.class);
                            Intent i = new Intent(getActivity().getApplicationContext(), InbDlvPutAway1Activity.class);
                            i.putExtra("TranNo", strTONo1);
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            dlDialog.dismiss();
        }
    }
}
