package com.example.avery.chamberofwizards.Forum;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClickNotificationActivity extends AppCompatActivity {

    private Toolbar notificationToolbar;
    private String postKey;
    private ConstraintLayout constraintLayout;

    private DatabaseReference postsRef;
    private DatabaseReference likesRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference usersRef;
    private DatabaseReference commentPostsRef;
    private DatabaseReference rootRef;

    //Views
    private CircleImageView postUserImg;
    private TextView txtPostUsername;
    private TextView txtDateAndTime;
    private TextView txtDesc;
    private TextView txtNumberOfLikes;
    private ImageView notifPostImage;
    private RecyclerView allComments;
    private ImageButton likeButton;

    private int numberOfLikes;
    private boolean likeChecker = false;

    private EditText txtNotifComment;
    private ImageButton btnNofifPostComment;
    private ImageButton btnNotifComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_notification);

        notificationToolbar = findViewById(R.id.notification_toolbar);
        setSupportActionBar(notificationToolbar);
        getSupportActionBar().setTitle("Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postKey = getIntent().getExtras().get("post_key").toString();
        commentPostsRef = FirebaseDatabase.getInstance().getReference().child("All Posts").child(postKey).child("Comments");
        postsRef = FirebaseDatabase.getInstance().getReference().child("All Posts");
        rootRef = FirebaseDatabase.getInstance().getReference();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        constraintLayout = findViewById(R.id.constraintLayoutMain);

        postUserImg = findViewById(R.id.imgPosterImg);
        txtPostUsername = findViewById(R.id.txtPostFullname);
        txtDateAndTime = findViewById(R.id.txtPosterDate);
        txtDesc = findViewById(R.id.txtDesc);
        txtNumberOfLikes = findViewById(R.id.txtNumberOfLikes);
        notifPostImage = findViewById(R.id.txtPosterPostImg);
        likeButton = findViewById(R.id.likeButtonNotif);

        allComments = findViewById(R.id.notificationsAllComments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        allComments.setLayoutManager(linearLayoutManager);

        txtNotifComment = findViewById(R.id.notif_txt_comment);
        btnNofifPostComment = findViewById(R.id.notif_send_comment);
        btnNotifComment = findViewById(R.id.btnComment);

        loadPost(postKey);
        loadComments(postKey);
        setLikeStatus(postKey);

        btnNofifPostComment.setVisibility(View.INVISIBLE);
        btnNofifPostComment.setEnabled(false);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeChecker = true;

                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (likeChecker == true) {
                                if (dataSnapshot.child(postKey).hasChild(currentUserID)) {
                                    //May like na ng current user
                                    //Unliking system
                                    likesRef.child(postKey).child(currentUserID).removeValue();
                                    likeChecker = false;
                                } else {
                                    //Wala pang like ng current user
                                    likesRef.child(postKey).child(currentUserID).setValue(true);
                                    likeChecker = false;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        btnNotifComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtNotifComment.requestFocus();
            }
        });

        txtNotifComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    //Walang laman
                    btnNofifPostComment.setEnabled(false);
                    btnNofifPostComment.setVisibility(View.INVISIBLE);
                } else {
                    btnNofifPostComment.setEnabled(true);
                    btnNofifPostComment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    //Walang laman
                    btnNofifPostComment.setEnabled(false);
                    btnNofifPostComment.setVisibility(View.INVISIBLE);
                } else {
                    btnNofifPostComment.setEnabled(true);
                    btnNofifPostComment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnNofifPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userName = dataSnapshot.child("username").getValue().toString();
                            String userCommentImg = dataSnapshot.child("profile_image").getValue().toString();

                            saveCommentToDatabase(userName, userCommentImg, postKey);

                            txtNotifComment.setText(" ");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    public void saveCommentToDatabase(final String user_name, final String user_img, final String post_key) {
        //Getting current date

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd");
        final String saveCurrentDate = currentDate.format(cal.getTime());

        //Getting current time

        Calendar calTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        final String saveCurrentTime = currentTime.format(calTime.getTime());

        final String randomCommentKey = currentUserID + saveCurrentDate + saveCurrentTime;

        HashMap<String, Object> commentsMap = new HashMap<>();
        commentsMap.put("uid", currentUserID);
        commentsMap.put("comment", txtNotifComment.getText().toString());
        commentsMap.put("date", saveCurrentDate);
        commentsMap.put("time", saveCurrentTime);
        commentsMap.put("username", user_name);
        commentsMap.put("user_comment_img", user_img);

        commentPostsRef.child(randomCommentKey).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    saveNotification(post_key, saveCurrentDate, saveCurrentTime, user_img, user_name);
                } else {
                    Toast.makeText(ClickNotificationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveNotification(final String post_key, final String date, final String time, final String img, final String username) {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String posterID = dataSnapshot.child(postKey).child("uid").getValue().toString();

                if (posterID != null) {
                    HashMap<String, Object> notificationMap = new HashMap<>();
                    notificationMap.put("poster", posterID);
                    notificationMap.put("commenter", username);
                    notificationMap.put("date", date);
                    notificationMap.put("time", time);
                    notificationMap.put("commenter_image", img);
                    notificationMap.put("post_key", post_key);

                    rootRef.child("Notifications").child(post_key + time + date + "notif").updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ClickNotificationActivity.this, "Notification saved.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ClickNotificationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();

        loadComments(postKey);

    }

    public void loadPost(final String postKey) {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(postKey)) {
                        final String userImage, userName, postTime, postDate, postImage, desc;

                        userImage = dataSnapshot.child(postKey).child("profile_image").getValue().toString();
                        userName = dataSnapshot.child(postKey).child("user_fullname").getValue().toString();
                        postTime = dataSnapshot.child(postKey).child("time").getValue().toString();
                        postDate = dataSnapshot.child(postKey).child("date").getValue().toString();
                        postImage = dataSnapshot.child(postKey).child("image_url").getValue().toString();
                        desc = dataSnapshot.child(postKey).child("description").getValue().toString();

                        likesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild(postKey)) {
                                        numberOfLikes = (int) dataSnapshot.child(postKey).getChildrenCount();

                                        if (postDate != null) {
                                            //May pic
                                            Picasso.get().load(userImage).into(postUserImg);
                                            txtPostUsername.setText(userName);
                                            txtDateAndTime.setText(postDate + " at " + postTime);
                                            txtDesc.setText(desc);
                                            txtNumberOfLikes.setText(Integer.toString(numberOfLikes));
                                            Picasso.get().load(postImage).into(notifPostImage);
                                        } else {
                                            //Walang pic
                                            Picasso.get().load(userImage).into(postUserImg);
                                            txtPostUsername.setText(userName);
                                            txtDateAndTime.setText(postDate + " at " + postTime);
                                            txtDesc.setText(desc);
                                            txtNumberOfLikes.setText(Integer.toString(numberOfLikes));

                                            //Removing view
                                            constraintLayout.removeView(notifPostImage);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

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

    public void loadComments(final String postKey) {

        /*
        FirebaseRecyclerAdapter<Comments,CommentsHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments, CommentsHolder>
                (
                        Comments.class,
                        R.layout.all_comments_layout,
                        CommentsHolder.class,
                        query
                )
        {
            @Override
            protected void populateViewHolder(CommentsHolder viewHolder, Comments model, int position)
            {
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setUser_comment_img(model.getUser_comment_img());
            }
        };
         */
        Query query = postsRef.child(postKey).child("Comments").orderByChild("date");

        FirebaseRecyclerOptions<ForumComments> options = new FirebaseRecyclerOptions.Builder<ForumComments>().setQuery(query, ForumComments.class).build();

        FirebaseRecyclerAdapter<ForumComments, CommentsHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ForumComments, CommentsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsHolder viewHolder, int position, @NonNull ForumComments model) {
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setUser_comment_img(model.getUser_comment_img());
            }

            @NonNull
            @Override
            public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout, parent, false);
                return new CommentsHolder(view);
            }
        };

        allComments.setAdapter(firebaseRecyclerAdapter);
    }

    public void setLikeStatus(final String key) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(key).hasChild(currentUserID)) {
                        //May like sa database
                        numberOfLikes = (int) dataSnapshot.child(key).getChildrenCount();
                        likeButton.setImageResource(R.drawable.gusto);
                        txtNumberOfLikes.setText(Integer.toString(numberOfLikes));
                    } else {
                        //Walang like sa database
                        numberOfLikes = (int) dataSnapshot.child(key).getChildrenCount();
                        likeButton.setImageResource(R.drawable.ayaw);
                        txtNumberOfLikes.setText(Integer.toString(numberOfLikes));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class CommentsHolder extends RecyclerView.ViewHolder {
        View mView;

        private DatabaseReference likesReference;
        private FirebaseAuth firebaseAuth;
        private String currentUserId;

        public CommentsHolder(View itemView) {
            super(itemView);

            mView = itemView;


            likesReference = FirebaseDatabase.getInstance().getReference().child("Likes");
            firebaseAuth = FirebaseAuth.getInstance();
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }

        public void setUser_comment_img(String user_comment_img) {
            CircleImageView circleImageView = mView.findViewById(R.id.commentUserImg);
            Picasso.get().load(user_comment_img).into(circleImageView);
        }

        public void setComment(String comment) {
            TextView commentView = mView.findViewById(R.id.comment_text);
            commentView.setText(comment);
        }

        public void setDate(String date) {
            TextView dateView = mView.findViewById(R.id.comment_date);
            dateView.setText(date + " at ");
        }

        public void setTime(String time) {
            TextView timeView = mView.findViewById(R.id.comment_time);
            timeView.setText(time);
        }

        public void setUsername(String username) {
            TextView usernameView = mView.findViewById(R.id.comment_username);
            usernameView.setText(username);
        }
    }

}
