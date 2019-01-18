package com.ist_systems.ytdwm.Activities;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ist_systems.ytdwm.Fragments.SummaryFragment;
import com.ist_systems.ytdwm.GlobalVariables;
import com.ist_systems.ytdwm.ListViewAndAdapters.AutoCompleteCustomAdapter;
import com.ist_systems.ytdwm.ListViewAndAdapters.AutoCompleteView;
import com.ist_systems.ytdwm.ListViewAndAdapters.StockInquiry;
import com.ist_systems.ytdwm.ListViewAndAdapters.StockInquiryAdapter;
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
import java.util.ArrayList;
import java.util.List;

public class IntWhseStocksInqMaterialActivity extends AppCompatActivity {

    ListView lvbinInquiry;
    StockInquiryAdapter stockInquiryAdapter;
    AutoCompleteTextView etMatNo;
    AutoCompleteTextView etBatch;
    Button btSummary;
    Button btView;
    AlertDialog alrtLog;
    ProgressDialog dlDialog;
    List<StockInquiry> bininquiry = new ArrayList<>();
    List<String> lADMatId = new ArrayList<>();
    List<String> lBatch = new ArrayList<>();
    String ScanType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intwhse_stocksinqmaterial);

        setTitle(getResources().getString(R.string.txtStocksInqMatNo));

        lvbinInquiry = findViewById(R.id.lvWhseStock);
        etMatNo = findViewById(R.id.etMatNo);
        etBatch = findViewById(R.id.etBatch);
        btSummary = findViewById(R.id.btSummary);
        btView = findViewById(R.id.btView);

        etMatNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    if (etMatNo.getText().length() > 0) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);

                        bininquiry.clear();
                        new PHPGetBinInquiry().execute();
                    }

                    return true;
                }
                return false;
            }
        });

        etBatch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    if (etBatch.getText().length() > 0) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);

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

        btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etMatNo.getText().length() == 0 && etBatch.getText().length() == 0) {
                    alrtLog = new AlertDialog.Builder(IntWhseStocksInqMaterialActivity.this).setMessage("At least one search parameter is required.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                } else {
                    bininquiry.clear();
                    new PHPGetBinInquiry().execute();
                }
            }
        });

        new PHPGetAutoCompleteList().execute();
    }

    private class PHPGetBinInquiry extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "GetStkInquiryMaterial.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("MatNo", etMatNo.getText().toString().replaceAll("\\r\\n", ""));
                jsonObject.put("Batch", etBatch.getText().toString().replaceAll("\\r\\n", ""));
                jsonObject.put("UserId", GlobalVariables.gblUserID);
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

            dlDialog = ProgressDialog.show(IntWhseStocksInqMaterialActivity.this, "Please wait", "Fetching data...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(IntWhseStocksInqMaterialActivity.this).setMessage(strMsg)
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
                        Toast.makeText(IntWhseStocksInqMaterialActivity.this, "No Data Found.", Toast.LENGTH_LONG).show();
                    }

                    stockInquiryAdapter = new StockInquiryAdapter(IntWhseStocksInqMaterialActivity.this, bininquiry, ScanType);
                    lvbinInquiry.setAdapter(stockInquiryAdapter);
                } else {
                    Toast.makeText(IntWhseStocksInqMaterialActivity.this, "No Data Found.", Toast.LENGTH_LONG).show();
                }
            }

            dlDialog.dismiss();

        }
    }

    private class PHPGetAutoCompleteList extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";
        List<AutoCompleteView> phpList = new ArrayList<>();
        List<AutoCompleteView> phplist2 = new ArrayList<>();

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "GetAutoComplete.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("PassCode", "letmein");
                jsonObject.put("sType", "ADMatIDBatch");
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

            dlDialog = ProgressDialog.show(IntWhseStocksInqMaterialActivity.this, "Please wait", "Fetching data...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(IntWhseStocksInqMaterialActivity.this).setMessage(strMsg)
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
                        String ADMatId, Batch;

                        if (!jsonResponse.getString("AdMatId").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("AdMatId");

                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                ADMatId = jsonChildNode.optString("Ad_Mat_Id");

                                //lADMatId.add(ADMatId);
                                phpList.add(new AutoCompleteView(ADMatId));
                            }
                        }

                        if (!jsonResponse.getString("Batch").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("Batch");

                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                Batch = jsonChildNode.optString("Batch");

                                //lBatch.add(Batch);
                                phplist2.add(new AutoCompleteView(Batch));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //if (lADMatId.size() > 0) {
                    if (phpList.size() > 0) {
                        /*ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                                (IntWhseStocksInqMaterialActivity.this, android.R.layout.select_dialog_item, lADMatId);
                        etMatNo.setAdapter(adapter1);
                        etMatNo.setThreshold(1);*/

                        Log.e("YTLog " + this.getClass().getSimpleName(), "IM NOT EMPTY");

                        AutoCompleteCustomAdapter customAdapter = new AutoCompleteCustomAdapter(
                                IntWhseStocksInqMaterialActivity.this,
                                0,
                                phpList);

                        etMatNo.setAdapter(customAdapter);
                        etMatNo.setThreshold(1);
                    }

                    //if (lBatch.size() > 0) {
                    if (phplist2.size() > 0) {
                        /*ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                                (IntWhseStocksInqMaterialActivity.this, android.R.layout.select_dialog_item, lADMatId);
                        etMatNo.setAdapter(adapter1);
                        etMatNo.setThreshold(1);*/

                        Log.e("YTLog " + this.getClass().getSimpleName(), "IM NOT EMPTY");

                        AutoCompleteCustomAdapter customAdapter = new AutoCompleteCustomAdapter(
                                IntWhseStocksInqMaterialActivity.this,
                                0,
                                phplist2);
                        etBatch.setAdapter(customAdapter);
                        etBatch.setThreshold(1);
                    }

                } else {
                    Toast.makeText(IntWhseStocksInqMaterialActivity.this, "No Data Found.", Toast.LENGTH_LONG).show();
                }
            }

            dlDialog.dismiss();

        }
    }
}
