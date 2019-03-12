package com.example.avery.chamberofwizards.Forum;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private android.support.v7.widget.Toolbar mToolbar;
    private CircleImageView navProfileImage;
    private TextView navProfileUsername;
    private FloatingActionButton btnAddPost;
    private Boolean likeChecker = false;

    //Firebase variables

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String currentUserID;
    private DatabaseReference allPostsRef;
    private DatabaseReference likesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");


        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        navProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        navProfileUsername = (TextView) navView.findViewById(R.id.nav_user_full_nav);

        btnAddPost = (FloatingActionButton) findViewById(R.id.button_add_post);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        //User table
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        allPostsRef = FirebaseDatabase.getInstance().getReference().child("All Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        //Retrieving data
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("fullname")) {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        navProfileUsername.setText(fullname);
                    }
                    if (dataSnapshot.hasChild("profile_image")) {
                        String profile_picture = dataSnapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(profile_picture).into(navProfileImage);
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });


        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToPost();
            }
        });

        DisplayAllUsersPost();
    }

    private void DisplayAllUsersPost() {

        /*
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                (
                        Posts.class,
                        R.layout.all_posts_layout,
                        PostsViewHolder.class,
                        sortPostInDescOrder
                ) {
            @Override
            protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position) {

                //Unique ID for the post
                final String postKey = getRef(position).getKey();

                viewHolder.setUser_fullname(model.getUser_fullname());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setDescription(model.getDescription());


                viewHolder.setLikeButtonStatus(postKey);
                viewHolder.setNumberOfComments(postKey);
                viewHolder.setProfile_image(model.getProfile_image());

                if (model.getImage_url() != null) {
                    viewHolder.setImage_url(model.getImage_url());
                } else {
                    // viewHolder.mView.findViewById(R.id.post_image).setVisibility(View.GONE);
                    viewHolder.linearLayout.removeView(viewHolder.postImage);
                    //  viewHolder.setHeightOfCard();

                }


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent clickPostIntent = new Intent(MainActivity.this, ClickPostActivity.class);
                        clickPostIntent.putExtra("postKey", postKey);
                        startActivity(clickPostIntent);
                    }
                });

                viewHolder.commentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent clickComment = new Intent(MainActivity.this, CommentsActivity.class);
                        clickComment.putExtra("postKey", postKey);
                        startActivity(clickComment);
                    }
                });

                viewHolder.likePostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeChecker = true;

                        likesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (likeChecker.equals(true)) {
                                    if (dataSnapshot.child(postKey).hasChild(currentUserID)) {
                                        //Like already exists
                                        likesRef.child(postKey).child(currentUserID).removeValue();
                                        likeChecker = false;
                                    } else {
                                        likesRef.child(postKey).child(currentUserID).setValue(true);
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

            }
        };
         */

        Query sortPostInDescOrder = allPostsRef.orderByChild("number_of_comments");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(sortPostInDescOrder, Posts.class).build();

        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder viewHolder, int position, @NonNull Posts model) {
                //Unique ID for the post
                final String postKey = getRef(position).getKey();

                viewHolder.setUser_fullname(model.getUser_fullname());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setDescription(model.getDescription());


                viewHolder.setLikeButtonStatus(postKey);
                viewHolder.setNumberOfComments(postKey);
                viewHolder.setProfile_image(model.getProfile_image());

                if (model.getImage_url() != null) {
                    viewHolder.setImage_url(model.getImage_url());
                } else {
                    // viewHolder.mView.findViewById(R.id.post_image).setVisibility(View.GONE);
                    viewHolder.linearLayout.removeView(viewHolder.postImage);
                    //  viewHolder.setHeightOfCard();

                }


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent clickPostIntent = new Intent(MainActivity.this, ClickPostActivity.class);
                        clickPostIntent.putExtra("postKey", postKey);
                        startActivity(clickPostIntent);
                    }
                });

                viewHolder.commentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent clickComment = new Intent(MainActivity.this, CommentsActivity.class);
                        clickComment.putExtra("postKey", postKey);
                        startActivity(clickComment);
                    }
                });

                viewHolder.likePostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeChecker = true;

                        likesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (likeChecker.equals(true)) {
                                    if (dataSnapshot.child(postKey).hasChild(currentUserID)) {
                                        //Like already exists
                                        likesRef.child(postKey).child(currentUserID).removeValue();
                                        likeChecker = false;
                                    } else {
                                        likesRef.child(postKey).child(currentUserID).setValue(true);
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

            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                return new PostsViewHolder(view);
            }
        };


        postList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        ImageButton likePostButton, commentPostButton;
        TextView displayNumberOfLikes;
        TextView numberOfComments;
        int countLikes;
        String countComments;
        String currentUserId;
        DatabaseReference LikesRef;
        DatabaseReference commentsRef;
        ImageView postImage;

        CardView post_card;
        LinearLayout linearLayout;
        ConstraintLayout consLayout;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            likePostButton = (ImageButton) mView.findViewById(R.id.like_button);
            commentPostButton = (ImageButton) mView.findViewById(R.id.comment_button);
            displayNumberOfLikes = (TextView) mView.findViewById(R.id.display_number_of_likes);
            numberOfComments = (TextView) mView.findViewById(R.id.number_of_comments);
            post_card = (CardView) mView.findViewById(R.id.postCardView);
            linearLayout = (LinearLayout) mView.findViewById(R.id.linearPost);
            consLayout = (ConstraintLayout) mView.findViewById(R.id.consLayout);
            postImage = mView.findViewById(R.id.post_image);
            commentsRef = FirebaseDatabase.getInstance().getReference().child("All Posts");

            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setNumberOfComments(final String postKey) {
            commentsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(postKey)) {
                        // countComments =  dataSnapshot.child(postKey).child("number_of_comments").getValue().toString();
                        if (dataSnapshot.child(postKey).hasChild("number_of_comments")) {
                            countComments = dataSnapshot.child(postKey).child("number_of_comments").getValue().toString();
                            numberOfComments.setText(countComments + " Comments");
                        } else {
                            countComments = "0";
                            numberOfComments.setText(countComments + " Comments");
                        }
                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setLikeButtonStatus(final String key) {
            LikesRef.addValueEventListener(new ValueEventListener()

            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(key).hasChild(currentUserId)) {
                        countLikes = (int) dataSnapshot.child(key).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.gusto);
                        displayNumberOfLikes.setText((Integer.toString(countLikes) + " Likes"));
                    } else {
                        countLikes = (int) dataSnapshot.child(key).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.ayaw);
                        displayNumberOfLikes.setText((Integer.toString(countLikes) + " Likes"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setUser_fullname(String user_fullname) {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(user_fullname);
        }

        public void setProfile_image(String profile_image) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profile_image).into(image);
        }

        public void setTime(String time) {
            TextView post_time = (TextView) mView.findViewById(R.id.posts_time);
            post_time.setText(" at " + time);
        }

        public void setDate(String date) {
            TextView post_date = (TextView) mView.findViewById(R.id.posts_date);
            post_date.setText("   " + date);
        }


        public void setDescription(String description) {
            TextView desc = (TextView) mView.findViewById(R.id.post_description);
            desc.setText(description);
        }

        public void setImage_url(String image_url) {
            if (image_url != null) {
                ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
                Picasso.get().load(image_url).into(post_image);
            }

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Kung may user currently
        if (currentUser == null) {
            sendToLogin();
        } else {
            checkUserExistence();
        }


        //Retrieving data to be displayed.


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    public void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            /*
              case R.id.nav_profile:
               sendToProfile();
                break;

            case R.id.nav_home:
                Toast.makeText(this, "Home!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Friends:

                break;

            case R.id.nav_find_friends:
               sendToFindFriends();
                break;

            case R.id.nav_messages:
                Toast.makeText(this, "Messages!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                sendToSettings();
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                sendToLogin();
                break;
             */

        }
    }

    public void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, login_activity.class);
        //Anti back button
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void sendToReg() {
        Intent intent = new Intent(MainActivity.this, register_activity.class);
        startActivity(intent);
        finish();
    }

    public void checkUserExistence() {

        final String current_user_id = mAuth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {
                    //Registered na, pero wala pang set up.
                    sendToSetUp();

                } else {
                    if (!dataSnapshot.child(current_user_id).hasChild("status")) {

                        sendToAccountSetup();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sendToSetUp() {
        Intent intent = new Intent(MainActivity.this, VerifiyStudentNumberActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendToPost() {
        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(intent);
    }

    public void sendToSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void sendToProfile() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    public void sendToFindFriends() {
        Intent intent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(intent);
    }

    public void sendToAccountSetup() {
        Intent intent = new Intent(MainActivity.this, setup_activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
