package com.example.avery.chamberofwizards.Books;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class WriteReviewActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference booksRef;
    private String currentUserID;
    private DatabaseReference usersRef;

    private ImageButton reviewBtnSend;
    private EditText txtComment;
    private RatingBar ratingBar;

    private String bookKey;


    String[] pushKeys;
    List<String> k;
    float sum = 0;
    float average;

    float size;
    float oldSummation, newSummation;
    float averageRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        //Firebase variables
        mAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        bookKey = getIntent().getExtras().get("bookKey").toString();

        reviewBtnSend = findViewById(R.id.review_btnComment);
        reviewBtnSend.setEnabled(false);
        reviewBtnSend.setVisibility(View.INVISIBLE);

        txtComment = findViewById(R.id.review_txtReview);
        ratingBar = findViewById(R.id.review_rating);

        k = new ArrayList<String>();

        txtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    reviewBtnSend.setEnabled(true);
                    reviewBtnSend.setVisibility(View.VISIBLE);
                } else {
                    reviewBtnSend.setEnabled(false);
                    reviewBtnSend.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    reviewBtnSend.setEnabled(true);
                    reviewBtnSend.setVisibility(View.VISIBLE);
                } else {
                    reviewBtnSend.setEnabled(false);
                    reviewBtnSend.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        reviewBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txtComment.getText().toString().trim())) {
                    txtComment.requestFocus();
                    reviewBtnSend.setEnabled(false);
                    reviewBtnSend.setVisibility(View.INVISIBLE);
                } else {
                    postReview(bookKey);
                }
            }
        });
    }

    public void postReview(final String bookKey) {
        //Getting current user information
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username, user_image, review;
                    final float rating;

                    review = txtComment.getText().toString();
                    rating = ratingBar.getRating();


                    username = dataSnapshot.child("username").getValue().toString();
                    user_image = dataSnapshot.child("profile_image").getValue().toString();

                    HashMap<String, Object> reviewMap = new HashMap<>();

                    reviewMap.put("uid", currentUserID); //Getting current date
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd");
                    final String saveCurrentDate = currentDate.format(cal.getTime());

                    //Getting current time
                    Calendar calTime = Calendar.getInstance();
                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                    final String saveCurrentTime = currentTime.format(calTime.getTime());

                    reviewMap.put("username", username);
                    reviewMap.put("user_image", user_image);
                    reviewMap.put("date", saveCurrentDate);
                    reviewMap.put("time", saveCurrentTime);
                    reviewMap.put("review", review);
                    reviewMap.put("rating", rating);
                    reviewMap.put("number_of_comments", 0);
                    reviewMap.put("summation_of_comments", 0);
                    reviewMap.put("book_key", bookKey);

                    booksRef.child(bookKey).child("Reviews").child(bookKey + saveCurrentDate + saveCurrentTime).updateChildren(reviewMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(WriteReviewActivity.this, "Review posted successfully!", Toast.LENGTH_SHORT).show();
                                setNumberOfReviews(bookKey);
                                //setReviewSummation(bookKey,rating);
                                setAverageRating(bookKey, setReviewSummation(bookKey, rating));
                            } else {
                                Toast.makeText(WriteReviewActivity.this, "Review not posted. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void setNumberOfReviews(final String bookKey) {
        booksRef.child(bookKey).child("Reviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String number_of_comments;
                    number_of_comments = Long.toString(dataSnapshot.getChildrenCount() * 1);

                    booksRef.child(bookKey).child("number_of_comments").setValue(number_of_comments).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public float setReviewSummation(final String bookKey, final float rating) {
        booksRef.child(bookKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    oldSummation = Float.parseFloat(dataSnapshot.child("summation_of_comments").getValue().toString());
                    newSummation = oldSummation + rating;
                    booksRef.child(bookKey).child("summation_of_comments").setValue(newSummation);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return newSummation;
    }

    public float setAverageRating(String bookKey, final float newSummation) {
        booksRef.child(bookKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    float numberOfReviews;
                    numberOfReviews = Float.parseFloat(dataSnapshot.child("number_of_comments").getValue().toString());
                    averageRating = newSummation / numberOfReviews;
                   // Toast.makeText(WriteReviewActivity.this, Float.toString(numberOfReviews), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return averageRating;
    }
}
