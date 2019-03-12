package com.example.avery.chamberofwizards.Forum;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Books.MainBooksActivity;
import com.example.avery.chamberofwizards.Events.EventsMainActivity;
import com.example.avery.chamberofwizards.Forum.Fragments.ForumFragment;
import com.example.avery.chamberofwizards.Games.MainGamesActivity;
import com.example.avery.chamberofwizards.Notes.MainNotesActivity;
import com.example.avery.chamberofwizards.Questions.QuestionsActivity;
import com.example.avery.chamberofwizards.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity2 extends AppCompatActivity {

    private ForumFragment forumFragment;
    private NotificationFragment notificationFragment;
    private MessagesFragment messagesFragment;

    private BottomNavigationView bottomNavigationView;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private CircleImageView navProfileImage;
    private TextView navProfileUsername;
    private TextView navCourse;
    private ImageView navUserBadge;

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private FirebaseUser currentUser;

    private Toolbar mToolbar;
    private Toolbar forumToolbar;

    private FloatingActionButton floatingActionButton;

    private ValueEventListener userBadgeEventListener;

    public static int recyclerViewFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        init();
        loadNavStuffs();

        if(getIntent().hasExtra("recycler_view_flag")) {
            recyclerViewFlag = getIntent().getExtras().getInt("recycler_view_flag");
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_forum:
                        replaceFragment(forumFragment);
                        return true;
                    case R.id.nav_messages:
                        Intent intent = new Intent(MainActivity2.this, FindFriendsActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.nav_notifications:
                        replaceFragment(notificationFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToPostActivity();
            }
        });
    }

    public void sendToPostActivity() {
        Intent intent = new Intent(MainActivity2.this, PostActivity.class);
        startActivity(intent);
    }


    public void replaceFragment(Fragment fragment) {
        //Replacing the fragments
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    public void loadNavStuffs() {
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("fullname")) {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        navProfileUsername.setText(fullname);
                    }
                    if (dataSnapshot.hasChild("profile_image")) {
                        String profile_picture = dataSnapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(profile_picture).into(navProfileImage);
                    }
                    if (dataSnapshot.hasChild("course")) {
                        String course = dataSnapshot.child("course").getValue().toString();
                        navCourse.setText(course);
                    }
                    if (dataSnapshot.hasChild("star_rating")) {
                        float level;
                        level = Float.parseFloat(dataSnapshot.child("star_ratings").getValue().toString());


                    }
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            sendToLogIn();
        } else {
            checkUserExistenceInDB();
        }

        maintainUserBadge();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userRef.removeEventListener(userBadgeEventListener);
    }

    public void maintainUserBadge() {
        userBadgeEventListener = userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
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

    public void checkUserExistenceInDB() {
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!dataSnapshot.hasChild(currentUserID)) {
                        //Wala sa database ang user
                        sendToVerificationOfStudentNumber();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendToVerificationOfStudentNumber() {
        Intent intent = new Intent(MainActivity2.this, VerifiyStudentNumberActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void sendToLogIn() {
        Intent intent = new Intent(MainActivity2.this, login_activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_notes:
                sendToNotes();
                break;
            case R.id.nav_books:
                goToBooks();
                break;
            case R.id.nav_events:
                goToEvents();
                break;
            //Local Menu
            case R.id.nav_questions:
                goToQ();
                break;
            case R.id.nav_g:
                goToGames();
                break;
            case R.id.nav_logout:
                logOut();
                break;
        }
    }

    public void goToGames() {
        Intent intent = new Intent(MainActivity2.this, MainGamesActivity.class);
        startActivity(intent);
    }

    public void goToEvents() {
        Intent intent = new Intent(MainActivity2.this, EventsMainActivity.class);
        startActivity(intent);
    }

    public void goToQ() {
        Intent intent = new Intent(MainActivity2.this, QuestionsActivity.class);
        startActivity(intent);
    }

    public void sendToNotes() {
        Intent intent = new Intent(MainActivity2.this, MainNotesActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToBooks() {
        Intent intent = new Intent(MainActivity2.this, MainBooksActivity.class);
        startActivity(intent);
    }

    public void goToFindPeers() {
        Intent intent = new Intent(MainActivity2.this, FindFriendsActivity.class);
        startActivity(intent);
    }

    public void goToMyAccount() {
        Intent intent = new Intent(MainActivity2.this, ProfileActivity.class);
        startActivity(intent);
    }

    public void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
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
                        Intent intent = new Intent(MainActivity2.this, login_activity.class);
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


    public void init() {
        //Fragments
        forumFragment = new ForumFragment();
        notificationFragment = new NotificationFragment();
        messagesFragment = new MessagesFragment();

        bottomNavigationView = findViewById(R.id.nav_bottom);
        bottomNavigationView.setSelectedItemId(R.id.nav_forum);

        forumToolbar = findViewById(R.id.forum_toolbar);
        setSupportActionBar(forumToolbar);
        getSupportActionBar().setTitle("Forum");

        //Para maipakita ang profile pic at user name.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, forumToolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header);

        //Navigation header
        navProfileImage = (CircleImageView) navView.findViewById(R.id.navProfileImage);
        navProfileUsername = (TextView) navView.findViewById(R.id.navUsername);
        navCourse = navView.findViewById(R.id.navUserCourse);
        navUserBadge = navView.findViewById(R.id.navUserBadge);

        //Firebase variables
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();

        //Toolbar


        //Floating action button
        floatingActionButton = findViewById(R.id.button_add_post);

        replaceFragment(forumFragment);
    }
}
