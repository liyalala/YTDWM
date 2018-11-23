package com.ist_systems.ytdwm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

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
import java.util.List;

public class HomeSaveInputPattern extends AppCompatActivity {

    PatternLockView mPatternLockView;

    Button btConfirm1;
    Button btCancel1;
    AlertDialog alrtLog;
    Dialog dlDialog;

    SQLiteDatabase SQLiteDatabase;
    SQLiteHelper SQLiteHelper;
    String password;

    @Override
    protected void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.activity_saveinput_pattern);

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        password = preferences.getString("password", "0");

        SQLiteHelper = new SQLiteHelper(this);
        CreateDB();

        btConfirm1 = findViewById(R.id.btConfirm1);
        btCancel1 = findViewById(R.id.btCancel1);
        SQLiteHelper = new SQLiteHelper(this);

        btCancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeSaveInputPattern.this, HomeInputNewPattern.class);
                startActivity(intent);
                finish();
            }
        });

        mPatternLockView = findViewById(R.id.pattern_confirm);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(final List<PatternLockView.Dot> pattern) {
                final String pass = PatternLockUtils.patternToString(mPatternLockView, pattern);

                btConfirm1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (password.equals(pass)) {
                            GlobalVariables.gblPattern = password;
                            mPatternLockView.clearPattern();
                            new PHPPatternSave().execute();
                        } else {
                            mPatternLockView.clearPattern();
                            Toast.makeText(getApplicationContext(), "Wrong Pattern!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onCleared() {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HomeSaveInputPattern.this, HomeInputNewPattern.class);
        startActivity(intent);
        finish();
    }

    public void CreateDB() {
        String strSQL;//
        SQLiteHelper = new SQLiteHelper(this);
        SQLiteDatabase = this.openOrCreateDatabase("YTDWMDB", Context.MODE_PRIVATE, null);

        strSQL = GlobalVariables.GetUserLogs();
        SQLiteDatabase.execSQL(strSQL);
    }


    private class PHPPatternSave extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;

            try {
                URL url = new URL(GlobalVariables.gblURL + "Pattern.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("UserId", GlobalVariables.gblUserID);
                    jsonObject.put("UserPattern", GlobalVariables.gblPattern);
                    String message = jsonObject.toString();

                    Log.e("YTLog " + this.getClass().getSimpleName(), message);

                    OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
                    os.write(message.getBytes());
                    os.flush();

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

            dlDialog = ProgressDialog.show(HomeSaveInputPattern.this, "Please wait", "Logging in...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {
                alrtLog = new AlertDialog.Builder(HomeSaveInputPattern.this).setMessage(strMsg)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
            } else {
                if (resString != null) {
                    Log.d("YTLog " + this.getClass().getSimpleName(), resString);
                    try {
                        JSONObject jsonResponse = new JSONObject(resString);
                        if (!jsonResponse.getString("pattern").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("pattern");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                            int fld = jsonChildNode.optInt("XField");

                            if (fld == 1) {
                                String strInsert = "UPDATE UserLog SET UserPattern = '1' WHERE UserId = '" + GlobalVariables.gblUserID + "'";
                                SQLiteDatabase.execSQL(strInsert);

                                Intent i = new Intent(HomeSaveInputPattern.this, MainActivity.class);
                                startActivity(i);
                                finish();

                                Toast.makeText(getApplicationContext(), "Pattern Saved", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            alrtLog = new AlertDialog.Builder(HomeSaveInputPattern.this).setMessage("Login failed.")
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



