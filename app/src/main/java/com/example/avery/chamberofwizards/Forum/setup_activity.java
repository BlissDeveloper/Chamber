package com.example.avery.chamberofwizards.Forum;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Prelims.HomePageActivity;
import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class setup_activity extends AppCompatActivity {

    private Spinner spinnerCourses;
    private String course;
    private TextInputEditText txtUsername;
    private EditText txtFullname;
    private Button btnSave;
    private ProgressBar progressBar;
    private CircleImageView imgProfileImage;
    final static int Gallery_Pick = 1;
    private ProgressBar progressBar_profile_img;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String currentUserID;
    private StorageReference UserProfileImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_activity);

        txtUsername = findViewById(R.id.profile_txtUsername);
        txtFullname = findViewById(R.id.txtFullname);
        btnSave = findViewById(R.id.btnS);
        progressBar = findViewById(R.id.progressBar);
        imgProfileImage = findViewById(R.id.setup_profile_image);
        progressBar_profile_img = findViewById(R.id.progress_profile_pic);

        //Spinner
        spinnerCourses = findViewById(R.id.spinner_course);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.courses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapter);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        spinnerCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Para makuha ang text
                course = adapterView.getItemAtPosition(i).toString();

                /*
                1 BSM
                2 BSB
                3 BSFS
                4 BSES
                 */

                switch (adapterView.getSelectedItemPosition()) {
                    case 1:
                        course = "BSM";
                        break;
                    case 2:
                        course = "BSB";
                        break;
                    case 3:
                        course = "BSFS";
                        break;
                    case 4:
                        course = "BSES";
                        break;
                    default:
                        course = null;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveAccountInformation();
                } catch (Exception e) {
                    Toast.makeText(setup_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        imgProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
                */
                openGallery();

            }
        });


        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profile_image")) {
                        String image = dataSnapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(image).into(imgProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                   if(dataSnapshot.hasChild("fullname")) {
                       //Meaning, nakapag-setup na yung user.
                       sendToMain();
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            /*CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);*/
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            progressBar.setVisibility(View.VISIBLE);
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //Saving image URI to the Firebase Storage
                final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    usersRef.child("profile_image").setValue(downloadUrl);
                                }
                            });

                        } else {
                            Toast.makeText(setup_activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                Toast.makeText(this, "Error occured: Image can't be cropped. Try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveAccountInformation() {
        String username = txtUsername.getText().toString();
        String fullname = txtFullname.getText().toString();
        String courseSelected = course;


        if (isUsernameValid(username) && isFullNameValid(fullname) && isCourseValid(courseSelected)) {
            //Save to the database

            HashMap<String, Object> userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("status", "I am a Wizard!");
            userMap.put("gender", "None");
            userMap.put("dob", "");
            userMap.put("course", courseSelected);
            userMap.put("badge", null);
            userMap.put("level", 0);
            userMap.put("star_ratings", 0);

            //Actual saving

            progressBar.setVisibility(View.VISIBLE);

            usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(setup_activity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        sendToMain();
                    } else {
                        Toast.makeText(setup_activity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        } else {
            Toast.makeText(this, "Error: Invalid Input/s", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isUsernameValid(String user_name) {
        if (!TextUtils.isEmpty(user_name)) {
            return true;
        } else {
            txtUsername.setError("Username is required.");
            txtUsername.requestFocus();
            return false;
        }
    }

    public boolean isFullNameValid(String full_name) {
        if (!TextUtils.isEmpty(full_name)) {
            if (!isAlpha(full_name)) {
                return true;
            } else {
                txtFullname.setError("Invalid full name. Make sure you enter letters only.");
                txtFullname.requestFocus();
                return false;
            }
        } else {
            txtUsername.setError("Full name is required.");
            txtUsername.requestFocus();
            return false;
        }
    }

    public boolean isCourseValid(String course_stud) {
        if (!TextUtils.isEmpty(course_stud)) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }

    public void sendToMain() {
        Intent intent = new Intent(setup_activity.this, HomePageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
