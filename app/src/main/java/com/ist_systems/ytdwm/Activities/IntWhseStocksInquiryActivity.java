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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ist_systems.ytdwm.Fragments.SummaryFragment;
import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.ListViewAndAdapters.StockInquiry;
import com.ist_systems.ytdwm.ListViewAndAdapters.StockInquiryAdapter;
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
import java.util.List;

import cn.pda.scan.ScanThread;

public class IntWhseStocksInquiryActivity extends AppCompatActivity {

    ListView lvbinInquiry;
    StockInquiryAdapter stockInquiryAdapter;
    EditText etBinCd;
    Button btSummary;
    AlertDialog alrtLog;
    ProgressDialog dlDialog;
    List<StockInquiry> bininquiry = new ArrayList<>();
    String ScanType = "";
    SharedPreferences prefs;
    private KeyReceiver keyReceiver;
    private ScanThread scanThread;
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
                    bininquiry.clear();
                    etBinCd.setText(data);
                    new PHPGetBinInquiry().execute();
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intwhse_stocksinquiry);

        setTitle(getResources().getString(R.string.txtStocksInquiry));
        prefs = PreferenceManager.getDefaultSharedPreferences(IntWhseStocksInquiryActivity.this.getApplicationContext());

        lvbinInquiry = findViewById(R.id.lvWhseStock);
        etBinCd = findViewById(R.id.etBinCd);
        btSummary = findViewById(R.id.btSummary);

        etBinCd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    if (etBinCd.getText().length() > 0) {
                        bininquiry.clear();
                        new PHPGetBinInquiry().execute();
                    }

                    return true;
                }
                return false;
            }
        });

        btSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ScanType.equals("HU")) {
                    SummaryFragment dialog = SummaryFragment.newInstance(bininquiry, "StockInquiry", ScanType);
                    dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                    dialog.show(getSupportFragmentManager(), "StockInquiry");
                }
            }
        });

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

        if (scanThread != null) {
            scanThread.interrupt();
            scanThread.close();
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

    private class PHPGetBinInquiry extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "GetBinInquiry.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("BinCd", etBinCd.getText().toString().replaceAll("\\r\\n", ""));
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

            if (scanThread != null) {
                scanThread.interrupt();
                scanThread.close();
            }

            unregisterReceiver();

            dlDialog = ProgressDialog.show(IntWhseStocksInquiryActivity.this, "Please wait", "Fetching data...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(IntWhseStocksInquiryActivity.this).setMessage(strMsg)
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
                        if (!jsonResponse.getString("sType").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("sType");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                            ScanType = jsonChildNode.optString("Type");
                        }

                        if (!jsonResponse.getString("BinInquiry").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("BinInquiry");
                            String MatNo, Color, Batch, HLHUID, HLHUID1, HUID, HUID1, VendorLot, FabTon, AvailQty, Bin, StorAreaCd;

                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                MatNo = jsonChildNode.optString("MatNo");
                                Color = jsonChildNode.optString("Color");
                                StorAreaCd = jsonChildNode.optString("StorAreaCd");
                                Bin = jsonChildNode.optString("BinCd");
                                Batch = jsonChildNode.optString("Batch");
                                HLHUID = jsonChildNode.optString("HLHUID");
                                HLHUID1 = jsonChildNode.optString("HLHUID1");
                                HUID = jsonChildNode.optString("IntHUID");
                                HUID1 = jsonChildNode.optString("HUID1");
                                VendorLot = jsonChildNode.optString("VendorLot");
                                FabTon = jsonChildNode.optString("FabToning");
                                AvailQty = jsonChildNode.optString("AvailQty");

                                bininquiry.add(new StockInquiry(MatNo, Batch, Color, HLHUID, HLHUID1, HUID, HUID1, VendorLot, FabTon, AvailQty, Bin, StorAreaCd));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (bininquiry.size() == 0) {
                        Toast.makeText(IntWhseStocksInquiryActivity.this, "No Data Found.", Toast.LENGTH_LONG).show();
                    }

                    stockInquiryAdapter = new StockInquiryAdapter(IntWhseStocksInquiryActivity.this, bininquiry, ScanType);
                    lvbinInquiry.setAdapter(stockInquiryAdapter);
                } else {
                    Toast.makeText(IntWhseStocksInquiryActivity.this, "No Data Found.", Toast.LENGTH_LONG).show();
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
