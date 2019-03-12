package com.example.avery.chamberofwizards.Events.EventsFragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    View mView;
    private CompactCalendarView eventsCalendar;
    private DocumentReference eventsRef;
    private Button button;


    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_calendar, container, false);
        eventsRef = FirebaseFirestore.getInstance().collection("Events").document("tOfle96rKrUec3vJBYK0");
        button = mView.findViewById(R.id.btnCal);

        eventsCalendar = mView.findViewById(R.id.eventCalendar);

        loadCalendar();
        return mView;
    }

    public void loadCalendar() {
        eventsRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                  // long millis = (long.class.cast(documentSnapshot.get("date_time_millis").toString()));
                   // long millis = Long.parseLong("1543161600000",10);
                   // Toast.makeText(getActivity(), String.valueOf(millis), Toast.LENGTH_SHORT).show();
                    //Event evt = new Event(Color.RED, millis);
                   // eventsCalendar.addEvent(evt, false);
                    //long dateInMillis = Long.parseLong()
                }
                else {
                    Toast.makeText(getActivity(), "Does not exists", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}
