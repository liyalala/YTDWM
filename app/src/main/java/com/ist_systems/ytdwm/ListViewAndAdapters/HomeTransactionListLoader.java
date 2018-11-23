package com.ist_systems.ytdwm.ListViewAndAdapters;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.ist_systems.ytdwm.GlobalVariables;

import org.json.JSONArray;
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

public class HomeTransactionListLoader extends AsyncTaskLoader<List<HomeTransactionList>> {

    public HomeTransactionListLoader(Context context) {
        super(context);
    }

    @Override
    public List<HomeTransactionList> loadInBackground() {
        List<HomeTransactionList> transactionLists = new ArrayList<>();

        try {
            String responseString = null;

            try {

                JSONObject jObject = new JSONObject();
                jObject.put("PassCode", "letmein");
                String sParam = jObject.toString();

                String strUrl = GlobalVariables.gblURL + "TransactionLog.php";

                URL url = new URL(strUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(sParam.getBytes());
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
            }

            JSONObject jsonResponse = new JSONObject(responseString);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("translog");
            String TransNo, TransTyp, Remarks, CreatedBy, CreatedDt = "", StatusCd;

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                TransNo = jsonChildNode.optString("TransNo");
                TransTyp = jsonChildNode.optString("TransTyp");
                Remarks = jsonChildNode.optString("Remarks");
                CreatedBy = jsonChildNode.optString("CreatedBy");
                CreatedDt = jsonChildNode.optString("CreatedDt");
                StatusCd = jsonChildNode.optString("StatusCd");

                if (!jsonChildNode.isNull("CreatedDt")) {
                    JSONObject jDocDt = jsonChildNode.getJSONObject("CreatedDt");
                    CreatedDt = jDocDt.optString("date").substring(0, 19);
                }

                transactionLists.add(new HomeTransactionList(TransNo, TransTyp, Remarks, CreatedBy, CreatedDt, StatusCd));
                Log.e("YTLog" + this.getClass().getSimpleName(), "");
            }
        } catch (Exception e) {
            Log.e("YTLog " + this.getClass().getSimpleName(), "JSon" + e.toString());
        }

        return transactionLists;
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged())
            forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}