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
 * Created by jmcaceres on 04/10/2017.
 */

public class HomeTaskListLoader extends AsyncTaskLoader<List<HomeTaskList>> {

    public HomeTaskListLoader(Context context) {
        super(context);
    }

    @Override
    public List<HomeTaskList> loadInBackground() {
        List<HomeTaskList> taskLists = new ArrayList<>();

        try {
            String responseString = null;
            String line;

            try {
                String strUser = GlobalVariables.gblUserID;
                JSONObject jObject = new JSONObject();
                jObject.put("PassCode", "letmein");
                jObject.put("UserID",strUser);
                String sParam = jObject.toString();



                String strUrl = GlobalVariables.gblURL + "GetTaskList.php";

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

            JSONObject jsonResponse = new JSONObject(responseString);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("tasklist");
            String TranNo, UpdMonth, UpdDay, Tag, Task, ContNo, Vessel, DlvNo,RsvNo,IONo;

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                TranNo = jsonChildNode.optString("TranNo");
                UpdMonth = jsonChildNode.optString("UpdMonth");
                UpdDay = jsonChildNode.optString("UpdDay");
                Tag = jsonChildNode.optString("Tag");
                Task = jsonChildNode.optString("Task");
                ContNo = jsonChildNode.optString("ContNo");
                Vessel = jsonChildNode.optString("Vessel");
                DlvNo = jsonChildNode.optString("DlvNo");
                RsvNo = jsonChildNode.optString("RsvNo");
                IONo = jsonChildNode.optString("IONo");


                switch (Tag)
                {
                    case "IDRcv1":
                    case "IDPost":
                    case "PutAway1":
                        taskLists.add(new HomeTaskList(TranNo, UpdMonth, UpdDay, Tag, Task, ContNo, Vessel, "","",""));
                        break;
                    case "Picking":
                    case "ODIssuance":
                        taskLists.add(new HomeTaskList(TranNo, UpdMonth, UpdDay, Tag, Task, ContNo, Vessel, DlvNo,RsvNo, IONo));
                        break;

                }

                Log.d("TestLog", TranNo+" "+ DlvNo+" "+RsvNo);
            }
        } catch (Exception e) {
            Log.e("YTLog " + this.getClass().getSimpleName(), "JSon" + e.toString());
        }

        return taskLists;
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
