package com.ist_systems.ytdwm;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jmcaceres on 03/27/2017.
 */

public class CheckNetwork {

    private Context _context;

    public CheckNetwork(Context context) {
        this._context = context;
    }

    public Boolean isConnectingToInternet() {

        if (networkConnectivity()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL(
                        GlobalVariables.gblURL + "testconn.php").openConnection());
                urlc.setInstanceFollowRedirects(false);
                urlc.setRequestMethod("HEAD");
                urlc.setRequestProperty("Accept-Encoding", "");
                urlc.setConnectTimeout(GlobalVariables.gblTimeOut);
                urlc.setReadTimeout(GlobalVariables.gblReadTime);
                urlc.connect();

                Boolean blnCheck;
                int resCode = urlc.getResponseCode();
                String resMsg = urlc.getResponseMessage();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    blnCheck = true;
                    Log.e("YTLog " + this.getClass().getSimpleName(), "Connection established. " + resMsg);
                } else {
                    blnCheck = false;
                    Log.e("YTLog " + this.getClass().getSimpleName(), "Connection could not be established. " + resMsg);
                }

                return (blnCheck);
            } catch (IOException e) {
                Log.e("YTLog " + this.getClass().getSimpleName(), "IOException: " + e.toString());
                return (false);
            }
        } else {
            return false;
        }
    }

    private boolean networkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
