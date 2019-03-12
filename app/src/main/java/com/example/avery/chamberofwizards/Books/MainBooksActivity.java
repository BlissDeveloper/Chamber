package com.example.avery.chamberofwizards.Books;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.NetworkOnMainThreadException;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Books.BookFragments.DownloadsFragment;
import com.example.avery.chamberofwizards.Books.BookFragments.FavoritesFragment;
import com.example.avery.chamberofwizards.Books.BookFragments.MainBooksScreenFragment;
import com.example.avery.chamberofwizards.Books.BookFragments.ReadFragment;
import com.example.avery.chamberofwizards.Books.BookFragments.SearchFragment;
import com.example.avery.chamberofwizards.Books.BookFragments.TabFragment;
import com.example.avery.chamberofwizards.Events.EventsMainActivity;
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

public class MainBooksActivity extends AppCompatActivity {


    //Firebase variables
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference usersRef;
    private ValueEventListener badgeListener;

    private Toolbar booksToolbar;
    private NavigationView booksNavView;
    private FrameLayout booksMainContainer;
    private DrawerLayout booksDrawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private BottomNavigationView bottomNavigationView;

    private CircleImageView navProfileImage;
    private TextView navProfileUsername;
    private TextView navUserCourse;
    private ImageView navUserBadge;

    private TextView txtSearchBooks;
    private Fragment readFragment;
    private Fragment searchFragment;
    private Fragment favoritesFragment;
    private Fragment tabFragment;
    private Fragment mainBooksFragment;
    private Fragment downloadsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_books);

        //Firebase declaration
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        booksToolbar = findViewById(R.id.searchBooksToolbar);
        setSupportActionBar(booksToolbar);
        getSupportActionBar().setTitle("CS Wizards");

        txtSearchBooks = findViewById(R.id.search_book_text);

        //Load navigation header
        booksNavView = findViewById(R.id.booksNavView);
        View navView = booksNavView.inflateHeaderView(R.layout.nav_header);
        navProfileImage = navView.findViewById(R.id.navProfileImage);
        navProfileUsername = navView.findViewById(R.id.navUsername);
        navUserCourse = navView.findViewById(R.id.navUserCourse);
        navUserBadge = navView.findViewById(R.id.navUserBadge);

        booksMainContainer = findViewById(R.id.booksMainContainer);
        booksDrawerLayout = findViewById(R.id.booksDrawerLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Fragments
        readFragment = new ReadFragment();
        searchFragment = new SearchFragment();
        favoritesFragment = new FavoritesFragment();
        tabFragment = new TabFragment();
        mainBooksFragment = new MainBooksScreenFragment();
        downloadsFragment = new DownloadsFragment();

        //Pagbukas ng nav drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, booksDrawerLayout, booksToolbar, R.string.drawer_open, R.string.drawer_close);
        booksDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        loadNavHeaderViews();

        replaceFragment(mainBooksFragment);

        txtSearchBooks.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    goToSearchBook();
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.books_read_nav:
                        replaceFragment(mainBooksFragment);
                        return true;


                    case R.id.books_favorites_nav:
                        replaceFragment(favoritesFragment);
                        return true;


                    case R.id.books_fav_nav:
                        replaceFragment(downloadsFragment);
                        return true;

                    default:
                        return false;

                }
            }

            ;
        });

        booksNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_forum:
                        goToForum();
                        return true;

                    case R.id.nav_notes:
                        goToNotes();
                        return true;

                    case R.id.nav_logout:
                        logOut();
                        return true;

                    case R.id.nav_events:
                        goToEvents();
                        return true;

                    case R.id.nav_q_n_a:
                        gotToQnA();
                        return true;

                    case R.id.nav_games:
                        goToGames();
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    public void goToGames() {
        Intent intent = new Intent(MainBooksActivity.this, MainGamesActivity.class);
        startActivity(intent);
    }

    public void gotToQnA() {
        Intent intent = new Intent(MainBooksActivity.this, QuestionsActivity.class);
        startActivity(intent);
    }

    public void goToSearchBook() {
        Intent intent = new Intent(MainBooksActivity.this, SearchBookActivity.class);
        startActivity(intent);
    }


    public void goToForum() {
        Intent intent = new Intent(MainBooksActivity.this, MainActivity2.class);
        startActivity(intent);
    }

    public void goToNotes() {
        Intent intent = new Intent(MainBooksActivity.this, MainNotesActivity.class);
        startActivity(intent);
    }

    public void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainBooksActivity.this);
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
                        Intent intent = new Intent(MainBooksActivity.this, login_activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
            }
        };
        builder.setNegativeButton("Logout", clickListener)
                .setPositiveButton("Cancel", clickListener)
                .show();
    }

    public void goToEvents() {
        Intent intent = new Intent(MainBooksActivity.this, EventsMainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.books_dot_nav, menu); //Nilalagyan ng options yung toolbar sa may gilid
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Pag nag-select ka ng menu sa gilid ng toolbar
        switch (item.getItemId()) {

            case R.id.books_dot_nav:
                goToSubmit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        replaceFragment(mainBooksFragment);
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

    public void loadNavHeaderViews() {

        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String username, userImage, userCourse;
                    Float level;

                    username = dataSnapshot.child("username").getValue().toString();
                    userImage = dataSnapshot.child("profile_image").getValue().toString();
                    userCourse = dataSnapshot.child("course").getValue().toString();
                    level = Float.parseFloat(dataSnapshot.child("star_ratings").getValue().toString());

                    if (userCourse == null) {
                        navUserCourse.setText("Not yet stated");
                    } else {
                        navUserCourse.setText(userCourse);
                    }

                    navProfileUsername.setText(username);
                    Picasso.get().load(userImage).into(navProfileImage);

                } else {
                    Toast.makeText(MainBooksActivity.this, "Error accessing account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void replaceFragment(Fragment fragment) {
        //Replacing the fragments
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.booksMainContainer, fragment);
        fragmentTransaction.commit();
    }

    public void goToSubmit() {

        Intent intent = new Intent(MainBooksActivity.this, SubmitActivity.class);
        startActivity(intent);

    }
}
