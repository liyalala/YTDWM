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

public class IDPendingHULoader extends AsyncTaskLoader<List<IDPendingHU>> {
    String DlvNo;
    String Module;

    public IDPendingHULoader(Context context, String strDlvNo, String strmodule) {
        super(context);
        DlvNo = strDlvNo;
        Module = strmodule;
    }

    @Override
    public List<IDPendingHU> loadInBackground() {
        List<IDPendingHU> idPendingHUS = new ArrayList<>();

        try {
            String responseString = null;
            String line;

            try {

                JSONObject jObject = new JSONObject();
                jObject.put("DlvNo", DlvNo);
                String sParam = jObject.toString();

                Log.e("YTLog " + this.getClass().getSimpleName(), sParam);
                String strUrl = Module.equals("IDNo") ? GlobalVariables.gblURL + "GetIDPendingScan.php" : GlobalVariables.gblURL + "GetIDPendingScanContVessel.php";

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
            JSONArray jsonMainNode = jsonResponse.optJSONArray("pendingresult");
            String intHUID, pkgNo, dlvqty, uom;

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                intHUID = jsonChildNode.optString("HUID");
                pkgNo = jsonChildNode.optString("PkgNo");
                dlvqty = jsonChildNode.optString("DlvQty");
                uom = jsonChildNode.optString("EntryUOM");

                idPendingHUS.add(new IDPendingHU(intHUID, pkgNo, dlvqty, uom));
            }
        } catch (Exception e) {
            Log.e("YTLog " + this.getClass().getSimpleName(), "JSon" + e.toString());
        }

        return idPendingHUS;
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
