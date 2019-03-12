package com.example.avery.chamberofwizards.Forum;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private String senderUserID, receiverUserID, CURRENT_STATE;
    private DatabaseReference usersRef;
    private DatabaseReference friendRequestRef;
    private DatabaseReference friendsRef;
    private DatabaseReference postsRef;
    private DatabaseReference likesRef;

    private CircleImageView personProfileImg;
    private TextView personStudentNumber;
    private TextView personProfileName;
    private TextView personUsername;
    private TextView personProfileStatus;
    private TextView personCourse;
    private TextView personDOB;
    private TextView personGender;
    private Button sendFriendRequest;
    private Button declineFriendRequest;
    private String saveCurrentDate;
    private String saveCurrentTime;

    private RecyclerView allPersonPosts;

    private boolean likeChecker;

    FirebaseRecyclerAdapter<Posts, personPostViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        //Firebase
        auth = FirebaseAuth.getInstance();
        senderUserID = auth.getCurrentUser().getUid();
        //Geting the id from the FindFriendsActivity
        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverUserID);
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        postsRef = FirebaseDatabase.getInstance().getReference().child("All Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        personProfileImg = findViewById(R.id.person_profile_pic);
        personStudentNumber = findViewById(R.id.person_student_number);
        personProfileName = findViewById(R.id.person_profile_name);
        personUsername = findViewById(R.id.person_username);
        personProfileStatus = findViewById(R.id.person_profile_status);
        personCourse = findViewById(R.id.person_course);
        personDOB = findViewById(R.id.person_DOB);
        personGender = findViewById(R.id.person_gender);
        sendFriendRequest = findViewById(R.id.person_send_friend_request_button);
        declineFriendRequest = findViewById(R.id.person_decline_friend_request);

        CURRENT_STATE = "not_friends";


        //Setting the RecyclerView
        allPersonPosts = (RecyclerView) findViewById(R.id.personPostsContainer);
        allPersonPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        allPersonPosts.setLayoutManager(linearLayoutManager);

        likeChecker = false;


        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String txtPersonImg = dataSnapshot.child("profile_image").getValue().toString();
                    String txtPersonStudentNumber = dataSnapshot.child("student_number").getValue().toString();
                    String txtPersonProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String txtPersonUsername = dataSnapshot.child("username").getValue().toString();
                    String txtPersonProfileStat = dataSnapshot.child("status").getValue().toString();
                    String txtPersonDOB = dataSnapshot.child("dob").getValue().toString();
                    String txtPersonGender = dataSnapshot.child("gender").getValue().toString();
                    String txtPersonCourse = dataSnapshot.child("course").getValue().toString();

                    if (txtPersonCourse.isEmpty()) {
                        txtPersonCourse = "None";
                    }

                    Picasso.get().load(txtPersonImg).into(personProfileImg);
                    personStudentNumber.setText(txtPersonStudentNumber);
                    personProfileName.setText(txtPersonProfileName);
                    personUsername.setText(txtPersonUsername);
                    personProfileStatus.setText(txtPersonProfileStat);
                    personCourse.setText(txtPersonCourse);
                    personDOB.setText(txtPersonDOB);
                    personGender.setText(txtPersonGender);

                    maintenanceOfButtons();
                } else {
                    Toast.makeText(PersonProfileActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        declineFriendRequest.setVisibility(View.INVISIBLE);
        declineFriendRequest.setEnabled(false);

        if (senderUserID.equals(receiverUserID)) {
            sendFriendRequest.setEnabled(false);
            sendFriendRequest.setVisibility(View.INVISIBLE);

            declineFriendRequest.setEnabled(false);
            declineFriendRequest.setVisibility(View.INVISIBLE);
        } else {
            sendFriendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sendFriendRequest.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friends")) {
                        sendFRequest();
                    }
                    if (CURRENT_STATE.equals("request_sent")) {
                        cancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received")) {
                        acceptFriendRequest();
                    }
                }
            });
        }

        displayPosts();
    }

    public void sendFRequest() {
        friendRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendRequestRef.child(receiverUserID).child(senderUserID).child("request_type")
                            .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendFriendRequest.setEnabled(true);
                                CURRENT_STATE = "request_sent";

                                sendFriendRequest.setText("Cancel Friend Request");

                                declineFriendRequest.setVisibility(View.INVISIBLE);
                                declineFriendRequest.setEnabled(false);

                            }
                        }
                    });
                } else {
                    Toast.makeText(PersonProfileActivity.this, "Error sending request. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void maintenanceOfButtons() {
        friendRequestRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiverUserID)) {
                    String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                    if (request_type.equals("sent")) {
                        CURRENT_STATE = "request_sent";
                        sendFriendRequest.setText("Cancel Friend Request");


                        declineFriendRequest.setVisibility(View.INVISIBLE);
                        declineFriendRequest.setEnabled(false);
                    } else if (request_type.equals("received")) {
                        CURRENT_STATE = "request_received";
                        sendFriendRequest.setText("Accept Friend Request");

                        declineFriendRequest.setEnabled(true);
                        declineFriendRequest.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void cancelFriendRequest() {
        friendRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRequestRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendFriendRequest.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequest.setText("Send Friend Request");

                                                declineFriendRequest.setVisibility(View.INVISIBLE);
                                                declineFriendRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(PersonProfileActivity.this, "Error sending request. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void acceptFriendRequest() {
        //Getting current Date

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd", Locale.US);
        saveCurrentDate = currentDate.format(callForDate.getTime());
///
        //Getting current Time

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        friendsRef.child(senderUserID).child(receiverUserID).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendsRef.child(receiverUserID).child(senderUserID).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                friendRequestRef.child(senderUserID).child(receiverUserID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    friendRequestRef.child(receiverUserID).child(senderUserID).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(PersonProfileActivity.this, "Friend request accepted. You are now friends!", Toast.LENGTH_SHORT).show();
                                                                                        sendFriendRequest.setEnabled(true);
                                                                                        CURRENT_STATE = "friends";
                                                                                        sendFriendRequest.setText("UNFRIEND");

                                                                                        declineFriendRequest.setVisibility(View.INVISIBLE);
                                                                                        declineFriendRequest.setEnabled(false);
                                                                                    }

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(PersonProfileActivity.this, "Error accepting friend request. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(PersonProfileActivity.this, "Error accepting friend request. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void displayPosts() {

        /*
         FirebaseRecyclerAdapter<Posts,personPostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, personPostViewHolder>
                (
                        Posts.class,
                        R.layout.all_posts_layout,
                        personPostViewHolder.class,
                        query
                )
        {
            @Override
            protected void populateViewHolder(personPostViewHolder viewHolder, Posts model, final int position)
            {


            }
        };
         */
        Query query = postsRef.orderByChild("uid").startAt(receiverUserID).endAt(receiverUserID);

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query, Posts.class).build();

        FirebaseRecyclerAdapter<Posts, personPostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, personPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull personPostViewHolder viewHolder, int position, @NonNull Posts model) {
                final String postKey = getRef(position).getKey();
                viewHolder.setUser_fullname(model.getUser_fullname());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setProfile_image(model.getProfile_image());
                viewHolder.setImage_url(model.getImage_url());

                viewHolder.setLikeButtonStatus(postKey);
                viewHolder.setCommentNumber(postKey);

                viewHolder.likePostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeChecker = true;

                        likesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (likeChecker) {
                                    if (dataSnapshot.child(postKey).hasChild(senderUserID)) {
                                        //Like already exists
                                        likesRef.child(postKey).child(senderUserID).removeValue();
                                        likeChecker = false;
                                    } else {
                                        //Encode the like
                                        likesRef.child(postKey).child(senderUserID).setValue(true);
                                        likeChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.commentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PersonProfileActivity.this, CommentsActivity.class);
                        intent.putExtra("postKey", postKey);
                        startActivity(intent);
                        finish();
                    }
                });


            }

            @NonNull
            @Override
            public personPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                return new personPostViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        allPersonPosts.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class personPostViewHolder extends RecyclerView.ViewHolder {

        View mView;

        int numberOfLikes;

        ImageButton likePostButton, commentPostButton;
        TextView displayNumberOfLikes;
        TextView numberOfComments;

        DatabaseReference LikesRef;
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference commentsRef;

        public personPostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            likePostButton = (ImageButton) mView.findViewById(R.id.like_button);
            commentPostButton = (ImageButton) mView.findViewById(R.id.comment_button);
            displayNumberOfLikes = (TextView) mView.findViewById(R.id.display_number_of_likes);
            numberOfComments = (TextView) mView.findViewById(R.id.number_of_comments);

            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            commentsRef = FirebaseDatabase.getInstance().getReference().child("All Posts");

            numberOfLikes = 0;
        }

        public void setTime(String time) {
            TextView mTime = mView.findViewById(R.id.posts_time);
            mTime.setText(time);
        }

        public void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.posts_date);
            mDate.setText(date);
        }

        public void setImage_url(String image_url) {
            ImageView imageView = mView.findViewById(R.id.post_image);
            Picasso.get().load(image_url).into(imageView);
        }

        public void setDescription(String description) {
            TextView textView = mView.findViewById(R.id.post_description);
            textView.setText(description);
        }

        public void setProfile_image(String profile_image) {
            CircleImageView circleImageView = mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profile_image).into(circleImageView);
        }

        public void setUser_fullname(String user_fullname) {
            TextView txtFullname = mView.findViewById(R.id.post_user_name);
            txtFullname.setText(user_fullname);
        }

        public void setLikeButtonStatus(final String postKey) {
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postKey).hasChild(currentUserID)) {
                        numberOfLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.gusto);
                        displayNumberOfLikes.setText(Integer.toString(numberOfLikes) + " Likes");
                    } else {
                        numberOfLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.ayaw);
                        displayNumberOfLikes.setText(Integer.toString(numberOfLikes) + " Likes");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setCommentNumber(final String postKey) {
            commentsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(postKey)) {
                        //May comment
                        String commentNumber = dataSnapshot.child(postKey).child("number_of_comments").getValue().toString();
                        numberOfComments.setText(commentNumber + "Comments");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
