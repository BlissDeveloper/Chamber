package com.example.avery.chamberofwizards.Games;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Books.MainBooksActivity;
import com.example.avery.chamberofwizards.Events.EventsMainActivity;
import com.example.avery.chamberofwizards.Forum.MainActivity2;
import com.example.avery.chamberofwizards.Forum.login_activity;
import com.example.avery.chamberofwizards.Games.Fragments.AllGamesFragment;
import com.example.avery.chamberofwizards.Notes.MainNotesActivity;
import com.example.avery.chamberofwizards.Questions.QuestionsActivity;
import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainGamesActivity extends AppCompatActivity {
    //Firebase
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference usersRef;
    private ValueEventListener navHeaderListener;
    private ValueEventListener badgeListener;
    private CollectionReference gameReviewsRef;
    private CollectionReference gamesRef;

    //XML Views
    private Toolbar gamesToolbar;
    private NavigationView gamesNavView;
    private FrameLayout gamesFrameLayout;
    private DrawerLayout gamesDrawerLayout;
    public static CardView cardviewGameReview;
    private RatingBar ratingBarGameReview;
    private EditText editTextGameReview;
    private ImageView btnCloseGameReview;
    private Button btnPostGameReview;
    private ProgressBar progressBarGameReview;

    private TextView navUsername;
    private TextView navUserCourse;
    private CircleImageView navUserImage;
    private ImageView navUserBadge;

    //Fragments
    private Fragment allGamesFragment;

    //Misc
    public static String selectedGsmeKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_games);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        gameReviewsRef = FirebaseFirestore.getInstance().collection("Game Reviews");
        gamesRef = FirebaseFirestore.getInstance().collection("Games");

        //XML Views
        gamesToolbar = findViewById(R.id.mainGamesActivityToolbar);
        gamesNavView = findViewById(R.id.games_nav_view);
        gamesDrawerLayout = findViewById(R.id.gamesDrawerLayout);
        cardviewGameReview = findViewById(R.id.cardviewGameReview);
        ratingBarGameReview = findViewById(R.id.ratingBarGameRating);
        editTextGameReview = findViewById(R.id.editTextGameReview);
        btnCloseGameReview = findViewById(R.id.btnCloseGameReview);
        btnPostGameReview = findViewById(R.id.btnPostGameReview);
        progressBarGameReview = findViewById(R.id.progressbarGameReview);

        //Toolbar Setup
        setSupportActionBar(gamesToolbar);
        getSupportActionBar().setTitle("Games");

        //Nav View
        View navView = gamesNavView.inflateHeaderView(R.layout.nav_header);
        navUsername = navView.findViewById(R.id.navUsername);
        navUserCourse = navView.findViewById(R.id.navUserCourse);
        navUserImage = navView.findViewById(R.id.navProfileImage);
        navUserBadge = navView.findViewById(R.id.navUserBadge);

        //Drawer Control
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, gamesDrawerLayout, gamesToolbar, R.string.drawer_open, R.string.drawer_close);
        gamesDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Fragment
        allGamesFragment = new AllGamesFragment();

        //Misc
        selectedGsmeKey = null;

        gamesNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_forum:
                        goToForum();
                        return true;
                    case R.id.nav_books:
                        goToBooks();
                        return true;
                    case R.id.nav_notes:
                        goToNotes();
                        return true;
                    case R.id.nav_q_n_a:
                        goToQAndA();
                        return true;
                    case R.id.nav_games:
                        goToGames();
                        return true;
                    case R.id.nav_logout:
                        logout();
                        return true;
                    case R.id.nav_events:
                        goToEvents();
                        return true;
                    default:
                        return false;
                }
            }
        });

        btnPostGameReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarGameReview.setVisibility(View.VISIBLE);
                saveGameReviewToFirestore();
            }
        });

        btnCloseGameReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardviewGameReview.setVisibility(View.GONE);
            }
        });

        editTextGameReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int textChar = s.toString().trim().length();
                if (textChar >= 5 && ratingBarGameReview.getRating() > 0) {
                    btnPostGameReview.setVisibility(View.VISIBLE);
                } else {
                    btnPostGameReview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textChar = s.toString().trim().length();
                if (textChar >= 5 && ratingBarGameReview.getRating() > 0) {
                    btnPostGameReview.setVisibility(View.VISIBLE);
                } else {
                    btnPostGameReview.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int textChar = s.toString().trim().length();
                if (textChar >= 5 && ratingBarGameReview.getRating() > 0) {
                    btnPostGameReview.setVisibility(View.VISIBLE);
                } else {
                    btnPostGameReview.setVisibility(View.GONE);
                }
            }
        });

        replaceFragment(allGamesFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadUserInfoToNavViewHeader();
        maintainUserBadge();
    }

    @Override
    protected void onStop() {
        super.onStop();

        usersRef.removeEventListener(navHeaderListener);
        usersRef.removeEventListener(badgeListener);
    }

    public void goToForum() {
        Intent intent = new Intent(MainGamesActivity.this, MainActivity2.class);
        startActivity(intent);
    }

    public void goToNotes() {
        Intent intent = new Intent(MainGamesActivity.this, MainNotesActivity.class);
        startActivity(intent);
    }

    public void goToBooks() {
        Intent intent = new Intent(MainGamesActivity.this, MainBooksActivity.class);
        startActivity(intent);
    }

    public void goToEvents() {
        Intent intent = new Intent(MainGamesActivity.this, EventsMainActivity.class);
        startActivity(intent);
    }

    public void goToQAndA() {
        Intent intent = new Intent(MainGamesActivity.this, QuestionsActivity.class);
        startActivity(intent);
    }

    public void goToGames() {
        Intent intent = new Intent(MainGamesActivity.this, MainGamesActivity.class);
        startActivity(intent);
    }

    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainGamesActivity.this);
        Dialog.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        //Cencel
                        dialog.dismiss();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        mAuth.signOut();
                        Intent intent = new Intent(MainGamesActivity.this, login_activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        };
        builder.setNegativeButton("Logout", clickListener)
                .setPositiveButton("Cancel", clickListener)
                .show();
    }

    public void saveGameReviewToFirestore() {
       usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()) {
                   String user_name, user_image, user_course;
                   user_name = dataSnapshot.child("fullname").getValue().toString();
                   user_image = dataSnapshot.child("profile_image").getValue().toString();
                   user_course = dataSnapshot.child("course").getValue().toString();

                   Map<String, Object> reviewMap = new ArrayMap();

                   reviewMap.put("gameKey", selectedGsmeKey);
                   reviewMap.put("game_reviewer", currentUserID);
                   reviewMap.put("game_rating", ratingBarGameReview.getRating());
                   reviewMap.put("game_review", editTextGameReview.getText().toString());
                   reviewMap.put("game_reviewer_name", user_name);
                   reviewMap.put("game_reviewer_image", user_image);
                   reviewMap.put("game_reviewer_course", user_course);


                   gameReviewsRef.document().set(reviewMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()) {
                               updateReviewDataOfGame();
                           } else {
                               Log.e("Avery", task.getException().getMessage());
                           }
                       }
                   });
               }
               else {
                   Log.e("Avery", "User snapshot does not exists");
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    public void updateReviewDataOfGame() {
        gamesRef.document(selectedGsmeKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    float average_rating = 0, total_review_score = 0;
                    int number_of_reviews = 0;

                    //////////////

                    float new_average_rating = 0, new_total_review_score = 0;
                    int new_number_of_reviews = 0;

                    average_rating = Float.parseFloat(documentSnapshot.get("average_rating").toString());
                    number_of_reviews = Integer.parseInt(documentSnapshot.get("number_of_reviews").toString());
                    total_review_score = Float.parseFloat(documentSnapshot.get("total_review_score").toString());

                    new_number_of_reviews = number_of_reviews + 1;
                    new_total_review_score = total_review_score + ratingBarGameReview.getRating();

                    new_average_rating = new_total_review_score / new_number_of_reviews;

                    Map<String, Object> reviewMap = new ArrayMap<>();
                    reviewMap.put("average_rating", new_average_rating);
                    reviewMap.put("number_of_reviews", new_number_of_reviews);
                    reviewMap.put("total_review_score", new_total_review_score);

                    gamesRef.document(selectedGsmeKey).update(reviewMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(MainGamesActivity.this, "Review posted successfully!", Toast.LENGTH_SHORT).show();
                                setDefaults();
                            }
                            else {
                                Log.e("Avery", "Review not posted");
                            }
                        }
                    });
                } else {
                    Log.e("Avery", "Document snapshot does not exists for the game");
                }
            }
        });
    }

    public void setDefaults() {
        editTextGameReview.setText("");
        ratingBarGameReview.setRating(0);
        cardviewGameReview.setVisibility(View.GONE);
        progressBarGameReview.setVisibility(View.GONE);
    }

    public void maintainUserBadge() {
        badgeListener = usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    float level = Float.parseFloat(dataSnapshot.child("star_ratings").getValue().toString());


                    if (level >= 0 && level <= 20) {
                        if (level >= 0 && level <= 3) {
                            navUserBadge.setImageResource(R.drawable.rookie_1);
                        } else if (level >= 4 && level <= 7) {
                            navUserBadge.setImageResource(R.drawable.rookie_2);
                        } else if (level >= 8 && level <= 11) {
                            navUserBadge.setImageResource(R.drawable.rookie_3);
                        } else if (level >= 12 && level <= 15) {
                            navUserBadge.setImageResource(R.drawable.rookie_4);
                        } else if (level >= 16 && level <= 20) {
                            navUserBadge.setImageResource(R.drawable.rookie_5);
                        }
                    } else if (level >= 21 && level <= 40) {
                        if (level >= 21 && level <= 24) {
                            navUserBadge.setImageResource(R.drawable.intermediate_1);
                        } else if (level >= 25 && level <= 28) {
                            navUserBadge.setImageResource(R.drawable.intermediate_2);
                        } else if (level >= 29 && level <= 32) {
                            navUserBadge.setImageResource(R.drawable.intermediate_3);
                        } else if (level >= 32 && level <= 36) {
                            navUserBadge.setImageResource(R.drawable.intermediate_4);
                        } else if (level >= 36 && level <= 40) {
                            navUserBadge.setImageResource(R.drawable.intermediate_5);
                        }
                    } else if (level >= 41 && level <= 60) {
                        if (level >= 41 && level <= 44) {
                            navUserBadge.setImageResource(R.drawable.proficient_1);
                        } else if (level >= 45 && level <= 48) {
                            navUserBadge.setImageResource(R.drawable.proficient_2);
                        } else if (level >= 49 && level <= 52) {
                            navUserBadge.setImageResource(R.drawable.proficient_3);
                        } else if (level >= 52 && level <= 56) {
                            navUserBadge.setImageResource(R.drawable.proficient_4);
                        } else if (level >= 56 && level <= 60) {
                            navUserBadge.setImageResource(R.drawable.proficient_5);
                        }
                    } else if (level >= 61 && level <= 80) {
                        if (level >= 61 && level <= 64) {
                            navUserBadge.setImageResource(R.drawable.senior_1);
                        } else if (level >= 65 && level <= 68) {
                            navUserBadge.setImageResource(R.drawable.senior_2);
                        } else if (level >= 69 && level <= 72) {
                            navUserBadge.setImageResource(R.drawable.senior_3);
                        } else if (level >= 72 && level <= 76) {
                            navUserBadge.setImageResource(R.drawable.senior_4);
                        } else if (level >= 76 && level <= 80) {
                            navUserBadge.setImageResource(R.drawable.senior_5);
                        }
                    } else if (level >= 81 && level <= 100) {
                        if (level >= 81 && level <= 84) {
                            navUserBadge.setImageResource(R.drawable.expert_1);
                        } else if (level >= 85 && level <= 88) {
                            navUserBadge.setImageResource(R.drawable.expert_2);
                        } else if (level >= 89 && level <= 92) {
                            navUserBadge.setImageResource(R.drawable.expert_3);
                        } else if (level >= 92 && level <= 96) {
                            navUserBadge.setImageResource(R.drawable.expert_4);
                        } else if (level >= 96 && level <= 100) {
                            navUserBadge.setImageResource(R.drawable.expert_5);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadUserInfoToNavViewHeader() {
        navHeaderListener = usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username, user_image, user_course;
                    username = dataSnapshot.child("fullname").getValue().toString();
                    user_image = dataSnapshot.child("profile_image").getValue().toString();
                    user_course = dataSnapshot.child("course").getValue().toString();

                    navUsername.setText(username);
                    navUserCourse.setText(user_course);
                    Picasso.get().load(user_image).into(navUserImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void replaceFragment(Fragment fragment) {
        //Replacing the fragments
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.gamesFragmentContainer, fragment);
        fragmentTransaction.commit();
    }

}
