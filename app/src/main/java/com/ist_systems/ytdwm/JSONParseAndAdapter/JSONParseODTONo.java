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

public class JSONParseODTONo {



    public List<ODTONoList> getParseJsonWCF(String sName)
    {
        List<ODTONoList> odtoNoLists = new ArrayList<ODTONoList>();
        try {
            String temp=sName.replace(" ", "%20");
            URL url = new URL(GlobalVariables.gblURL + "GetODTONo.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(GlobalVariables.gblTimeOut);
            urlConnection.setReadTimeout(GlobalVariables.gblReadTime);

            URLConnection jc = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
            String line = reader.readLine();

            JSONObject jsonResponse = new JSONObject(line);
            JSONArray jsonArray = jsonResponse.getJSONArray("TONoSearch");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject r = jsonArray.getJSONObject(i);
                odtoNoLists.add(new ODTONoList(r.getString("TONo")));
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            Log.e("YTLog" + getClass().getSimpleName(), e1.toString());
            e1.printStackTrace();
        }
        return odtoNoLists;

    }

}