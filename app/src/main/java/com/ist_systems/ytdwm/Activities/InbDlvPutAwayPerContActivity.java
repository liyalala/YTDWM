package com.ist_systems.ytdwm.Activities;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
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
import android.support.design.widget.TextInputLayout;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ist_systems.ytdwm.Fragments.SummaryFragment;
import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.ListViewAndAdapters.BarcodeListPutAway;
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

public class InbDlvPutAwayPerContActivity extends AppCompatActivity {

    private static int sessionDepth = 0;
    AlertDialog alrtLog;
    ProgressDialog dlDialog;
    TextInputLayout tilBin;
    TextInputLayout tilNewHUID;
    TextInputLayout tilHUID;
    AutoCompleteTextView etBinCd;
    EditText etHU;
    EditText etHLHU;
    Button btConfirmTO;
    Button btSummary;
    ImageView imgDelete;
    ListView lvputAway;
    String strScanObj = "";
    String strBinCd = "";
    String strHLHUID = "";
    String strScanItem = "";
    String strContVessel = "";
    Boolean onDeleteMode = false;
    Boolean destroy = true;
    List<Map<String, String>> listMap;
    List<String> lBin = new ArrayList<>();
    SharedPreferences prefs;
    private SimpleAdapter adapter = null;
    private List<BarcodeListPutAway> listBarcode = new ArrayList<>();
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

                    switch (strScanObj) {
                        case "BinCd":
                            etBinCd.setText(strScanItem);
                            break;
                        case "HU":
                            etHU.setText(strScanItem);
                            break;
                        case "HLHU":
                            etHLHU.setText(strScanItem);
                            break;
                    }

                    DoScan();
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbdlv_putaway1);

        prefs = PreferenceManager.getDefaultSharedPreferences(InbDlvPutAwayPerContActivity.this.getApplicationContext());

        Intent i = getIntent();
        String strTranNo = i.getStringExtra("TranNo");
        setTitle(strTranNo + " - Put Away");
        strContVessel = strTranNo;

        etBinCd = findViewById(R.id.etIDBin);
        etHU = findViewById(R.id.etIDHU);
        etHLHU = findViewById(R.id.etNewHLHUID);
        lvputAway = findViewById(R.id.lvPutAway);
        btConfirmTO = findViewById(R.id.btIDConfirm);
        btSummary = findViewById(R.id.btIDSummary);
        imgDelete = findViewById(R.id.imgDelete);
        tilBin = findViewById(R.id.tilBin);
        tilNewHUID = findViewById(R.id.tilHLHUID);
        tilHUID = findViewById(R.id.tilIDHU);

        Initialize();

        etBinCd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strScanObj = "BinCd";
                    tilBin.setBackgroundResource(R.drawable.et_focused);
                } else
                    tilBin.setBackgroundResource(0);
            }
        });

        etBinCd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    strScanItem = etBinCd.getText().toString();
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
                    tilHUID.setBackgroundResource(R.drawable.et_focused);
                } else
                    tilHUID.setBackgroundResource(0);
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

        etHLHU.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strScanObj = "HLHU";
                    tilNewHUID.setBackgroundResource(R.drawable.et_focused);
                } else
                    tilNewHUID.setBackgroundResource(0);
            }
        });

        etHLHU.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    strScanItem = etHLHU.getText().toString();
                    DoScan();

                    return true;
                }
                return false;
            }
        });

        btConfirmTO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int iTotalHUScanned = listBarcode.size();

                if (iTotalHUScanned > 0) {
                    new PHPConfirmTO().execute();
                    /*if ((new CheckNetwork(InbDlvPutAway1Activity.this)).isConnectingToInternet()) {
                        new PHPConfirmTO().execute();
                    } else {
                        alrtLog = new AlertDialog.Builder(InbDlvPutAway1Activity.this).setMessage("Network Connection failed.")
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

        btSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SummaryFragment dialog = SummaryFragment.newInstance(listBarcode, "PutAway");
                dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                dialog.show(getSupportFragmentManager(), "PutAway");
            }
        });

        new PHPOnloadPutAway().execute();
        /*if ((new CheckNetwork(InbDlvPutAway1Activity.this)).isConnectingToInternet()) {
            new PHPOnloadPutAway().execute();
        } else {
            alrtLog = new AlertDialog.Builder(InbDlvPutAway1Activity.this)
                    .setMessage("Network Connection failed.")
                    .setNegativeButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                    .setCancelable(false)
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

                if (listBarcode.size() == 0) {
                    alrtLog = new AlertDialog.Builder(InbDlvPutAwayPerContActivity.this).setMessage("No Data Found.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {
                    btConfirmTO.setEnabled(false);
                    imgDelete.setVisibility(View.VISIBLE);
                    onDeleteMode = true;
                    etHU.requestFocus();
                    etBinCd.setEnabled(false);
                    etHLHU.setEnabled(false);
                }
            } else {
                btConfirmTO.setEnabled(true);
                imgDelete.setVisibility(View.INVISIBLE);
                onDeleteMode = false;
                etBinCd.setEnabled(true);
                etHLHU.setEnabled(true);
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

                        Intent intent = new Intent(InbDlvPutAway1Activity.this, LoginActivity.class);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("YTLog " + this.getClass().getSimpleName(), Integer.toString(keyCode));

        if (keyCode == 133 || keyCode == 135) {
            scanThread.scan();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void Initialize() {
        prefs = PreferenceManager.getDefaultSharedPreferences(InbDlvPutAwayPerContActivity.this.getApplicationContext());
        if (prefs.contains("prefKeyboard")) {
            if (prefs.getString("prefKeyboard", "true").equals("true")) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                etBinCd.setInputType(InputType.TYPE_CLASS_TEXT);
                etHU.setInputType(InputType.TYPE_CLASS_TEXT);
                etHLHU.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                etBinCd.setInputType(InputType.TYPE_NULL);
                etHU.setInputType(InputType.TYPE_NULL);
                etHLHU.setInputType(InputType.TYPE_NULL);
            }
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            etBinCd.setInputType(InputType.TYPE_NULL);
            etHU.setInputType(InputType.TYPE_NULL);
            etHLHU.setInputType(InputType.TYPE_NULL);
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
        strBinCd = etBinCd.getText().toString();
        strHLHUID = etHLHU.getText().toString();

        if (!onDeleteMode) {
            if ((strScanObj.equals("HU") || strScanObj.equals("HLHU")) && strBinCd.length() == 0) {
                alrtLog = new AlertDialog.Builder(InbDlvPutAwayPerContActivity.this).setMessage("Bin is required.")
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
            } else {
                Log.d("YTLog " + this.getClass().getSimpleName(), "DoScan: ".concat(strScanObj));

                if (strScanObj.equals("HU")) {
                    if (checkHUExists(listBarcode, strScanItem)) {
                        new PHPCheckScanItem().execute();
                    } else
                        Toast.makeText(InbDlvPutAwayPerContActivity.this, "Item doesn't exists or already scanned.", Toast.LENGTH_LONG).show();
                } else {
                    new PHPCheckScanItem().execute();
                }
            }
        } else {
            if (checkHUExists(listBarcode, strScanItem)) {
                new PHPRemoveScanItem().execute();
                updateListView("", "", strScanItem);
            } else
                Toast.makeText(InbDlvPutAwayPerContActivity.this, "Item doesn't exists.", Toast.LENGTH_LONG).show();
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

    private boolean checkHUExists(List<BarcodeListPutAway> list, String HU) { // if HU exists and Bin is already assigned.
        for (int i = 0; i < list.size(); i++) {
            if (!onDeleteMode) {
                if (HU.equals(list.get(i).getHU()) && (list.get(i).getBinCd().equals("")))
                    return true;
                if (HU.equals(list.get(i).getOuterPkg()) && (list.get(i).getBinCd().equals("")))
                    return true;
            } else {
                if (HU.equals(list.get(i).getHU()) && (!list.get(i).getBinCd().equals("")))
                    return true;
                if (HU.equals(list.get(i).getOuterPkg()) && (!list.get(i).getBinCd().equals("")))
                    return true;
            }
        }
        return false;
    }

    private void addtoBarcodeList(List<BarcodeListPutAway> list, String OuterPkg, String HU, String HUType) {

        if (HUType.equals("OuterPkg"))
            list.add(new BarcodeListPutAway(strBinCd, "", HU, strHLHUID));
        else
            list.add(new BarcodeListPutAway(strBinCd, HU, OuterPkg, strHLHUID));

        arrHU.add(HU);
    }

    private void addToListView() {
        listMap = new ArrayList<>();
        int id = 1;

        for (BarcodeListPutAway barcode : listBarcode) {
            Map<String, String> map = new HashMap<>();
            map.put("id", id + "");
            map.put("bincd", barcode.getBinCd());
            map.put("hu", barcode.getHU());
            map.put("outpkg", barcode.getOuterPkg());
            map.put("hlhu", barcode.getHLHUID());
            listMap.add(map);

            id++;
        }

        adapter = new SimpleAdapter(this, listMap, R.layout.listview_putaway,
                new String[]{"bincd", "hu", "outpkg", "hlhu"}, new int[]{
                R.id.tvIDBin,
                R.id.tvIDHU,
                R.id.tvIDOuterPkg,
                R.id.tvHLHUID});

        lvputAway.setAdapter(adapter);
    }

    private void updateListView(String strNewBin, String strNewTrack, String strHU) {
        boolean bUpdate = false;
        int id = 1;

        int iInd = arrHU.indexOf(strHU);
        if (iInd >= 0) {
            //if (listBarcode.get(iInd).getHLHUID().equals("")) { // only update if HLHUID is null.
            listBarcode.get(iInd).setBinCd(strNewBin);
            listBarcode.get(iInd).setHLHUID(strNewTrack);

            bUpdate = true;
            //}
        } else {
            ArrayList<String> iUpdate = new ArrayList<>();
            for (BarcodeListPutAway barcode : listBarcode) {
                if (barcode.getOuterPkg().equals(strHU)) {
                    iUpdate.add(barcode.getHU());
                }
            }

            int iIndOP;
            for (int i = 0; i < iUpdate.size(); i++) {
                iIndOP = arrHU.indexOf(iUpdate.get(i));
                if (iIndOP >= 0) {
                    listBarcode.get(iIndOP).setBinCd(strNewBin);
                    listBarcode.get(iIndOP).setHLHUID(strNewTrack);

                    bUpdate = true;
                }
            }
        }

        if (bUpdate) {
            listMap.clear();
            for (BarcodeListPutAway barcode : listBarcode) {
                Map<String, String> map = new HashMap<>();
                map.put("id", id + "");
                map.put("bincd", barcode.getBinCd());
                map.put("hu", barcode.getHU());
                map.put("outpkg", barcode.getOuterPkg());
                map.put("hlhu", barcode.getHLHUID());
                listMap.add(map);

                id++;
            }

            adapter = new SimpleAdapter(this, listMap, R.layout.listview_putaway,
                    new String[]{"bincd", "hu", "outpkg", "hlhu"}, new int[]{
                    R.id.tvIDBin,
                    R.id.tvIDHU,
                    R.id.tvIDOuterPkg,
                    R.id.tvHLHUID});

            lvputAway.setAdapter(adapter);
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

    private class PHPOnloadPutAway extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "OnloadPutAwayContVessel.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("ContVessel", strContVessel);
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

            if (scanThread != null) {
                scanThread.interrupt();
                scanThread.close();
            }

            unregisterReceiver();

            dlDialog = ProgressDialog.show(InbDlvPutAwayPerContActivity.this, "Please wait", "Checking data...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(InbDlvPutAwayPerContActivity.this).setMessage(strMsg)
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
                        if (!jsonResponse.getString("ScanItems").equals("null")) {
                            JSONArray jsonSecNode = jsonResponse.optJSONArray("ScanItems");
                            String DestBin;
                            String DstHUID;
                            String HLHUID;
                            String NewHLHUID;

                            for (int i = 0; i < jsonSecNode.length(); i++) {
                                JSONObject jsonSecNode1 = jsonSecNode.getJSONObject(i);
                                DestBin = jsonSecNode1.optString("DestBin");
                                DstHUID = jsonSecNode1.optString("DstHUID");
                                HLHUID = jsonSecNode1.optString("HLHUID");
                                NewHLHUID = jsonSecNode1.optString("NewHLHUID");

                                strBinCd = DestBin;
                                strHLHUID = NewHLHUID;

                                addtoBarcodeList(listBarcode, HLHUID, DstHUID, "HU");
                                addToListView();
                            }
                        }

                        if (!jsonResponse.getString("BinCd").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("BinCd");
                            String BinCd;

                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                BinCd = jsonChildNode.optString("BinCd");

                                lBin.add(BinCd);
                            }
                        }

                        if (lBin.size() > 0) {
                            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                                    (InbDlvPutAwayPerContActivity.this, android.R.layout.select_dialog_item, lBin);
                            etBinCd.setAdapter(adapter1);
                            etBinCd.setThreshold(1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    strAlertMsg = "Error Encountered.";
                }

                if (strAlertMsg.length() > 0) {
                    alrtLog = new AlertDialog.Builder(InbDlvPutAwayPerContActivity.this).setMessage(strAlertMsg)
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

    private class PHPCheckScanItem extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "CheckScanItemPutAwayContVessel.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("ObjRef", strScanObj.replaceAll("\\r\\n", ""));
                    jObjectList.put("ScanItem", strScanItem.replaceAll("\\r\\n", ""));
                    jObjectList.put("Bin", strBinCd.replaceAll("\\r\\n", ""));
                    jObjectList.put("HLHUID", strHLHUID.replaceAll("\\r\\n", ""));
                    jObjectList.put("ContVessel", strContVessel);
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

            if (scanThread != null) {
                scanThread.interrupt();
                scanThread.close();
            }

            unregisterReceiver();

            dlDialog = ProgressDialog.show(InbDlvPutAwayPerContActivity.this, "Please wait", "Checking Scanned Item...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);
            String strAlertMsg = "";
            String strHUTyp = "";
            String strTrk = "";

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(InbDlvPutAwayPerContActivity.this).setMessage(strMsg)
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
                        if (!jsonResponse.getString("CheckScanItem").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("CheckScanItem");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                            int fld = jsonChildNode.optInt("Val");
                            strHUTyp = jsonChildNode.optString("HUTyp");
                            String strOuterPkg = jsonChildNode.optString("OuterPkg");
                            strTrk = jsonChildNode.optString("TRack");

                            if ((fld > 0)) {
                                switch (strHUTyp) {
                                    case "BinCd":
                                        strBinCd = strScanItem;
                                        etBinCd.setText(strScanItem);
                                        break;
                                    case "HLHU":
                                        strHLHUID = strScanItem;
                                        etHLHU.setText(strScanItem);
                                        break;
                                    case "OuterPkg":
                                    case "HU":
                                        if (strBinCd.length() > 0) {
                                            etHU.setText(strScanItem);
                                            switch (strHUTyp) {
                                                case "HU":
                                                    updateListView(strBinCd, strHLHUID, strScanItem);
                                                    break;
                                                case "OuterPkg":
                                                    if (!jsonResponse.getString("HUIDList").equals("null")) {
                                                        JSONArray jsonSecNode = jsonResponse.optJSONArray("HUIDList");
                                                        String HUID;

                                                        for (int i = 0; i < jsonSecNode.length(); i++) {
                                                            JSONObject jsonSecNode1 = jsonSecNode.getJSONObject(i);
                                                            HUID = jsonSecNode1.optString("HUID");

                                                            updateListView(strBinCd, strOuterPkg, HUID);
                                                        }
                                                    }
                                                    break;
                                            }
                                        }
                                }
                            } else {
                                if (strScanObj.equals("HU") && strHLHUID.length() == 0) {
                                    strAlertMsg = strHUTyp.concat(" is required.");
                                } else {
                                    strAlertMsg = "Invalid " + strHUTyp + " scanned.";
                                }
                            }
                        } else {
                            strAlertMsg = "Invalid " + strHUTyp + " scanned.";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    strAlertMsg = "Invalid " + strHUTyp + " scanned.";
                }

                if (strAlertMsg.length() > 0) {
                    alrtLog = new AlertDialog.Builder(InbDlvPutAwayPerContActivity.this).setMessage(strAlertMsg)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                }

                switch (strHUTyp) {
                    case "BinCd":
                        if (strAlertMsg.length() > 0) {
                            etBinCd.setText("");
                            etBinCd.requestFocus();
                        } else {
                            etHLHU.setText("");
                            etHU.setText("");
                            etHU.requestFocus();

                            if (etHLHU.getText().length() == 0 && strTrk.length() > 0 && (!strTrk.equals("null")))
                                etHLHU.setText(strTrk);
                        }
                        break;
                    case "HU":
                        etHU.requestFocus();
                        break;
                    case "OuterPkg":
                    case "HLHU":
                    case "TRK":
                        if (strAlertMsg.length() > 0) {
                            etHLHU.setText("");
                            etHLHU.requestFocus();
                        } else
                            etHU.requestFocus();
                        break;
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
                URL url = new URL(GlobalVariables.gblURL + "RemoveScanItemPutAwayContVessel.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("ObjRef", strScanObj.replaceAll("\\r\\n", ""));
                    jObjectList.put("ScanItem", strScanItem.replaceAll("\\r\\n", ""));
                    jObjectList.put("Bin", strBinCd.replaceAll("\\r\\n", ""));
                    jObjectList.put("ContVessel", strContVessel);
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

            if (scanThread != null) {
                scanThread.interrupt();
                scanThread.close();
            }

            unregisterReceiver();

            dlDialog = ProgressDialog.show(InbDlvPutAwayPerContActivity.this, "Please wait", "Checking Scanned Item...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(InbDlvPutAwayPerContActivity.this).setMessage(strMsg)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
            } else {
                if (strScanObj.equals("HU"))
                    etHU.setText(strScanItem);
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
        Boolean bSuccess = false;

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "ConfirmPutAwayContVessel.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("ContVessel", strContVessel);
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

            if (scanThread != null) {
                scanThread.interrupt();
                scanThread.close();
            }

            unregisterReceiver();

            dlDialog = ProgressDialog.show(InbDlvPutAwayPerContActivity.this, "Please wait", "Processing Data...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);
            String strAlertMsg = "";

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(InbDlvPutAwayPerContActivity.this).setMessage(strMsg)
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

                            if (strResultCd.equals("0")) {
                                strAlertMsg = strDesc;
                            } else {
                                strAlertMsg = "Successfully confirmed " + strContVessel + ".";
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
                    alrtLog = new AlertDialog.Builder(InbDlvPutAwayPerContActivity.this).setMessage(strAlertMsg)
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
