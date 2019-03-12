package com.example.avery.chamberofwizards.Forum;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth auth;
    private DatabaseReference settingsUsersRef;
    private String currentUserID;
    private StorageReference usersProfileImageRef;

    private CircleImageView imgProfilePic;
    private EditText txtProfileStatus;
    private EditText txtUsername;
    private TextView txtFullname;
    private TextView textCourse;
    private EditText txtDOB;
    private EditText txtGender;
    private Button btnSaveAcc;
    private TextView txtStudentNumber;
    private ProgressBar progressBar;
    private int Gallery_Pick = 1;

    private String imgProfilePicURL, profileStatus, username, fullname, course, dob, gender;

    private String profStatus, userName, fullName, dateOfBirth, Gender;

    //Date picker
    private DatePickerDialog.OnDateSetListener datePickerDialog;
    private DatePickerDialog dialog;

    //Toolbar
    private Toolbar mToolbar;


    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Firebase
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        settingsUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        usersProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        //Toolbar
        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Misc
        imgProfilePic = findViewById(R.id.settings_profile_image);
        txtProfileStatus = findViewById(R.id.settings_status);
        txtUsername = findViewById(R.id.settings_username);
        txtFullname = findViewById(R.id.settings_full_name);
        txtStudentNumber = findViewById(R.id.profile_txtStudentNumber);
        textCourse = findViewById(R.id.profile_txtCourse);
        txtDOB = findViewById(R.id.settings_dob);
        txtGender = findViewById(R.id.settings_gender);
        btnSaveAcc = findViewById(R.id.update_account);
        progressBar = findViewById(R.id.settings_pBar);

        txtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                dialog = new DatePickerDialog(SettingsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        txtDOB.setText(i1 + "/" + i2 + "/" + i);
                    }
                }, year, month, day);
                dialog.show();
            }
        });

        settingsUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    imgProfilePicURL = dataSnapshot.child("profile_image").getValue().toString();
                    username = dataSnapshot.child("username").getValue().toString();
                    fullname = dataSnapshot.child("fullname").getValue().toString();
                    course = dataSnapshot.child("course").getValue().toString();
                    dob = dataSnapshot.child("dob").getValue().toString();
                    gender = dataSnapshot.child("gender").getValue().toString();
                    profileStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(imgProfilePicURL).into(imgProfilePic);
                    txtProfileStatus.setText(profileStatus);
                    txtUsername.setText(username);
                    txtFullname.setText(fullname);
                    textCourse.setText(course);
                    txtDOB.setText(dob);
                    txtGender.setText(gender);
                } else {
                    Toast.makeText(SettingsActivity.this, "Error retrieving account data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });

        btnSaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                ValidateAccountInfo();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void ValidateAccountInfo() {
        profileStatus = txtProfileStatus.getText().toString();
        userName = txtUsername.getText().toString();
        fullName = txtFullname.getText().toString();
        dateOfBirth = txtDOB.getText().toString();
        gender = txtGender.getText().toString();

        if (validateProfileStatus() && validateUsername() && validateDOB()) {
            updateAccountInfo(profileStatus, userName, dateOfBirth);
        } else {
            Toast.makeText(this, "Error validating data.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validateProfileStatus() {
        if (!TextUtils.isEmpty(profileStatus)) {
            return true;
        } else {
            txtProfileStatus.setError("Field cannot be empty.");
            txtProfileStatus.requestFocus();
            return false;
        }
    }

    public boolean validateUsername() {
        if (!TextUtils.isEmpty(userName)) {
            return true;
        } else {
            txtUsername.setError("Username must not be empty");
            txtUsername.requestFocus();
            return false;
        }
    }

    public boolean validateDOB() {
        if (!TextUtils.isEmpty(dateOfBirth)) {
            return true;
        } else {
            txtDOB.setError("Date of birth must not be empty.");
            txtDOB.requestFocus();
            return false;
        }
    }

    public void sendToMain() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void updateAccountInfo(String profStat, String uName, String dateOB) {
        HashMap<String, Object> updateUserInfoMap = new HashMap<>();

        updateUserInfoMap.put("status", profStat);
        updateUserInfoMap.put("username", uName);
        updateUserInfoMap.put("dob", dateOB);
        updateUserInfoMap.put("gender", gender);

        settingsUsersRef.updateChildren(updateUserInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "Account information updated successfully!", Toast.LENGTH_SHORT).show();
                    sendToMain();
                } else {
                    Toast.makeText(SettingsActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

                /*
                if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null)
                {

                }
                */

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                //Saving image to Storage
                final StorageReference filePath = usersProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            //final String downloadURL = task.getResult().getDownloadUrl().toString();

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadURL = uri.toString();

                                    settingsUsersRef.child("profile_image").setValue(downloadURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                final Query query = FirebaseDatabase.getInstance().getReference().child("All Posts").orderByChild("uid").equalTo(currentUserID);

                                                query.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                                        String keys;
                                                        for (DataSnapshot dataKeys : dataSnapshot.getChildren()) {
                                                            keys = dataKeys.getKey();

                                                            FirebaseDatabase.getInstance().getReference().child("All Posts").child(keys).child("profile_image").setValue(downloadURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {

                                                                    }
                                                                }
                                                            });

                                                        }

                                                        Picasso.get().load(downloadURL).into(imgProfilePic);


                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                                //
                                            } else {
                                                Toast.makeText(SettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });


                        }
                    }
                });

            }
        }


    }


}
