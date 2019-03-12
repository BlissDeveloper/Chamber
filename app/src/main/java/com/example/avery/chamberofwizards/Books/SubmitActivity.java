package com.example.avery.chamberofwizards.Books;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class SubmitActivity extends AppCompatActivity {

    //Firebase variables
    FirebaseStorage firebaseStorage;
    FirebaseDatabase database;
    private Uri pdfUri, bookCoverUri;
    private DatabaseReference bookRequestsRef;
    private DatabaseReference usersRef;
    private String currentUserID;
    private ProgressDialog progressDialog;
    private StorageReference bookCoversRef;

    private String pdfUrl;

    private FloatingActionButton submitButton;

    private Toolbar toolbar;

    private ImageButton btnSelectPDF;
    private ImageButton btnRemovePDF;
    private TextInputEditText txtBookTitle;
    private Button btnUpload;
    private TextView txtSelectionStatus;

    private ImageView submitBookCover;

    private final int Gallery_Pick = 11;
    private String bookCoverURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        bookRequestsRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseStorage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        bookCoversRef = FirebaseStorage.getInstance().getReference().child("Book Covers");

        submitButton = findViewById(R.id.submitButton);

        btnSelectPDF = findViewById(R.id.btnSelectPDF);
        btnRemovePDF = findViewById(R.id.btnRemove);
        txtBookTitle = findViewById(R.id.txtBookTitle);
        btnUpload = findViewById(R.id.btnUpload);
        txtSelectionStatus = findViewById(R.id.txtSelectionStatus);
        submitBookCover = findViewById(R.id.submitBookCover);

        btnRemovePDF.setVisibility(View.INVISIBLE);
        btnRemovePDF.setEnabled(false);

        toolbar = findViewById(R.id.submitBookToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Submit a Book");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submitBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pdfUri != null && bookCoverUri != null) {
                    uploadFile(pdfUri, bookCoverUri);
                } else {
                    if (pdfUri == null) {
                        Toast.makeText(SubmitActivity.this, "Please select a PDF file", Toast.LENGTH_SHORT).show();
                    }
                    if (bookCoverUri == null) {
                        Toast.makeText(SubmitActivity.this, "Please select an image for the cover", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        btnSelectPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SubmitActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    selectPDF();
                } else {

                    ActivityCompat.requestPermissions(SubmitActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking for permission

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPDF();
        } else {
            Toast.makeText(this, "Kindly provide permission.", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT); //To fetch the files
        startActivityForResult(intent, 26);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 26 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData(); // Return the uri of the selected file

            btnSelectPDF.setVisibility(View.INVISIBLE);
            btnSelectPDF.setEnabled(false);
            btnRemovePDF.setVisibility(View.VISIBLE);
            btnRemovePDF.setEnabled(true);

            txtSelectionStatus.setText("Remove file");

        } else if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            bookCoverUri = data.getData();
            submitBookCover.setImageURI(bookCoverUri);
        }
    }

    public void uploadFile(final Uri pdfUri, final Uri bookCoverUri) {


        final String fileName = System.currentTimeMillis() + "";

        final StorageReference storageReference = firebaseStorage.getReference();// Root path

        if (!TextUtils.isEmpty(txtBookTitle.getText().toString().trim())) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Uploading file...");
            progressDialog.setProgress(0);
            progressDialog.show();

            storageReference.child("Uploads").child(fileName).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //pdfUrl = taskSnapshot.getDownloadUrl().toString();
                    storageReference.child("Uploads").child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            pdfUrl = uri.toString();
                            Calendar callForDate = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd", Locale.US);
                            final String saveCurrentDate = currentDate.format(callForDate.getTime());
///
                            //Getting current Time
                            Calendar callForTime = Calendar.getInstance();
                            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");
                            final String saveCurrentTime = currentTime.format(callForTime.getTime());

                            bookCoversRef.child(fileName).putFile(bookCoverUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    if (task.isSuccessful()) {

                                        //bookCoverURL = task.getResult().getDownloadUrl().toString();

                                        bookCoversRef.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                bookCoverURL = uri.toString();

                                                usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {

                                                            String username, userImage, userCourse, userStudentNumber;

                                                            username = dataSnapshot.child("username").getValue().toString();
                                                            userImage = dataSnapshot.child("profile_image").getValue().toString();
                                                            userCourse = dataSnapshot.child("course").getValue().toString();
                                                            userStudentNumber = dataSnapshot.child("student_number").getValue().toString();

                                                            HashMap<String, Object> bookRequestsMap = new HashMap<>();

                                                            bookRequestsMap.put("uid", currentUserID);
                                                            bookRequestsMap.put("book_url", pdfUrl);
                                                            bookRequestsMap.put("book_file_name",fileName);
                                                            bookRequestsMap.put("book_cover", bookCoverURL);
                                                            bookRequestsMap.put("book_title", txtBookTitle.getText().toString());
                                                            bookRequestsMap.put("student_number", userStudentNumber);
                                                            bookRequestsMap.put("username", username);
                                                            bookRequestsMap.put("course", userCourse);
                                                            bookRequestsMap.put("user_image", userImage);
                                                            bookRequestsMap.put("time", saveCurrentTime);
                                                            bookRequestsMap.put("date", saveCurrentDate);
                                                            bookRequestsMap.put("number_of_comments","0");
                                                            bookRequestsMap.put("summation_of_comments",0);
                                                            bookRequestsMap.put("average_rating", 0);

                                                            bookRequestsRef.push().updateChildren(bookRequestsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(SubmitActivity.this, "Book submitted successfully!", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Toast.makeText(SubmitActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    progressDialog.dismiss();
                                                                }
                                                            });

                                                        } else {
                                                            Toast.makeText(SubmitActivity.this, "Invalid user", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        });



                                    } else {
                                        Toast.makeText(SubmitActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(SubmitActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    //Track the progress of the upload
                    int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress(currentProgress);
                }
            });
        }
        else {
            txtBookTitle.setError("Please enter a book title.");
        }

    }

    public void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }
}
