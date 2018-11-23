package com.ist_systems.ytdwm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jmcaceres on 5/25/2018.
 */
public class SettingsActivity extends PreferenceActivity {

    static SwitchPreference swScanSound;
    static SwitchPreference swKeyboard;
    static ListPreference lSession;

    String strParentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent();
        strParentActivity = myIntent.getStringExtra("parentActivity");

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        AlertDialog alrtLog;
        ProgressDialog dlDialog;
        String cachedir = "";
        String newFile = "";

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            boolean dfScanSound = prefs.getString("prefScanSound", "").equals("true");
            swScanSound = (SwitchPreference) getPreferenceManager().findPreference("setScanSound");
            swScanSound.setDefaultValue(dfScanSound);
            swScanSound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("prefScanSound", newValue.toString());
                    editor.apply();

                    Log.d("YTLog " + this.getClass().getSimpleName(), "prefScanSound" + newValue.toString());
                    return true;
                }
            });

            boolean dfKeyboard = prefs.getString("prefKeyboard", "").equals("true");
            swKeyboard = (SwitchPreference) getPreferenceManager().findPreference("setKeyboard");
            swKeyboard.setDefaultValue(dfKeyboard);
            swKeyboard.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("prefKeyboard", newValue.toString());
                    editor.apply();

                    Log.d("YTLog " + this.getClass().getSimpleName(), "prefKeyboard" + newValue.toString());
                    return true;
                }
            });

            /*String dtRetenSumm = prefs.getString("prefSessionTime", "30");
            lSession = (ListPreference) getPreferenceManager().findPreference("setSession");
            lSession.setSummary(getResources().getString(R.string.prefSessionSumm) + dtRetenSumm);
            lSession.setOnPreferenceChangeListener(new android.preference.Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(android.preference.Preference preference, Object newValue) {

                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(newValue.toString());
                    CharSequence[] entries = listPreference.getEntries();

                    String currText = getResources().getString(R.string.prefSessionSumm) + " " + entries[index].toString();
                    preference.setSummary(currText);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("prefSessionVal", newValue.toString());
                    editor.putString("prefSessionTime", entries[index].toString());
                    editor.apply();

                    Log.d("YTLog " + this.getClass().getSimpleName(), newValue.toString());

                    return true;
                }
            });*/

            android.preference.Preference downloadNewVersion = findPreference("setCheckUpdate");
            downloadNewVersion.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(android.preference.Preference preference) {

                    new WebRequestForVersion().execute();
                    /*if ((new CheckNetwork(getActivity())).isConnectingToInternet()) {
                        new WebRequestForVersion().execute();
                    } else {
                        alrtLog = new AlertDialog.Builder(getActivity()).setMessage("Network connection failed.")
                                .setNegativeButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                .show();
                    }*/

                    return true;
                }
            });


        }

        public void DownloadInstall() {

            File getCache = getActivity().getCacheDir();
            cachedir = getCache.getAbsolutePath();

            dlDialog = new ProgressDialog(getActivity());
            dlDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dlDialog.setTitle("New version found.");
            dlDialog.setMessage("Downloading");
            dlDialog.show();

            new Thread(new Runnable() {
                public void run() {

                    String filePath = GlobalVariables.gblFolerPath + File.separator + "NewVersion" + File.separator; // path to save downloaded file
                    final String apkFileSource = GlobalVariables.gblURL + "NewVersion" + File.separator + "WMSClientInstaller.apk"; // source file

                    InputStream is = null;
                    OutputStream os = null;
                    URLConnection URLConn = null;

                    try {
                        URL fileUrl;
                        byte[] buf;
                        int ByteRead = 0;
                        int ByteWritten = 0;

                        fileUrl = new URL(apkFileSource);
                        URLConn = fileUrl.openConnection();
                        is = URLConn.getInputStream();

                        String fileName = apkFileSource.substring(apkFileSource.lastIndexOf("/") + 1);
                        File file = new File(filePath);
                        file.mkdir();

                        String downloadedFile = filePath + fileName;
                        file = new File(downloadedFile);
                        newFile = downloadedFile;
                        Log.d("YTLog " + this.getClass().getSimpleName(), apkFileSource);

                        os = new BufferedOutputStream(new FileOutputStream(downloadedFile));
                        buf = new byte[1024];

                        while ((ByteRead = is.read(buf)) != -1) {
                            os.write(buf, 0, ByteRead);
                            ByteWritten += ByteRead;

                            final int tmpWritten = ByteWritten;
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    dlDialog.setMessage("" + tmpWritten + " Bytes");
                                }
                            });
                        }

                        is.close();
                        os.flush();
                        os.close();

                        Thread.sleep(200);
                        dlDialog.dismiss();

                        if (Build.VERSION.SDK_INT >= 24) {
                            Context con = getActivity();
                            Uri myuri = FileProvider.getUriForFile(con, con.getApplicationContext().getPackageName() + ".provider", file);

                            Intent promptInstall = new Intent(Intent.ACTION_VIEW).setDataAndType(myuri, "application/vnd.android.package-archive");
                            promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            con.startActivity(promptInstall);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(newFile)),
                                    "application/vnd.android.package-archive");
                            startActivity(intent);
                            getActivity().finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        private class WebRequestForVersion extends AsyncTask<String, Void, String> {
            Boolean bError = false;
            String strMsg = "";

            @Override
            protected String doInBackground(String... strings) {

                String responseString = null;
                String line;

                try {
                    URL url = new URL(GlobalVariables.gblURL + "NewVersion/VersionControl.php");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        urlConnection.setRequestMethod("POST");
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));
                        StringBuilder sb = new StringBuilder();

                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }

                        in.close();
                        responseString = sb.toString();
                    } catch (Exception e) {
                        Log.e("YTLog " + this.getClass().getSimpleName(), e.toString());
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
            }

            @Override
            protected void onPostExecute(String resString) {
                super.onPostExecute(resString);

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

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
                    int newVersionCode = 0;
                    String newVersionName = "1.0.0.0";
                    Log.d("YTLog " + this.getClass().getSimpleName() + " Check Version", resString);
                    if (resString != null) {
                        try {
                            JSONObject jsonResponse = new JSONObject(resString);
                            if (!jsonResponse.getString("version").equals("null")) {
                                JSONArray jsonMainNode = jsonResponse.optJSONArray("version");

                                for (int i = 0; i < jsonMainNode.length(); i++) {
                                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                    newVersionCode = Integer.parseInt(jsonChildNode.optString("versioncode"));
                                    newVersionName = jsonChildNode.optString("versionname");
                                }
                            }

                            Log.d("YTLog " + this.getClass().getSimpleName(), "New - VersionCode : " + Integer.toString(newVersionCode) + ", VersionName : " + newVersionName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            PackageInfo currentInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

                            Log.d("YTLog " + this.getClass().getSimpleName(), "Current - VersionCode : " + currentInfo.versionCode + ", VersionName : " + currentInfo.versionName);
                            Log.d("YTLog " + this.getClass().getSimpleName(), "New - VersionCode : " + Integer.toString(newVersionCode) + ", VersionName : " + newVersionName);

                            if (newVersionCode != currentInfo.versionCode) {
                                DownloadInstall();
                            } else {
                                alrtLog = new AlertDialog.Builder(getActivity()).setMessage("No new version found!")
                                        .setNegativeButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                    }
                                                })
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
