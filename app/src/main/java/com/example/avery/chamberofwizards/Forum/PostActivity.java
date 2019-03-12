package com.example.avery.chamberofwizards.Forum;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.theartofdev.edmodo.cropper.CropImage;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.grpc.Compressor;

public class PostActivity extends AppCompatActivity {

    private ImageView selectImage;
    private EditText txtPostDesc;
    private Button btnPost;
    private static final int Gallery_Pick = 1;
    private Uri imageUri;
    private String desc;
    private String saveCurrentDate, saveCurrentTime, postRandomName;
    private ProgressBar pBarPost;
    private String imageURL;
    private String currentUserID;
    private Toolbar pToolbar;
    private ImageButton postAddImage;
    private FloatingActionButton btnClose;

    //Firebase
    private FirebaseAuth mAuth;
    private StorageReference postsImageReference;
    private DatabaseReference postsRef;
    private DatabaseReference usersRef;
    private DatabaseReference allPostsRef;

    private Bitmap compressedPostImage;

    private long countPosts = 0;
    private byte[] compressedImageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        //Firebase
        mAuth = FirebaseAuth.getInstance();
        postsImageReference = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserID = mAuth.getCurrentUser().getUid();
        //postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        allPostsRef = FirebaseDatabase.getInstance().getReference().child("All Posts");

        postAddImage = findViewById(R.id.btnAddImage);
        selectImage = (ImageView) findViewById(R.id.select_image);
        txtPostDesc = (EditText) findViewById(R.id.txtPostDesc);
        btnPost = (Button) findViewById(R.id.btnPost);
        pBarPost = (ProgressBar) findViewById(R.id.pBar_post);
        pToolbar = findViewById(R.id.post_toolbar);
        setSupportActionBar(pToolbar);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnClose = findViewById(R.id.btnClose);

        btnPost.setVisibility(View.INVISIBLE);
        btnPost.setEnabled(false);

        selectImage.setEnabled(false);
        selectImage.setVisibility(View.INVISIBLE);

        btnClose.setEnabled(false);
        btnClose.setVisibility(View.INVISIBLE);

        //Selecting an image:
        postAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnClose.isEnabled() && btnClose.getVisibility() == View.VISIBLE) {
                    deleteImage();
                }
            }
        });

        txtPostDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    btnPost.setEnabled(false);
                    btnPost.setVisibility(View.INVISIBLE);
                } else {
                    btnPost.setEnabled(true);
                    btnPost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    btnPost.setEnabled(false);
                    btnPost.setVisibility(View.INVISIBLE);
                } else {
                    btnPost.setEnabled(true);
                    btnPost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Posting

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pBarPost.setVisibility(View.VISIBLE);
                btnPost.setEnabled(false);
                try {
                    validatePostInfo();
                } catch (Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    public void deleteImage() {
        selectImage.setImageURI(null);

        selectImage.setEnabled(false);
        selectImage.setVisibility(View.INVISIBLE);

        btnClose.setEnabled(false);
        btnClose.setVisibility(View.INVISIBLE);
    }

    //Getting the image

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();

            selectImage.setImageURI(imageUri);
            selectImage.setVisibility(View.VISIBLE);
            btnClose.setEnabled(true);
            btnClose.setVisibility(View.VISIBLE);
            btnPost.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(this, "Error accessing image", Toast.LENGTH_SHORT).show();
        }
    }

    public void validatePostInfo() {
        String desc, imgUri;
        desc = txtPostDesc.getText().toString();

        if (TextUtils.isEmpty(desc)) {
            txtPostDesc.setError("Please say something");
        }

        if (imageUri == null) {
            savePostInfoToDB();
        } else {
            pBarPost.setVisibility(View.VISIBLE);
            storeImage(compressedImageData);
            pBarPost.setVisibility(View.INVISIBLE);
        }


    }

    public void storeImage(byte[] bytes) {

        //Getting current Date

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd", Locale.US);
        saveCurrentDate = currentDate.format(callForDate.getTime());
///
        //Getting current Time

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = postsImageReference.child("Post Images").child(postRandomName + ".jpg");
        final StorageReference postImageThumbRef = postsImageReference.child("Post Images").child(postRandomName + ".jpg");

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageURL = uri.toString();
                            savePostInfoToDB();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(PostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void savePostInfoToDB() {

        allPostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    countPosts = dataSnapshot.getChildrenCount();
                } else {
                    countPosts = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    String userImage = dataSnapshot.child("profile_image").getValue().toString();
                    desc = txtPostDesc.getText().toString();

                    Map<String, Object> postsMap = new ArrayMap<>();

                    if (imageURL != null) {
                        postsMap.put("uid", currentUserID);
                        postsMap.put("date", saveCurrentDate);
                        postsMap.put("time", saveCurrentTime);
                        postsMap.put("image_url", imageURL);
                        postsMap.put("profile_image", userImage);
                        postsMap.put("user_fullname", userFullName);
                        postsMap.put("description", desc);
                        postsMap.put("counter", countPosts);
                        postsMap.put("number_of_comments", 0);
                        postsMap.put("latest_transaction", null);
                    } else {
                        Calendar callForDate = Calendar.getInstance();
                        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd", Locale.US);
                        saveCurrentDate = currentDate.format(callForDate.getTime());
///
                        //Getting current Time
                        Calendar callForTime = Calendar.getInstance();
                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");
                        saveCurrentTime = currentTime.format(callForTime.getTime());
                        postsMap.put("uid", currentUserID);
                        postsMap.put("date", saveCurrentDate);
                        postsMap.put("time", saveCurrentTime);
                        postsMap.put("image_url", null);
                        postsMap.put("profile_image", userImage);
                        postsMap.put("user_fullname", userFullName);
                        postsMap.put("description", desc);
                        postsMap.put("counter", countPosts);
                        postsMap.put("latest_transaction", null);
                    }

                    allPostsRef.push().updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PostActivity.this, "Posted succesfully!", Toast.LENGTH_SHORT).show();
                                sendToMain();
                            } else {
                                Toast.makeText(PostActivity.this, "Error posting", Toast.LENGTH_SHORT).show();
                            }
                            pBarPost.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(PostActivity.this, "User error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendToMain() {
        Intent intent = new Intent(PostActivity.this, MainActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
