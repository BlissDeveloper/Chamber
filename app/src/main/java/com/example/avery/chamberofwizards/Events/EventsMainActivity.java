package com.example.avery.chamberofwizards.Events;

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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Books.MainBooksActivity;
import com.example.avery.chamberofwizards.Events.EventsFragments.AnnouncementsFragment;
import com.example.avery.chamberofwizards.Events.EventsFragments.BlankFragment;
import com.example.avery.chamberofwizards.Events.EventsFragments.CalendarFragment;
import com.example.avery.chamberofwizards.Events.EventsFragments.EventsTabLayoutFragment;
import com.example.avery.chamberofwizards.Forum.MainActivity2;
import com.example.avery.chamberofwizards.Forum.login_activity;
import com.example.avery.chamberofwizards.Games.MainGamesActivity;
import com.example.avery.chamberofwizards.Notes.MainNotesActivity;
import com.example.avery.chamberofwizards.Questions.QuestionsActivity;
import com.example.avery.chamberofwizards.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventsMainActivity extends AppCompatActivity {

    //Firebase Variables
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private DatabaseReference usersRef;
    private String currentUserID;
    private ValueEventListener badgeListener;

    //XML Views
    private DrawerLayout eventsDrawerLayout;
    private Toolbar eventsToolbar;
    private FrameLayout eventsFragmentsContainer;
    private BottomNavigationView eventsBottomNavView;
    private NavigationView eventsNavView;

    //Nav View
    private CircleImageView navProfileImage;
    private TextView navProfileUsername;
    private TextView navUserCourse;
    private ImageView navUserBadge;

    //Fragments
    private Fragment announcementsFragment;
    private Fragment calendarFragment;
    private Fragment eventsTabLayoutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_main);

        //Firebase Declarations
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("Users");

        //XML Views
        eventsDrawerLayout = findViewById(R.id.eventsDrawerLayout);

        eventsToolbar = findViewById(R.id.eventsToolbar);
        setSupportActionBar(eventsToolbar);
        getSupportActionBar().setTitle("Events & Announcements");

        eventsFragmentsContainer = findViewById(R.id.eventsFragmentContainer);
        eventsBottomNavView = findViewById(R.id.eventsBottomNav);

        eventsNavView = findViewById(R.id.eventsNavView);
        View navView = eventsNavView.inflateHeaderView(R.layout.nav_header);
        navProfileImage = navView.findViewById(R.id.navProfileImage);
        navProfileUsername = navView.findViewById(R.id.navUsername);
        navUserCourse = navView.findViewById(R.id.navUserCourse);
        navUserBadge = navView.findViewById(R.id.navUserBadge);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, eventsDrawerLayout, eventsToolbar, R.string.drawer_open, R.string.drawer_close);
        eventsDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //Fragments
        announcementsFragment = new BlankFragment();
        calendarFragment = new CalendarFragment();
        eventsTabLayoutFragment = new EventsTabLayoutFragment();

        loadNavigationViewComponents(currentUserID);

        replaceFragment(eventsTabLayoutFragment);

        eventsBottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.calendarMenu:
                        replaceFragment(calendarFragment);
                        return true;
                    case R.id.announcementsMenu:
                        replaceFragment(eventsTabLayoutFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        eventsNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_forum:
                        goToForum();
                        return true;
                    case R.id.nav_notes:
                        goToNotes();
                        return true;
                    case R.id.nav_books:
                        goToBooks();
                        return true;
                    case R.id.nav_q_n_a:
                        goToQnA();
                        return true;
                    case R.id.nav_games:
                        goToGames();
                        return true;
                    case R.id.nav_logout:
                        logOut();
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        maintainUserBadge();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usersRef.removeEventListener(badgeListener);
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

    public void loadNavigationViewComponents(String current_id) {
        usersRef.child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentUserImage = dataSnapshot.child("profile_image").getValue().toString();
                    String currentUsername = dataSnapshot.child("username").getValue().toString();
                    String currentUserCourse = dataSnapshot.child("course").getValue().toString();
                    Float level = Float.parseFloat(dataSnapshot.child("star_ratings").getValue().toString());

                    Picasso.get().load(currentUserImage).into(navProfileImage);
                    navProfileUsername.setText(currentUsername);
                    navUserCourse.setText(currentUserCourse);
                } else {
                    Toast.makeText(EventsMainActivity.this, "Error retrieving account information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EventsMainActivity.this);
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
                        Intent intent = new Intent(EventsMainActivity.this, login_activity.class);
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

    public void goToGames() {
        Intent intent = new Intent(EventsMainActivity.this, MainGamesActivity.class);
        startActivity(intent);
    }

    public void goToQnA() {
        Intent intent = new Intent(EventsMainActivity.this, QuestionsActivity.class);
        startActivity(intent);
    }

    public void goToForum() {
        Intent intent = new Intent(EventsMainActivity.this, MainActivity2.class);
        startActivity(intent);
    }

    public void goToNotes() {
        Intent intent = new Intent(EventsMainActivity.this, MainNotesActivity.class);
        startActivity(intent);
    }

    public void goToBooks() {
        Intent intent = new Intent(EventsMainActivity.this, MainBooksActivity.class);
        startActivity(intent);
    }

    public void replaceFragment(Fragment fragment) {
        //Replacing the fragments
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.eventsFragmentContainer, fragment);
        fragmentTransaction.commit();
    }

}
