package com.example.avery.chamberofwizards.Forum;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String currentUserID;
    private String currentUserEmail;

    private Toolbar toolbar;
    private TextInputEditText txtEmail;
    private Button btnReset;

    private String email_on_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.forget_password_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtEmail = findViewById(R.id.txt_email);
        btnReset = findViewById(R.id.btn_reset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email_on_text = txtEmail.getText().toString();

                if (isEmailValid(email_on_text)) {
                    //Reset
                    auth.sendPasswordResetEmail(email_on_text).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "Email sent. Kindly check your mail. Thank you!", Toast.LENGTH_SHORT).show();
                                sendToLog();
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }

    public void sendToLog() {
        Intent intent = new Intent(ResetPasswordActivity.this, login_activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public boolean isEmailValid(String email_on_text) {
        if (!TextUtils.isEmpty(email_on_text)) {
            if (Patterns.EMAIL_ADDRESS.matcher(email_on_text).matches()) {
                return true;
            } else {
                txtEmail.setError("Enter your valid email address.");
                txtEmail.requestFocus();
                return false;
            }
        } else {
            txtEmail.setError("Email cannot be empty.");
            txtEmail.requestFocus();
            return false;
        }
    }

}
