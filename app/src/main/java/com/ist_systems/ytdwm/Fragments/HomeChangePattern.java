package com.ist_systems.ytdwm.Fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.ist_systems.ytdwm.CheckNetwork;
import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.HomeInputNewPattern;
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

public class HomeChangePattern extends Fragment {

    Button btTasks;
    Button btTransactions;

    EditText etPassword1;
    Button btLogin1;
    CheckBox cbShowPass1;
    EditText etUserId;
    AlertDialog alrtLog;
    Dialog dlDialog;

    public HomeChangePattern() {
        // Required empty public constructor
    }

    public static HomeChangePattern newInstance() {
        HomeChangePattern fragment = new HomeChangePattern();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_change_pattern, container, false);

        btTasks = view.findViewById(R.id.btHFTaskList);
        btTransactions = view.findViewById(R.id.btHFTrans);
        etPassword1 = view.findViewById(R.id.etPassword1);
        btLogin1 = view.findViewById(R.id.btLogin1);
        etUserId = view.findViewById(R.id.etUserID);
        cbShowPass1 = view.findViewById(R.id.cbShowPass1);

        btLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etPassword1.getText().toString().length() == 0) {
                    Toast.makeText(getContext(), "Password is required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ((new CheckNetwork(getContext())).isConnectingToInternet()) {
                    GlobalVariables.gblUserPW = etPassword1.getText().toString();

                    new PHPLogin().execute();
                } else {
                    alrtLog = new AlertDialog.Builder(getContext()).setMessage("Network Connection failed.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                }

                /*Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();*/
            }
        });

        cbShowPass1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    etPassword1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    etPassword1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        btTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeTasksFragment fragment = new HomeTasksFragment();

                FragmentManager fManager = getFragmentManager();
                FragmentTransaction fTransact;
                fTransact = fManager.beginTransaction();
                fTransact.replace(R.id.fmHome, fragment)
                        .commit();
            }
        });

        btTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeTransactionsFragment fragment = new HomeTransactionsFragment();

                FragmentManager fManager = getFragmentManager();
                FragmentTransaction fTransact;
                fTransact = fManager.beginTransaction();
                fTransact.replace(R.id.fmHome, fragment)
                        .commit();
            }
        });

        return view;
    }

    private class PHPLogin extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "CreatePatternLogin.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("UserId", GlobalVariables.gblUserID);
                    jsonObject.put("Password", GlobalVariables.gblUserPW);
                    String message = jsonObject.toString();

                    Log.e("YTLog " + this.getClass().getSimpleName(), message);

                    OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
                    os.write(message.getBytes());
                    os.flush();

                    /*InputStream in = new BufferedInputStream(urlc.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    */

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

            dlDialog = ProgressDialog.show(getContext(), "Please wait", "Logging in...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {
                alrtLog = new AlertDialog.Builder(getContext()).setMessage(strMsg)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
            } else {
                if (resString != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(resString);
                        if (!jsonResponse.getString("login").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("login");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                            int fld = jsonChildNode.optInt("XField");

                            Log.d("YTLog " + this.getClass().getSimpleName(), resString);
                            if (fld == 1) {
                                etPassword1.setText("");
                                Intent i = new Intent(getContext(), HomeInputNewPattern.class);
                                startActivity(i);
                            } else {
                                alrtLog = new AlertDialog.Builder(getContext()).setMessage("Login failed.")
                                        .setNegativeButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                    }
                                                })
                                        .show();
                            }
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
