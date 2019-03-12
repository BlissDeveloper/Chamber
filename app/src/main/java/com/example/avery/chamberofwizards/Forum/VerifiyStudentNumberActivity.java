package com.example.avery.chamberofwizards.Forum;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerifiyStudentNumberActivity extends AppCompatActivity {

    private TextInputEditText txtStudentNumber;
    private Button btnVerify;
    private Toolbar mToolbar;

    private DatabaseReference numbersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifiy_student_number);

        txtStudentNumber = findViewById(R.id.txtStudentNumber);
        btnVerify = findViewById(R.id.btnVerify);
        mToolbar = findViewById(R.id.toolbarVerification);

        numbersRef = FirebaseDatabase.getInstance().getReference().child("Student Numbers");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Verify Your Student Number");

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String studentNumber = txtStudentNumber.getText().toString();

                if (isValidInput(studentNumber)) {
                    numbersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild(studentNumber)) {
                                    registerUser(studentNumber);
                                } else {
                                    Toast.makeText(VerifiyStudentNumberActivity.this, "The student number is already registered.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        goToRegisterActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goToRegisterActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isValidInput(String studentNumber) {
        if (!TextUtils.isEmpty(studentNumber)) {
            if (studentNumber.matches("^[0-9]{2}-[0-9]{6}$")) {
                return true;
            } else {
                txtStudentNumber.setError("Invalid student number.");
                return false;
            }
        } else {
            txtStudentNumber.setError("Please enter your student number.");
            return false;
        }
    }

    public void registerUser(final String studentNumber) {
        usersRef.child(currentUserID).child("student_number").setValue(studentNumber)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            numbersRef.child(studentNumber).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(VerifiyStudentNumberActivity.this, "Student number verified!", Toast.LENGTH_SHORT).show();
                                        sendToSetUp();
                                    } else {
                                        Toast.makeText(VerifiyStudentNumberActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                        }
                    }
                });
    }

    public void goToRegisterActivity() {
        Intent intent = new Intent(VerifiyStudentNumberActivity.this, register_activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void sendToSetUp() {
        Intent intent = new Intent(VerifiyStudentNumberActivity.this, setup_activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(currentUserID)) {
                        sendToSetUp();
                    }
                } else {
                    Toast.makeText(VerifiyStudentNumberActivity.this, "Error retrieving data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
