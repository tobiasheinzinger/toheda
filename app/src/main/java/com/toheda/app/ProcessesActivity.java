package com.toheda.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.toheda.app.util.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessesActivity extends AppCompatActivity {

    private ArrayAdapter<JSONObject> adapter;
    private ProgressBar progressBar;
    private String processesServiceUrl;
    private String processImageServiceUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processes);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String serverUrl = sharedPreferences.getString(getString(R.string.server_url), "not available");
        String processesServicePath = sharedPreferences.getString(getString(R.string.processes_request), "not available");
        String processImageServicePath = sharedPreferences.getString(getString(R.string.process_image_request), "not available");

        processesServiceUrl = TextUtils.join("/", new String[]{serverUrl, processesServicePath});
        processImageServiceUrl = TextUtils.join("/", new String[]{serverUrl, processImageServicePath});

        adapter = new ProcessesAdapter(this, new ArrayList<JSONObject>());
        adapter.setNotifyOnChange(true);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        JSONObject jsonObject = (JSONObject) parent.getItemAtPosition(position);

                        try {
                            Toast.makeText(ProcessesActivity.this, jsonObject.getString("name"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        loadProcesses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_processes_activity, menu);
        return true;
    }

    public void loadProcesses(MenuItem item) {
        loadProcesses();
    }


    private void loadProcesses() {
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.POST, processesServiceUrl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<JSONObject> processes = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                processes.add(response.getJSONObject(i));
                            }

                            adapter.clear();
                            adapter.addAll(processes);

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(getApplicationContext(), "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                2 * 60 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        RequestQueueSingleton.getInstance(ProcessesActivity.this).
                addToRequestQueue(request);
    }

    class ProcessesAdapter extends ArrayAdapter<JSONObject> {

        public ProcessesAdapter(Context context, ArrayList<JSONObject> processes) {
            super(context, 0, processes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject jsonObject = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_processes, parent, false);
            }

            ImageView imageViewIcon = (ImageView) convertView.findViewById(R.id.process_icon);
            TextView textViewId = (TextView) convertView.findViewById(R.id.process_id);
            TextView textViewName = (TextView) convertView.findViewById(R.id.process_name);

            try {
                textViewId.setText(jsonObject.getString("id"));
                textViewName.setText(jsonObject.getString("name"));

                Uri iconUri = Uri.parse(TextUtils.join("?processId=", new String[]{processImageServiceUrl, jsonObject.getString("id")}));

                RequestOptions options = new RequestOptions()
                        .override(100, 100).timeout(60 * 1000).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);

                Glide.with(getContext())
                        .load(iconUri)
                        .apply(options)
                        .into(imageViewIcon);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}