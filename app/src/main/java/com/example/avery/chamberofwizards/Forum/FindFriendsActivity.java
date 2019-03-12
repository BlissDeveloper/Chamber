package com.example.avery.chamberofwizards.Forum;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class FindFriendsActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private String currentUserID;

    private Toolbar toolbar;

    private EditText txtSearch;
    private RecyclerView SearchResultList;

    FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        //Firebase
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //Toolbar
        toolbar = findViewById(R.id.find_friends_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Peers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtSearch = findViewById(R.id.search_box_input);
        txtSearch.setOnEditorActionListener(editorListener);

        SearchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));


    }

    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            String searchBoxInput = txtSearch.getText().toString();
            SearchPeople(searchBoxInput);
            return false;
        }
    };


    public void SearchPeople(String search) {

        /*
         FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>
                (
                        FindFriends.class,
                        R.layout.all_users_display_layout,
                        FindFriendsViewHolder.class,
                        searchPeople
                ) {
            @Override
            protected void populateViewHolder(final FindFriendsViewHolder viewHolder, FindFriends model, final int position) {
                viewHolder.setFullname(model.getFullname());
                viewHolder.setCourse(model.getCourse());
                viewHolder.setProfile_image(model.getProfile_image());
                final String visit_user_id = getRef(position).getKey();

                usersRef.child(visit_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String visit_fullname = dataSnapshot.child("fullname").getValue().toString();

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Visit " + visit_fullname + "'s Profile",
                                                    "Send Message"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(FindFriendsActivity.this);
                                    builder.setTitle("Select Option");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                                goToAUserProfile(visit_user_id);
                                            } else if (i == 1) {
                                                //Send to chat
                                                goToChat(visit_user_id, visit_fullname);
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

         */

        Query searchPeople = usersRef.orderByChild("fullname")
                .startAt(search.toUpperCase()).endAt(search.toLowerCase() + "\uf8ff");

        FirebaseRecyclerOptions<FindFriends> options = new FirebaseRecyclerOptions.Builder<FindFriends>().setQuery(searchPeople, FindFriends.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FindFriendsViewHolder viewHolder, int position, @NonNull FindFriends model) {
                viewHolder.setFullname(model.getFullname());
                viewHolder.setCourse(model.getCourse());
                viewHolder.setProfile_image(model.getProfile_image());
                final String visit_user_id = getRef(position).getKey();

                usersRef.child(visit_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String visit_fullname = dataSnapshot.child("fullname").getValue().toString();

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Visit " + visit_fullname + "'s Profile",
                                                    "Send Message"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(FindFriendsActivity.this);
                                    builder.setTitle("Select Option");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                                goToAUserProfile(visit_user_id);
                                            } else if (i == 1) {
                                                //Send to chat
                                                goToChat(visit_user_id, visit_fullname);
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                return new FindFriendsViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        SearchResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FindFriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setProfile_image(String profile_image) {
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profile_image).into(myImage);
        }

        public void setFullname(String fullname) {
            TextView myName = (TextView) mView.findViewById(R.id.all_users_profile_full_name);
            myName.setText(fullname);
        }

        public void setCourse(String course) {
            TextView myCourse = (TextView) mView.findViewById(R.id.all_users_course);
            myCourse.setText(course);
        }

    }

    public void goToAUserProfile(String visit_user_id) {
        Intent intent = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
        intent.putExtra("visit_user_id", visit_user_id);
        startActivity(intent);
        finish();
    }

    public void goToChat(String visit_user_id, String fullname) {
        Intent intent = new Intent(FindFriendsActivity.this, ChatActivity.class);
        intent.putExtra("visit_user_id", visit_user_id);
        intent.putExtra("fullname", fullname);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
