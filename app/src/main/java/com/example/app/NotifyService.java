package com.example.app;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by ShaDynastys on 2/28/14.
 */
public class NotifyService extends IntentService {
    private static final int NOTIFY_ME_ID=1337;
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String URL = "http://api.zappos.com/Product?id=[";

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    public static String networkPref = null;
    private SharedPreferences prefs;
    private ArrayList<String> itemIds;
    private List<HashMap<String, String>> items;
    private Notification notif = null;
    NotificationManager mNotificationManager;

    public NotifyService(){
        super("NotifyService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        updateConnectedFlags();

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        prefs = getDefaultSharedPreferences(this);
        networkPref = prefs.getString("listPref", "Wi-Fi");
        ProductJSONParser pjsonParse = new ProductJSONParser();

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
            String lURL = URL;
            if(!checkSharedPreferences()){
                if(itemIds.isEmpty()){
                    stopSelf();
                }
                for(int i = 0; i < itemIds.size(); i++){
                    lURL = lURL.concat(itemIds.get(i));
                    if(i != itemIds.size() - 1)
                    lURL = lURL.concat(",");
                }
                lURL = lURL.concat("]&includes=[\"styles\"]");
                lURL = lURL.concat(getResources().getString(R.string.api_key));
                Toast.makeText(this, lURL, Toast.LENGTH_SHORT).show();
                try {
                    InputStream JSONStream = downloadUrl(lURL);
                    items = pjsonParse.readStream(JSONStream);
                    if (items == null) {
                        Toast.makeText(this, getResources().getString(R.string.data_not_there), Toast.LENGTH_LONG).show();
                        stopSelf();
                    } else
                        Toast.makeText(this, getResources().getString(R.string.data_loaded), Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    if (e.getMessage().contains("authentication challenge")) {
                        Toast.makeText(this, "Error " + Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED) + "\nYou are not authorized to see this data", Toast.LENGTH_LONG).show();
                    }
                }
                comparePrices();
            }
        }else {stopSelf();}
    }

    private void comparePrices(){

        //compare prices and launch nnotifications if needed.
        for(HashMap<String, String> s: items){
            String originalPrice = s.get("originalPrice");
            String  price = s.get("price");
            double foriginalPrice = Double.valueOf(originalPrice);
            double fprice = Double.valueOf(price);
            double percentOff = foriginalPrice - (foriginalPrice * 0.2);
            if (fprice <= percentOff){
                NotificationCompat.Builder b = new NotificationCompat.Builder(this);
                Intent notificationIntent = new Intent(this, ItemListActivity.class);
                notificationIntent.putExtra("data", (Serializable) items);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);
                b.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).setWhen(System.currentTimeMillis())
                        .setContentTitle(getString(R.string.notify)).setContentText(getString(R.string.notify_text))
                        .setSmallIcon(R.drawable.ic_launcher).setContentIntent(pendingIntent);
                mNotificationManager.notify(NOTIFY_ME_ID, b.build());
            }


        }
    }

        //check percentoff
        //notify user.

    private Boolean checkSharedPreferences(){

        String user_items = prefs.getString("user_items", null);
        if (user_items != null)
        {
            itemIds.addAll(Arrays.asList(user_items.split(",")));
            return true;
        }
        return false;
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

    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }
}