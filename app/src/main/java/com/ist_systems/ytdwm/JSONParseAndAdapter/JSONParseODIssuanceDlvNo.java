package com.ist_systems.ytdwm.JSONParseAndAdapter;

import android.util.Log;

import com.ist_systems.ytdwm.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class JSONParseODIssuanceDlvNo {

    public List<ODIssuanceDlvNoList> getParseJsonWCF(String sName)
    {
        List<ODIssuanceDlvNoList> odIssuanceDlvNoLists = new ArrayList<ODIssuanceDlvNoList>();
        try {

            URL url = new URL(GlobalVariables.gblURL + "GetODIssuanceDlvNo.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
            urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

            URLConnection jc = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
            String line = reader.readLine();

            JSONObject jsonResponse = new JSONObject(line);
            JSONArray jsonArray = jsonResponse.getJSONArray("ODDlvNo");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject r = jsonArray.getJSONObject(i);
                odIssuanceDlvNoLists.add(new ODIssuanceDlvNoList(r.getString("DlvNo")));
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            Log.e("YTLog" + getClass().getSimpleName(), e1.toString());
            e1.printStackTrace();
        }
        return odIssuanceDlvNoLists;

    }

}