package com.example.avery.chamberofwizards.Forum;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForumLikersActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference likesRef;
    private String currentUserID;
    // private static FirebaseRecyclerAdapter<Likes, LikesViewHolder> firebaseRecyclerAdapter;
    private static FirebaseRecyclerAdapter<LikesModel, LikesViewHolder> firebaseRecyclerAdapter;

    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private String post_key;
    private int post_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_likers);

        mAuth = FirebaseAuth.getInstance();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        currentUserID = mAuth.getCurrentUser().getUid();

        post_key = getIntent().getExtras().getString("post_key");
        post_position = getIntent().getExtras().getInt("post_position");

        mToolbar = (Toolbar) findViewById(R.id.toolbarForumPostLikers);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewForumPostLikers);
        linearLayoutManager = new LinearLayoutManager(ForumLikersActivity.this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Likers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadLikers();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ForumLikersActivity.this, MainActivity2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    public void loadLikers() {
        Query query = likesRef.child(post_key);

        // FirebaseRecyclerOptions<Likes> options = new FirebaseRecyclerOptions.Builder<Likes>().setQuery(query, Likes.class).build();
        FirebaseRecyclerOptions<LikesModel> option = new FirebaseRecyclerOptions.Builder<LikesModel>().setQuery(query, LikesModel.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<LikesModel, LikesViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull LikesViewHolder holder, int position, @NonNull LikesModel model) {
                holder.loadDataToView(model.getLiker_id());
            }

            @NonNull
            @Override
            public LikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_likers_layout, parent, false);
                return new LikesViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class LikesViewHolder extends RecyclerView.ViewHolder {
        View mView;

        private DatabaseReference usersRef;

        CircleImageView circleImageViewLikerImage;
        TextView textViewLikerName;
        TextView textViewLikerCourse;

        public LikesViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            circleImageViewLikerImage = mView.findViewById(R.id.circleImageViewLikesLayout);
            textViewLikerName = mView.findViewById(R.id.textViewLikerName);
            textViewLikerCourse = mView.findViewById(R.id.textviewLikerCourse);
        }

        public void setLiker_id(String liker_id) {

        }

        public void loadDataToView(String user_id) {
            usersRef.child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String image, name, course;
                        image = dataSnapshot.child("profile_image").getValue().toString();
                        name = dataSnapshot.child("fullname").getValue().toString();
                        course = dataSnapshot.child("course").getValue().toString();

                        Picasso.get().load(image).into(circleImageViewLikerImage);
                        textViewLikerName.setText(name);
                        textViewLikerCourse.setText(course);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
