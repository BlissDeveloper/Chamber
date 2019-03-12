package com.example.avery.chamberofwizards.Notes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Books.MainBooksActivity;
import com.example.avery.chamberofwizards.Events.EventsMainActivity;
import com.example.avery.chamberofwizards.Forum.MainActivity2;
import com.example.avery.chamberofwizards.Forum.NotesViewHolder;
import com.example.avery.chamberofwizards.Forum.login_activity;
import com.example.avery.chamberofwizards.Games.MainGamesActivity;
import com.example.avery.chamberofwizards.Notes.Adapters.NotesPagerAdapter;
import com.example.avery.chamberofwizards.Notes.Fragments.AllNotesFragment;
import com.example.avery.chamberofwizards.Notes.Fragments.SharedNotesFragment;
import com.example.avery.chamberofwizards.Notes.Fragments.YourNotesFragment;
import com.example.avery.chamberofwizards.Questions.QuestionsActivity;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainNotesActivity extends AppCompatActivity {

    //Firebase variables
    private FirebaseAuth mAuth;
    private static DatabaseReference usersRef;
    private String currentUserID;
    private DatabaseReference notesRef;
    private ValueEventListener badgeListener;
    private static FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter;

    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private CircleImageView navProfileImage;
    private TextView navProfileUsername;
    private TextView navUserCourse;
    private ImageView navUserBadge;

    private TabLayout tabLayoutNotes;
    private ViewPager viewPagerNotes;

    private FloatingActionButton floatingActionButton;

    private Fragment allNotesFragment;
    private Fragment yourNotesFragment;
    private Fragment sharedNotesFragment;

    //SHare note XML
    public static CardView cardViewSHareNote;
    private ImageView imageViewCloseShareNote;
    private static RecyclerView recyclerViewShareNote;

    public static String to_share_note_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_notes);

        toolbar = findViewById(R.id.notes_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notes");

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header);

        navProfileImage = navView.findViewById(R.id.navProfileImage);
        navProfileUsername = navView.findViewById(R.id.navUsername);
        navUserCourse = navView.findViewById(R.id.navUserCourse);
        navUserBadge = navView.findViewById(R.id.navUserBadge);

        //Share note XML
        /*
         private CardView cardViewSHareNote;
        private ImageView imageViewCloseShareNote;
        private RecyclerView recyclerViewShareNote;
         */
        cardViewSHareNote = findViewById(R.id.cardViewShareNote);
        imageViewCloseShareNote = findViewById(R.id.imageButtonCloseShareNote);
        recyclerViewShareNote = findViewById(R.id.recyclerViewShareNote);

        //Firebase declaration
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserID = mAuth.getCurrentUser().getUid();
        notesRef = FirebaseDatabase.getInstance().getReference().child("Notes");

        floatingActionButton = findViewById(R.id.floatingButtonAddNote);

        tabLayoutNotes = findViewById(R.id.tabLayoutNotes);
        viewPagerNotes = findViewById(R.id.viewPagerNotes);

        allNotesFragment = new AllNotesFragment();
        yourNotesFragment = new YourNotesFragment();
        sharedNotesFragment = new SharedNotesFragment();

        imageViewCloseShareNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseRecyclerAdapter != null) {
                    firebaseRecyclerAdapter.stopListening();
                    cardViewSHareNote.setVisibility(View.GONE);
                    cardViewSHareNote.setEnabled(false);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.nav_forum:
                        goToForum();
                        return true;

                    case R.id.nav_books:
                        Intent intent = new Intent(MainNotesActivity.this, MainBooksActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.nav_events:
                        goToEvents();
                        return true;

                    case R.id.nav_q_n_a:
                        Intent i = new Intent(MainNotesActivity.this, QuestionsActivity.class);
                        startActivity(i);
                    case R.id.nav_g:
                        goToGames();
                        return true;
                    case R.id.nav_logout:
                        logOut();
                    default:
                        return false;

                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNewNote();
            }
        });

        loadNavigationHeaderViews(currentUserID);

        cardViewSHareNote.setVisibility(View.GONE);
        cardViewSHareNote.setEnabled(false);

        //loadUsers();

        setupViewPager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        maintainUserBadge();
    }

    public void setupViewPager() {
        NotesPagerAdapter notesPagerAdapter = new NotesPagerAdapter(getSupportFragmentManager());

        notesPagerAdapter.addFragment(allNotesFragment, "All");
        notesPagerAdapter.addFragment(yourNotesFragment, "Your Notes");
        notesPagerAdapter.addFragment(sharedNotesFragment, "Shared");

        viewPagerNotes.setAdapter(notesPagerAdapter);
        tabLayoutNotes.setupWithViewPager(viewPagerNotes);
    }

    @Override
    protected void onStop() {
        super.onStop();
        usersRef.removeEventListener(badgeListener);
    }

    public void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainNotesActivity.this);
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
                        Intent intent = new Intent(MainNotesActivity.this, login_activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void goToGames() {
        Intent intent = new Intent(MainNotesActivity.this, MainGamesActivity.class);
        startActivity(intent);
    }

    public void goToForum() {
        Intent intent = new Intent(MainNotesActivity.this, MainActivity2.class);
        startActivity(intent);
        finish();
    }

    public void goToNewNote() {
        Intent intent = new Intent(MainNotesActivity.this, NewNoteActivity.class);
        startActivity(intent);
    }

    public void goToEvents() {
        Intent intent = new Intent(MainNotesActivity.this, EventsMainActivity.class);
        startActivity(intent);
    }

    public void loadNavigationHeaderViews(String currentUserID) {

        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName, userImage, userCourse;
                    Float level;

                    userName = dataSnapshot.child("username").getValue().toString();
                    userImage = dataSnapshot.child("profile_image").getValue().toString();
                    userCourse = dataSnapshot.child("course").getValue().toString();
                    level = Float.parseFloat(dataSnapshot.child("star_ratings").getValue().toString());

                    if (userCourse != null) {
                        navUserCourse.setText(userCourse);
                    } else {
                        navUserCourse.setText("Course not stated yet.");
                    }
                    if (userImage != null) {
                        Picasso.get().load(userImage).into(navProfileImage);
                    } else {
                        navProfileImage.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
