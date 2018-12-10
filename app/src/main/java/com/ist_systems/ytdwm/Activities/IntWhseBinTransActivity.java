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
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.JSONParseAndAdapter.DestBinList;
import com.ist_systems.ytdwm.JSONParseAndAdapter.SuggestionAdapterDestBin;
import com.ist_systems.ytdwm.ListViewAndAdapters.BinTransfer;
import com.ist_systems.ytdwm.MainActivity;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pda.scan.ScanThread;

public class IntWhseBinTransActivity extends AppCompatActivity  {

    private static int sessionDepth = 0;
    AlertDialog alrtLog;
    ProgressDialog dlDialog;
    AutoCompleteTextView etDestBin;
    EditText etHUID;
    EditText etNewHLHUID;
    TextInputLayout tilDestBin;
    TextInputLayout tilHUID;
    TextInputLayout tilNewHLHUID;
    ListView lvScanned;
    Button btSubmit;
    Button btReject;
    ImageView imgDelete;
    String strScanObj = "";
    String strScanItem = "";
    String strDestBin = "";
    String strHLHUID = "";
    Boolean onDeleteMode = false;
    Boolean forReject = false;
    SharedPreferences prefs;
    private SimpleAdapter adapter = null;
    private List<BinTransfer> listBarcode = new ArrayList<>();
    private ArrayList<String> arrHU = new ArrayList<>();
    private List<DestBinList> destBin = new ArrayList<>();
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
                        case "DestBin":
                            etDestBin.setText(strScanItem);
                            break;
                        case "HU":
                            etHUID.setText(strScanItem);
                            break;
                        case "HLHU":
                            etNewHLHUID.setText(strScanItem);
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
        setContentView(R.layout.activity_intwhse_bintrans);

        prefs = PreferenceManager.getDefaultSharedPreferences(IntWhseBinTransActivity.this.getApplicationContext());

        String strTitle = getResources().getString(R.string.txtBinTransfer);
        setTitle(strTitle);

        etDestBin = findViewById(R.id.etDestBin);
        etHUID = findViewById(R.id.etHU);
        etNewHLHUID = findViewById(R.id.etNewHLHUID);
        lvScanned = findViewById(R.id.lvScanned);
        btSubmit = findViewById(R.id.btSubmit);
        btReject = findViewById(R.id.btReject);
        imgDelete = findViewById(R.id.imgDelete);
        tilDestBin = findViewById(R.id.tilBin);
        tilHUID = findViewById(R.id.tilHU);
        tilNewHLHUID = findViewById(R.id.tilHLHUID);

        Initialize();


        etDestBin.setAdapter(new SuggestionAdapterDestBin(this,etDestBin.getText().toString()));
        etDestBin.setThreshold(1);

        etDestBin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strScanObj = "DestBin";
                    tilDestBin.setBackgroundResource(R.drawable.et_focused);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    etDestBin.setInputType(InputType.TYPE_CLASS_TEXT);



                } else
                    tilDestBin.setBackgroundResource(0);
            }
        });

        etDestBin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.showSoftInput(etDestBin, InputMethodManager.SHOW_IMPLICIT);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    etDestBin.setInputType(InputType.TYPE_CLASS_TEXT);

                    strScanItem = etDestBin.getText().toString();
                    DoScan();

                    return true;
                }
                return false;
            }
        });

        etNewHLHUID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strScanObj = "HLHUID";
                    tilNewHLHUID.setBackgroundResource(R.drawable.et_focused);
                } else
                    tilNewHLHUID.setBackgroundResource(0);
            }
        });

        etNewHLHUID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    strScanItem = etNewHLHUID.getText().toString();
                    DoScan();

                    return true;
                }
                return false;
            }
        });

        etHUID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strScanObj = "HUID";
                    tilHUID.setBackgroundResource(R.drawable.et_focused);
                } else
                    tilHUID.setBackgroundResource(0);
            }
        });

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
                    alrtLog = new AlertDialog.Builder(IntWhseBinTransActivity.this).setMessage("No Data to Process.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {
                    forReject = false;
                    new PHPDoStockTransfer().execute();
                }
            }
        });

        btReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listBarcode.size() > 0) {
                    alrtLog = new AlertDialog.Builder(IntWhseBinTransActivity.this).setMessage("Transfer to Reject? Please confirm.")
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            forReject = true;
                                            new PHPDoStockTransfer().execute();
                                        }
                                    })
                            .setCancelable(false)
                            .show();
                }
            }
        });
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
                    alrtLog = new AlertDialog.Builder(IntWhseBinTransActivity.this).setMessage("No Data Found.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {
                    btSubmit.setEnabled(false);

                    imgDelete.setVisibility(View.VISIBLE);
                    onDeleteMode = true;

                    etHUID.requestFocus();
                    etDestBin.setEnabled(false);
                    etNewHLHUID.setEnabled(false);
                }
            } else {
                btSubmit.setEnabled(true);

                imgDelete.setVisibility(View.INVISIBLE);
                onDeleteMode = false;

                etDestBin.setEnabled(true);
                etNewHLHUID.setEnabled(true);
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

                        Intent intent = new Intent(IntWhseBinTransActivity.this, LoginActivity.class);
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
        prefs = PreferenceManager.getDefaultSharedPreferences(IntWhseBinTransActivity.this.getApplicationContext());
        if (prefs.contains("prefKeyboard")) {
            if (prefs.getString("prefKeyboard", "true").equals("true")) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                etDestBin.setInputType(InputType.TYPE_CLASS_TEXT);
                etHUID.setInputType(InputType.TYPE_CLASS_TEXT);
                etNewHLHUID.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                etDestBin.setInputType(InputType.TYPE_NULL);
                etHUID.setInputType(InputType.TYPE_NULL);
                etNewHLHUID.setInputType(InputType.TYPE_NULL);
            }
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            etDestBin.setInputType(InputType.TYPE_NULL);
            etHUID.setInputType(InputType.TYPE_NULL);
            etNewHLHUID.setInputType(InputType.TYPE_NULL);
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
        strDestBin = etDestBin.getText().toString();
        strHLHUID = etNewHLHUID.getText().toString();

        if (!onDeleteMode) {
            if ((strScanObj.equals("HUID") || strScanObj.equals("HLHUID")) && strDestBin.length() == 0) {
                alrtLog = new AlertDialog.Builder(IntWhseBinTransActivity.this).setMessage("Bin is required.")
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
            } else {
                Log.d("YTLog " + this.getClass().getSimpleName(), "DoScan: ".concat(strScanObj));

                if (strScanObj.equals("HUID")) {
                    if (checkHUExists(listBarcode, strScanItem)) {
                        Toast.makeText(IntWhseBinTransActivity.this, "Item already scanned.", Toast.LENGTH_LONG).show();
                    } else {
                        new PHPCheckScanItem().execute();
                    }
                } else {
                    new PHPCheckScanItem().execute();
                }
            }
        } else {
            if (checkHUExists(listBarcode, strScanItem)) {
                removeFromListView();
            } else
                Toast.makeText(IntWhseBinTransActivity.this, "Item doesn't exists.", Toast.LENGTH_LONG).show();
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

    private boolean checkHUExists(List<BinTransfer> list, String HU) {
        Log.d("YTLog " + this.getClass().getSimpleName(), HU);
        for (int i = 0; i < list.size(); i++) {
            Log.d("YTLog " + this.getClass().getSimpleName(), list.get(i).getHUID());
            if (HU.equals(list.get(i).getHUID()))
                return true;
        }
        return false;
    }

    private void addtoBarcodeList(List<BinTransfer> list, String HU) {

        list.add(new BinTransfer(strDestBin, strHLHUID, HU));
        arrHU.add(HU);
    }

    private void addToListView() {
        List<Map<String, String>> listMap;
        listMap = new ArrayList<>();
        int id = 1;

        for (BinTransfer barcode : listBarcode) {
            Map<String, String> map = new HashMap<>();
            map.put("id", id + "");
            map.put("destbin", barcode.getDestBin());
            map.put("hlhuid", barcode.getHLHUID());
            map.put("hu", barcode.getHUID());
            listMap.add(map);

            id++;
        }

        adapter = new SimpleAdapter(this, listMap, R.layout.listview_bintransfer,
                new String[]{"destbin", "hlhuid", "hu",},
                new int[]{
                        R.id.tvDestBin,
                        R.id.tvHLHUID,
                        R.id.tvHU});

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

            for (BinTransfer barcode : listBarcode) {
                Map<String, String> map = new HashMap<>();
                map.put("id", id + "");
                map.put("destbin", barcode.getDestBin());
                map.put("hu", barcode.getHUID());
                listMap.add(map);

                id++;
            }

            adapter = new SimpleAdapter(this, listMap, R.layout.listview_bintransfer,
                    new String[]{"destbin", "hu",}, new int[]{
                    R.id.tvDestBin,
                    R.id.tvHU});

            lvScanned.setAdapter(adapter);
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

    private class PHPCheckScanItem extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "CheckScanItemBinTrans.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("ObjRef", strScanObj.replaceAll("\\r\\n", ""));
                    jObjectList.put("ScanItem", strScanItem.replaceAll("\\r\\n", ""));
                    jObjectList.put("Bin", strDestBin.replaceAll("\\r\\n", ""));
                    jObjectList.put("HLHUID", strHLHUID.replaceAll("\\r\\n", ""));

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

            dlDialog = ProgressDialog.show(IntWhseBinTransActivity.this, "Please wait", "Checking Scanned Item...");
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

                alrtLog = new AlertDialog.Builder(IntWhseBinTransActivity.this).setMessage(strMsg)
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
                                    case "DestBin":
                                        strDestBin = strScanItem;
                                        etDestBin.setText(strScanItem);
                                        break;
                                    case "HLHUID":
                                        strHLHUID = strScanItem;
                                        etNewHLHUID.setText(strScanItem);
                                        break;
                                    case "OuterPkg":
                                    case "HU":
                                        if (strDestBin.length() > 0) {
                                            etHUID.setText(strScanItem);
                                            addtoBarcodeList(listBarcode, strScanItem);
                                            addToListView();
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
                    alrtLog = new AlertDialog.Builder(IntWhseBinTransActivity.this).setMessage(strAlertMsg)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                }

                switch (strHUTyp) {
                    case "DestBin":
                        if (strAlertMsg.length() > 0) {
                            etDestBin.setText("");
                            etDestBin.requestFocus();
                        } else {
                            etNewHLHUID.setText("");
                            etHUID.requestFocus();
                            etHUID.setText("");

                            if (etNewHLHUID.getText().length() == 0 && strTrk.length() > 0 && (!strTrk.equals("null")))
                                etNewHLHUID.setText(strTrk);
                        }
                        break;
                    case "HU":
                        etHUID.requestFocus();
                        break;
                    case "OuterPkg":
                    case "HLHUID":
                    case "HLHU":
                    case "TRK":
                        if (strAlertMsg.length() > 0) {
                            etNewHLHUID.setText("");
                            etNewHLHUID.requestFocus();
                        } else
                            etHUID.requestFocus();
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

    private class PHPDoStockTransfer extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";
        String strChk = "";
        String result = "";

        @Override
        protected String doInBackground(String... strings) {
            String responseString = null;

            try {
                URL url = new URL(GlobalVariables.gblURL + "DoStockTransfer.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    BinTransfer ST;
                    JSONArray jArrayPick = new JSONArray();
                    for (int i = 0; i < listBarcode.size(); i++) {
                        ST = listBarcode.get(i);

                        JSONObject jObject = new JSONObject();
                        jObject.put("DestBin", ST.getDestBin());
                        jObject.put("HLHUID", ST.getHLHUID());
                        jObject.put("HUID", ST.getHUID());

                        jArrayPick.put(jObject);
                    }

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("BarcodeList", jArrayPick);
                    jObjectList.put("UserId", GlobalVariables.gblUserID);
                    jObjectList.put("forReject", String.valueOf(forReject));
                    jObjectList.put("DlvNo", "");

                    String message = jObjectList.toString();
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

            dlDialog = ProgressDialog.show(IntWhseBinTransActivity.this, "Please wait", "Processing...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);
            String strAlertMsg = "";

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(IntWhseBinTransActivity.this).setMessage(strMsg)
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
                    Boolean Chk = true;

                    try {
                        JSONObject jsonResponse = new JSONObject(resString);
                        if (!jsonResponse.getString("ResultChk").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("ResultChk");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                            strChk = jsonChildNode.optString("blnChk");
                            result = jsonChildNode.optString("Result");

                            if (strChk.equals("0")) {
                                strAlertMsg = result;
                                Chk = false;
                            }
                        } else {
                            strAlertMsg = "Error Encountered.";
                        }

                        if (Chk) {
                            if (!jsonResponse.getString("ResultTrans").equals("null")) {
                                JSONArray jsonMainNodeTrans = jsonResponse.optJSONArray("ResultTrans");
                                JSONObject jsonChildNodeTrans = jsonMainNodeTrans.getJSONObject(0);

                                strChk = jsonChildNodeTrans.optString("blnChk");
                                result = jsonChildNodeTrans.optString("Result");

                                if (strChk.equals("0")) {
                                    strAlertMsg = result;
                                } else {
                                    if (result.contains("Successfully")) {
                                        strAlertMsg = result;
                                    } else {
                                        strAlertMsg = "TO " + result + " generated.";
                                    }
                                }
                            } else {
                                strAlertMsg = "Error Encountered.";
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    strAlertMsg = "Error Encountered.";
                }

                if (strAlertMsg.length() > 0) {
                    alrtLog = new AlertDialog.Builder(IntWhseBinTransActivity.this).setMessage(strAlertMsg)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (strChk.equals("1"))
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
