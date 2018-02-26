package com.toheda.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.toheda.app.R;
import com.toheda.app.model.ActorInstanceIdentifierModel;
import com.toheda.app.model.FieldModel;
import com.toheda.app.model.TransitionModel;
import com.toheda.app.request.FollowTransitionRequest;
import com.toheda.app.request.GetActorInstanceDataRequest;
import com.toheda.app.request.GetTransitionListRequest;

import java.util.List;

public class WorkActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private ActorInstanceIdentifierModel actorInstanceIdentifier;

    private ListView listViewField;

    private Spinner spinnerTransition;

    private ArrayAdapter adapterField;

    private ArrayAdapter adapterTransition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        actorInstanceIdentifier = (ActorInstanceIdentifierModel) getIntent().getExtras().get("actorInstanceIdentifier");

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        adapterField = new FieldListAdapter(this);
        adapterField.setNotifyOnChange(true);

        listViewField = (ListView) findViewById(R.id.list_view_fields);
        listViewField.setAdapter(adapterField);

        adapterTransition = new TransitionListAdapter(this);
        adapterTransition.setNotifyOnChange(true);

        spinnerTransition = (Spinner) findViewById(R.id.spinner_transitions);
        spinnerTransition.setAdapter(adapterTransition);

        loadFields();
        loadTransitions();
    }

    private void loadFields() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        new GetActorInstanceDataRequest.Builder()
                .forContext(this)
                .forActorInstanceIdentifier(actorInstanceIdentifier)
                .onError(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(WorkActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSuccess(new Response.Listener<List<FieldModel>>() {
                    @Override
                    public void onResponse(List<FieldModel> fields) {
                        adapterField.clear();
                        adapterField.addAll(fields);

                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                })
                .execute();
    }

    private void loadTransitions() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        new GetTransitionListRequest.Builder()
                .forContext(this)
                .forActorInstanceIdentifier(actorInstanceIdentifier)
                .onError(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(WorkActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSuccess(new Response.Listener<List<TransitionModel>>() {
                    @Override
                    public void onResponse(List<TransitionModel> transitions) {
                        adapterTransition.clear();
                        adapterTransition.addAll(transitions);
                        if (transitions != null && !transitions.isEmpty()) {
                            spinnerTransition.setSelection(0);
                        }

                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                })
                .execute();
    }

    public void followTransition(View view) {
        if (spinnerTransition.getSelectedItem() != null) {
            TransitionModel transitionModel = (TransitionModel) spinnerTransition.getSelectedItem();

            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            new FollowTransitionRequest.Builder()
                    .forContext(this)
                    .forActorInstanceIdentifier(actorInstanceIdentifier)
                    .useTransition(transitionModel)
                    .onError(new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            Toast.makeText(WorkActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onSuccess(new Response.Listener<TransitionModel>() {
                        @Override
                        public void onResponse(TransitionModel response) {
                            Intent intent = new Intent(WorkActivity.this, ChatActivity.class);
                            intent.putExtra("actorInstanceIdentifier", actorInstanceIdentifier);

                            startActivity(intent);
                        }
                    }).execute();

        }
    }

    final class FieldListAdapter extends ArrayAdapter<FieldModel> {

        public FieldListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final FieldModel field = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_field_list, parent, false);
            }

            final TextView textViewName = (TextView) convertView.findViewById(R.id.name);
            final TextView textViewValue = (TextView) convertView.findViewById(R.id.value);

            textViewName.setText(field.getName());
            textViewValue.setText(field.getValue());

            return convertView;
        }
    }

    final class TransitionListAdapter extends ArrayAdapter<TransitionModel> {

        public TransitionListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TransitionModel transition = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }

            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            text.setText(transition.getName());

            return convertView;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final TransitionModel transition = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }

            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            text.setText(transition.getName());

            return convertView;
        }
    }
}