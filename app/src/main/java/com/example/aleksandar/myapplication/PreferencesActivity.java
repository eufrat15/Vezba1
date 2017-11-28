package com.example.aleksandar.myapplication;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Aleksandar on 28-Nov-17.
 */

public class PreferencesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

}

