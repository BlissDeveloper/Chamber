package com.example.avery.chamberofwizards.Forum.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Forum.ClickPostActivity;
import com.example.avery.chamberofwizards.Forum.CommentsActivity;
import com.example.avery.chamberofwizards.Forum.EditPostActivity;
import com.example.avery.chamberofwizards.Forum.ForumLikersActivity;
import com.example.avery.chamberofwizards.Forum.MainActivity2;
import com.example.avery.chamberofwizards.Forum.Posts;
import com.example.avery.chamberofwizards.Forum.login_activity;
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

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ForumFragment extends Fragment {

    RecyclerView postsList;

    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String currentUserID;
    private DatabaseReference allPostsRef;
    private DatabaseReference likesRef;

    private boolean likeChecker = false;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    public static int FORUM_POST_KEY = 0;

    private static Context mContext;

    public ForumFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_forum, container, false);

        mContext = getActivity();

        //Firebase variables
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        allPostsRef = FirebaseDatabase.getInstance().getReference().child("All Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        allPostsRef.keepSynced(true);
        userRef.keepSynced(true);
        likesRef.keepSynced(true);

        postsList = (RecyclerView) mView.findViewById(R.id.fragment_all_users_post);
        // postsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postsList.setLayoutManager(linearLayoutManager);

        if (String.valueOf(MainActivity2.recyclerViewFlag) != null) {
            linearLayoutManager.scrollToPosition(MainActivity2.recyclerViewFlag);
        } else {
            Log.d("Avery", "Recycler viewposition set");
        }

        displayAllUserPosts();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        displayAllUserPosts();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public void displayAllUserPosts() {

        Query query = allPostsRef.orderByChild("latest_transaction");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query, Posts.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostHolder>(options) {

            @NonNull
            @Override
            public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                return new PostHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final PostHolder viewHolder, int position, final @NonNull Posts model) {
                final String postKey = getRef(position).getKey();
                final int pos = position;

                viewHolder.setUser_fullname(model.getUser_fullname());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setProfile_image(model.getProfile_image());

                //Sets the number of likes
                viewHolder.setLikeButtonStatus(postKey);
                viewHolder.setNumberOfComments(postKey);

                viewHolder.displayNumberOfLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ForumLikersActivity.class);
                        intent.putExtra("post_key", postKey);
                        intent.putExtra("post_position", pos);
                        mContext.startActivity(intent);
                    }
                });

                viewHolder.maintainDotsButton(postKey, currentUserID);

                viewHolder.imageButtonForumOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "Edit Post",
                                "Delete Post"
                        };

                        AlertDialog.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        //Edit post
                                        viewHolder.editForumPost(postKey, currentUserID, getActivity());
                                        break;
                                    case 1:
                                        //Delete post
                                        viewHolder.deleteForumPost(postKey, currentUserID, getActivity());
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setItems(options, clickListener)
                                .show();
                    }
                });

                if (model.getImage_url() == null) {
                    viewHolder.postImage.setVisibility(View.GONE);
                } else if (model.getImage_url() != null) {
                    viewHolder.postImage.setVisibility(View.VISIBLE);
                    viewHolder.setImage_url(model.getImage_url());
                } else {
                    Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
                }

                viewHolder.postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent clickPostIntent = new Intent(getActivity(), ClickPostActivity.class);
                        clickPostIntent.putExtra("postKey", postKey);
                        startActivity(clickPostIntent);
                    }
                });

                viewHolder.commentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent clickComment = new Intent(getActivity(), CommentsActivity.class);
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
                                if (likeChecker) {
                                    if (dataSnapshot.child(postKey).hasChild(currentUserID)) {
                                        //Like already exist
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
        firebaseRecyclerAdapter.startListening();
        postsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostHolder extends RecyclerView.ViewHolder {
        View V;

        Button likePostButton, commentPostButton;
        TextView displayNumberOfLikes;
        TextView numberOfComments;
        int countLikes;
        String countComments;
        String currentUserId;
        DatabaseReference LikesRef;
        DatabaseReference postsRef;
        DatabaseReference commentsRef;
        ImageView postImage;

        CardView post_card;
        LinearLayout linearLayout;
        ConstraintLayout consLayout;
        ImageButton imageButtonForumOptions;

        boolean likeChecker = false;

        public PostHolder(View itemView) {
            super(itemView);
            V = itemView;

            initViewHolder();
        }

        public void editForumPost(final String postKey, final String currentUserId, final Context context) {
            Intent intent = new Intent(context, EditPostActivity.class);
            intent.putExtra("key", postKey);
            intent.putExtra("edit_flag", FORUM_POST_KEY);
            context.startActivity(intent);
        }

        public void deleteForumPost(final String postKey, final String currentUserId, final Context context) {
            AlertDialog.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case Dialog.BUTTON_POSITIVE:
                            //Cancel
                            dialog.dismiss();
                            break;
                        case Dialog.BUTTON_NEGATIVE:
                            //Delete
                            postsRef.child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Post deleted successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Post deleted unsucessfully!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            break;
                    }
                }
            };

            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setPositiveButton("Cancel", clickListener)
                    .setNegativeButton("Delete", clickListener)
                    .show();
        }

        public void maintainDotsButton(final String post_key, final String currentUserId) {
            postsRef.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String poster = dataSnapshot.child("uid").getValue().toString();
                        if (poster.equals(currentUserId)) {
                            imageButtonForumOptions.setVisibility(View.VISIBLE);
                        } else {
                            imageButtonForumOptions.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public boolean isPostByTheCurrentUser(final String posterId, final String currentUser) {
            if (posterId.equals(currentUser)) {
                return true;
            } else {
                return false;
            }
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
                            numberOfComments.setText("No comments yet");
                        }
                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setUser_fullname(String user_fullname) {
            TextView username = (TextView) V.findViewById(R.id.post_user_name);
            username.setText(user_fullname);
        }

        public void setProfile_image(String profile_image) {
            CircleImageView image = (CircleImageView) V.findViewById(R.id.post_profile_image);
            Picasso.get().load(profile_image).into(image);
        }

        public void setTime(String time) {
            TextView post_time = (TextView) V.findViewById(R.id.posts_time);
            post_time.setText(" at " + time);
        }

        public void setDate(String date) {
            TextView post_date = (TextView) V.findViewById(R.id.posts_date);
            post_date.setText("   " + date);
        }


        public void setDescription(String description) {
            TextView desc = (TextView) V.findViewById(R.id.post_description);
            desc.setText(description);
        }

        public void setImage_url(String image_url) {
            ImageView post_image = (ImageView) V.findViewById(R.id.post_image);
            Picasso.get().load(image_url).into(post_image);
        }

        public void setLikeButtonStatus(final String key) {
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(key).hasChild(currentUserId)) {
                        countLikes = (int) dataSnapshot.child(key).getChildrenCount();
                        likePostButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_filled, 0, 0, 0);
                        displayNumberOfLikes.setText((Integer.toString(countLikes) + " Likes"));
                    } else {
                        countLikes = (int) dataSnapshot.child(key).getChildrenCount();
                        likePostButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_heart, 0, 0, 0);
                        displayNumberOfLikes.setText((Integer.toString(countLikes) + " Likes"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        public void initViewHolder() {
            likePostButton = (Button) V.findViewById(R.id.like_button);
            commentPostButton = (Button) V.findViewById(R.id.comment_button);
            displayNumberOfLikes = (TextView) V.findViewById(R.id.display_number_of_likes);
            numberOfComments = (TextView) V.findViewById(R.id.number_of_comments);
            post_card = (CardView) V.findViewById(R.id.postCardView);
            //linearLayout = (LinearLayout) V.findViewById(R.id.linearPost);
            //consLayout = (ConstraintLayout) V.findViewById(R.id.consLayout);
            postImage = V.findViewById(R.id.post_image);
            commentsRef = FirebaseDatabase.getInstance().getReference().child("All Posts");
            imageButtonForumOptions = V.findViewById(R.id.imageButtonForumOptions);
            postsRef = FirebaseDatabase.getInstance().getReference().child("All Posts");

            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }


    public void init() {

    }

    public void sendToLogIn() {
        Intent intent = new Intent(getActivity(), login_activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
