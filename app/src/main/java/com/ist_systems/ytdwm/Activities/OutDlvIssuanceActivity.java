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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ist_systems.ytdwm.Fragments.OutDlvIssNewQtyFragment;
import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.ListViewAndAdapters.ODIssuance;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pda.scan.ScanThread;

public class OutDlvIssuanceActivity extends AppCompatActivity {

    private static int sessionDepth = 0;
    AlertDialog alrtLog;
    ProgressDialog dlDialog;
    EditText etHUID;
    ListView lvScanned;
    Button btSubmit;
    Button btCancel;
    ImageView imgDelete;
    String strScanItem = "";
    String strGUID;
    String strNewQty = "";
    String strUpdHUID = "";
    Boolean onDeleteMode = false;
    SharedPreferences prefs;
    List<Map<String, String>> listMap;
    private SimpleAdapter adapter = null;
    private List<ODIssuance> listBarcode = new ArrayList<>();
    private ArrayList<String> arrHU = new ArrayList<>();
    private ScanThread scanThread;
    private KeyReceiver keyReceiver;
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
                    etHUID.setText(strScanItem);

                    DoScan();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outdlv_issuance);

        prefs = PreferenceManager.getDefaultSharedPreferences(OutDlvIssuanceActivity.this.getApplicationContext());

        String strTitle = GlobalVariables.gblDlvNo + " for Issuance";
        setTitle(strTitle);

        etHUID = findViewById(R.id.etHUID);
        lvScanned = findViewById(R.id.lvTOIssuance);
        btSubmit = findViewById(R.id.btSubmit);
        btCancel = findViewById(R.id.btCancel);
        imgDelete = findViewById(R.id.imgDelete);

        Initialize();

        etHUID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    strScanItem = etHUID.getText().toString();
                    DoScan();

                    return true;
                }
                return false;
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listBarcode.size() == 0) {
                    alrtLog = new AlertDialog.Builder(OutDlvIssuanceActivity.this).setMessage("No Data to Process.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {
                    new PHPODIssue().execute();
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lvScanned.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                String strHUID = listBarcode.get(pos).getHUID();
                String strOldQty = listBarcode.get(pos).getIssQty();
                strUpdHUID = strHUID;

                OutDlvIssNewQtyFragment dialog = OutDlvIssNewQtyFragment.newInstance(strOldQty);
                dialog.show(getSupportFragmentManager(), strHUID);

                return true;
            }
        });
    }

    public void UpdateQty(String _strNewQty) {
        strNewQty = _strNewQty;

        new PHPUpdateScanItem().execute();
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
                if (listBarcode.size() == 0) {
                    alrtLog = new AlertDialog.Builder(OutDlvIssuanceActivity.this).setMessage("No Data Found.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {
                    btSubmit.setEnabled(false);
                    btCancel.setEnabled(false);

                    imgDelete.setVisibility(View.VISIBLE);
                    onDeleteMode = true;
                }
            } else {
                btSubmit.setEnabled(true);
                btCancel.setEnabled(true);

                imgDelete.setVisibility(View.INVISIBLE);
                onDeleteMode = false;
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

                        Intent intent = new Intent(OutDlvIssuanceActivity.this, LoginActivity.class);
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

    private void Initialize() {
        strGUID = "RFScan".concat(java.util.UUID.randomUUID().toString());

        prefs = PreferenceManager.getDefaultSharedPreferences(OutDlvIssuanceActivity.this.getApplicationContext());
        if (prefs.contains("prefKeyboard")) {
            if (prefs.getString("prefKeyboard", "true").equals("true")) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                etHUID.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                etHUID.setInputType(InputType.TYPE_NULL);
            }
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            etHUID.setInputType(InputType.TYPE_NULL);
        }

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

        if (!onDeleteMode) {
            if (checkHUExists(listBarcode, strScanItem)) {
                Toast.makeText(OutDlvIssuanceActivity.this, "Item already scanned.", Toast.LENGTH_LONG).show();
            } else {
                new PHPCheckScanItem().execute();
            }
        } else {
            if (checkHUExists(listBarcode, strScanItem)) {
                new PHPRemoveScanItem().execute();
            } else
                Toast.makeText(OutDlvIssuanceActivity.this, "Item doesn't exists.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkHUExists(List<ODIssuance> list, String HU) {
        Log.d("YTLog " + this.getClass().getSimpleName(), HU);
        for (int i = 0; i < list.size(); i++) {
            Log.d("YTLog " + this.getClass().getSimpleName(), list.get(i).getHUID());
            if (HU.equals(list.get(i).getHUID()))
                return true;
        }
        return false;
    }

    private void addtoBarcodeList(List<ODIssuance> list, String HU, String Qty) {

        list.add(new ODIssuance(HU, Qty));
        arrHU.add(HU);
    }

    private void addToListView() {
        List<Map<String, String>> listMap;
        listMap = new ArrayList<>();
        int id = 1;

        for (ODIssuance barcode : listBarcode) {
            Map<String, String> map = new HashMap<>();
            map.put("id", id + "");
            map.put("huid", barcode.getHUID());
            map.put("qty", barcode.getIssQty());
            listMap.add(map);

            id++;
        }

        adapter = new SimpleAdapter(this, listMap, R.layout.listview_odissuance,
                new String[]{"huid", "qty",},
                new int[]{
                        R.id.tvHUID,
                        R.id.tvQty});

        lvScanned.setAdapter(adapter);
    }

    private void removeFromListView() {
        List<Map<String, String>> listMap;
        listMap = new ArrayList<>();
        int id = 1;

        int iInd = arrHU.indexOf(strScanItem);
        if (iInd >= 0) {
            listBarcode.remove(iInd);
            arrHU.remove(iInd);

            for (ODIssuance barcode : listBarcode) {
                Map<String, String> map = new HashMap<>();
                map.put("id", id + "");
                map.put("huid", barcode.getHUID());
                map.put("qty", barcode.getIssQty());
                listMap.add(map);

                id++;
            }

            adapter = new SimpleAdapter(this, listMap, R.layout.listview_odissuance,
                    new String[]{"huid", "qty",}, new int[]{
                    R.id.tvHUID,
                    R.id.tvQty});

            lvScanned.setAdapter(adapter);
        }
    }

    private void updateListView(String strHU, String strQty) {
        boolean bUpdate = false;
        int id = 1;

        int iInd = arrHU.indexOf(strHU);
        if (iInd >= 0) {
            listBarcode.get(iInd).setIssQty(strQty);

            bUpdate = true;
        }

        listMap = new ArrayList<>();
        if (bUpdate) {
            listMap.clear();
            for (ODIssuance barcode : listBarcode) {
                Map<String, String> map = new HashMap<>();
                map.put("id", id + "");
                map.put("huid", barcode.getHUID());
                map.put("qty", barcode.getIssQty());
                listMap.add(map);

                id++;
            }

            adapter = new SimpleAdapter(this, listMap, R.layout.listview_odissuance,
                    new String[]{"huid", "qty"}, new int[]{
                    R.id.tvHUID,
                    R.id.tvQty});

            lvScanned.setAdapter(adapter);
        }
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

    private class PHPCheckScanItem extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "CheckScanItemODIssuance.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("GUID", strGUID.replaceAll("\\r\\n", ""));
                    jObjectList.put("DlvNo", GlobalVariables.gblDlvNo.replaceAll("\\r\\n", ""));
                    jObjectList.put("HUID", strScanItem.replaceAll("\\r\\n", ""));
                    jObjectList.put("UserId", GlobalVariables.gblUserID.replaceAll("\\r\\n", ""));

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

            dlDialog = ProgressDialog.show(OutDlvIssuanceActivity.this, "Please wait", "Checking Scanned Item...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);
            String strAlertMsg = "";
            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(OutDlvIssuanceActivity.this).setMessage(strMsg)
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
                        if (!jsonResponse.getString("ResultTrans").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("ResultTrans");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                            String strMsg, strHUID, Qty;
                            strHUID = jsonChildNode.optString("HUID");
                            Qty = jsonChildNode.optString("SrcPlantQty");

                            etHUID.setText(strHUID);
                            addtoBarcodeList(listBarcode, strHUID, Qty);
                            addToListView();
                        } else {
                            strAlertMsg = "Invalid HU scanned.";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    strAlertMsg = "Invalid HU scanned.";
                }

                if (strAlertMsg.length() > 0) {
                    alrtLog = new AlertDialog.Builder(OutDlvIssuanceActivity.this).setMessage(strAlertMsg)
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

    private class PHPRemoveScanItem extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "RemoveScanItemODIssuance.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("GUID", strGUID.replaceAll("\\r\\n", ""));
                    jObjectList.put("DlvNo", GlobalVariables.gblDlvNo.replaceAll("\\r\\n", ""));
                    jObjectList.put("HUID", strScanItem.replaceAll("\\r\\n", ""));
                    jObjectList.put("UserId", GlobalVariables.gblUserID.replaceAll("\\r\\n", ""));

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

            dlDialog = ProgressDialog.show(OutDlvIssuanceActivity.this, "Please wait", "Checking Scanned Item...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);
            String strAlertMsg = "";
            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(OutDlvIssuanceActivity.this).setMessage(strMsg)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
            } else {
                removeFromListView();
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

    private class PHPUpdateScanItem extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "UpdateScanItemODIssuance.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("GUID", strGUID.replaceAll("\\r\\n", ""));
                    jObjectList.put("DlvNo", GlobalVariables.gblDlvNo.replaceAll("\\r\\n", ""));
                    jObjectList.put("HUID", strUpdHUID.replaceAll("\\r\\n", ""));
                    jObjectList.put("Qty", strNewQty.replaceAll("\\r\\n", ""));

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

            dlDialog = ProgressDialog.show(OutDlvIssuanceActivity.this, "Please wait", "Checking Scanned Item...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);
            String strAlertMsg = "";
            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(OutDlvIssuanceActivity.this).setMessage(strMsg)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
            } else {
                updateListView(strUpdHUID, strNewQty);
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

    private class PHPODIssue extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";
        Boolean bSuccess = false;

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "ConfirmODIssuance.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    //urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    //urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("GUID", strGUID.replaceAll("\\r\\n", ""));
                    jObjectList.put("DlvNo", GlobalVariables.gblDlvNo.replaceAll("\\r\\n", ""));
                    jObjectList.put("UserId", GlobalVariables.gblUserID.replaceAll("\\r\\n", ""));
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

            dlDialog = ProgressDialog.show(OutDlvIssuanceActivity.this, "Please wait", "Processing Data...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);
            String strAlertMsg = "";

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(OutDlvIssuanceActivity.this).setMessage(strMsg)
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
                        if (!jsonResponse.getString("ResultTrans").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("ResultTrans");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                            String chkResult = jsonChildNode.optString("chkResult");
                            String chkMsg = jsonChildNode.optString("chkMsg");

                            if (chkResult.equals("0")) {
                                strAlertMsg = "Posting failed.";
                            } else {
                                strAlertMsg = chkMsg;
                                bSuccess = true;
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
                    alrtLog = new AlertDialog.Builder(OutDlvIssuanceActivity.this).setMessage(strAlertMsg)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (bSuccess)
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


}
