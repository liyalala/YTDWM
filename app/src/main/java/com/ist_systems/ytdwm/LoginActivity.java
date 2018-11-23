package com.ist_systems.ytdwm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    final private int ReadWriteExternal = 1;
    public volatile boolean parsingComplete = true;
    AutoCompleteTextView etUserID;
    TextInputLayout tilUser;
    TextInputLayout tilPW;
    EditText etPassword;
    Button btLogin;
    TextView tvVersion;
    TextView tvIPAddress;
    CheckBox cbShowPass;
    XmlPullParserFactory xmlPullParserFactory;
    SharedPreferences prefs;
    Cursor cursor;

    AlertDialog alrtLog;
    ProgressDialog dlDialog;

    SQLiteHelper SQLiteHelper;
    SQLiteDatabase SQLiteDatabase;
    CursorAdapter mAdapter;

    PatternLockView mPatternLockView;

    String[] userIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");
        Initialize();

        tvIPAddress = findViewById(R.id.tvIPAddress);
        etUserID = findViewById(R.id.etUserID);
        etPassword = findViewById(R.id.etPassword);
        btLogin = findViewById(R.id.btLogin);
        tvVersion = findViewById(R.id.tvVersion);
        cbShowPass = findViewById(R.id.cbShowPass);
        tilUser = findViewById(R.id.tilUserId);
        tilPW = findViewById(R.id.tilPW);
        mPatternLockView = findViewById(R.id.patternView);

        if (Build.VERSION.SDK_INT >= 23) {
            int WriteExt = ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (WriteExt != PackageManager.PERMISSION_GRANTED) {
                alrtLog = new AlertDialog.Builder(LoginActivity.this).setMessage("You need to allow YT DWM to access Local Storage.")
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, ReadWriteExternal);
                                    }
                                })
                        .setCancelable(false)
                        .show();
            } else {
                CreateFolder();
                ReadConfigFile();
                tvIPAddress.setText(GlobalVariables.gblURL);
            }
        } else {
            CreateFolder();
            ReadConfigFile();
            tvIPAddress.setText(GlobalVariables.gblURL);
        }

        //etUserID.setText("joan");
        //etPassword.setText("joan");

        etUserID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tilUser.setBackgroundResource(R.drawable.et_focused);
                } else {
                    tilUser.setBackgroundResource(0);
                }
            }
        });

        etUserID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUserID.showDropDown();
            }
        });

        etUserID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalVariables.gblUserID = etUserID.getText().toString();

                String strSQl = "SELECT * FROM UserLog WHERE  UserPattern = '1' AND UserId = '" + GlobalVariables.gblUserID + "'";
                cursor = SQLiteDatabase.rawQuery(strSQl, null);
                Log.e("YTLog " + this.getClass().getSimpleName(), strSQl);

                userIDs = new String[cursor.getCount()];

                String sUserIds;
                Integer iCnt = 0;
                if (cursor.moveToFirst()) {
                    do {
                        sUserIds = cursor.getString(0);
                        userIDs[iCnt] = sUserIds;
                        iCnt++;
                        mPatternLockView.setVisibility(View.VISIBLE);
                    }
                    while (cursor.moveToNext());

                }
                cursor.close();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
            }

        });

        etUserID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPatternLockView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPatternLockView.setVisibility(View.INVISIBLE);
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tilPW.setBackgroundResource(R.drawable.et_focused);
                } else {
                    tilPW.setBackgroundResource(0);
                }
            }
        });

        cbShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etUserID.getText().toString().length() == 0 || etPassword.getText().toString().length() == 0) {
                    Toast.makeText(getBaseContext(), "User ID and Password are required!", Toast.LENGTH_LONG).show();
                    return;
                }

                GlobalVariables.gblUserID = etUserID.getText().toString();
                GlobalVariables.gblUserPW = etPassword.getText().toString();

                new PHPLogin().execute();

                /*if ((new CheckNetwork(LoginActivity.this)).isConnectingToInternet()) {
                    GlobalVariables.gblUserID = etUserID.getText().toString();
                    GlobalVariables.gblUserPW = etPassword.getText().toString();

                    new PHPLogin().execute();
                } else {
                    alrtLog = new AlertDialog.Builder(LoginActivity.this).setMessage("Network Connection failed.")
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                            .show();
                }*/

                /*Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();*/
            }
        });

        mPatternLockView.setCorrectStateColor(getResources().getColor(R.color.colorGreen));
        mPatternLockView.setWrongStateColor(getResources().getColor(R.color.colorRed));
        mPatternLockView.setVisibility(View.INVISIBLE);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {

                String pass = PatternLockUtils.patternToString(mPatternLockView, pattern);
                GlobalVariables.gblPattern = pass;
                GlobalVariables.gblUserID = etUserID.getText().toString();
                new PHPPattern().execute();
            }

            @Override
            public void onCleared() {
                Log.d(getClass().getName(), "Pattern has been cleared");
            }
        });

        try {
            PackageInfo currentInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVersion.setText("Version: " + currentInfo.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userIDs.length > 0) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                    (this, android.R.layout.select_dialog_item, userIDs);
            etUserID.setAdapter(adapter1);
            etUserID.setThreshold(1);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            i.putExtra("parentActivity", "Login");
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ReadWriteExternal:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CreateFolder();
                    ReadConfigFile();
                }
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    /*static void sum_up_recursive(ArrayList<Integer> numbers, int target, ArrayList<Integer> partial) {
        int s = 0;
        for (int x: partial) s += x;
        if (s == target)
            Log.e("YTLog Recursive", "sum("+ Arrays.toString(partial.toArray())+")="+target);
        if (s >= target)
            return;
            //Log.e("YTLog Recursive GT", "sum("+ Arrays.toString(partial.toArray())+")="+target);
        for(int i=0;i<numbers.size();i++) {
            ArrayList<Integer> remaining = new ArrayList<Integer>();
            int n = numbers.get(i);
            for (int j=i+1; j<numbers.size();j++) remaining.add(numbers.get(j));
            ArrayList<Integer> partial_rec = new ArrayList<Integer>(partial);
            partial_rec.add(n);
            sum_up_recursive(remaining,target,partial_rec);
        }
    }

    static void sum_up(ArrayList<Integer> numbers, int target) {
        sum_up_recursive(numbers,target,new ArrayList<Integer>());
    }*/

    private void Initialize() {

        /*Integer[] numbers = {2,3,3,2};
        int target = 7;
        sum_up(new ArrayList<Integer>(Arrays.asList(numbers)),target);*/

        try {
            System.loadLibrary("devapi");
            System.loadLibrary("irdaSerialPort");
        } catch (UnsatisfiedLinkError e) {
            Log.e("YTLog " + this.getClass().getSimpleName(), e.toString());
        } catch (Error | Exception e) {
            Log.e("YTLog " + this.getClass().getSimpleName(), e.toString());
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            // to avoid android.os.NetworkOnMainThreadException
            // error occurs after calling CheckNetwork.java and the functions are not enclosed with thread or asynctask
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //reset Global Variables:
        ResetGlobalVariables resetGlobalVars = new ResetGlobalVariables();
        resetGlobalVars.ResetValues();

        //set Global Variables;
        String devicename = Settings.Secure.getString(getContentResolver(), "bluetooth_name");
        GlobalVariables gVars = new GlobalVariables();
        gVars.InitVariables(devicename);

        SQLiteHelper = new SQLiteHelper(this);
        CreateDB();

        prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();

        if (!prefs.contains("prefScanSound")) {
            editor.putString("prefScanSound", String.valueOf(true));
            editor.apply();
        }

        if (!prefs.contains("prefKeyboard")) {
            editor.putString("prefKeyboard", String.valueOf(false));
            editor.apply();
        }

        if ((!prefs.contains("prefSession") && (!prefs.contains("prefSessionVal")))) {
            editor.putString("prefSessionVal", "30");
            editor.putString("prefSession", "30 Minutes");
            editor.apply();
        }

        String strSQl = "SELECT UserId FROM UserLog ORDER BY UserId";
        cursor = SQLiteDatabase.rawQuery(strSQl, null);

        userIDs = new String[cursor.getCount()];

        String sUserIds;
        Integer iCnt = 0;
        if (cursor.moveToFirst()) {
            do {
                sUserIds = cursor.getString(0);
                userIDs[iCnt] = sUserIds;

                iCnt++;
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        // region check screen size, resolution and density

        DisplayMetrics dm = new DisplayMetrics();
        LoginActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        double wi = (double) width / (double) dens;
        double hi = (double) height / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        Log.i("YTLog Check" + "Screen Size ", String.format("%.2f", screenInches));
        Log.i("YTLog Check" + "Resolution ", "{" + width + "," + height + "}");

        String strScreenSize;
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            strScreenSize = "Large screen";
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            strScreenSize = "Normal sized screen";
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            strScreenSize = "Small sized screen";
        } else {
            strScreenSize = "Screen size is neither large, normal nor small";
        }
        Log.i("YTLog Check" + "Screen Size ", strScreenSize);

        //Determine density
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;

        String strDensity;
        if (density == DisplayMetrics.DENSITY_HIGH) {
            strDensity = "Density High " + String.valueOf(density);
        } else if (density == DisplayMetrics.DENSITY_MEDIUM) {
            strDensity = "Density Medium " + String.valueOf(density);
        } else if (density == DisplayMetrics.DENSITY_LOW) {
            strDensity = "Density Low " + String.valueOf(density);
        } else {
            strDensity = "Density is neither large, normal nor small " + String.valueOf(density);
        }
        Log.i("YTLog Check" + "Density ", strDensity);

        //endregion
    }

    private void CreateFolder() {
        GlobalVariables.gblFolerPath = Environment.getExternalStorageDirectory() + File.separator + "YTDWM";
        File folder = new File(GlobalVariables.gblFolerPath);
        if (!folder.exists())
            folder.mkdirs();

        CopyAssets();
    }

    private void CopyAssets() {
        AssetManager assetManager = getAssets();
        File outFile = new File(GlobalVariables.gblFolerPath, "config.xml");
        if (!outFile.exists()) {
            InputStream in;
            OutputStream out;

            try {
                in = assetManager.open("config.xml");
                out = new FileOutputStream(outFile);
                CopyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e("YTLog " + this.getClass().getSimpleName(), "Failed to copy config.xml", e);
            }
        }
    }

    private void CopyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void ReadConfigFile() {
        InputStream iStream;
        try {
            String XMLString = GlobalVariables.gblFolerPath + File.separator + "config.xml";
            iStream = new FileInputStream(XMLString);
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(iStream, null);
            ParseXML(xmlPullParser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ParseXML(XmlPullParser xmlPullParser) {
        int event;
        String text = null;
        String ipAddress = null, folderName = null;
        try {
            event = xmlPullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = xmlPullParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = xmlPullParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (name.equals("ipaddress")) {
                            ipAddress = text;
                        } else if (name.equals("foldername")) {
                            folderName = text;
                        }
                        break;
                }
                event = xmlPullParser.next();
            }
            parsingComplete = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        GlobalVariables.gblURL = ipAddress + "/" + folderName + "/";
        Log.d("YTLog " + this.getClass().getSimpleName(), GlobalVariables.gblURL);
    }

    public void CreateDB() {
        String strSQL;//
        SQLiteHelper = new SQLiteHelper(this);
        SQLiteDatabase = this.openOrCreateDatabase("YTDWMDB", Context.MODE_PRIVATE, null);

        strSQL = GlobalVariables.GetUserLogs();
        SQLiteDatabase.execSQL(strSQL);

        strSQL = GlobalVariables.GetSummary();
        SQLiteDatabase.execSQL(strSQL);
    }

    private class PHPLogin extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";

        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "Login.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                    urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("UserId", GlobalVariables.gblUserID);
                    jsonObject.put("Password", GlobalVariables.gblUserPW);
                    String message = jsonObject.toString();

                    Log.e("YTLog " + this.getClass().getSimpleName(), message);

                    OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
                    os.write(message.getBytes());
                    os.flush();

                    /*InputStream in = new BufferedInputStream(urlc.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    */

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

            dlDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "Logging in...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {

                if (strMsg.contains("Timeout") || strMsg.contains("Connect"))
                    strMsg = "Network Connection Failed.";

                alrtLog = new AlertDialog.Builder(LoginActivity.this).setMessage(strMsg)
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
                        if (!jsonResponse.getString("login").equals("null")) {
                            JSONArray jsonMainNode = jsonResponse.optJSONArray("login");
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                            int fld = jsonChildNode.optInt("XField");

                            Log.d("YTLog " + this.getClass().getSimpleName(), resString);
                            if (fld == 1) {
                                if (prefs.contains("prefSession")) { // reset session
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.remove("prefSession");
                                    editor.apply();
                                }

                                String devicename = Settings.Secure.getString(getContentResolver(), "bluetooth_name");
                                GlobalVariables gVars = new GlobalVariables();
                                gVars.InitVariables(devicename);

                                boolean chkIfExists = true;
                                String strIfExists = "SELECT * FROM UserLog WHERE UserId = '" + GlobalVariables.gblUserID + "'";
                                cursor = SQLiteDatabase.rawQuery(strIfExists, null);
                                if (cursor.getCount() == 0) {
                                    cursor.close();
                                    chkIfExists = false;
                                    Log.e("YTLog " + this.getClass().getSimpleName(), strIfExists);
                                }
                                cursor.close();

                                if (!chkIfExists) {
                                    String strInsert = "INSERT INTO UserLog (UserId) VALUES ('" + GlobalVariables.gblUserID + "')";
                                    SQLiteDatabase.execSQL(strInsert);
                                }

                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                alrtLog = new AlertDialog.Builder(LoginActivity.this).setMessage("Login failed.")
                                        .setNegativeButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                    }
                                                })
                                        .show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            dlDialog.dismiss();
        }
    }

    private class PHPPattern extends AsyncTask<String, Void, String> {
        Boolean bError = false;
        String strMsg = "";


        @Override
        protected String doInBackground(String... strings) {

            String responseString = null;
            String line;

            try {
                URL url = new URL(GlobalVariables.gblURL + "PatternLogin.php");
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

            dlDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "Logging in...");
        }

        @Override
        protected void onPostExecute(String resString) {
            super.onPostExecute(resString);

            if (bError) {
                alrtLog = new AlertDialog.Builder(LoginActivity.this).setMessage(strMsg)
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
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(getBaseContext(), "Invalid pattern.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            alrtLog = new AlertDialog.Builder(LoginActivity.this).setMessage("Login failed.")
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

            mPatternLockView.clearPattern();
            dlDialog.dismiss();
        }
    }
}
