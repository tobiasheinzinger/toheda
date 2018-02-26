package com.toheda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.toheda.app.R;
import com.toheda.app.model.ActorInstanceIdentifierModel;
import com.toheda.app.model.TaskModel;
import com.toheda.app.request.GetTaskListRequest;

import java.util.ArrayList;
import java.util.List;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 */
public class TaskListActivity extends AppCompatActivity {

    private ArrayAdapter adapter;

    private ProgressBar progressBar;

    //    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();

        adapter = new TaskListAdapter(this, new ArrayList<TaskModel>());

        final TextView emptyText = (TextView) findViewById(android.R.id.empty);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyText);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TaskModel task = (TaskModel) parent.getItemAtPosition(position);
                final ActorInstanceIdentifierModel actorInstanceIdentifier = new ActorInstanceIdentifierModel(task);

                Intent intent = new Intent(TaskListActivity.this, ChatActivity.class);
                intent.putExtra("actorInstanceIdentifier", actorInstanceIdentifier);

                startActivity(intent);
            }
        });

        loadTasks();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadTasks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(TaskListActivity.this, SettingsActivity.class));
                break;
            case R.id.action_contacts:
                startActivity(new Intent(TaskListActivity.this, ContactsActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showStartActivity(View view) {
        final Intent intent = new Intent(TaskListActivity.this, ActorListActivity.class);
        startActivity(intent);
    }

    public void loadTasks(MenuItem menuItem) {
        loadTasks();
    }

    private void loadTasks() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        new GetTaskListRequest.Builder()
                .forContext(this)
                .onError(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(TaskListActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSuccess(new Response.Listener<List<TaskModel>>() {
                    @Override
                    public void onResponse(List<TaskModel> response) {
                        adapter.clear();
                        adapter.addAll(response);

                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                })
                .execute();
    }

    public void signout(MenuItem item) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(TaskListActivity.this);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("x-auth-token");
        editor.remove("userid");
        editor.remove("email");
        editor.remove("username");
        editor.commit();

        startActivity(new Intent(TaskListActivity.this, SignInActivity.class));
    }

    class TaskListAdapter extends ArrayAdapter<TaskModel> {

        public TaskListAdapter(Context context, List<TaskModel> tasks) {
            super(context, 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskModel task = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_task_list, parent, false);
            }
            TextView textViewTitle = (TextView) convertView.findViewById(R.id.title);
            TextView textViewActor = (TextView) convertView.findViewById(R.id.actor);
            TextView textViewState = (TextView) convertView.findViewById(R.id.state);

            textViewTitle.setText(task.getTitle());
            textViewActor.setText(task.getActorName());
            textViewState.setText(task.getStateName());

            return convertView;
        }
    }
}
