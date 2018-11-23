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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ist_systems.ytdwm.Fragments.InbDlvViewPendingHUFragment;
import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.ListViewAndAdapters.BarcodeListRcvHU;
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

public class InbDlvRcvHUPerContActivity extends AppCompatActivity {

    private static int sessionDepth = 0;
    AlertDialog alrtLog;
    ProgressDialog dlDialog;
    TextView tvTotPkg;
    TextView tvHUScannedPerOuterPkg;
    TextView tvTotalHUScanned;
    TextInputLayout tilOuterPkg;
    TextInputLayout tilHU;
    EditText etOuterPkg;
    EditText etHU;
    ListView lvScannedItems;
    Button btDlvRcv;
    Button btViewPending;
    ImageView imgDelete;
    String strScanObj = "";
    String strScanItem = "";
    String strOuterPkg = "";
    Boolean onDeleteMode = false;
    Boolean destroy = true;
    Boolean blnOnloadItems = false;
    String splitDlv = "0";
    int HUScannedPerOuterPkg = 0;
    int TotalHUScanned = 0;
    SharedPreferences prefs;
    private SimpleAdapter adapter = null;
    private List<BarcodeListRcvHU> listBarcode = new ArrayList<>();
    private ArrayList<String> arrHU = new ArrayList<>();
    private ScanThread scanThread;
    //private Timer scanTimer = null;
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

                    DoScan();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbdlv_rcvhupercontainer);

        prefs = PreferenceManager.getDefaultSharedPreferences(InbDlvRcvHUPerContActivity.this.getApplicationContext());

        String strTitle = GlobalVariables.gblContVessel + " - " + GlobalVariables.gblDlvStatus;
        setTitle(strTitle);

        etOuterPkg = findViewById(R.id.etIDOutPkg);
        etHU = findViewById(R.id.etIDHU);
        tvTotPkg = findViewById(R.id.tvIDTotNoPkgVal);
        tvHUScannedPerOuterPkg = findViewById(R.id.tvIDRNoPkg);
        tvTotalHUScanned = findViewById(R.id.tvIDTotRcvPkgVal);
        lvScannedItems = findViewById(R.id.lvScanned);
        btDlvRcv = findViewById(R.id.btIDRcvHU);
        btViewPending = findViewById(R.id.btViewPend);
        imgDelete = findViewById(R.id.imgDelete);
        tilOuterPkg = findViewById(R.id.tilOuterPkg);
        tilHU = findViewById(R.id.tilHU);

        Initialize();

        tvHUScannedPerOuterPkg.setText("0");
        tvTotalHUScanned.setText("0");

        etOuterPkg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    /*if (scanTimer != null) {
                        scanTimer.cancel();
                    }*/

                    strScanObj = "OuterPkg";
                    //scanThread.scan();
                    tilOuterPkg.setBackgroundResource(R.drawable.et_focused);
                } else
                    tilOuterPkg.setBackgroundResource(0);
            }
        });

        etOuterPkg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    strScanItem = etOuterPkg.getText().toString();
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
                    /*scanTimer = new Timer();
                    scanTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            if (scanThread != null) {
                                strScanObj = "HU";
                                scanThread.scan();
                            }

                        }
                    }, 100, 200);*/

                    strScanObj = "HU";
                    //scanThread.scan();
                    tilHU.setBackgroundResource(R.drawable.et_focused);
                } else
                    tilHU.setBackgroundResource(0);
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

        btViewPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InbDlvViewPendingHUFragment dialog = InbDlvViewPendingHUFragment.newInstance(GlobalVariables.gblContVessel, "ContVessel");
                dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                dialog.show(getSupportFragmentManager(), "PendingHU");
            }
        });

        btDlvRcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int iTotalHUScanned = Integer.parseInt(tvTotalHUScanned.getText().toString());
                int iTotPkg = Integer.parseInt(tvTotPkg.getText().toString());

                if (iTotalHUScanned > 0) {
                    if (iTotalHUScanned < iTotPkg) {
                        alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage("Not all items were scanned. Split Delivery?")
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                splitDlv = "1";

                                                new PHPSetDlvRcvd().execute();
                                                /*if ((new CheckNetwork(InbDlvRcvHUActivity.this)).isConnectingToInternet()) {
                                                    new PHPSetDlvRcvd().execute();
                                                } else {
                                                    alrtLog = new AlertDialog.Builder(InbDlvRcvHUActivity.this).setMessage("Network Connection failed.")
                                                            .setNegativeButton("Ok",
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                        }
                                                                    })
                                                            .show();
                                                }*/
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                .setCancelable(false)
                                .show();
                    } else {
                        splitDlv = "0";

                        new PHPSetDlvRcvd().execute();
                        /*if ((new CheckNetwork(InbDlvRcvHUActivity.this)).isConnectingToInternet()) {
                            new PHPSetDlvRcvd().execute();
                        } else {
                            alrtLog = new AlertDialog.Builder(InbDlvRcvHUActivity.this).setMessage("Network Connection failed.")
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
            }
        });

        new PHPGetDetails().execute();
        /*if ((new CheckNetwork(InbDlvRcvHUActivity.this)).isConnectingToInternet()) {
            new PHPGetDetails().execute();
        } else {
            alrtLog = new AlertDialog.Builder(InbDlvRcvHUActivity.this).setMessage("Network Connection failed.")
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

            /*if (GlobalVariables.gblDlvStatusCd.equals("01")) {
                alrtLog = new AlertDialog.Builder(InbDlvRcvHUActivity.this).setMessage("Status should be For RF Receiving")
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
            }*/

            if (!onDeleteMode) {
                if (listBarcode.size() == 0) {
                    alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage("No Data Found.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {
                    btDlvRcv.setEnabled(false);

                    imgDelete.setVisibility(View.VISIBLE);
                    onDeleteMode = true;

                    etHU.requestFocus();
                    etOuterPkg.setEnabled(false);
                }
            } else {
                btDlvRcv.setEnabled(true);

                imgDelete.setVisibility(View.INVISIBLE);
                onDeleteMode = false;

                etOuterPkg.setEnabled(true);
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

                        Intent intent = new Intent(InbDlvRcvHUActivity.this, LoginActivity.class);
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

        /*if (scanTimer != null) {
            scanTimer.cancel();
        }*/

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

        /*if (scanTimer != null) {
            scanTimer.cancel();
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
            String strTotNoPkgs = tvTotPkg.getText().toString();
            if ((!onDeleteMode) && Integer.toString(TotalHUScanned).equals(strTotNoPkgs)) {
                alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage("All items are already scanned. Continue with Delivery Receive?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new PHPSetDlvRcvd().execute();
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                        .setCancelable(false)
                        .show();
            } else {
                scanThread.scan();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void Initialize() {
        prefs = PreferenceManager.getDefaultSharedPreferences(InbDlvRcvHUPerContActivity.this.getApplicationContext());
        if (prefs.contains("prefKeyboard")) {
            if (prefs.getString("prefKeyboard", "true").equals("true")) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                etOuterPkg.setInputType(InputType.TYPE_CLASS_TEXT);
                etHU.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                etOuterPkg.setInputType(InputType.TYPE_NULL);
                etHU.setInputType(InputType.TYPE_NULL);
            }
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            etOuterPkg.setInputType(InputType.TYPE_NULL);
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
        /*if (!(new CheckNetwork(InbDlvRcvHUActivity.this)).isConnectingToInternet()) {
            alrtLog = new AlertDialog.Builder(InbDlvRcvHUActivity.this).setMessage("Network Connection failed.")
                    .setNegativeButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                    .show();
        } else {*/
        strOuterPkg = etOuterPkg.getText().toString();

        if (!onDeleteMode) {
            if (!checkHUExists(listBarcode, strScanItem))
                new PHPCheckScanItem().execute();
            else
                Toast.makeText(InbDlvRcvHUPerContActivity.this, "Item already scanned.", Toast.LENGTH_LONG).show();
        } else {
            if (checkHUExists(listBarcode, strScanItem)) {
                new PHPRemoveScanItem().execute();
                removeFromListView();
            } else
                Toast.makeText(InbDlvRcvHUPerContActivity.this, "Item doesn't exists.", Toast.LENGTH_LONG).show();
        }

                        /*if (strScanObj == "OuterPkg") {
                            strOuterPkg = data;
                            etOuterPkg.setText(data);
                            //etHU.requestFocus();

                            HUScannedPerOuterPkg = 0;
                        } else if (strScanObj == "HU") {

                            if (strOuterPkg.length() > 0) {
                                etHU.setText(data);
                                addtoBarcodeList(listBarcode, data);
                                addToListView();
                            }
                        }*/
        //}
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

    private boolean checkHUExists(List<BarcodeListRcvHU> list, String HU) {
        for (int i = 0; i < list.size(); i++) {
            if (/*strOuterPkg.equals(list.get(i).getOuterPkg()) && */HU.equals(list.get(i).getHU()))
                return true;
        }
        return false;
    }

    private void addtoBarcodeList(List<BarcodeListRcvHU> list, String HU) {

        /*HUScannedPerOuterPkg++;
        tvHUScannedPerOuterPkg.setText(String.valueOf(HUScannedPerOuterPkg));*/

        TotalHUScanned++;
        tvTotalHUScanned.setText(String.valueOf(TotalHUScanned));

        list.add(new BarcodeListRcvHU(strOuterPkg, HU));
        arrHU.add(HU);

        String strTotNoPkgs = tvTotPkg.getText().toString();
        if (Integer.toString(TotalHUScanned).equals(strTotNoPkgs)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage("All items are already scanned. Continue with Delivery Receive?")
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            /*if (scanTimer != null) {
                                                scanTimer.cancel();
                                            }*/

                                            new PHPSetDlvRcvd().execute();
                                        }
                                    })
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                            .setCancelable(false)
                            .show();
                }
            });
        }
    }

    private void addToListView() {
        List<Map<String, String>> listMap;
        listMap = new ArrayList<>();
        int id = 1;
        int NoOfHUPerHL = 0;

        for (BarcodeListRcvHU barcode : listBarcode) {
            Map<String, String> map = new HashMap<>();
            map.put("id", id + "");
            map.put("outerpkg", barcode.getOuterPkg());
            map.put("hu", barcode.getHU());
            listMap.add(map);

            id++;

            if (barcode.getOuterPkg().equals(strOuterPkg))
                NoOfHUPerHL++;
        }

        adapter = new SimpleAdapter(this, listMap, R.layout.listview_scanneditems,
                new String[]{"outerpkg", "hu",},
                new int[]{
                        R.id.tvOuterPkg,
                        R.id.tvHU});

        lvScannedItems.setAdapter(adapter);

        if (blnOnloadItems)
            tvHUScannedPerOuterPkg.setText("0");
        else
            tvHUScannedPerOuterPkg.setText(String.valueOf(NoOfHUPerHL));
    }

    private void removeFromListView() {
        List<Map<String, String>> listMap;
        listMap = new ArrayList<>();
        int id = 1;
        int NoOfHUPerHL = 0;

        /*if (HUScannedPerOuterPkg > 0) {
            HUScannedPerOuterPkg--;
            tvHUScannedPerOuterPkg.setText(String.valueOf(HUScannedPerOuterPkg));
        }*/

        if (TotalHUScanned > 0) {
            TotalHUScanned--;
            tvTotalHUScanned.setText(String.valueOf(TotalHUScanned));
        }

        int iInd = arrHU.indexOf(strScanItem);
        if (iInd >= 0) {
            listBarcode.remove(iInd);
            arrHU.remove(iInd);

            for (BarcodeListRcvHU barcode : listBarcode) {
                Map<String, String> map = new HashMap<>();
                map.put("id", id + "");
                map.put("outerpkg", barcode.getOuterPkg());
                map.put("hu", barcode.getHU());
                listMap.add(map);

                id++;

                if (barcode.getOuterPkg().equals(strOuterPkg))
                    NoOfHUPerHL++;
            }

            adapter = new SimpleAdapter(this, listMap, R.layout.listview_scanneditems,
                    new String[]{"outerpkg", "hu",}, new int[]{
                    R.id.tvOuterPkg,
                    R.id.tvHU});

            lvScannedItems.setAdapter(adapter);

            if (blnOnloadItems)
                tvHUScannedPerOuterPkg.setText("0");
            else
                tvHUScannedPerOuterPkg.setText(String.valueOf(NoOfHUPerHL));
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
                    /*if (scanTimer != null) {
                        scanTimer.cancel();
                    }*/

                    String strTotNoPkgs = tvTotPkg.getText().toString();
                    if ((!onDeleteMode) && Integer.toString(TotalHUScanned).equals(strTotNoPkgs)) {
                        alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage("All items are already scanned. Continue with Delivery Receive?")
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                new PHPSetDlvRcvd().execute();
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                .setCancelable(false)
                                .show();
                    } else {
                        scanThread.scan();
                    }
                }
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
                /*alrtLog = new AlertDialog.Builder(InbDlvRcvHUActivity.this).setMessage(strMsg)
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

    private class PHPGetDetails extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "GetIDRcvHUDetContVessel.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("ContVessel", GlobalVariables.gblContVessel);
                    jsonObject.put("UserId", GlobalVariables.gblUserID);
                    jsonObject.put("DeviceId", GlobalVariables.gblDeviceName);
                    String message = jsonObject.toString();
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

            dlDialog = ProgressDialog.show(InbDlvRcvHUPerContActivity.this, "Please wait", "Fetching Data...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage(strMsg)
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
                        Log.e("YTLog " + this.getClass().getSimpleName(), resString);
                        JSONObject jsonResponse = new JSONObject(resString);

                        if (!jsonResponse.getString("IDRcvHU").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("IDRcvHU");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                            String strDlvStatus = jsonChildNode.optString("DlvStatus");
                            String strTotalPkg = jsonChildNode.optString("TotalPkg");

                            setTitle(GlobalVariables.gblContVessel + " - " + strDlvStatus);
                            tvTotPkg.setText(strTotalPkg);
                        } else {
                            alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage("No Data Found.")
                                    .setNegativeButton("Ok",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                }
                                            })
                                    .show();
                        }

                        if (!jsonResponse.getString("IDScan").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("IDScan");
                            String OuterHUID = "", HUID = "";

                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                OuterHUID = jsonChildNode.optString("OuterHUID");
                                HUID = jsonChildNode.optString("HUID");

                                blnOnloadItems = true;
                                strOuterPkg = OuterHUID;
                                addtoBarcodeList(listBarcode, HUID);
                                addToListView();
                            }
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

    private class PHPSetDlvRcvd extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        Boolean bSuccess = false;
        String nDlv = "";
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "SetStatDlvRcvdContVessel.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("ContVessel", GlobalVariables.gblContVessel);
                    jObjectList.put("UserId", GlobalVariables.gblUserID);
                    jObjectList.put("SplitDlv", splitDlv);

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

            dlDialog = ProgressDialog.show(InbDlvRcvHUPerContActivity.this, "Please wait", "Processing Data...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);
            String strAlertMsg = "";

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage(strMsg)
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
                            String strDlvStatusCd = jsonChildNode.optString("DlvStatusCd");
                            String strDesc = jsonChildNode.optString("Desc");
                            nDlv = jsonChildNode.optString("newDlvNo");

                            if (strResultCd.equals("0")) {
                                strAlertMsg = strDesc;
                            } else {
                                GlobalVariables.gblDlvStatusCd = strDlvStatusCd;
                                GlobalVariables.gblDlvStatus = strDesc;

                                setTitle(GlobalVariables.gblContVessel + " - " + strDesc);
                                strAlertMsg = "Status is now " + strDesc + ".";
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

                String sMsgNewDlv = "";
                if (!nDlv.equals("") && !nDlv.equals("null")) {
                    sMsgNewDlv = " New Split Dlv " + nDlv + " generated.";
                }

                if (strAlertMsg.length() > 0) {
                    alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage(strAlertMsg + sMsgNewDlv)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (bSuccess) {
                                                finish();
                                            }
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
                URL url = new URL(GlobalVariables.gblURL + "CheckScanItemContVessel.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("ObjRef", strScanObj.replaceAll("\\r\\n", ""));
                    jObjectList.put("ScanItem", strScanItem.replaceAll("\\r\\n", ""));
                    jObjectList.put("OutPkg", strOuterPkg.replaceAll("\\r\\n", ""));
                    jObjectList.put("ContVessel", GlobalVariables.gblContVessel);
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

            dlDialog = ProgressDialog.show(InbDlvRcvHUPerContActivity.this, "Please wait", "Checking Scanned Item...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);
            String strAlertMsg = "";

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage(strMsg)
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

                        if (strScanObj.equals("HU")) {
                            if (!jsonResponse.getString("CheckMsg").equals("null")) {
                                JSONArray jsonMainNode = jsonResponse.optJSONArray("CheckMsg");
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                                if (!jsonChildNode.optString("Msg").equals("null"))
                                    strAlertMsg = jsonChildNode.optString("Msg");
                            }
                        }

                        if (strAlertMsg.length() == 0) {
                            if (!jsonResponse.getString("CheckScanItem").equals("null")) {
                                JSONArray jsonMainNode = jsonResponse.optJSONArray("CheckScanItem");
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                                int fld = jsonChildNode.optInt("Val");

                                if (fld > 0) {
                                    if (strScanObj.equals("OuterPkg")) {
                                        strOuterPkg = strScanItem;
                                        etOuterPkg.setText(strScanItem);

                                        HUScannedPerOuterPkg = 0;
                                    } else if (strScanObj.equals("HU")) {
                                        //if (strOuterPkg.length() > 0) {
                                        etHU.setText(strScanItem);

                                        blnOnloadItems = false;
                                        addtoBarcodeList(listBarcode, strScanItem);
                                        addToListView();
                                        //}
                                    }
                                } else {
                                    strAlertMsg = "Invalid " + strScanObj + " scanned.";
                                }
                            } else {
                                strAlertMsg = "Invalid " + strScanObj + " scanned.";
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    strAlertMsg = "Invalid " + strScanObj + " scanned.";
                }

                if (strAlertMsg.length() > 0) {
                    alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage(strAlertMsg)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                }
            }

            switch (strScanObj) {
                case "OuterPkg":
                    if (strAlertMsg.length() > 0) {
                        etOuterPkg.setText("");
                        etOuterPkg.requestFocus();
                    } else
                        etHU.requestFocus();
                    break;
                case "HU":
                    if (strAlertMsg.contains("TRK")) {
                        etOuterPkg.setText("");
                        etOuterPkg.requestFocus();
                    } else
                        etHU.requestFocus();
                    break;
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
                URL url = new URL(GlobalVariables.gblURL + "RemoveScanItemContVessel.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jObjectList = new JSONObject();
                    jObjectList.put("ObjRef", strScanObj.replaceAll("\\r\\n", ""));
                    jObjectList.put("ScanItem", strScanItem.replaceAll("\\r\\n", ""));
                    jObjectList.put("OutPkg", strOuterPkg.replaceAll("\\r\\n", ""));
                    jObjectList.put("ContVessel", GlobalVariables.gblContVessel);
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

            dlDialog = ProgressDialog.show(InbDlvRcvHUPerContActivity.this, "Please wait", "Checking Scanned Item...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(InbDlvRcvHUPerContActivity.this).setMessage(strMsg)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .show();
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
