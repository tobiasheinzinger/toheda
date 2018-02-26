package com.toheda.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.toheda.app.R;
import com.toheda.app.model.ActorInstanceIdentifierModel;
import com.toheda.app.model.ActorModel;
import com.toheda.app.request.StartActorInstanceRequest;

public class StartActorActivity extends AppCompatActivity {

    private EditText titleEditText;

    private ActorModel actor;

    private Gson gson;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_actor);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        actor = (ActorModel) getIntent().getExtras().get("actor");

        titleEditText = (EditText) findViewById(R.id.field_title);
    }

    public void start(View view) {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        final String title = titleEditText.getText().toString();

        new StartActorInstanceRequest.Builder()
                .forContext(this)
                .forActor(actor)
                .useTitle(title)
                .onError(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(StartActorActivity.this, "Error" + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .onSuccess(new Response.Listener<ActorInstanceIdentifierModel>() {
                    @Override
                    public void onResponse(ActorInstanceIdentifierModel response) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Intent intent = new Intent(StartActorActivity.this, ChatActivity.class);
                        intent.putExtra("actorInstanceIdentifier", response);

                        startActivity(intent);
                    }
                })
                .execute();
    }
}
