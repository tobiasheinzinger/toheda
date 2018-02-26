package com.toheda.app.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.toheda.app.R;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameEditText;
    private EditText phoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nameEditText = (EditText) findViewById(R.id.field_name);
        emailEditText = (EditText) findViewById(R.id.field_email);
        phoneEditText = (EditText) findViewById(R.id.field_phone);
        passwordEditText = (EditText) findViewById(R.id.field_password);

        mAuth = FirebaseAuth.getInstance();
    }

    public void register(View view) {
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        final String phone = phoneEditText.getText().toString();
        final String name = nameEditText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("REGISTRATION", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(userProfileChangeRequest);

                            startActivity(new Intent(RegistrationActivity.this, TaskListActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("REGISTRATION", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
