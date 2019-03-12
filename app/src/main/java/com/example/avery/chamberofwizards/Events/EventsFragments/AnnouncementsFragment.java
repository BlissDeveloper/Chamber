package com.example.avery.chamberofwizards.Events.EventsFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.avery.chamberofwizards.Events.Events;
import com.example.avery.chamberofwizards.Events.EventsAdapter;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnnouncementsFragment extends Fragment {

    private View mView;

    private RecyclerView eventsContainer;
    private LinearLayoutManager linearLayoutManager;

    private FirebaseFirestore firebaseFirestore;

    private CollectionReference eventsRef;

    private EventsAdapter adapter;

    public AnnouncementsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_announcements, container, false);

        eventsRef = FirebaseFirestore.getInstance().collection("Events");
        firebaseFirestore = FirebaseFirestore.getInstance();

        eventsContainer = mView.findViewById(R.id.eventsContainer);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true); //Contect, Orientation, reverse
        eventsContainer.setLayoutManager(linearLayoutManager);

        return mView;
    }}
