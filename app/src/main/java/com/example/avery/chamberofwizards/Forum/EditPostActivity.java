package com.example.avery.chamberofwizards.Forum;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Books.ClickBookActivity;
import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class EditPostActivity extends AppCompatActivity {
    private DatabaseReference postsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference booksRef;

    //XML Views
    private Toolbar toolbarEditPost;
    private Button btnEditPost;
    private EditText editTextPostToEdit;
    private ImageView imageViewPostImageToEdit;
    private ProgressBar progressbarEditPost;
    private RatingBar ratingBarEditPost;

    private String key;
    private int edit_flag;
    private String book_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        postsRef = FirebaseDatabase.getInstance().getReference().child("All Posts");
        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");

        toolbarEditPost = findViewById(R.id.toolbarEditPost);
        btnEditPost = findViewById(R.id.btnSaveEditedPost);
        editTextPostToEdit = findViewById(R.id.editTextPostToEdit);
        imageViewPostImageToEdit = findViewById(R.id.imageViewPostImageToEdit);
        progressbarEditPost = findViewById(R.id.progressBarEditPost);
        ratingBarEditPost = findViewById(R.id.ratingBarEditPost);

        setSupportActionBar(toolbarEditPost);

        getSupportActionBar().setTitle("Edit Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        key = getIntent().getExtras().get("key").toString();
        edit_flag = Integer.parseInt(getIntent().getExtras().get("edit_flag").toString());

        switch (edit_flag) {
            case 1: //Book review
                book_key = getIntent().getExtras().get("book_key").toString();
                break;
        }

        btnEditPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (edit_flag) {
                    case 0:
                        updateForumPost();
                        break;
                    case 1:
                        updateBookReviewPost();
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadPostData();

        progressbarEditPost.setVisibility(View.GONE);
        imageViewPostImageToEdit.setVisibility(View.GONE);
        ratingBarEditPost.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void updateBookData(final float updated_rating, final float old_rating) {
        booksRef.child(book_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    float new_minus_old_rating = 0, summation_of_reviews = 0, new_average_rating = 0, number_of_reviews = 0;

                    summation_of_reviews = Float.parseFloat(dataSnapshot.child("summation_of_comments").getValue().toString());
                    number_of_reviews = Float.parseFloat(dataSnapshot.child("number_of_comments").getValue().toString());

                    new_minus_old_rating = updated_rating - old_rating;

                    summation_of_reviews = summation_of_reviews + new_minus_old_rating;

                    new_average_rating = summation_of_reviews / number_of_reviews;

                    Map<String, Object> map = new ArrayMap<>();
                    map.put("summation_of_comments", summation_of_reviews);
                    map.put("average_rating", new_average_rating);

                    booksRef.child(book_key).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditPostActivity.this, "Review edited successfully!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(EditPostActivity.this, ClickBookActivity.class);
                                intent.putExtra("bookKey", book_key);
                                startActivity(intent);
                            } else {
                                Log.e("Avery", task.getException().getMessage());
                            }
                            progressbarEditPost.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateBookReviewPost() {
        progressbarEditPost.setVisibility(View.VISIBLE);

        final float updated_rating = ratingBarEditPost.getRating();
        final String updated_review = editTextPostToEdit.getText().toString();

        booksRef.child(book_key).child("Reviews").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final float old_rating = Float.parseFloat(dataSnapshot.child("rating").getValue().toString());

                    Map<String, Object> map = new ArrayMap<>();
                    map.put("review", updated_review);
                    map.put("rating", updated_rating);

                    booksRef.child(book_key).child("Reviews").child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                updateBookData(updated_rating, old_rating);
                            } else {
                                Log.e("Avery", task.getException().getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateForumPost() {
        progressbarEditPost.setVisibility(View.VISIBLE);

        String updated_description = editTextPostToEdit.getText().toString();

        Map<String, Object> map = new ArrayMap<>();
        map.put("description", updated_description);

        postsRef.child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditPostActivity.this, "Your post is successfully updated!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditPostActivity.this, MainActivity2.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(EditPostActivity.this, "Your post is not updated, please try again.", Toast.LENGTH_SHORT).show();
                }
                progressbarEditPost.setVisibility(View.GONE);
            }
        });
    }

    public void loadPostData() {
        switch (edit_flag) {
            case 0: //Forum post key
                loadForumPost();
                break;
            case 1: //Book review
                loadBookReviewPost();
                break;
        }
    }

    public void loadBookReviewPost() {
        booksRef.child(book_key).child("Reviews").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Rating, review mismo, image (if any):
                    float rating;
                    String review, review_image;

                    rating = Float.parseFloat(dataSnapshot.child("rating").getValue().toString());
                    review = dataSnapshot.child("review").getValue().toString();

                    if (dataSnapshot.hasChild("book_review_image")) {
                        review_image = dataSnapshot.child("book_review_image").getValue().toString();
                    } else {
                        review_image = null;
                    }

                    ratingBarEditPost.setRating(rating);
                    ratingBarEditPost.setVisibility(View.VISIBLE);
                    editTextPostToEdit.setText(review);
                    Picasso.get().load(review_image).into(imageViewPostImageToEdit);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadForumPost() {
        postsRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Post caption and image (if any)
                    String post_caption, post_image;

                    post_caption = dataSnapshot.child("description").getValue().toString();

                    if (dataSnapshot.hasChild("image_url")) {
                        //May pic yung post:
                        post_image = dataSnapshot.child("image_url").getValue().toString();
                    } else {
                        post_image = null;
                    }

                    editTextPostToEdit.setText(post_caption);
                    Picasso.get().load(post_image).into(imageViewPostImageToEdit);
                    imageViewPostImageToEdit.setVisibility(View.VISIBLE);
                } else {
                    Log.e("Avery", "No forum post exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
