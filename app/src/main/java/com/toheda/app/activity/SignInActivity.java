package com.toheda.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.toheda.app.R;
import com.toheda.app.model.UserModel;
import com.toheda.app.request.GetSignedInUserRequest;
import com.toheda.app.request.SignInRequest;

public class SignInActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
//    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailEditText = (EditText) findViewById(R.id.field_email);
        passwordEditText = (EditText) findViewById(R.id.field_password);

//        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(View view) {
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        new SignInRequest.Builder()
                .forContext(this)
                .useUsername(email)
                .usePassword((password))
                .onError(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignInActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSuccess(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences settings = PreferenceManager
                                .getDefaultSharedPreferences(SignInActivity.this);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("x-auth-token", response);
                        editor.commit();

                        FirebaseMessaging.getInstance().subscribeToTopic("all");

                        loadUserData();
                    }
                }).execute();

//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("SIGN_IN", "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//
//                            startActivity(new Intent(SignInActivity.this, TaskListActivity.class));
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("SIGN_IN", "signInWithEmail:failure", task.getException());
//                            Toast.makeText(SignInActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
    }

    private void loadUserData() {
        new GetSignedInUserRequest.Builder()
                .forContext(this)
                .onError(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignInActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSuccess(new Response.Listener<UserModel>() {
                    @Override
                    public void onResponse(UserModel response) {
                        SharedPreferences settings = PreferenceManager
                                .getDefaultSharedPreferences(SignInActivity.this);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("userid", response.getUserId());
                        editor.putString("email", response.getEmail());
                        editor.putString("username", response.getUsername());
                        editor.commit();

                        startActivity(new Intent(SignInActivity.this, TaskListActivity.class));
                    }
                }).execute();
    }

    public void forwardToRegistration(View view) {
        startActivity(new Intent(SignInActivity.this, RegistrationActivity.class));
    }
}