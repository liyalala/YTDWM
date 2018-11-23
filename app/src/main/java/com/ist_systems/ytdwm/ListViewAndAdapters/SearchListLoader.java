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

/**
 * Created by jmcaceres on 3/22/2018.
 */

public class SearchListLoader extends AsyncTaskLoader<List<SearchList>> {

    private String SearchTyp;

    public SearchListLoader(Context context, String strSearchTyp) {
        super(context);

        SearchTyp = strSearchTyp;
    }

    @Override
    public List<SearchList> loadInBackground() {
        List<SearchList> searchLists = new ArrayList<>();

        try {
            String responseString = null;
            String line;

            try {

                JSONObject jObject = new JSONObject();
                jObject.put("PassCode", "letmein");
                jObject.put("SearchTyp", SearchTyp);
                String sParam = jObject.toString();

                String strUrl = GlobalVariables.gblURL + "GetSearchResult.php";

                URL url = new URL(strUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
                urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

                OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(sParam.getBytes());
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
            }

            Log.e("YTLog " + this.getClass().getSimpleName(), responseString);

            JSONObject jsonResponse = new JSONObject(responseString);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("searchresult");
            String TranNo, Typ, Desc, Plant, SLoc, ContNo, Vessel, CreatedBy, CreatedDt, ActDt = "", ReqdDt = "", ERPIONo, DlvNo, RsvNo;

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                TranNo = jsonChildNode.optString("TranNo");
                Typ = jsonChildNode.optString("Typ");
                Desc = jsonChildNode.optString("Desc");
                Plant = jsonChildNode.optString("Plant");
                SLoc = jsonChildNode.optString("SLoc");
                ContNo = jsonChildNode.optString("ContNo");
                Vessel = jsonChildNode.optString("Vessel");
                CreatedBy = jsonChildNode.optString("CreatedBy");
                CreatedDt = jsonChildNode.optString("CreatedDt");
                ERPIONo = jsonChildNode.optString("ERPIONo");
                DlvNo = jsonChildNode.optString("DlvNo");
                RsvNo = jsonChildNode.optString("RsvNo");
                //ActDt = jsonChildNode.optString("ActDlvDt");

                if (!jsonChildNode.isNull("ActDlvDt")) {
                    JSONObject jDocDt = jsonChildNode.getJSONObject("ActDlvDt");
                    ActDt = jDocDt.optString("date").substring(0, 10);
                }

                if (!jsonChildNode.isNull("ReqdDt")) {
                    JSONObject jDocDt = jsonChildNode.getJSONObject("ReqdDt");
                    ReqdDt = jDocDt.optString("date").substring(0, 10);
                }

                searchLists.add(new SearchList(TranNo, Typ, Desc, Plant, SLoc, ContNo, Vessel, CreatedBy, CreatedDt, ActDt, ReqdDt, ERPIONo, DlvNo, RsvNo));
            }
        } catch (Exception e) {
            Log.e("YTLog " + this.getClass().getSimpleName(), "JSon" + e.toString());
        }

        return searchLists;
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
