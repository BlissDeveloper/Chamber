package com.example.avery.chamberofwizards.Events.EventsFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Events.Events;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 */
public class MathSocEventsFragment extends Fragment {

    private View mView;

    private CollectionReference eventsRef;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    //Views
    private RecyclerView orgEventsContainer;
    private LinearLayoutManager linearLayoutManager;

    public MathSocEventsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView =  inflater.inflate(R.layout.fragment_math_soc_events, container, false);

        //Firebase
        eventsRef = FirebaseFirestore.getInstance().collection("Events");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        //Views
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        orgEventsContainer = mView.findViewById(R.id.orgEventsContainer);
        orgEventsContainer.setLayoutManager(linearLayoutManager);

        loadEvents(currentUserID);

        return mView;
    }

    public void loadEvents(String user_ID) {
        usersRef.child(user_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String course = dataSnapshot.child("course").getValue().toString();
                    getEventsFromUserID(course);
                }
                else {
                    Toast.makeText(getActivity(), "Cannot load events, unknown user ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getEventsFromUserID(String c) {
        com.google.firebase.firestore.Query orgQuery = eventsRef.whereEqualTo("acro_audience", c)
                                                                .orderBy("event_date_announced");
        FirestoreRecyclerOptions<Events> options = new FirestoreRecyclerOptions.Builder<Events>().setQuery(orgQuery, Events.class).build();

        FirestoreRecyclerAdapter<Events, BlankFragment.EventsViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Events, BlankFragment.EventsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BlankFragment.EventsViewHolder holder, int position, @NonNull Events model) {
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
            public BlankFragment.EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_events_layout, parent, false);
                return new BlankFragment.EventsViewHolder(view);
            }
        };
        firestoreRecyclerAdapter.startListening();
        orgEventsContainer.setAdapter(firestoreRecyclerAdapter);
    }
}
