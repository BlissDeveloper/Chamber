package com.example.avery.chamberofwizards.Forum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{
    //Firebase
    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private StorageReference imagesRef;

    private CircleImageView profile_img;
    private TextView profile_txtSN;
    private TextView profile_username;
    private TextView profile_fullname;
    private TextView profile_status;
    private TextView profile_course;
    private TextView profile_DOB;
    private TextView profile_gender;

    private Toolbar toolbar;

    private String currentUserID;

    private String imgURL, student_number, user_name, full_name, profStatus, profCourse, profDOB, profGender;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Firebase
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);


        profile_img = findViewById(R.id.my_profile_pic);
        profile_txtSN = findViewById(R.id.profile_txtStudentNumber);
        profile_username = findViewById(R.id.profile_txtUsername);
        profile_fullname = findViewById(R.id.profile_txtProfileName);
        profile_status = findViewById(R.id.profile_txtStatus);
        profile_course = findViewById(R.id.profile_txtCourse);
        profile_DOB = findViewById(R.id.profile_txtDOB);
        profile_gender = findViewById(R.id.profile_txtGender);

        usersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {

                    imgURL = dataSnapshot.child("profile_image").getValue().toString();
                    student_number = dataSnapshot.child("student_number").getValue().toString();
                    user_name = dataSnapshot.child("username").getValue().toString();
                    full_name = dataSnapshot.child("fullname").getValue().toString();
                    profStatus = dataSnapshot.child("status").getValue().toString();
                    profCourse = dataSnapshot.child("course").getValue().toString();
                    profDOB = dataSnapshot.child("dob").getValue().toString();
                    profGender = dataSnapshot.child("gender").getValue().toString();

                    Picasso.get().load(imgURL).into(profile_img);
                    profile_txtSN.setText("Student Number: " + student_number);
                    profile_username.setText("Username: " + user_name);
                    profile_fullname.setText("Full Name: " + full_name);
                    profile_status.setText("Status: " + profStatus);
                    profile_course.setText("Course: " + profCourse);
                    profile_DOB.setText("Date of Birth: " + profDOB);
                    profile_gender.setText("Gender:"  + profGender);


                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "Error retrieving account data.", Toast.LENGTH_SHORT).show();
                    sendToMain();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public void sendToMain()
    {
        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
