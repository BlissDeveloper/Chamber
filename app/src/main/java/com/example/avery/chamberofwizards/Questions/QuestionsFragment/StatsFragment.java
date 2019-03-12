package com.example.avery.chamberofwizards.Questions.QuestionsFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatsFragment extends Fragment {

    private View mView;

    private ValueEventListener userInfoListener;
    private ValueEventListener userBadgeListener;
    private ListenerRegistration statsListener;
    private ListenerRegistration answersListener;
    private ListenerRegistration bestAnswerListener;
    private ListenerRegistration averageRatingListener;

    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private CollectionReference questionsRef;
    private CollectionReference answersRef;


    private TextView txtNumberOfQuestions;
    private TextView txtNofAnswers;
    private TextView txtNumberOfBestAnswers;
    private TextView txtAverageAnswerRating;
    private TextView txtRatingHigh;

    float totalNumberOfAnswers = 0;
    float average = 0;

    public StatsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_stats, container, false);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        questionsRef = FirebaseFirestore.getInstance().collection("Questions");
        answersRef = FirebaseFirestore.getInstance().collection("Answers");

        txtNumberOfQuestions = mView.findViewById(R.id.txtNumberOfQuestions);
        txtNofAnswers = mView.findViewById(R.id.txtNofAnswers);
        txtNumberOfBestAnswers = mView.findViewById(R.id.txtNumberOfBestAnswers);
        txtAverageAnswerRating = mView.findViewById(R.id.txtAverageAnswerRating);
        txtRatingHigh = mView.findViewById(R.id.txtRatingHigh);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        loadStats(currentUserID);
    }

    @Override
    public void onStop() {
        super.onStop();

        //usersRef.removeEventListener(userInfoListener);
        //usersRef.removeEventListener(userBadgeListener);
        statsListener.remove();
        answersListener.remove();
        bestAnswerListener.remove();
    }

    public void loadStats(String userInforToBeLoaded) {
        loadQuestionStats(userInforToBeLoaded);
        loadAnswerStats(userInforToBeLoaded);
        loadBestAnswerStats();
        loadRatingHigh();
    }

    public void loadRatingHigh() {
        usersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    float rating_high = Float.parseFloat(dataSnapshot.child("answer_rating_high").getValue().toString());
                    if (rating_high >= 0) {
                        txtRatingHigh.setText(String.valueOf(rating_high));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadAverageRatingListener() {
        final Query query = answersRef.whereEqualTo("respondent_id", currentUserID)
                .whereEqualTo("is_best_answer", 1);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        totalNumberOfAnswers++; //Eto yung total number of answers ni user na na-vote bilang best answer.
                    }

                    usersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                float star_ratings = Float.parseFloat(dataSnapshot.child("star_ratings").getValue().toString());
                                if (star_ratings >= 0) {
                                    average = star_ratings / totalNumberOfAnswers;
                                    txtAverageAnswerRating.setText(String.valueOf(average));
                                    average = 0;
                                    totalNumberOfAnswers = 0;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadBestAnswerStats() {
        final Query query = answersRef.whereEqualTo("respondent_id", currentUserID)
                .whereEqualTo("is_best_answer", 1);

        bestAnswerListener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty() && e == null) {
                    int count = 0;
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        count++;
                    }
                    txtNumberOfBestAnswers.setText(String.valueOf(count));
                }
            }
        });
    }

    public void loadAnswerStats(String uid) {
        Query query = answersRef.whereEqualTo("respondent_id", uid);

        answersListener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int count = 0;
                if (!queryDocumentSnapshots.isEmpty() && e == null) {
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        count++;
                    }
                }
                txtNofAnswers.setText(String.valueOf(count));
            }
        });
    }

    public void loadQuestionStats(String uid) {
        Query query = questionsRef.whereEqualTo("asker_id", uid);

        statsListener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty() && e == null) {
                    int count = 0;
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        count++;
                    }
                    txtNumberOfQuestions.setText(String.valueOf(count));
                }
            }
        });
    }
}
