package com.ist_systems.ytdwm.Fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ist_systems.ytdwm.Activities.InbDlvRcvHUActivity;
import com.ist_systems.ytdwm.GlobalVariables;
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

public class InbDlvHeaderFragment extends Fragment {

    static JSONArray jDlvHdr;
    Button btRcvHU, btIDPost;
    EditText etVendor, etVendorRef, etDocDt, etRcvPlant, etRcvSLoc, etContNo, etVessel;
    AlertDialog alrtLog;
    ProgressDialog dlDialog;

    public InbDlvHeaderFragment() {
        // Required empty public constructor
    }

    public static InbDlvHeaderFragment newInstance(JSONArray jsonDlvHdr) {
        InbDlvHeaderFragment fragment = new InbDlvHeaderFragment();
        jDlvHdr = jsonDlvHdr;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbdlv_header, container, false);

        etVendor = view.findViewById(R.id.etIDVendor);
        etVendorRef = view.findViewById(R.id.etIDVendorRef);
        etDocDt = view.findViewById(R.id.etIDDocDt);
        etRcvPlant = view.findViewById(R.id.etIDRcvPlant);
        etRcvSLoc = view.findViewById(R.id.etIDRcvSLoc);
        etContNo = view.findViewById(R.id.etIDCont);
        etVessel = view.findViewById(R.id.etIDVessel);
        btRcvHU = view.findViewById(R.id.btIDRcvHU);
        btIDPost = view.findViewById(R.id.btIDPost);

        String strVendorCd = "", strVendorRef = "", strDocDt = "", strRcvPlant = "", strRcvSloc = "", strCntNo = "", strVessel = "";
        try {
            if (jDlvHdr.getJSONObject(0) != null) {
                JSONObject jsonChildNode = jDlvHdr.getJSONObject(0);
                strVendorCd = jsonChildNode.optString("VendorCd");
                strVendorRef = jsonChildNode.optString("RefDoc");

                JSONObject jDocDt = jsonChildNode.getJSONObject("DocDt");
                strDocDt = jDocDt.optString("date");

                strRcvPlant = jsonChildNode.optString("RcvPlant");
                strRcvSloc = jsonChildNode.optString("RcvSLoc");
                strCntNo = jsonChildNode.optString("ContNo").equals("null") ? "" : jsonChildNode.optString("ContNo");
                strVessel = jsonChildNode.optString("Vessel").equals("null") ? "" : jsonChildNode.optString("Vessel");
            }
        } catch (JSONException e) {
            Log.e("YTLog " + this.getClass().getSimpleName(), e.toString());
        }

        etVendor.setText(strVendorCd);
        etVendorRef.setText(strVendorRef);
        etDocDt.setText(strDocDt);
        etRcvPlant.setText(strRcvPlant);
        etRcvSLoc.setText(strRcvSloc);
        etContNo.setText(strCntNo);
        etVessel.setText(strVessel);

        btRcvHU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GlobalVariables.gblDlvStatusCd.equals("01")) {
                    alrtLog = new AlertDialog.Builder(getActivity()).setMessage("Status should be For RF Receving.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {
                    Intent i = new Intent(getActivity(), InbDlvRcvHUActivity.class);
                    startActivity(i);
                }
            }
        });

        btIDPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GlobalVariables.gblDlvStatusCd.equals("02")) {
                    alrtLog = new AlertDialog.Builder(getActivity()).setMessage("Status should be Delivery Received.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {
                    new PHPSetGRPosted().execute();
                    /*if ((new CheckNetwork(getActivity())).isConnectingToInternet()) {
                        new PHPSetGRPosted().execute();
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

        return view;
    }

    private class PHPSetGRPosted extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "SetStatGRPosted.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("DlvNo", GlobalVariables.gblDlvNo);
                    jObjectList.put("UserId", GlobalVariables.gblUserID);

                    String message = jObjectList.toString();
                    Log.e("YTLog " + this.getClass().getSimpleName(), message);

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

            dlDialog = ProgressDialog.show(getActivity(), "Please wait", "Processing Data...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);
            String strAlertMsg = "";

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
                if (resString != null) {
                    Log.e("YTLog " + this.getClass().getSimpleName(), resString);

                    try {
                        JSONObject jsonResponse = new JSONObject(resString);
                        if (!jsonResponse.getString("Result").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("Result");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                            String strResultCd = jsonChildNode.optString("ResultCd");
                            String strDesc = jsonChildNode.optString("Desc");
                            String strDlvStatusCd = jsonChildNode.optString("DlvStatusCd");
                            String strDlvStatus = jsonChildNode.optString("DlvStatus");
                            String strMatDoc = jsonChildNode.optString("MatDocs");

                            if (strResultCd.equals("0")) {
                                strAlertMsg = strDesc;
                            } else {
                                GlobalVariables.gblDlvStatusCd = strDlvStatusCd;
                                GlobalVariables.gblDlvStatus = strDlvStatus;

                                getActivity().setTitle("ID " + GlobalVariables.gblDlvNo + " - " + strDlvStatus);
                                strAlertMsg = "Material Document/s " + strMatDoc + " created.";
                            }
                        } else {
                            strAlertMsg = "Error Encountered.";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    strAlertMsg = "Error Encountered.";
                }

                if (strAlertMsg.length() > 0) {
                    alrtLog = new AlertDialog.Builder(getActivity()).setMessage(strAlertMsg)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                }
            }

            dlDialog.dismiss();
        }
    }
}
