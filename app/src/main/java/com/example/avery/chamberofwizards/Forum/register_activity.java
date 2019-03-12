package com.example.avery.chamberofwizards.Forum;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Prelims.VerifyEmailActivity;
import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Pattern;

public class register_activity extends AppCompatActivity {
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

    private TextInputEditText txtEmail;
    private TextInputEditText txtPass;
    private TextInputEditText txtPassConfirm;
    private EditText txtStudentNumber;
    private Button btnRegister;
    private String email, pass, passConfirm, studentNumber;
    private ProgressBar progressBar;
    private boolean exists;
    private int userCountOfStudentNumber;


    //Firebase
    FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference studentNumberRef;
    private DatabaseReference usersRef;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);

        txtEmail = findViewById(R.id.reg_email);
        txtPass = findViewById(R.id.reg_pass);
        txtPassConfirm = findViewById(R.id.reg_confirm_pass);
        btnRegister = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.progress_create);


        //Firebase
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        studentNumberRef = FirebaseDatabase.getInstance().getReference().child("Student Numbers");
        rootRef = FirebaseDatabase.getInstance().getReference();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    createAccount();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            sendToMain();
        }

    }

    public void createAccount() {
        email = txtEmail.getText().toString();
        pass = txtPass.getText().toString();
        passConfirm = txtPassConfirm.getText().toString();

        if (isValidPassword() == true && isValidEmailAdd() == true) {

            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                        //Toast.makeText(register_activity.this, "Account successfully created!", Toast.LENGTH_SHORT).show();
                        sendToSetUp();
                    } else {
                        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    if(auth.getCurrentUser().isEmailVerified()) {
                                        //Kapag verified na yung email:
                                        Toast.makeText(register_activity.this, "The email address is already taken.", Toast.LENGTH_SHORT).show();
                                        auth.signOut();
                                    }
                                    else{
                                        sendToSetUp();
                                    }
                                }
                            }
                        });
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


    public boolean isValidEmailAdd() {
        if (!email.isEmpty()) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return true;
            } else {
                txtEmail.setError("Please enter a valid email address.");
                txtEmail.requestFocus();
                return false;
            }
        } else {
            txtEmail.setError("Email is required.");
            txtEmail.requestFocus();
            return false;
        }
    }

    public boolean isValidPassword() {
        if (!pass.isEmpty() || !passConfirm.isEmpty()) {
            if (pass.equals(passConfirm)) {
                if (PASSWORD_PATTERN.matcher(pass).matches()) {
                    return true;
                } else {
                    txtPass.setError("Password is too weak.");
                    txtPass.requestFocus();
                    return false;
                }
            } else {
                txtPass.setError("Passwords do not match.");
                txtPass.requestFocus();
                return false;
            }
        } else {
            if (pass.isEmpty()) {
                txtPass.setError("Password is required.");
                txtPass.requestFocus();
            }
            if (passConfirm.isEmpty()) {
                txtPassConfirm.setError("Field cannot be empty.");
                txtPassConfirm.requestFocus();
            }
            return false;
        }
    }

    public void sendToLog() {
        Intent intent = new Intent(register_activity.this, login_activity.class);
        startActivity(intent);
    }

    public void sendToSetUp() {
        Intent intent = new Intent(register_activity.this, VerifyEmailActivity.class);
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
       // finish();
    }

    public void sendToMain() {
        Intent intent = new Intent(register_activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*



     auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();


                        Toast.makeText(register_activity.this, "Account successfully created!", Toast.LENGTH_SHORT).show();
                        sendToSetUp();
                    }
                    else
                    {
                        Toast.makeText(register_activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
     */


}
