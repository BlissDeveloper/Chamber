package com.example.avery.chamberofwizards.Events.EventsFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Events.Events;
import com.example.avery.chamberofwizards.Events.PostAnnouncementActivity;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {

    private View mView;

    //XML Views
    private RecyclerView announcementsContainer;
    private FloatingActionButton floatingActionButton;
    private LinearLayoutManager linearLayoutManager;

    //Firebase
    private CollectionReference eventsRef;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    String courseAcronym;

    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_blank, container, false);

        //Firebase declarations
        eventsRef = FirebaseFirestore.getInstance().collection("Events");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        //XML Views Declaration
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        announcementsContainer = mView.findViewById(R.id.announcementsContainer);
        announcementsContainer.setLayoutManager(linearLayoutManager);
        floatingActionButton = mView.findViewById(R.id.announcementsFloatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAnnounceEvent();
            }
        });

        displayEvents();


        return mView;
    }

    public void goToAnnounceEvent() {
        Intent intent = new Intent(getActivity(), PostAnnouncementActivity.class);
        startActivity(intent);
    }

    public String getCurrentUserCourseAcro() {
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    courseAcronym = dataSnapshot.child("course").getValue().toString();
                }
                else {
                    Toast.makeText(getActivity(), "User does not exists in the database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return  courseAcronym;
    }

    public void displayEvents() {
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String acro_audience = dataSnapshot.child("course").getValue().toString();

                    Toast.makeText(getActivity(), acro_audience, Toast.LENGTH_SHORT).show();
                    com.google.firebase.firestore.Query query = eventsRef
                                                                //.whereEqualTo("acro_audience", acro_audience)
                                                               // .whereEqualTo("universal", true
                                                                .whereEqualTo("acro_audience", "CS");

                    //.orderBy("event_date_announced")

                    FirestoreRecyclerOptions<Events> options = new FirestoreRecyclerOptions.Builder<Events>().setQuery(query, Events.class).build();

                    FirestoreRecyclerAdapter<Events, EventsViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Events, EventsViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull EventsViewHolder holder, int position, @NonNull Events model) {
                            holder.setAnnouncer(model.getAnnouncer());
                            holder.setAnnouncer_image(model.getAnnouncer_image());
                            holder.setEvent_end_time(model.getEvent_end_time());
                            holder.setEvent_location(model.getEvent_location());
                            holder.setEvent_start_layout(model.getEvent_start_layout());
                            holder.setEvent_start_time(model.getEvent_start_time());
                            holder.setSelected_day_display(model.getSelected_day_display());
                            holder.setSelected_month_display(model.getSelected_month_display());
                            holder.setEvent_title(model.getEvent_title());
                            holder.setEvent_description(model.getEvent_description());
                            holder.setEvent_audience(model.getEvent_audience());
                            holder.setEvent_date_announced(model.getEvent_date_announced());

                            if(model.getEvent_end_layout() == null) {
                                holder.linearLayout.removeView(holder.txtEndDate);
                                holder.txtEndTime.setText(" " + model.getEvent_end_time());
                            }
                            else {
                                holder.txtEndDate.setVisibility(View.VISIBLE);
                            }
                        }

                        @NonNull
                        @Override
                        public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_events_layout, parent, false);
                            return new EventsViewHolder(view);
                        }
                    };
                    firestoreRecyclerAdapter.startListening();
                    announcementsContainer.setAdapter(firestoreRecyclerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public TextView txtEndDate;
        public  LinearLayout linearLayout;
        public TextView txtEndTime;

        public EventsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            txtEndDate = mView.findViewById(R.id.txtEventDateEnd);
            linearLayout = mView.findViewById(R.id.linearLayout1);
            txtEndTime = mView.findViewById(R.id.txtEventTimeEnd);
        }

        public void setAnnouncer_image(String announcer_image) {
            CircleImageView circleImageView = mView.findViewById(R.id.eventsPosterImage);
            Picasso.get().load(announcer_image).into(circleImageView);
        }

        public void setSelected_month_display(String selected_month_display) {
            TextView textView = mView.findViewById(R.id.txtDisplayMonth);
            textView.setText(selected_month_display);
        }

        public void setSelected_day_display(String selected_day_display) {
            TextView textView = mView.findViewById(R.id.txtDisplayDay);
            textView.setText(selected_day_display);
        }

        public void setEvent_title(String event_title) {
            TextView textView = mView.findViewById(R.id.txtEventTitleDisplay);
            textView.setText(event_title);
        }

        public void setAnnouncer(String announcer) {
            TextView textView = mView.findViewById(R.id.txtEventPoster);
            textView.setText(announcer);
        }

        public void setEvent_start_layout(String event_start_layout) {
            TextView textView = mView.findViewById(R.id.txtEventStartDate);
            textView.setText(event_start_layout + " at ");
        }


        public void setEvent_start_time(String event_start_time) {
            TextView textView = mView.findViewById(R.id.txtEventTimeStart);
            textView.setText(event_start_time +" -");
        }


        public void setEvent_end_layout(String event_end_layout) {
            TextView textView = mView.findViewById(R.id.txtEventDateEnd);
            textView.setText(" " + event_end_layout);
        }

        public void setEvent_end_time(String event_end_time) {
            TextView textView = mView.findViewById(R.id.txtEventTimeEnd);
            textView.setText(" at " + event_end_time);
        }

        public void setEvent_location(String event_location) {
            TextView textView = mView.findViewById(R.id.txtEventLocationDisplay);
            textView.setText(event_location);
        }

        public void setEvent_audience(String event_audience) {
            TextView textView = mView.findViewById(R.id.txtEventAudience);
            textView.setText(event_audience);
        }

        public void setEvent_description(String event_description) {
            TextView textView = mView.findViewById(R.id.txtEventDescription);
            textView.setText(event_description);
        }

        public void setEvent_date_announced(String event_date_announced) {
            TextView textView = mView.findViewById(R.id.txtEventDateAnnounced);
            textView.setText(event_date_announced);
        }
    }

}
