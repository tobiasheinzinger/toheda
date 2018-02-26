package com.toheda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.toheda.app.R;
import com.toheda.app.model.ActorInstanceIdentifierModel;
import com.toheda.app.model.ActorInstanceModel;
import com.toheda.app.model.RecordModel;
import com.toheda.app.model.UserModel;
import com.toheda.app.request.GetActorInstanceRequest;
import com.toheda.app.request.GetRecordListRequestGroup;
import com.toheda.app.request.GetUserRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ArrayAdapter adapter;

    private ProgressBar progressBar;

    private ListView listView;

    private ActorInstanceIdentifierModel actorInstanceIdentifier;

    private Button buttonWork;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        buttonWork = (Button) findViewById(R.id.button_work);

        adapter = new ChatListAdapter(this, new ArrayList<RecordModel>());
        adapter.setNotifyOnChange(true);

        if (getIntent().hasExtra("actorInstanceIdentifier")) {
            actorInstanceIdentifier = (ActorInstanceIdentifierModel) getIntent().getExtras().get("actorInstanceIdentifier");
        }

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        loadRecords();
        loadActorInstance();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        loadRecords();
        loadActorInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TaskListActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat_activity, menu);
        return true;
    }

    public void loadRecords(MenuItem item) {
        loadRecords();
        loadActorInstance();
    }

    private void loadRecords() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        new GetRecordListRequestGroup.Builder()
                .forContext(this)
                .forActorInstanceIdentifier(actorInstanceIdentifier)
                .onError(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(ChatActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSuccess(new Response.Listener<List<RecordModel>>() {
                    @Override
                    public void onResponse(List<RecordModel> response) {
                        adapter.clear();
                        adapter.addAll(response);
                        listView.setSelection(adapter.getCount() - 1);

                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                })
                .execute();
    }

    private void loadActorInstance() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        new GetActorInstanceRequest.Builder()
                .forContext(this)
                .forActorInstanceIdentifier(actorInstanceIdentifier)
                .onError(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        buttonWork.setClickable(false);
                        buttonWork.setEnabled(false);
                    }
                })
                .onSuccess(new Response.Listener<ActorInstanceModel>() {
                    @Override
                    public void onResponse(ActorInstanceModel actorInstance) {
                        if (!TextUtils.equals(actorInstance.getActiveStateType(), "4")) {
                            buttonWork.setClickable(true);
                            buttonWork.setEnabled(true);
                        } else {
                            buttonWork.setClickable(false);
                            buttonWork.setEnabled(false);
                        }
                    }
                })
                .execute();
    }

    public void openWorkActivity(View view) {
        Intent intent = new Intent(ChatActivity.this, WorkActivity.class);
        intent.putExtra("actorInstanceIdentifier", actorInstanceIdentifier);

        startActivity(intent);
    }

    final class ChatListAdapter extends ArrayAdapter<RecordModel> {

        final Map<String, UserModel> userCache = new HashMap<>();

        public ChatListAdapter(Context context, List<RecordModel> messages) {
            super(context, 0, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final RecordModel record = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_record, parent, false);
            }

            final String message = record.getActorname() + "\n" + record.getLog();
            final String userName = "Loading...";

            final LinearLayout layout = (LinearLayout) convertView
                    .findViewById(R.id.bubble_layout);
            final LinearLayout parent_layout = (LinearLayout) convertView
                    .findViewById(R.id.bubble_layout_parent);

            final TextView textViewUser = (TextView) convertView.findViewById(R.id.record_user);
            final TextView textViewMessage = (TextView) convertView.findViewById(R.id.record_body);
            textViewMessage.setText(message);
            textViewUser.setText(userName);

            if (TextUtils.equals(actorInstanceIdentifier.getActorId(), record.getActorid())) {
                textViewMessage.setBackgroundResource(R.drawable.rounded_rectangle_orange);
                parent_layout.setGravity(Gravity.RIGHT);
                textViewUser.setGravity(Gravity.RIGHT);

            } else {
                textViewMessage.setBackgroundResource(R.drawable.rounded_rectangle_blue);
                parent_layout.setGravity(Gravity.LEFT);
                textViewUser.setGravity(Gravity.LEFT);
            }


            if (userCache.containsKey(record.getUserid())) {
                UserModel user = userCache.get(record.getUserid());
                textViewUser.setText(user.getUsername());

            } else {
                new GetUserRequest.Builder()
                        .forUserId(record.getUserid())
                        .forContext(ChatActivity.this)
                        .onError(new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ChatActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onSuccess(new Response.Listener<UserModel>() {
                            @Override
                            public void onResponse(UserModel user) {
                                userCache.put(user.getUserId(), user);
                                textViewUser.setText(user.getUsername() + " (" + record.getTimestamp() + ")");
                            }
                        }).execute();
            }

            return convertView;
        }
    }
}