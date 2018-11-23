package com.ist_systems.ytdwm.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.ListViewAndAdapters.PickingFreeItems;
import com.ist_systems.ytdwm.ListViewAndAdapters.PickingFreeItemsAdapter;
import com.ist_systems.ytdwm.ListViewAndAdapters.PickingFreeSubItems;
import com.ist_systems.ytdwm.R;
import com.ist_systems.ytdwm.Util;

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
import java.util.ArrayList;
import java.util.LinkedHashMap;

import cn.pda.scan.ScanThread;

public class OutDlvPickAccFreeActivity extends AppCompatActivity {

    private static int sessionDepth = 0;
    TextView tvOutPkgLabel;
    EditText etPckHU;
    EditText etHU;
    ExpandableListView lvPicking;
    Button btConfirm;
    ImageView imgDelete;
    String strScanItem;
    String strScanObj;
    Boolean onDeleteMode = false;
    Boolean destroy = true;
    AlertDialog alrtLog;
    ProgressDialog dlDialog;
    PickingFreeItemsAdapter pickingAdapter;
    SharedPreferences prefs;
    private ScanThread scanThread;
    private KeyReceiver keyReceiver;
    private LinkedHashMap<String, PickingFreeItems> hashMap = new LinkedHashMap<>();
    private ArrayList<PickingFreeItems> PickList = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == ScanThread.SCAN) {

                if (prefs.contains("prefScanSound")) {
                    if (prefs.getString("prefScanSound", "true").equals("true")) {
                        Util.play(1, 0);
                    }
                }

                String data = msg.getData().getString("data");
                if (data != null) {
                    strScanItem = data.trim();

                    DoScan();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outdlv_pickaccfree);

        prefs = PreferenceManager.getDefaultSharedPreferences(OutDlvPickAccFreeActivity.this.getApplicationContext());

        setTitle(GlobalVariables.gblTask);

        etPckHU = findViewById(R.id.etODOutPkg);
        etHU = findViewById(R.id.etODHU);
        tvOutPkgLabel = findViewById(R.id.tvOuterPkg);
        lvPicking = findViewById(R.id.lvPickingCont);
        btConfirm = findViewById(R.id.btPickConfirm);
        imgDelete = findViewById(R.id.imgDelete);

        Initialize();

        etPckHU.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strScanObj = "OuterPkg";
                }
            }
        });

        etPckHU.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    strScanItem = etPckHU.getText().toString();
                    DoScan();

                    return true;
                }
                return false;
            }
        });

        etHU.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strScanObj = "HU";
                }
            }
        });

        etHU.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    strScanItem = etHU.getText().toString();
                    DoScan();

                    return true;
                }
                return false;
            }
        });

        lvPicking.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true;
            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new PHPConfirmTO().execute();

                /*if ((new CheckNetwork(OutDlvPickAccFreeActivity.this)).isConnectingToInternet()) {
                    new PHPConfirmTO().execute();
                } else {
                    alrtLog = new AlertDialog.Builder(OutDlvPickAccFreeActivity.this).setMessage("Network Connection failed.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                }*/
            }
        });

        new PHPOnload().execute();
        /*if ((new CheckNetwork(OutDlvPickAccFreeActivity.this)).isConnectingToInternet()) {
            new PHPOnload().execute();
        } else {
            alrtLog = new AlertDialog.Builder(OutDlvPickAccFreeActivity.this)
                    .setTitle("Unable to retrieve data.")
                    .setMessage("Network Connection failed.")
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scanning, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            if (!onDeleteMode) {

                if (PickList.size() == 0) {
                    alrtLog = new AlertDialog.Builder(OutDlvPickAccFreeActivity.this).setMessage("No Data Found.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {
                    btConfirm.setEnabled(false);
                    imgDelete.setVisibility(View.VISIBLE);
                    onDeleteMode = true;

                    etHU.requestFocus();
                    etPckHU.setEnabled(false);
                }
            } else {
                btConfirm.setEnabled(true);
                imgDelete.setVisibility(View.INVISIBLE);
                onDeleteMode = false;

                etPckHU.setEnabled(true);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*sessionDepth++;

        if (prefs.contains("prefSession")) {
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

                        Intent intent = new Intent(OutDlvPickAccFreeActivity.this, LoginActivity.class);
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
    protected void onDestroy() {
        super.onDestroy();

        if (scanThread != null) {
            scanThread.interrupt();
            scanThread.close();
        }
        unregisterReceiver();

        if (destroy) {
            new PHPUnLock().execute();
        }
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

        if (scanThread != null) {
            scanThread.interrupt();
            scanThread.close();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("YTLog " + this.getClass().getSimpleName(), Integer.toString(keyCode));

        if (keyCode == 133 || keyCode == 135) {
            scanThread.scan();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void Initialize() {
        prefs = PreferenceManager.getDefaultSharedPreferences(OutDlvPickAccFreeActivity.this.getApplicationContext());
        if (prefs.contains("prefKeyboard")) {
            if (prefs.getString("prefKeyboard", "true").equals("true")) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                etPckHU.setInputType(InputType.TYPE_CLASS_TEXT);
                etHU.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                etPckHU.setInputType(InputType.TYPE_NULL);
                etHU.setInputType(InputType.TYPE_NULL);
            }
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            etPckHU.setInputType(InputType.TYPE_NULL);
            etHU.setInputType(InputType.TYPE_NULL);
        }

        /*registerReceiver();
        try {
            scanThread = new ScanThread(mHandler);
        } catch (Exception e) {
            Log.e("YTLog " + getClass().getSimpleName(), e.toString());
            return;
        }
        scanThread.start();
        Util.initSoundPool(this);*/

        try {
            registerReceiver();
            scanThread = new ScanThread(mHandler);
            scanThread.start();
            Util.initSoundPool(this);
        } catch (UnsatisfiedLinkError e) {
            Log.e("YTLog 1" + this.getClass().getSimpleName(), e.toString());
        } catch (NoClassDefFoundError e) {
            Log.e("YTLog 2" + this.getClass().getSimpleName(), e.toString());
        } catch (Exception e) {
            Log.e("YTLog 3" + this.getClass().getSimpleName(), e.toString());
        }
    }

    private void DoScan() {
        if (strScanObj.equals("OuterPkg")) {
            etPckHU.setText(strScanItem);
            etHU.setText("");
        } else if (strScanObj.equals("HU")) {
            etHU.setText(strScanItem);
        }

        new PHPCheckScanItem().execute();
        /*if ((new CheckNetwork(OutDlvPickAccFreeActivity.this)).isConnectingToInternet()) {
            new PHPCheckScanItem().execute();
        } else {
            alrtLog = new AlertDialog.Builder(OutDlvPickAccFreeActivity.this).setMessage("Network Connection failed.")
                    .setNegativeButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                    .show();
        }*/
    }

    private void registerReceiver() {
        keyReceiver = new KeyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        registerReceiver(keyReceiver, filter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(keyReceiver);
    }

    private void ExpandAll() {
        int count = pickingAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            lvPicking.expandGroup(i);
        }
    }

    private class KeyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keyCode", 0);
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0);
            }
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            if (keyDown) {
                Log.e("YTLog " + this.getClass().getSimpleName(), Integer.toString(keyCode));

                if (keyCode == 133 || keyCode == 135) {
                    scanThread.scan();
                }
            }
        }
    }

    private class PHPOnload extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "OnloadPicking.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("TONo", GlobalVariables.gblTONo);
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

            if (scanThread != null) {
                scanThread.interrupt();
                scanThread.close();
            }

            unregisterReceiver();

            dlDialog = ProgressDialog.show(OutDlvPickAccFreeActivity.this, "Please wait", "Checking data...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(OutDlvPickAccFreeActivity.this).setMessage(strMsg)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
            } else {
                String strAlertMsg = "";

                if (resString != null) {
                    Log.e("YTLog " + this.getClass().getSimpleName(), resString);

                    try {
                        JSONObject jsonResponse = new JSONObject(resString);
                        if (!jsonResponse.getString("CheckLock").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("CheckLock");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                            String strLocked = jsonChildNode.optString("Locked");
                            String strLockedBy = jsonChildNode.optString("LockedBy");
                            String strRemarks = jsonChildNode.optString("Remarks");
                            //String strDevice = jsonChildNode.optString("Device");

                            if (strLocked.equals("0")) {
                                strAlertMsg = "TO " + GlobalVariables.gblTONo + " is currently locked by User " + strLockedBy + " in module " + strRemarks + ".";
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
                    alrtLog = new AlertDialog.Builder(OutDlvPickAccFreeActivity.this).setMessage(strAlertMsg)
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
                }
            }

            dlDialog.dismiss();

            try {
                registerReceiver();
                scanThread = new ScanThread(mHandler);
                scanThread.start();
            } catch (Exception e) {
                Log.e("YTLog " + this.getClass().getSimpleName(), e.toString());

            }
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
                    jObjectList.put("ObjID", "TONo");
                    jObjectList.put("ObjKey", GlobalVariables.gblTONo);
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
                /*alrtLog = new AlertDialog.Builder(OutDlvPickAccFreeActivity.this).setMessage(strMsg)
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

    private class PHPCheckScanItem extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "CheckScanItemFreePick.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("ObjRef", strScanObj.replaceAll("\\r\\n", ""));
                    jObjectList.put("ScanItem", strScanItem.replaceAll("\\r\\n", ""));
                    jObjectList.put("TONo", GlobalVariables.gblTONo);

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

            if (scanThread != null) {
                scanThread.interrupt();
                scanThread.close();
            }

            unregisterReceiver();

            dlDialog = ProgressDialog.show(OutDlvPickAccFreeActivity.this, "Please wait", "Checking Scanned Item...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);
            String strAlertMsg = "";

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(OutDlvPickAccFreeActivity.this).setMessage(strMsg)
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
                        if (!jsonResponse.getString("PickFree").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("PickFree");
                            String DlvItem, BinCd, IntHUID, MatNo, Batch, ReqdQty, PickQty, PckHU;
                            Boolean bContinue = true;

                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                DlvItem = jsonChildNode.optString("DlvItem");
                                BinCd = jsonChildNode.optString("BinCd");
                                IntHUID = jsonChildNode.optString("IntHUID");
                                MatNo = jsonChildNode.optString("MatNo");
                                Batch = jsonChildNode.optString("Batch");
                                ReqdQty = jsonChildNode.optString("ReqdQty");
                                PickQty = jsonChildNode.optString("AvailQty");
                                PckHU = etPckHU.getText().toString();

                                PickingFreeItems header = hashMap.get(DlvItem);
                                if (!onDeleteMode) {
                                    if (header != null) {
                                        if (header.getHUs() != null) {
                                            ArrayList<String> arrHU = header.getHUs();
                                            int ind = arrHU.indexOf(IntHUID);
                                            if (ind >= 0) {
                                                bContinue = false;
                                            }
                                        }
                                    }

                                    if (bContinue) {
                                        if (header == null) {
                                            header = new PickingFreeItems();
                                            header.setValues(DlvItem, MatNo, Batch, ReqdQty);
                                            hashMap.put(DlvItem, header);
                                            PickList.add(header);
                                        }

                                        ArrayList<PickingFreeSubItems> subItems = header.getSubItems();
                                        int listSize = subItems.size();
                                        listSize++;

                                        PickingFreeSubItems detail = new PickingFreeSubItems();
                                        detail.setSequence(String.valueOf(listSize));
                                        detail.setValues(DlvItem, BinCd, IntHUID, PickQty, PckHU);
                                        subItems.add(detail);
                                        header.setSubItems(subItems);
                                    }

                                } else {

                                    ArrayList<PickingFreeSubItems> subItems = header.getSubItems();
                                    ArrayList<String> arrHU = header.getHUs();
                                    int ind = arrHU.indexOf(IntHUID);
                                    if (ind >= 0) {
                                        subItems.remove(ind);

                                        Log.e("YTLog " + this.getClass().getSimpleName(), "onDeleteMode: " + String.valueOf(ind));
                                        header.setSubItems(subItems);

                                        if (subItems.size() == 0) {
                                            hashMap.remove(DlvItem);
                                            PickList.remove(header);
                                        }
                                    }
                                }
                            }

                            if (!bContinue) {
                                Toast.makeText(OutDlvPickAccFreeActivity.this, "Item already scanned.", Toast.LENGTH_LONG).show();
                                bContinue = true;
                            }

                        } else {
                            strAlertMsg = "Invalid " + strScanObj + " scanned.";
                        }

                        pickingAdapter = new PickingFreeItemsAdapter(OutDlvPickAccFreeActivity.this, PickList);
                        lvPicking.setAdapter(pickingAdapter);
                        ExpandAll();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    strAlertMsg = "Invalid " + strScanObj + " scanned.";
                }

                if (strAlertMsg.length() > 0) {
                    alrtLog = new AlertDialog.Builder(OutDlvPickAccFreeActivity.this).setMessage(strAlertMsg)
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

            try {
                registerReceiver();
                scanThread = new ScanThread(mHandler);
                scanThread.start();
            } catch (Exception e) {
                Log.e("YTLog " + this.getClass().getSimpleName(), e.toString());

            }
        }
    }

    private class PHPConfirmTO extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "ProcessPicking.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    PickingFreeItems phd;
                    PickingFreeSubItems phds;
                    ArrayList<PickingFreeSubItems> pdt;
                    JSONArray jArrayPick = new JSONArray();

                    for (int i = 0; i < PickList.size(); i++) {
                        phd = PickList.get(i);
                        pdt = phd.getSubItems();

                        for (int j = 0; j < pdt.size(); j++) {
                            phds = pdt.get(j);

                            JSONObject jObject = new JSONObject();
                            jObject.put("MatNo", phd.getMatNo());
                            jObject.put("Batch", phd.getBatch());
                            jObject.put("ReqdQty", phd.getReqdQty());
                            jObject.put("Bin", phds.getBin());
                            jObject.put("HUID", phds.getHU());
                            jObject.put("PickingHU", phds.getPickingHU());
                            jObject.put("PickQty", phds.getPickQty());

                            jArrayPick.put(jObject);
                        }
                    }

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("TONo", GlobalVariables.gblTONo);
                    jObjectList.put("UserId", GlobalVariables.gblUserID);
                    jObjectList.put("PickList", jArrayPick);

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

            if (scanThread != null) {
                scanThread.interrupt();
                scanThread.close();
            }

            unregisterReceiver();

            dlDialog = ProgressDialog.show(OutDlvPickAccFreeActivity.this, "Please wait", "Checking Scanned Item...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(OutDlvPickAccFreeActivity.this).setMessage(strMsg)
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
                        if (!jsonResponse.getString("confirmTO").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("confirmTO");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                            String Ind, Msg;
                            Ind = jsonChildNode.optString("Ind");
                            Msg = jsonChildNode.optString("Msg");

                            alrtLog = new AlertDialog.Builder(OutDlvPickAccFreeActivity.this).setMessage(Msg)
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

            try {
                registerReceiver();
                scanThread = new ScanThread(mHandler);
                scanThread.start();
            } catch (Exception e) {
                Log.e("YTLog " + this.getClass().getSimpleName(), e.toString());

            }
        }
    }
}
