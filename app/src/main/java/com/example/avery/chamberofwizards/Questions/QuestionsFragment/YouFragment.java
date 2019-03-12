package com.example.avery.chamberofwizards.Questions.QuestionsFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Questions.QuestionsPagerAdapter;
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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class YouFragment extends Fragment {

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

    private View mView;

    private CircleImageView userImage;
    private TextView userName;
    private TextView userCourse;
    private ImageView userBadge;

    private TextView txtNumberOfQuestions;
    private TextView txtNofAnswers;
    private TextView txtNumberOfBestAnswers;
    private TextView txtAverageAnswerRating;
    private TextView txtRatingHigh;

    //Display of questions, answers etc:
    private TabLayout fragmentQuestionsYouTabLayout;
    private ViewPager fragmentQuestionsYouViewPager;

    //Fragments
    private Fragment userQuestionsFragment;
    private Fragment userAnswersFragment;
    private Fragment userBookmarksFragment;
    private Fragment userStatsFragment;

    float totalNumberOfAnswers = 0;
    float average = 0;

    public YouFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_questions_you, container, false);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        questionsRef = FirebaseFirestore.getInstance().collection("Questions");
        answersRef = FirebaseFirestore.getInstance().collection("Answers");

        userImage = mView.findViewById(R.id.fragmentYouUserImage);
        userName = mView.findViewById(R.id.fragmentYouUserName);
        userCourse = mView.findViewById(R.id.fragmentYouUserCourse);
        userBadge = mView.findViewById(R.id.fragmentYouUserBadge);

        //Fragments
        userQuestionsFragment = new FragmentUserQuestions();
        userAnswersFragment = new FragmentUserAnswers();
        userBookmarksFragment = new FragmentUserBookmarks();
        userStatsFragment = new StatsFragment();

        fragmentQuestionsYouTabLayout = mView.findViewById(R.id.fragmentQuestionsYouTabLayout);
        fragmentQuestionsYouViewPager = mView.findViewById(R.id.fragmentQuestionsYouViewPager);

        setupViewPager();

        //loadAverageRatingListener();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadUserInformation(currentUserID);
        maintainUserBadge(currentUserID);
        //loadStats(currentUserID);
    }

    @Override
    public void onStop() {
        super.onStop();

        usersRef.removeEventListener(userInfoListener);
        usersRef.removeEventListener(userBadgeListener);
        // statsListener.remove();
        // answersListener.remove();
        // bestAnswerListener.remove();
    }


    public void setupViewPager() {
        QuestionsPagerAdapter questionsPagerAdapter = new QuestionsPagerAdapter(getChildFragmentManager());
        questionsPagerAdapter.addFragment(userStatsFragment, "");
        questionsPagerAdapter.addFragment(userQuestionsFragment, "");
        questionsPagerAdapter.addFragment(userAnswersFragment, "");
        questionsPagerAdapter.addFragment(userBookmarksFragment, "");

        fragmentQuestionsYouViewPager.setAdapter(questionsPagerAdapter);

        fragmentQuestionsYouTabLayout.setupWithViewPager(fragmentQuestionsYouViewPager);

        fragmentQuestionsYouTabLayout.getTabAt(0).setIcon(R.drawable.stats_cion_holo_dark);
        fragmentQuestionsYouTabLayout.getTabAt(1).setIcon(R.drawable.ask_icon_white);
        fragmentQuestionsYouTabLayout.getTabAt(2).setIcon(R.drawable.answer_icon_white);
        fragmentQuestionsYouTabLayout.getTabAt(3).setIcon(R.drawable.book_mark_icon_white);
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


    public void maintainUserBadge(String userInforToBeLoaded) {
        userBadgeListener = usersRef.child(userInforToBeLoaded).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    float level;
                    level = Float.parseFloat(dataSnapshot.child("star_ratings").getValue().toString());

                    if (level >= 0 && level <= 20) {
                        if (level >= 0 && level <= 3) {
                            userBadge.setImageResource(R.drawable.rookie_1);
                        } else if (level >= 4 && level <= 7) {
                            userBadge.setImageResource(R.drawable.rookie_2);
                        } else if (level >= 8 && level <= 11) {
                            userBadge.setImageResource(R.drawable.rookie_3);
                        } else if (level >= 12 && level <= 15) {
                            userBadge.setImageResource(R.drawable.rookie_4);
                        } else if (level >= 16 && level <= 20) {
                            userBadge.setImageResource(R.drawable.rookie_5);
                        }
                    } else if (level >= 21 && level <= 40) {
                        if (level >= 21 && level <= 24) {
                            userBadge.setImageResource(R.drawable.intermediate_1);
                        } else if (level >= 25 && level <= 28) {
                            userBadge.setImageResource(R.drawable.intermediate_2);
                        } else if (level >= 29 && level <= 32) {
                            userBadge.setImageResource(R.drawable.intermediate_3);
                        } else if (level >= 32 && level <= 36) {
                            userBadge.setImageResource(R.drawable.intermediate_4);
                        } else if (level >= 36 && level <= 40) {
                            userBadge.setImageResource(R.drawable.intermediate_5);
                        }
                    } else if (level >= 41 && level <= 60) {
                        if (level >= 41 && level <= 44) {
                            userBadge.setImageResource(R.drawable.proficient_1);
                        } else if (level >= 45 && level <= 48) {
                            userBadge.setImageResource(R.drawable.proficient_2);
                        } else if (level >= 49 && level <= 52) {
                            userBadge.setImageResource(R.drawable.proficient_3);
                        } else if (level >= 52 && level <= 56) {
                            userBadge.setImageResource(R.drawable.proficient_4);
                        } else if (level >= 56 && level <= 60) {
                            userBadge.setImageResource(R.drawable.proficient_5);
                        }
                    } else if (level >= 61 && level <= 80) {
                        if (level >= 61 && level <= 64) {
                            userBadge.setImageResource(R.drawable.senior_1);
                        } else if (level >= 65 && level <= 68) {
                            userBadge.setImageResource(R.drawable.senior_2);
                        } else if (level >= 69 && level <= 72) {
                            userBadge.setImageResource(R.drawable.senior_3);
                        } else if (level >= 72 && level <= 76) {
                            userBadge.setImageResource(R.drawable.senior_4);
                        } else if (level >= 76 && level <= 80) {
                            userBadge.setImageResource(R.drawable.senior_5);
                        }
                    } else if (level >= 81 && level <= 100) {
                        if (level >= 81 && level <= 84) {
                            userBadge.setImageResource(R.drawable.expert_1);
                        } else if (level >= 85 && level <= 88) {
                            userBadge.setImageResource(R.drawable.expert_2);
                        } else if (level >= 89 && level <= 92) {
                            userBadge.setImageResource(R.drawable.expert_3);
                        } else if (level >= 92 && level <= 96) {
                            userBadge.setImageResource(R.drawable.expert_4);
                        } else if (level >= 96 && level <= 100) {
                            userBadge.setImageResource(R.drawable.expert_5);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadUserInformation(String userInforToBeLoaded) {

        userInfoListener = usersRef.child(userInforToBeLoaded).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String user_image, user_course, user_name;
                    user_image = dataSnapshot.child("profile_image").getValue().toString();
                    user_course = dataSnapshot.child("course").getValue().toString();
                    user_name = dataSnapshot.child("fullname").getValue().toString();

                    if (userImage != null && userCourse != null && userName != null) {
                        Picasso.get().load(user_image).into(userImage);
                        userCourse.setText(user_course);
                        userName.setText(user_name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
