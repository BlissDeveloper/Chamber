package com.example.avery.chamberofwizards.Forum;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Questions.Comments;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private String currentUserID;
    private DatabaseReference usersRef;
    private DatabaseReference postsRef;
    private DatabaseReference postsRefCount;
    private DatabaseReference allPostsRef;
    private DatabaseReference rootRef;
    private DatabaseReference likesRef;
    private DatabaseReference commentsRef;

    private EditText commentInputText;
    private RecyclerView commentsList;
    private ImageButton postCommentButton;
    private Toolbar toolbarForumComments;
    private ProgressBar progressBarForumComments;

    private String post_key;
    private String user_name;
    private String userCommentImg;
    private long numbeOfComments;
    private String posterUsername;

    private CircleImageView circleImageViewPosterImage;
    private TextView textViewPosterName;
    private TextView textViewPosterPost;
    private TextView textViewPostDate;
    private TextView textViewPostTime;
    private TextView textViewPosterCourse;
    private Button btnLikePost;
    private Button btnCommentPost;
    private ImageButton imageButtonForumCommentDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        /*
        private CircleImageView circleImageViewPosterImage;
    private TextView textViewPosterName;
    private TextView textViewPosterPost;
    private TextView textViewPostDate;
    private TextView textViewPostTime;
    private TextView textViewPosterCourse;
    private Button btnLikePost;
    private Button btnCommentPost;
    private ImageButton imageButtonForumCommentDots;
         */

        circleImageViewPosterImage = findViewById(R.id.circleImageViewPosterImage);
        textViewPosterName = findViewById(R.id.textViewPosterName);
        textViewPosterPost = findViewById(R.id.textViewPosterPost);
        textViewPostDate = findViewById(R.id.textViewPostDate);
        textViewPostTime = findViewById(R.id.textViewPostTime);
        btnLikePost = findViewById(R.id.btnLikePost);
        btnCommentPost = findViewById(R.id.btnCommentPost);
        imageButtonForumCommentDots = findViewById(R.id.imageButtonDots);

        post_key = getIntent().getExtras().get("postKey").toString();

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("All Posts").child(post_key).child("Comments");
        postsRefCount = FirebaseDatabase.getInstance().getReference().child("All Posts").child(post_key);
        allPostsRef = FirebaseDatabase.getInstance().getReference().child("All Posts");
        rootRef = FirebaseDatabase.getInstance().getReference();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        commentsRef = allPostsRef.child(post_key).child("Comments");

        commentInputText = findViewById(R.id.commentInput);
        postCommentButton = (ImageButton) findViewById(R.id.postCommentButton);
        commentsList = (RecyclerView) findViewById(R.id.comments_list);
        toolbarForumComments = findViewById(R.id.toolbarForumComments);
        progressBarForumComments = findViewById(R.id.progressBarForumComments);

        postCommentButton.setVisibility(View.INVISIBLE);
        postCommentButton.setEnabled(false);

        commentsList.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentsList.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbarForumComments);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnCommentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentInputText.requestFocus();
            }
        });

        btnLikePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });

        commentInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(commentInputText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
                commentInputText.setTranslationY(0f);
                return false;
            }
        });


        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarForumComments.setVisibility(View.VISIBLE);

                usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            user_name = dataSnapshot.child("username").getValue().toString();
                            userCommentImg = dataSnapshot.child("profile_image").getValue().toString();

                            saveCommentToDatabase(user_name, userCommentImg, post_key);

                            commentInputText.setText("");
                        } else {
                            Toast.makeText(CommentsActivity.this, "Error retrieving account information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        commentInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    postCommentButton.setVisibility(View.INVISIBLE);
                    postCommentButton.setEnabled(false);
                } else {
                    postCommentButton.setVisibility(View.VISIBLE);
                    postCommentButton.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    postCommentButton.setVisibility(View.INVISIBLE);
                    postCommentButton.setEnabled(false);
                } else {
                    postCommentButton.setVisibility(View.VISIBLE);
                    postCommentButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadPostInformation(post_key);

        progressBarForumComments.setVisibility(View.GONE);

        maintainNumberOfLikes();
        maintainNumberOfComments();

        Query query = postsRef.orderByChild("timestamp");

        FirebaseRecyclerOptions<ForumComments> options = new FirebaseRecyclerOptions.Builder<ForumComments>().setQuery(query, ForumComments.class).build();

        FirebaseRecyclerAdapter<ForumComments, CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ForumComments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder viewHolder, int position, @NonNull ForumComments model) {
                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setUser_comment_img(model.getUser_comment_img());
            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout, parent, false);
                return new CommentsViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        commentsList.setAdapter(firebaseRecyclerAdapter);
    }

    public void maintainNumberOfComments() {
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int number_of_comments = 0;
                    number_of_comments = (int) dataSnapshot.getChildrenCount();

                    if(number_of_comments > 0) {
                        if(number_of_comments == 1) {
                            btnCommentPost.setText(String.valueOf(number_of_comments) + " comment");
                        }
                        else{
                            btnCommentPost.setText(String.valueOf(number_of_comments) + " comments");
                        }
                    }
                    else {
                        btnCommentPost.setText("No comments yet");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void maintainNumberOfLikes() {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(dataSnapshot.child(post_key).hasChild(currentUserID)) {
                        int number_of_likes = 0;
                        number_of_likes = (int) dataSnapshot.child(post_key).getChildrenCount();
                        btnLikePost.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_filled,0,0,0);

                        if(number_of_likes > 0) {
                            if(number_of_likes == 1) {
                                btnLikePost.setText(String.valueOf(number_of_likes) + " like");
                            }
                            else {
                                btnLikePost.setText(String.valueOf(number_of_likes) + " likes");
                            }
                        }
                        else {
                            btnLikePost.setText("No likes yet");
                        }
                    }
                    else {
                        int number_of_likes = 0;
                        number_of_likes = (int) dataSnapshot.child(post_key).getChildrenCount();

                        if(number_of_likes > 0) {
                            if(number_of_likes == 1) {
                                btnLikePost.setText(String.valueOf(number_of_likes) + " like");
                            }
                            else {
                                btnLikePost.setText(String.valueOf(number_of_likes) + " likes");
                            }
                        }
                        else {
                            btnLikePost.setText("No likes yet");
                        }

                        btnLikePost.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_heart,0,0,0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void likePost() {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(dataSnapshot.child(post_key).hasChild(currentUserID)) {
                        //May like na, so ire-remove:
                        likesRef.child(post_key).child(currentUserID).removeValue();
                    }
                    else {
                        likesRef.child(post_key).child(currentUserID).setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadPostInformation(String post_key) {
        allPostsRef.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String image, name, date, time, desc;

                    image = dataSnapshot.child("profile_image").getValue().toString();
                    name = dataSnapshot.child("user_fullname").getValue().toString();
                    //course = dataSnapshot.child("course").getValue().toString();
                    date = dataSnapshot.child("date").getValue().toString();
                    time = dataSnapshot.child("time").getValue().toString();
                    desc = dataSnapshot.child("description").getValue().toString();

                    Picasso.get().load(image).into(circleImageViewPosterImage);
                    textViewPosterName.setText(name);
                    textViewPostDate.setText(date + " ");
                    textViewPostTime.setText(time);
                    textViewPosterPost.setText(desc);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public CommentsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setUser_comment_img(String user_comment_img) {
            CircleImageView circleImageView = mView.findViewById(R.id.commentUserImg);
            Picasso.get().load(user_comment_img).into(circleImageView);
        }

        public void setUsername(String username) {
            TextView myUsername = mView.findViewById(R.id.comment_username);
            myUsername.setText(username);
        }

        public void setComment(String comment) {
            TextView myComment = mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }

        public void setTime(String time) {
            TextView commentTime = mView.findViewById(R.id.comment_time);
            commentTime.setText(" at " + time);
        }

        public void setDate(String date) {
            TextView commentDate = mView.findViewById(R.id.comment_date);
            commentDate.setText(date);
        }

    }

    public void saveCommentToDatabase(final String user_name, final String userCommentImg, final String postKey) {
        //Getting current date
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd");
        final String saveCurrentDate = currentDate.format(cal.getTime());

        //Getting current time
        Calendar calTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        final String saveCurrentTime = currentTime.format(calTime.getTime());

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss"); //2016-11-16 06:43:19.77
        long timestamp = System.currentTimeMillis();

        final String randomCommentKey = currentUserID + saveCurrentDate + saveCurrentTime;

        HashMap<String, Object> commentsMap = new HashMap<>();
        commentsMap.put("uid", currentUserID);
        commentsMap.put("comment", commentInputText.getText().toString());
        commentsMap.put("date", saveCurrentDate);
        commentsMap.put("time", saveCurrentTime);
        commentsMap.put("username", user_name);
        commentsMap.put("user_comment_img", userCommentImg);
        commentsMap.put("timestamp", timestamp);

        postsRef.push().updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    saveNotification(postKey, saveCurrentDate, saveCurrentTime, userCommentImg, user_name);
                } else {
                    Toast.makeText(CommentsActivity.this, "Error posting comment, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Getting the number of comments

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    numbeOfComments = dataSnapshot.getChildrenCount();

                    postsRefCount.child("number_of_comments").setValue(numbeOfComments)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CommentsActivity.this, "Count added.", Toast.LENGTH_SHORT).show();
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

    public void saveLatestCommentTransaction(final String post_key) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss"); //2016-11-16 06:43:19.77
        long timestamp = System.currentTimeMillis();

        try {

            Map<String, Object> map = new ArrayMap<>();
            map.put("latest_transaction", timestamp);

            allPostsRef.child(post_key).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CommentsActivity.this, "Comment successfully posted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Avery", task.getException().getMessage());
                    }
                    progressBarForumComments.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.e("Avery", e.getMessage());
        }
    }

    public void saveNotification(final String pKey, final String date, final String time, final String commenterImg, final String uName) {
        allPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    posterUsername = dataSnapshot.child(pKey).child("uid").getValue().toString();

                    if (posterUsername != null) {
                        HashMap<String, Object> notificationMap = new HashMap<>();
                        notificationMap.put("poster", posterUsername);
                        notificationMap.put("commenter", uName);
                        notificationMap.put("date", date);
                        notificationMap.put("time", time);
                        notificationMap.put("commenter_image", commenterImg);
                        notificationMap.put("post_key", pKey);

                        rootRef.child("Notifications").child(pKey + time + date + "notif").updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    saveLatestCommentTransaction(pKey);
                                } else {
                                    Toast.makeText(CommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}