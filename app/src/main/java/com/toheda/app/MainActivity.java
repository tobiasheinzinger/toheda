package com.toheda.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        } else {
            TextView welcomeTextView = (TextView) findViewById(R.id.welcome);
            welcomeTextView.setText("Welcome " + currentUser.getDisplayName() + " to ToHeDA");
        }

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        List<KeyValue> keyValues = new ArrayList();
        keyValues.add(new KeyValue("Signed in user name", currentUser != null ? currentUser.getDisplayName() : "not signed in"));
        keyValues.add(new KeyValue("Signed in user email", currentUser != null ? currentUser.getEmail() : "not signed in"));
        keyValues.add(new KeyValue("Signed in user id", currentUser != null ? currentUser.getUid() : "not signed in"));
        keyValues.add(new KeyValue("Signed in user token", currentUser != null ? currentUser.getIdToken(false).toString() : "not signed in"));
        keyValues.add(new KeyValue("Firebase notification token", FirebaseInstanceId.getInstance().getToken()));

        ArrayAdapter adapter = new KeyValueArrayAdapter(this, keyValues);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                KeyValue keyValue = (KeyValue) parent.getItemAtPosition(position);

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", keyValue.getValue());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(MainActivity.this, "Content copied to clipboard", Toast.LENGTH_SHORT).show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_contacts:
                startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                break;
            case R.id.action_processes:
                startActivity(new Intent(MainActivity.this, ProcessesActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void signout(MenuItem item) {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
    }

    private class KeyValue {

        private final String key;
        private final String value;

        public KeyValue(final String key, final String value) {

            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    class KeyValueArrayAdapter extends ArrayAdapter<KeyValue> {

        public KeyValueArrayAdapter(Context context, List<KeyValue> keyValues) {
            super(context, 0, keyValues);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            KeyValue keyValue = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_main, parent, false);
            }
            TextView textViewKey = (TextView) convertView.findViewById(R.id.key);
            TextView textViewValue = (TextView) convertView.findViewById(R.id.value);

            textViewKey.setText(keyValue.getKey());
            textViewValue.setText(keyValue.getValue());

            return convertView;
        }
    }
}
