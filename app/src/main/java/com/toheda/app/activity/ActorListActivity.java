package com.toheda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.toheda.app.R;
import com.toheda.app.model.ActorModel;
import com.toheda.app.request.GetActorListRequest;

import java.util.ArrayList;
import java.util.List;

public class ActorListActivity extends AppCompatActivity {

    private ArrayAdapter<ActorModel> adapter;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_list);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        adapter = new ActorListAdapter(this, new ArrayList<ActorModel>());
        adapter.setNotifyOnChange(true);

        final TextView emptyText = (TextView) findViewById(android.R.id.empty);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyText);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ActorModel actor = (ActorModel) parent.getItemAtPosition(position);

                        Intent intent = new Intent(ActorListActivity.this, StartActorActivity.class);
                        intent.putExtra("actor", actor);

                        startActivity(intent);
                    }
                }
        );

        loadProcesses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actor_list_activity, menu);
        return true;
    }

    public void loadProcesses(MenuItem item) {
        loadProcesses();
    }


    private void loadProcesses() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        new GetActorListRequest.Builder()
                .forContext(this)
                .onError(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(ActorListActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSuccess(new Response.Listener<List<ActorModel>>() {
                    @Override
                    public void onResponse(List<ActorModel> response) {
                        adapter.clear();
                        adapter.addAll(response);

                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                })
                .execute();
    }

    final class ActorListAdapter extends ArrayAdapter<ActorModel> {

        public ActorListAdapter(Context context, List<ActorModel> actors) {
            super(context, 0, actors);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ActorModel actor = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_actor_list, parent, false);
            }

            final ImageView imageViewIcon = (ImageView) convertView.findViewById(R.id.process_icon);
            final TextView textViewId = (TextView) convertView.findViewById(R.id.process_id);
            final TextView textViewName = (TextView) convertView.findViewById(R.id.process_name);

            textViewId.setText(actor.getActorId());
            textViewName.setText(actor.getName());

            Uri iconUri;
            if (!TextUtils.isEmpty(actor.getImageUrl())) {
                iconUri = Uri.parse(TextUtils.join("", new String[]{"http://www.actorsphere.de", actor.getImageUrl()}));
            } else {
                iconUri = Uri.parse(TextUtils.join("", new String[]{"http://www.actorsphere.de", "/anc/static/actnconnect/actors/juggler.png"}));
            }
            RequestOptions options = new RequestOptions()
                    .override(100, 100).timeout(60 * 1000)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(getContext())
                    .load(iconUri)
                    .apply(options)
                    .into(imageViewIcon);

            return convertView;
        }
    }
}