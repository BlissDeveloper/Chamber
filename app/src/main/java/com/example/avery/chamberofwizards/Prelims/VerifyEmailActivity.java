package com.example.avery.chamberofwizards.Prelims;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Forum.VerifiyStudentNumberActivity;
import com.example.avery.chamberofwizards.Forum.register_activity;
import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class VerifyEmailActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String currentUserID;
    private String currentUserEmail;

    private Toolbar mToolbar;
    private TextView textViewMessage;
    private Button btnVerify;
    private TextView textViewChangeEmail;

    private CardView cardviewChangeEmail;
    private TextInputEditText editTextNewEmail;
    private TextInputEditText editTextConfirmNewEmail;
    private ImageView imageViewClose;
    private Button btnConfirmChangeEmail;
    private TextInputEditText editTextPassword;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    // "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 4 characters
                    "$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        mToolbar = findViewById(R.id.toolbarVerifyEmail);
        textViewMessage = findViewById(R.id.textViewVerifyMessage);
        btnVerify = findViewById(R.id.btnVerifyEmail);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Verify Your Email");

        currentUserEmail = mAuth.getCurrentUser().getEmail();
        cardviewChangeEmail = findViewById(R.id.cardviewChangeEmail);
        editTextNewEmail = findViewById(R.id.editTextNewEmail);
        editTextConfirmNewEmail = findViewById(R.id.editTextConfirmNewEmail);
        imageViewClose = findViewById(R.id.imageViewClose);
        btnConfirmChangeEmail = findViewById(R.id.btnConfirmNewEmail);
        textViewChangeEmail = findViewById(R.id.textViewChangeEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        btnConfirmChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEmail() && isValidPassword()) {
                    final String email = editTextConfirmNewEmail.getText().toString();
                    final String p = editTextPassword.getText().toString();
                    final String old_email = mAuth.getCurrentUser().getEmail();

                    mAuth.signInWithEmailAndPassword(old_email, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                mAuth.getCurrentUser().updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(VerifyEmailActivity.this, "Your email is updated!", Toast.LENGTH_SHORT).show();
                                            String updated_email = mAuth.getCurrentUser().getEmail();
                                            textViewMessage.setText("By clicking the button below, we will send a confirmation message to " + updated_email);
                                        }
                                        else {
                                            Toast.makeText(VerifyEmailActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(VerifyEmailActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        textViewChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardviewChangeEmail.setVisibility(View.VISIBLE);
            }
        });

        textViewMessage.setText("By clicking the button below, we will send a confirmation message to " + currentUserEmail);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserEmail != null) {
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(VerifyEmailActivity.this, "We've sent a confirmation message. Please check your email. Thank you!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        cardviewChangeEmail.setVisibility(View.GONE);
        cardviewChangeEmail.setEnabled(false);

        mAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (mAuth.getCurrentUser().isEmailVerified()) {
                    goToStudentNumberVerification();
                    Log.d("Avery", "Email verified!");
                    Log.d("Avery", currentUserEmail);
                } else {
                    Log.d("Avery", "Email not verified.");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        goToRegister();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                goToRegister();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public boolean isValidPassword() {
        String pass = editTextPassword.getText().toString();

        if (pass.length() == 0) {
            Toast.makeText(this, "Enter a password", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidEmail() {
        String email, confirm_email;
        email = editTextNewEmail.getText().toString();
        confirm_email = editTextConfirmNewEmail.getText().toString();

        if (editTextNewEmail.length() == 0 && editTextConfirmNewEmail.length() == 0) {
            Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
            Log.e("Avery", "Fields cannot be empty");
            return false;
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email address.", Toast.LENGTH_SHORT).show();
                Log.e("Avery", "Enter a valid email address.");
                return false;
            } else {
                if (email.equals(confirm_email)) {
                    Log.d("Avery", "Valid email");
                    return true;
                } else {
                    Toast.makeText(this, "Emails do not match.", Toast.LENGTH_SHORT).show();
                    Log.e("Avery", "Emails do not match");
                    return false;
                }
            }
        }
    }

    public void goToRegister() {
        Intent intent = new Intent(VerifyEmailActivity.this, register_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    public void goToStudentNumberVerification() {
        Intent intent = new Intent(VerifyEmailActivity.this, VerifiyStudentNumberActivity.class);
        startActivity(intent);
    }
}
