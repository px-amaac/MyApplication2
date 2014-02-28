package com.example.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by ShaDynastys on 2/23/14.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
    {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Loads the XML preferences file.
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();

            // Registers a callback to be invoked whenever a user changes a preference.
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();

            // Unregisters the listener set in onResume().
            // It's best practice to unregister listeners when your app isn't using them to cut down on
            // unnecessary system overhead. You do this in onPause().
            getPreferenceScreen()
                    .getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        // Fires when the user changes a preference.
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Sets refreshDisplay to true so that when the user returns to the main
            // activity, the display refreshes to reflect the new settings.
            ItemListFragment.refreshDisplay = true;
        }

}
