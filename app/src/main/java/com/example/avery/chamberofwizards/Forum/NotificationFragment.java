package com.example.avery.chamberofwizards.Forum;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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


public class NotificationFragment extends Fragment {
    View mView;

    //Firebase variables
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private DatabaseReference notifRef;
    private String currentUserID;
    private String notifPostKey;

    //Recycler View
    private RecyclerView allNotifs;

    FirebaseRecyclerAdapter<ChamberNotifications, NotificationsViewHolder> firebaseRecyclerAdapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_notification, container, false);

        allNotifs = mView.findViewById(R.id.all_notif_list);
        allNotifs.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        allNotifs.setLayoutManager(linearLayoutManager);


        mAuth = FirebaseAuth.getInstance();
        notifRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        currentUserID = mAuth.getCurrentUser().getUid();


        displayNotifications();

        return mView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void displayNotifications() {

        Query query = notifRef.orderByChild("poster").equalTo(currentUserID);

        FirebaseRecyclerOptions<ChamberNotifications> options = new FirebaseRecyclerOptions.Builder<ChamberNotifications>().setQuery(query, ChamberNotifications.class).build();

        FirebaseRecyclerAdapter<ChamberNotifications, NotificationsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChamberNotifications, NotificationsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationsViewHolder viewHolder, int position, @NonNull ChamberNotifications model) {
                final String postKey = getRef(position).getKey();

                viewHolder.setCommenter(model.getCommenter());
                viewHolder.setCommenter_image(model.commenter_image);
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());

                //Changing the color of the container once clicked

                viewHolder.mainNotifContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //

                        //Send to the click notif activity

                        notifRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild(postKey)) {
                                        if (dataSnapshot.child(postKey).hasChild("post_key")) {
                                            notifPostKey = dataSnapshot.child(postKey).child("post_key").getValue().toString();

                                            Toast.makeText(getActivity(), notifPostKey, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getActivity(), ClickNotificationActivity.class);
                                            intent.putExtra("post_key", notifPostKey);
                                            startActivity(intent);
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
            }

            @NonNull
            @Override
            public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_notification_format, parent, false);
                return new NotificationsViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        allNotifs.setAdapter(firebaseRecyclerAdapter);

    }

    public static class NotificationsViewHolder extends RecyclerView.ViewHolder {
        View V;

        private View notifContainer;
        private View mainNotifContainer;

        private View root;

        public NotificationsViewHolder(View itemView) {
            super(itemView);
            V = itemView;

            notifContainer = V.findViewById(R.id.notificationContainer);
            mainNotifContainer = V.findViewById(R.id.notifMainContainer);

            //root = notifContainer.getRootView();
        }

        public void setCommenter(String commenter) {
            TextView txtCommenter = V.findViewById(R.id.txtCommenterUsername);
            txtCommenter.setText(commenter + " commented on your post.");
        }

        public void setCommenter_image(String commenter_image) {
            CircleImageView imgCommenter = V.findViewById(R.id.notification_profile_image);
            Picasso.get().load(commenter_image).into(imgCommenter);
        }

        public void setDate(String date) {
            TextView txtDate = V.findViewById(R.id.txtCommentDate);
            txtDate.setText(date);
        }

        public void setTime(String time) {
            TextView txtTime = V.findViewById(R.id.txtCommentTime);
            txtTime.setText(time);
        }
    }

}
