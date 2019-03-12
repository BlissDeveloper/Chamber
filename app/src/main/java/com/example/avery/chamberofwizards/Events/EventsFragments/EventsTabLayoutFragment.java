package com.example.avery.chamberofwizards.Events.EventsFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.avery.chamberofwizards.Events.PagesAdapter;
import com.example.avery.chamberofwizards.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsTabLayoutFragment extends Fragment {

    private View mView;

    private String userCourse;
    private String userOrg;

    private PagesAdapter pagesAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String currentUserID;

    String org = "";

    public EventsTabLayoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_events_tab_layout, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        getUserCourse(currentUserID);

        pagesAdapter = new PagesAdapter(getChildFragmentManager());
        tabLayout = mView.findViewById(R.id.eventsTabLayout);

        viewPager = mView.findViewById(R.id.eventsViewPager);
        setupViewPager(viewPager, org);

        tabLayout.setupWithViewPager(viewPager);
        return mView;
    }

    public void getUserCourse(String user_id) {

        usersRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userCourse = dataSnapshot.child("course").getValue().toString();

                    if (userCourse.equals("M")) {
                        org = "Mathematics Society";
                    } else if (userCourse.equals("B")) {
                        org = "Biological Society";
                        Toast.makeText(getActivity(), "bIO", Toast.LENGTH_SHORT).show();
                    } else if (userCourse.equals("ES")) {
                        org = "";
                    } else if (userCourse.equals("FS")) {
                        org = "";
                    } else {
                        org = "";
                    }

                    Toast.makeText(getActivity(), org, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to retrieve user course.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setupViewPager(ViewPager v, String c) {
        PagesAdapter adapter = new PagesAdapter(getChildFragmentManager());
        adapter.addFragment(new BlankFragment(), "College of Science");
        adapter.addFragment(new MathSocEventsFragment(), "Organization");
        v.setAdapter(adapter);
    }
}
