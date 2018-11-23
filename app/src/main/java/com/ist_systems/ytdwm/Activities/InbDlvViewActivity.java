package com.ist_systems.ytdwm.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ist_systems.ytdwm.Fragments.InbDlvDetailsFragment;
import com.ist_systems.ytdwm.Fragments.InbDlvHeaderFragment;
import com.ist_systems.ytdwm.Fragments.InbDlvPOSummFragment;
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

public class InbDlvViewActivity extends AppCompatActivity {

    private static int sessionDepth = 0;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    AlertDialog alrtLog;
    ProgressDialog dlDialog;
    Boolean destroy = true;
    JSONArray jDlvHdr, jDlvDet, jPOSumm;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbdlv_view);

        prefs = PreferenceManager.getDefaultSharedPreferences(InbDlvViewActivity.this.getApplicationContext());

        String strTitle = "ID " + GlobalVariables.gblDlvNo + " - " + GlobalVariables.gblDlvStatus;
        setTitle(strTitle);

        new PHPGetIDDetails().execute();
        /*if ((new CheckNetwork(InbDlvViewActivity.this)).isConnectingToInternet()) {
            new PHPGetIDDetails().execute();
        } else {
            alrtLog = new AlertDialog.Builder(InbDlvViewActivity.this).setMessage("Network Connection failed.")
                    .setNegativeButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                    .show();
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        String strTitle = "ID " + GlobalVariables.gblDlvNo + " - " + GlobalVariables.gblDlvStatus;
        setTitle(strTitle);

        sessionDepth++;
        /*if (prefs.contains("prefSession")) {
            if (!prefs.getString("prefSession", "").equals("")) {
                try {
                    SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy HH:mm");

                    String strDateEnded = prefs.getString("prefSession", "");
                    Calendar calendarDateEnded = Calendar.getInstance();
                    calendarDateEnded.setTime(format1.parse(strDateEnded));

                    Calendar calendarDateFound = Calendar.getInstance();

                    long dateDiff = calendarDateFound.getTimeInMillis() - calendarDateEnded.getTimeInMillis();
                    int minutes = (int) ((dateDiff / (1000 * 60)) % 60);
                    Log.e("YTLog " + "CHECK SESSION", String.valueOf(minutes));

                    int sessionTime = 10;
                    if (prefs.contains("prefSessionTime")) {
                        sessionTime = Integer.getInteger(prefs.getString("prefSessionTime", "10"));
                    }

                    if (minutes > sessionTime) { // show Login activity after 10mins of running in the background
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("prefSession");
                        editor.apply();

                        GlobalVariables.gblUserID = null;
                        GlobalVariables.gblUserPW = null;

                        Intent intent = new Intent(InbDlvViewActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else { // reset session
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("prefSession");
                        editor.apply();
                    }
                } catch (Exception e) {
                    Log.e("YTLog " + "CHECK SESSION", e.toString());
                }
            }
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*if (sessionDepth > 0)
            sessionDepth--;
        if (sessionDepth == 0) {
            SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sPrefs.edit();

            SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            Calendar calendarDateFound = Calendar.getInstance();
            String strDateFound = format1.format(calendarDateFound.getTime());

            if (!sPrefs.contains("prefSession")) {
                editor.putString("prefSession", strDateFound);
                editor.apply();

                Log.e("YTLog " + "CHECK SESSION", strDateFound);
            }
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (destroy) {
            new PHPUnLock().execute();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return InbDlvHeaderFragment.newInstance(jDlvHdr);
                case 1:
                    return InbDlvDetailsFragment.newInstance(jDlvDet);
                case 2:
                    return InbDlvPOSummFragment.newInstance(jPOSumm);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Header";
                case 1:
                    return "Details";
                case 2:
                    return "PO Summary";
            }
            return null;
        }
    }

    private class PHPUnLock extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "UnlockRF.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("ObjID", "DlvNo");
                    jObjectList.put("ObjKey", GlobalVariables.gblDlvNo);
                    jObjectList.put("UserId", GlobalVariables.gblUserID);
                    jObjectList.put("DeviceId", GlobalVariables.gblDeviceName);

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
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                Log.e("YTLog " + this.getClass().getSimpleName(), strMsg);
                /*alrtLog = new AlertDialog.Builder(InbDlvViewActivity.this).setMessage(strMsg)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();*/
            }

        }
    }

    private class PHPGetIDDetails extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "GetIDDetails.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("DlvNo", GlobalVariables.gblDlvNo);
                jsonObject.put("UserId", GlobalVariables.gblUserID);
                jsonObject.put("DeviceId", GlobalVariables.gblDeviceName);
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
                urlConnection.disconnect();

                responseString = sb.toString();
            } catch (Exception e) {
                Log.e("YTLog " + this.getClass().getSimpleName(), "Network: " + e.toString());

                bError = true;
                strMsg = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dlDialog = ProgressDialog.show(InbDlvViewActivity.this, "Please wait", "Fetching data...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(InbDlvViewActivity.this).setMessage(strMsg)
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
                        if (!jsonResponse.getString("CheckLock").equals("null")) {
                            JSONArray jsonMainNodeLock = jsonResponse.optJSONArray("CheckLock");
                            JSONObject jsonChildNodeLock = jsonMainNodeLock.getJSONObject(0);

                            String strLocked = jsonChildNodeLock.optString("Locked");
                            String strLockedBy = jsonChildNodeLock.optString("LockedBy");
                            String strRemarks = jsonChildNodeLock.optString("Remarks");
                            //String strDevice = jsonChildNode.optString("Device");

                            if (strLocked.equals("0")) {
                                String strAlertMsg = "TO " + GlobalVariables.gblTONo + " is currently locked by User " + strLockedBy + " in module " + strRemarks + ".";
                                alrtLog = new AlertDialog.Builder(InbDlvViewActivity.this).setMessage(strAlertMsg)
                                        .setNegativeButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        destroy = false;
                                                        dlDialog.dismiss();

                                                        finish();
                                                    }
                                                })
                                        .setCancelable(false)
                                        .show();
                            } else {
                                jDlvHdr = jsonResponse.optJSONArray("DlvHdr");
                                JSONObject jsonChildNode = jDlvHdr.getJSONObject(0);
                                String strDlvStatusDesc = jsonChildNode.optString("LongText");
                                GlobalVariables.gblDlvStatusCd = jsonChildNode.optString("DlvStatusCd");
                                setTitle("ID " + GlobalVariables.gblDlvNo + " - " + strDlvStatusDesc);

                                jDlvDet = jsonResponse.optJSONArray("DlvDet");
                                jPOSumm = jsonResponse.optJSONArray("POSumm");

                                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                                mViewPager = findViewById(R.id.container);
                                mViewPager.setAdapter(mSectionsPagerAdapter);

                                TabLayout tabLayout = findViewById(R.id.tabs);
                                tabLayout.setupWithViewPager(mViewPager);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    alrtLog = new AlertDialog.Builder(InbDlvViewActivity.this).setMessage("No Data Found.")
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
