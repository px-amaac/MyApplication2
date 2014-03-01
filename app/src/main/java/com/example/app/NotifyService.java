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
import android.util.Log;
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
    private static final String URL = "http://api.zappos.com/Product?id=";

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

        updateConnectedFlags();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        prefs = getDefaultSharedPreferences(this);
        networkPref = prefs.getString("listPref", "Wi-Fi");
        items = new ArrayList<HashMap<String, String>>();
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
        /*Log.d("Service up to web call", "*******************************************************************************************************************");
        DummyContentCreator contentCreator = new DummyContentCreator();
        items = contentCreator.generateServiceItems();
        comparePrices();
        */

        if (((prefs.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((networkPref.equals(WIFI)) && (wifiConnected))) {

            if(checkSharedPreferences()){
                if (itemIds == null)
                {
                    itemIds = new ArrayList<String>();
                }
                if(itemIds.isEmpty()){
                    stopSelf();
                }

                try {
                    for(int i = 0; i < itemIds.size(); i++){
                        String lURL = URL;
                        lURL = lURL.concat(itemIds.get(i));
                        lURL = lURL.concat("&includes=[\"styles\"]");
                        lURL = lURL.concat(getResources().getString(R.string.api_key));
                        InputStream JSONStream = downloadUrl(lURL);

                        items.add(pjsonParse.readStream(JSONStream));
                    }
                        if (items == null || items.isEmpty()) {
                            stopSelf();
                        }else
                            comparePrices();

                } catch (IOException e) {
                    if (e.getMessage().contains("authentication challenge")) {
                        Toast.makeText(this, "Error " + Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED) + "\nYou are not authorized to see this data", Toast.LENGTH_LONG).show();
                    }
                }

            }
        }else {stopSelf();}
    }

    private void comparePrices(){

        Log.d("Compare Price", "*******************************************************************************************************************");


        //compare prices and launch nnotifications if needed.
        for(HashMap<String, String> s: items){
            String originalPrice = s.get("originalPrice");
            String  price = s.get("price");
            Log.d("Original price and price", "*************************" + originalPrice + "**************************" + price + "****************************************************************");

            originalPrice = originalPrice.substring(1);
            price = price.substring(1);
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

    private Boolean checkSharedPreferences(){


        String user_items = prefs.getString("user_items", null);
        if (user_items != null)
        {
            if (itemIds == null)
            {
                itemIds = new ArrayList<String>();
            }
            itemIds.addAll(Arrays.asList(user_items.split(",")));
            return true;
        }
        else
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