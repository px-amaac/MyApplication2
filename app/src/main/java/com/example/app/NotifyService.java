package com.example.app;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by ShaDynastys on 2/28/14.
 */
public class NotifyService extends IntentService {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String URL = "http://api.zappos.com/Search?term=";

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    public static String networkPref = null;
    private SharedPreferences prefs;

    public NotifyService(){
        super("NotifyService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }



    @Override
    protected void onHandleIntent(Intent intent) {

        prefs = getDefaultSharedPreferences(this);
        networkPref = prefs.getString("listPref", "Wi-Fi");
        //check network connection
        final ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo;
        activeNetworkInfo = manager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            wifiConnected = activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }

        if (((prefs.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((prefs.equals(WIFI)) && (wifiConnected))) {
            String query = URLEncoder.encode(mCallbacks.getQuery(), "utf-8");
            ///Search/term/<SEARCH_TERM>?limit=<LIMIT>&page=<PAGE_NUMBER>
            //URL = "http://api.zappos.com/Search?term=";
            String lUrl = URL + query + getResources().getString(R.string.limit_page) + currentpage + getResources().getString(R.string.api_key);
            Toast.makeText(getActivity(), lUrl, Toast.LENGTH_SHORT).show();
            //new FakeDownloadResultTask().execute();
            new DownloadResultTask().execute(lUrl);


        //download json
        //parse json
        //check percentoff
        //notify user.
        }
    }
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(100000 /* milliseconds */);
        connection.setConnectTimeout(150000 /* milliseconds */);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        // Starts the query
        connection.connect();
        InputStream stream = connection.getInputStream();
        return stream;
    }

    public static boolean checkConnection(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();

        Intent intent = new Intent(context, CommunicationService.class);
        intent.putExtra("kind", "");

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            // start service
            context.startService(intent);
            return true;
        } else {
            // stop service
            context.stopService(intent);
            return false;
        }
    }
}
    }


            try {
                InputStream JSONStream = downloadUrl(params[0]);
                items = zjsonParse.readStream(JSONStream);
                if (items == null) {
                    return getResources().getString(R.string.data_not_there);
                } else
                    return getResources().getString(R.string.data_loaded);

            } catch (IOException e) {
                if (e.getMessage().contains("authentication challenge")) {
                    return "Error " + Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED) + "\nYou are not authorized to see this data";
                } else return "";
            }
        }

    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

// Retrieves a string value for the preferences. The second parameter
// is the default value to use if a preference value is not found.
sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();

        // Only loads the page if refreshDisplay is true. Otherwise, keeps previous
        // display. For example, if the user has set "Wi-Fi only" in prefs and the
        // device loses its Wi-Fi connection midway through the user using the app,
        // you don't want to refresh the display--this would force the display of
        // an error page.
        try {
        loadPage(0);
        } catch (UnsupportedEncodingException e) {
        throw new AssertionError("UTF-8 is unknown");
        }

    public void loadPage(int currentpage) throws UnsupportedEncodingException {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(100000 /* milliseconds */);
        connection.setConnectTimeout(150000 /* milliseconds */);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        // Starts the query
        connection.connect();
        InputStream stream = connection.getInputStream();
        return stream;
    }



}
