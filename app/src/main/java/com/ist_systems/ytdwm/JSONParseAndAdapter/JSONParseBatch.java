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

public class JSONParseBatch {

    public List<BatchList> getParseJsonWCF(String sName)
    {
        List<BatchList> batchLists = new ArrayList<BatchList>();
        try {

            URL url = new URL(GlobalVariables.gblURL + "GetBatch.php");
            URLConnection jc = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
            String line = reader.readLine();

            JSONObject jsonResponse = new JSONObject(line);
            JSONArray jsonArray = jsonResponse.getJSONArray("BatchSearch");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject r = jsonArray.getJSONObject(i);
                batchLists.add(new BatchList(r.getString("Batch")));
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            Log.e("YTLog" + getClass().getSimpleName(), e1.toString());
            e1.printStackTrace();
        }
        return batchLists;

    }

}