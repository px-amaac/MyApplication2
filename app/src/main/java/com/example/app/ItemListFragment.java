package com.example.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.app.dummy.DummyContentCreator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * A list fragment representing a list of Items. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ItemDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ItemListFragment extends ListFragment implements AbsListView.OnScrollListener {

    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String URL = "http://api.zappos.com/Search?term=";




    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    // The user's current network preference setting.
    public static String sPref = null;

    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /*cnumber of paged in the list/ number of times list has gotten more data*/
    private int currentPage = 0;
    /*actual data to fill the list.*/
    private ArrayList<HashMap<String,String>>  data = null;
    /*custom list view loader task uses Universal image adapter*/
    private ListViewLoaderTask listTask = null;
    private static final int threshold = 3;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //checks when scroll state is idle. This happens at the bottem of the list view. When user reaches bottom it loads more data.
        if(scrollState == SCROLL_STATE_IDLE){
            if (view.getLastVisiblePosition() >= view.getCount() - 1 - threshold) {
                Toast.makeText(getActivity(), "Scrol StateIdle, last visible " + view.getLastVisiblePosition() + "threshold = " + threshold, Toast.LENGTH_LONG).show();
                currentPage++;
                //load more list items:
                try {
                    Toast.makeText(getActivity(), "LoadPage", Toast.LENGTH_LONG).show();
                    loadPage(currentPage);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(HashMap<String, String> aaItem);

        public String getQuery();

    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }
    //sets up the connectivity action intent filter. registers broadcastreciever
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        getActivity().registerReceiver(receiver, filter);
        data = new ArrayList<HashMap<String, String> >();

        setHasOptionsMenu(true);
    }

    public boolean checkAdapter() {
        SimpleAdapter adapter = (SimpleAdapter) getListAdapter();
        return adapter != null;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(this);
        View footer = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
        getListView().addFooterView(footer);
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }
    //sets up shared preferences and updates connected flags. try catch load page at the bottom
    @Override
    public void onStart() {
        super.onStart();
        // Gets the user's network preference settings
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
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

    // Uses AsyncTask subclass to download the requested JSON from Zappos
    // This avoids UI lock up.
    public void loadPage(int currentpage) throws UnsupportedEncodingException {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass


            String query = URLEncoder.encode(mCallbacks.getQuery(), "utf-8");
            ///Search/term/<SEARCH_TERM>?limit=<LIMIT>&page=<PAGE_NUMBER>
            /*************************************************************************************************************************************/
            String lUrl = URL + query + getResources().getString(R.string.limit_page) + currentpage + getResources().getString(R.string.api_key);
            /***********************************************************Test This Tomrrow***************************************************************************/
           new FakeDownloadResultTask().execute();
           // new DownloadResultTask().execute(lUrl);
        } else {
            //TODO: Modify layout to display an error
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();


    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(data.get(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // Checks the user prefs and the network connection. Based on the result, decides
            // whether
            // to refresh the display or keep the current display.
            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
            if (WIFI.equals(sPref) && networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // If device has its Wi-Fi connection, sets refreshDisplay
                // to true. This causes the display to be refreshed when the user
                // returns to the app.
                refreshDisplay = true;
                Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();

                // If the setting is ANY network and there is a network connection
                // (which by process of elimination would be mobile), sets refreshDisplay to true.
            } else if (ANY.equals(sPref) && networkInfo != null) {
                refreshDisplay = true;

                // Otherwise, the app can't download content--either because there is no network
                // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
                // is no Wi-Fi connection.
                // Sets refreshDisplay to false.
            } else {
                refreshDisplay = false;
                Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FakeDownloadResultTask extends AsyncTask<Void, Void, Void>{
        View footer;
        List<HashMap<String, String>> items = null;
        DummyContentCreator contentCreator = new DummyContentCreator();



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                items = contentCreator.generateItems();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (items.isEmpty()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.query_empty), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
            } else{
                data.addAll(items);
                ListViewLoaderTask lvLoader = new ListViewLoaderTask();
                lvLoader.execute(data);
            }
        }


    }

    private class DownloadResultTask extends AsyncTask<String, Void, String> {
        List<HashMap<String, String>> items = null;
        ZapposJSONParser zjsonParse = new ZapposJSONParser();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

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

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            if (items.isEmpty()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.query_empty), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
            } else{
                currentPage++;
                data.addAll(items);
                ListViewLoaderTask lvLoader = new ListViewLoaderTask();
                lvLoader.execute(data);
            }
        }
    }

    private class ListViewLoaderTask extends AsyncTask<ArrayList<HashMap<String, String>>, Void, ImageLoaderListAdapter> {
        ImageLoaderListAdapter mAdapter = null;
        int count;
        String imgUrl = null;

        // String url = "http://kzfr.org/u/img/original/";

        @Override
        protected ImageLoaderListAdapter doInBackground(ArrayList<HashMap<String, String>>... list) {
            ArrayList<HashMap<String, String>> items = list[0];
            String[] from = {"thumbnailImageUrl", "brandName", "productName", "price", "percentOff", "productUrl"};
            int[] to = {R.id.item_image, R.id.brand_name, R.id.product_name, R.id.price, R.id.percentoff, R.id.url};


            mAdapter = new ImageLoaderListAdapter(getActivity()
                    .getBaseContext(), items, R.layout.searchrow, from, to);

            return mAdapter;
        }


        @Override
        protected void onPostExecute(ImageLoaderListAdapter mAdapter) {
            setListAdapter(mAdapter);
            updateThisList();
        }
    }

    public void updateThisList() {
        if (data != null) {
            if (!checkAdapter()) {
                if (listTask == null) {
                    listTask = new ListViewLoaderTask();
                    listTask.execute(data);
                }
            } else {
                ((ImageLoaderListAdapter) getListAdapter())
                        .notifyDataSetChanged();
                getListView().setSelection(currentPage * 10);
            }
        }
    }
    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
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
