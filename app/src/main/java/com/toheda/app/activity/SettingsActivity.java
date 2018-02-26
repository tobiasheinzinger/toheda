package com.toheda.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.toheda.app.R;

public class SettingsActivity extends PreferenceActivity {

    private SharedPreferences defaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Preference button = findPreference(getString(R.string.reset_button));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                editor.clear().commit();
                PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preferences, true);

                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);

                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_preferences, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
