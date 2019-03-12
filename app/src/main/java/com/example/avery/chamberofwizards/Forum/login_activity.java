package com.example.avery.chamberofwizards.Forum;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Prelims.HomePageActivity;
import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class login_activity extends AppCompatActivity {

    private EditText txtEmail;
    private TextInputEditText txtPassword;
    private Button btnLogin;
    private String email, pass;
    private Button btnNoAcc;
    private ProgressBar progressBar;
    private TextView txtForgotPass;
    private ImageView imageView;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);


        txtEmail = findViewById(R.id.login_email);
        txtPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.login_button);
        btnNoAcc = findViewById(R.id.btnToReg);
        progressBar = findViewById(R.id.progressLogin);
        txtForgotPass = findViewById(R.id.txtForgotPassword);
        //imageView = findViewById(R.id.register_icon);


        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowUserToLogin();
            }
        });

        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToForgotPass();
            }
        });


    }

    public void sendToMain() {
        Intent intent = new Intent(login_activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendToMain2() {
        Intent intent = new Intent(login_activity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendToReg(View view) {
        Intent intent = new Intent(login_activity.this, register_activity.class);
        startActivity(intent);
    }

    public void allowUserToLogin() {
        email = txtEmail.getText().toString();
        pass = txtPassword.getText().toString();

        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)) {
            try {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String online_user_id = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            usersRef.child(online_user_id).child("device_token").setValue(deviceToken)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            sendToMain2();
                                        }
                                    });
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(login_activity.this, "Error occured: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            if (email.isEmpty()) {
                txtEmail.setError("Enter your email.");
                txtEmail.requestFocus();
            }
            if (pass.isEmpty()) {
                txtPassword.setError("Enter your password.");
                txtPassword.requestFocus();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            sendToMain2();
        }

    }

    public void sendToForgotPass() {
        Intent intent = new Intent(login_activity.this, ResetPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendToSet() {
        Intent intent = new Intent(login_activity.this, VerifiyStudentNumberActivity.class);
        startActivity(intent);
        finish();
    }


}

