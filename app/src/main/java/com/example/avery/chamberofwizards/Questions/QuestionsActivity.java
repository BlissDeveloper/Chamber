package com.example.avery.chamberofwizards.Questions;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
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
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Books.MainBooksActivity;
import com.example.avery.chamberofwizards.Events.EventsMainActivity;
import com.example.avery.chamberofwizards.Forum.MainActivity2;
import com.example.avery.chamberofwizards.Forum.VerifiyStudentNumberActivity;
import com.example.avery.chamberofwizards.Forum.login_activity;
import com.example.avery.chamberofwizards.Games.MainGamesActivity;
import com.example.avery.chamberofwizards.Notes.MainNotesActivity;
import com.example.avery.chamberofwizards.Questions.QuestionsFragment.MathematicsFragment;
import com.example.avery.chamberofwizards.Questions.QuestionsFragment.QuestionsHomeTabFragment;
import com.example.avery.chamberofwizards.Questions.QuestionsFragment.YouFragment;
import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


public class QuestionsActivity extends AppCompatActivity {

    public static String currentQuestionID;

    //Firebase
    private FirebaseAuth mAuth;
    private String currentUsetID;
    private DatabaseReference usersRef;
    private StorageReference questionsImagesRef;
    private StorageReference answersImageRef;
    private CollectionReference questionsRef;
    private CollectionReference answersRef;
    private ValueEventListener badgeListener;

    private final int Gallery_Flag = 26;
    private int CATEGORY;
    private final int answer_flag = 0;
    private Uri toAnswerImageUri;

    private ArrayList<CategoryItem> mCategoryList;
    private CategoryAdapter categoryAdapter;

    private Toolbar questionsToolbar;
    private BottomNavigationView questionsBottomNav;
    private NavigationView questionsNavView;
    private DrawerLayout questionsDrawerLayout;

    //Navigation Header Views
    private TextView navUsername;
    private TextView navUserCourse;
    private CircleImageView navUserImage;
    private ImageView navUserBadge;

    //Activity Views
    private FloatingActionButton btnAsk;
    private CardView askCardView;
    private ImageView btnClose;
    private LinearLayout slideUp;
    private Button btnPostQuestion;
    private EditText txtQuestion;
    private Toolbar cardViewToolbar;
    private Spinner categorySpinner;
    private ImageView imgQuestion;
    private ImageButton questionsAddImage;
    private ProgressBar questionsProgressBar;

    private ValueEventListener userBadgeEventListener;

    //Para sa answer cardview:
    private CardView toAnswerCardview;
    private CardView cardviewOptions;
    private Button btnAnswer;
    private EditText txtAnswer;
    private ImageButton imgBtnClose;
    private BottomNavigationView toAnswerBottomNav;
    private ImageView imgviewAnswer;
    private Toolbar answerQuestionToolbar;

    private Uri questionsImageUri;
    private int categoryCode;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;

    private String uriToSave;

    private Fragment questionsHomeTabFragment;
    private Fragment questionsYouFragment;
    private Fragment questionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUsetID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        questionsImagesRef = FirebaseStorage.getInstance().getReference();
        questionsRef = FirebaseFirestore.getInstance().collection("Questions");
        answersImageRef = FirebaseStorage.getInstance().getReference().child("Answers Images");

        questionsToolbar = findViewById(R.id.questionsToolbar);
        setSupportActionBar(questionsToolbar);
        getSupportActionBar().setTitle("Question & Answer");

        questionsBottomNav = findViewById(R.id.questions_bottom_nav);
        questionsDrawerLayout = findViewById(R.id.questionsDrawerLayout);
        questionsNavView = findViewById(R.id.questions_nav_view);
        btnAsk = findViewById(R.id.questionsFloatingButton);
        askCardView = findViewById(R.id.askCardView);
        btnClose = findViewById(R.id.questionsBtnClose);
        btnPostQuestion = findViewById(R.id.questionsBtnAsk);
        txtQuestion = findViewById(R.id.txtQuestion);
        cardViewToolbar = findViewById(R.id.cardViewToolbar);
        categorySpinner = findViewById(R.id.spinnerQuestionCategory);
        questionsAddImage = findViewById(R.id.imgBtnAddImage);
        questionsProgressBar = findViewById(R.id.questionsProgressBar);

        //To answer cardview:
        toAnswerCardview = findViewById(R.id.cardToAnswer);
        cardviewOptions = findViewById(R.id.cardViewOptions);
        btnAnswer = findViewById(R.id.questionsBtnAnswer);
        txtAnswer = findViewById(R.id.txtAnswer);
        imgBtnClose = findViewById(R.id.imgBtnClose);
        toAnswerBottomNav = findViewById(R.id.toAnswerBottomNav);
        imgviewAnswer = findViewById(R.id.imgviewAnswer);
        answerQuestionToolbar = findViewById(R.id.answerQuestionToolbar);
        setSupportActionBar(answerQuestionToolbar);
        getSupportActionBar().setTitle("Answer");

        //Setting up the cardview toolbar:
        //setSupportActionBar(cardViewToolbar);

        //Inflate the user image
        View navView = questionsNavView.inflateHeaderView(R.layout.nav_header);
        navUsername = navView.findViewById(R.id.navUsername);
        navUserCourse = navView.findViewById(R.id.navUserCourse);
        navUserImage = navView.findViewById(R.id.navProfileImage);
        navUserBadge = navView.findViewById(R.id.navUserBadge);

        //Pagbukas ng NavView
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, questionsDrawerLayout, questionsToolbar, R.string.drawer_open, R.string.drawer_close);
        questionsDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //Fragments
        questionsHomeTabFragment = new QuestionsHomeTabFragment();
        questionsYouFragment = new YouFragment();
        questionsFragment = new MathematicsFragment();

        questionsNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
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

        toAnswerBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                final int startSelection = txtAnswer.getSelectionStart();
                switch (item.getItemId()) {
                    case R.id.to_answer_add_image:
                        cardviewOptions.setVisibility(View.GONE);
                        item.setCheckable(true);
                        openGallery(answer_flag);
                        return true;
                    case R.id.to_answer_bold:
                        //Walang naka-highlight
                        cardviewOptions.setVisibility(View.GONE);
                        item.setCheckable(true);
                        setTextToBold();
                        return true;
                    case R.id.to_answer_italic:
                        cardviewOptions.setVisibility(View.GONE);
                        item.setCheckable(true);
                        setTextToItalic();
                        return true;
                    case R.id.to_answer_underline:
                        cardviewOptions.setVisibility(View.GONE);
                        item.setCheckable(true);
                        setTextToUnderline();
                        return true;
                    case R.id.to_answer_add_something:
                        cardviewOptions.setVisibility(View.VISIBLE);
                        item.setCheckable(true);
                    default:
                        return false;
                }
            }
        });

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAnswerCardview.setVisibility(View.GONE);
                toAnswerCardview.setEnabled(false);
            }
        });

        txtAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().trim().length() == 0) {
                    btnAnswer.setVisibility(View.INVISIBLE);
                    btnAnswer.setEnabled(false);
                } else {
                    //May text sa edit text
                    btnAnswer.setVisibility(View.VISIBLE);
                    btnAnswer.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                0. Respondent
                1. Date
                2. Time
                3. is_best_answer
                4. Upvotes
                5. Respondent Image
                6. Respondent Course
                7. Answer
                8. Badge
                9. best_answer_rating
                 */
                questionsProgressBar.setVisibility(View.VISIBLE);

                if (toAnswerImageUri == null) {
                    //Walang image:
                    saveAnswerToFiretore(currentQuestionID, null);
                } else {
                    saveAnswerImageIntoFirebaseStorage(currentQuestionID, toAnswerImageUri);
                }
            }
        });

        questionsAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(Gallery_Flag);
            }
        });

        questionsBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.questions_feed_menu:
                        replaceFragment(questionsFragment);
                        return true;
                    case R.id.questions_you_menu:
                        replaceFragment(questionsYouFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        btnAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAskCardView();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCardView.setVisibility(View.GONE);
            }
        });

        //Kapag wala pang laman yung edit text, disable muna yung button, pero kapag nag-type na yung user, lalabas na siya
        txtQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().trim().length() == 0) {
                    btnPostQuestion.setVisibility(View.INVISIBLE);
                    btnPostQuestion.setEnabled(false);
                } else {
                    btnPostQuestion.setVisibility(View.VISIBLE);
                    btnPostQuestion.setEnabled(true);

                    //Papaliitin yung text kapag marami na yung characters.
                    if (s.toString().trim().length() >= 10) {
                        txtQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    } else {
                        txtQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnPostQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                1. Askee
                2. Question
                3. Date and time
                4. Course
                5. Badge (Developing)
                6. Stars
                7. etc
                8. Image (if there is any)
                9. Cateogry ng tanong
                 */

                if (isACategorySelected() && !TextUtils.isEmpty(txtQuestion.getText().toString())) {
                    //May category na pinili
                    //Validate kung may question talaga
                    //Check if may image or wala:

                    Calendar callForDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd", Locale.US);
                    saveCurrentDate = currentDate.format(callForDate.getTime());

                    Calendar callForTime = Calendar.getInstance();
                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                    saveCurrentTime = currentTime.format(callForTime.getTime());

                    postRandomName = saveCurrentDate + saveCurrentTime;
                    questionsProgressBar.setVisibility(View.VISIBLE);
                    if (questionsImageUri != null) {
                        //May image
                        saveQuestionImageToStorage(questionsImageUri);
                    } else {
                        //Walang image:
                        //questionsProgressBar.setVisibility(View.VISIBLE);
                        saveQuestionToFirestore(null);
                    }
                } else if (TextUtils.isEmpty(txtQuestion.getText().toString())) {
                    Toast.makeText(QuestionsActivity.this, "Kindly write a question.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        imgQuestion = findViewById(R.id.imgQuestion);
        imgQuestion.setVisibility(View.GONE);

        askCardView.setVisibility(View.GONE);
        toAnswerCardview.setVisibility(View.GONE);
        cardviewOptions.setVisibility(View.GONE);
        btnPostQuestion.setVisibility(View.INVISIBLE);
        btnPostQuestion.setEnabled(false);
        btnAnswer.setVisibility(View.INVISIBLE);
        btnAnswer.setEnabled(false);
        imgviewAnswer.setVisibility(View.GONE);
        initList();

        //Pag-fill ng spinner:
        categoryAdapter = new CategoryAdapter(this, mCategoryList);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*
                0. Category
                1. Math
                2. Business
                3. Computer Science
                4. Biology
                5. Envi Scie
                 */
                int pos = parent.getSelectedItemPosition();
                switch (pos) {
                    case 1:
                        categoryCode = pos;
                        break;
                    case 2:
                        categoryCode = pos;
                        break;
                    case 3:
                        categoryCode = pos;
                        break;
                    case 4:
                        categoryCode = pos;
                        break;
                    case 5:
                        categoryCode = pos;
                        break;
                    default:
                        categoryCode = 0;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadNavViews(currentUsetID);
        questionsProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        replaceFragment(questionsFragment);
    }

    public void goToForum() {
        Intent intent = new Intent(QuestionsActivity.this, MainActivity2.class);
        startActivity(intent);
    }

    public void goToNotes() {
        Intent intent = new Intent(QuestionsActivity.this, MainNotesActivity.class);
        startActivity(intent);
    }

    public void goToBooks() {
        Intent intent = new Intent(QuestionsActivity.this, MainBooksActivity.class);
        startActivity(intent);
    }

    public void goToEvents() {
        Intent intent = new Intent(QuestionsActivity.this, EventsMainActivity.class);
        startActivity(intent);
    }

    public void goToQAndA() {
        Intent intent = new Intent(QuestionsActivity.this, QuestionsActivity.class);
        startActivity(intent);
    }

    public void goToGames() {
        Intent intent = new Intent(QuestionsActivity.this, MainGamesActivity.class);
        startActivity(intent);
    }

    public void logout() {
        mAuth.signOut();
        Intent intent = new Intent(QuestionsActivity.this, login_activity.class);
        startActivity(intent);
    }

    public void maintainUserBadge() {
        badgeListener = usersRef.child(currentUsetID).addValueEventListener(new ValueEventListener() {
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

    //Load nav header
    public void loadNavViews(String id) {
        usersRef.child(currentUsetID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username, course, image;
                    float level;

                    username = dataSnapshot.child("username").getValue().toString();
                    course = dataSnapshot.child("course").getValue().toString();
                    image = dataSnapshot.child("profile_image").getValue().toString();
                    level = Float.parseFloat(dataSnapshot.child("star_ratings").getValue().toString());

                    navUsername.setText(username);
                    navUserCourse.setText(course);
                    Picasso.get().load(image).into(navUserImage);

                    //Loading of badge:
                } else {
                    Toast.makeText(QuestionsActivity.this, "Error loading navigation header.", Toast.LENGTH_SHORT).show();
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
        fragmentTransaction.replace(R.id.questions_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void showAskCardView() {
        askCardView.setVisibility(View.VISIBLE);
    }

    private void initList() {
        mCategoryList = new ArrayList<>();
        mCategoryList.add(new CategoryItem("Choose a category for your question", R.drawable.category_icon_green));
        mCategoryList.add(new CategoryItem("Mathematics", R.drawable.math_icon_green));
        mCategoryList.add(new CategoryItem("Business", R.drawable.business_icon_green));
        mCategoryList.add(new CategoryItem("Computer Science", R.drawable.cs_icon_green));
        mCategoryList.add(new CategoryItem("Biology", R.drawable.bio_icon_green));
        mCategoryList.add(new CategoryItem("Environmental Science", R.drawable.envi_scie_icon_green));
    }

    public void openGallery(int flag) {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, flag);
    }

    //Pag na-close or nakapili na ng image yung user:

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Gallery_Flag && resultCode == RESULT_OK && data != null) {
            //Nakapili ng image yung user
            //Yung data ay yung image mismo, kukunin ang uri using .getData method
            questionsImageUri = data.getData();
            imgQuestion.setImageURI(questionsImageUri);
            imgQuestion.setVisibility(View.VISIBLE);
        } else if (requestCode == answer_flag && resultCode == RESULT_OK && data != null) {
            toAnswerImageUri = data.getData();
            imgviewAnswer.setImageURI(toAnswerImageUri);
            imgviewAnswer.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Error accessing image, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isACategorySelected() {
        if (categoryCode != 0) {
            //May category na pinili
            return true;
        } else {
            //Walang category na pinili
            Toast.makeText(this, "Kindly select a category for your question.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void saveQuestionImageToStorage(Uri uri) {

        final StorageReference filePath = questionsImagesRef.child("Questions Images").child(postRandomName + ".jpg");

        //Show progressbar

        filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                uriToSave = uri.toString();
                                saveQuestionToFirestore(uriToSave);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(QuestionsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            questionsProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    Toast.makeText(QuestionsActivity.this, "Image unsuccessfully uploaded.", Toast.LENGTH_SHORT).show();
                    questionsProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void saveQuestionToFirestore(final String uri_save) {
        if (uri_save != null) {
            /*
                1. Askee
                2. Question
                3. Date and time
                4. Course
                5. Badge (Developing)
                6. Stars
                7. etc
                8. Image (if there is any)
                 */

            //Kukunin muna yung username:
            usersRef.child(currentUsetID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username, course, image;
                        username = dataSnapshot.child("fullname").getValue().toString();
                        course = dataSnapshot.child("course").getValue().toString();
                        image = dataSnapshot.child("profile_image").getValue().toString();

                        Map<String, Object> questionsMap = new ArrayMap<>();
                        questionsMap.put("asker", username);
                        questionsMap.put("question", txtQuestion.getText().toString());
                        questionsMap.put("date", saveCurrentDate);
                        questionsMap.put("time", saveCurrentTime);
                        questionsMap.put("course", course);
                        questionsMap.put("badge", null);
                        questionsMap.put("star_ratings", null);
                        questionsMap.put("questions_image", uri_save);
                        questionsMap.put("category_code", categoryCode);
                        questionsMap.put("asker_image", image);
                        questionsMap.put("number_of_answers", 0);
                        questionsMap.put("asker_id", currentUsetID);
                        questionsMap.put("has_best_answer", false);

                        questionsRef.document().set(questionsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(QuestionsActivity.this, "Question posted!", Toast.LENGTH_SHORT).show();
                                    resetQuestionToDefaults();
                                } else {
                                    Toast.makeText(QuestionsActivity.this, "Question not posted.", Toast.LENGTH_SHORT).show();
                                }
                                questionsProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        Toast.makeText(QuestionsActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                        questionsProgressBar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            //Walang image yung question.
            usersRef.child(currentUsetID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username, course, image;
                        username = dataSnapshot.child("fullname").getValue().toString();
                        course = dataSnapshot.child("course").getValue().toString();
                        image = dataSnapshot.child("profile_image").getValue().toString();

                        Map<String, Object> questionsMap = new ArrayMap<>();
                        questionsMap.put("asker", username);
                        questionsMap.put("question", txtQuestion.getText().toString());
                        questionsMap.put("date", saveCurrentDate);
                        questionsMap.put("time", saveCurrentTime);
                        questionsMap.put("course", course);
                        questionsMap.put("badge", null);
                        questionsMap.put("star_ratings", null);
                        questionsMap.put("questions_image", null);
                        questionsMap.put("category_code", categoryCode);
                        questionsMap.put("asker_image", image);
                        questionsMap.put("number_of_answers", 0);
                        questionsMap.put("asker_id", currentUsetID);
                        questionsMap.put("has_best_answer", false);

                        questionsRef.document().set(questionsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(QuestionsActivity.this, "Question posted!", Toast.LENGTH_SHORT).show();
                                    resetQuestionToDefaults();
                                } else {
                                    Toast.makeText(QuestionsActivity.this, "Question not posted.", Toast.LENGTH_SHORT).show();
                                }
                                questionsProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        Toast.makeText(QuestionsActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                        questionsProgressBar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void saveAnswerImageIntoFirebaseStorage(final String q_id, final Uri u) {
        postRandomName = getCurrentDate() + getCurrentTime();

        final StorageReference answerImageFilePath = answersImageRef.child(postRandomName + ".jpg");

        answerImageFilePath.putFile(u).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    //saveAnswerToFiretore(q_id, uri.toString());
                    answerImageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            saveAnswerToFiretore(q_id, uri.toString());
                        }
                    });
                } else {
                    Toast.makeText(QuestionsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveAnswerToFiretore(final String q_id, final String img_uri) {
        final String answer;
        answer = Html.toHtml(txtAnswer.getText());
        final Question question = new Question();

        usersRef.child(currentUsetID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name, course, image;
                    name = dataSnapshot.child("fullname").getValue().toString();
                    course = dataSnapshot.child("course").getValue().toString();
                    image = dataSnapshot.child("profile_image").getValue().toString();
                    if (name != null && course != null) {
                         /*
                0. Respondent
                1. Date
                2. Time
                3. is_best_answer
                4. Upvotes
                5. Respondent Image
                6. Respondent Course
                7. Answer
                8. Badge
                9. best_answer_rating
                 */
                        question.setRespondent(name);
                        question.setDate(getCurrentDate());
                        question.setTime(getCurrentTime());
                        question.setIs_best_answer(0);
                        question.setNumber_of_upvotes(0);
                        question.setRespondent_image(image);
                        question.setRespondent_course(course);
                        question.setAnswer(answer);
                        question.setBadge("");
                        question.setBest_answer_rating(0);
                        question.setRespondent_id(currentUsetID);
                        question.setQuestion_id(q_id);

                        if (img_uri != null) {
                            question.setAnswer_image(img_uri.toString());
                        } else {
                            question.setAnswer_image(null);
                        }

                        //Actual Saving

                        //answersRef = questionsRef.document(currentQuestionID).collection("Answers");
                        answersRef = FirebaseFirestore.getInstance().collection("Answers");
                        DocumentReference d = answersRef.document();

                        question.setAnswer_id(d.getId());

                        answersRef.document(question.getAnswer_id()).set(question).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(QuestionsActivity.this, "Answer stored into Firestore", Toast.LENGTH_SHORT).show();
                                    resetAnswerCardToDefault();
                                    updateNumberOfAnswers(q_id);
                                } else {
                                    Toast.makeText(QuestionsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                questionsProgressBar.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        Toast.makeText(QuestionsActivity.this, "Error retrieving name and course", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(QuestionsActivity.this, "Error retrieving user data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void resetQuestionToDefaults() {
        Toast.makeText(this, "Resetting defaults", Toast.LENGTH_SHORT).show();
        txtQuestion.setText("");
        imgQuestion.setImageURI(null);
        imgQuestion.setVisibility(View.GONE);
        categorySpinner.setSelection(0);
        btnPostQuestion.setVisibility(View.GONE);
        btnPostQuestion.setEnabled(false);
    }

    public void resetAnswerCardToDefault() {
        txtAnswer.setText("");
        imgviewAnswer.setImageURI(null);
        imgviewAnswer.setVisibility(View.GONE);
    }

    public String getCurrentDate() {
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd", Locale.US);
        saveCurrentDate = currentDate.format(callForDate.getTime());

        return saveCurrentDate;
    }

    public String getCurrentTime() {
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        return saveCurrentTime;
    }

    public void setTextToBold() {
        /*
        EditText et=(EditText)findViewById(R.id.edit);

        int startSelection=et.getSelectionStart();
        int endSelection=et.getSelectionEnd();

        String selectedText = et.getText().toString().substring(startSelection, endSelection);
         */
        final String currentText = Html.toHtml(txtAnswer.getText());
        final int startSelection = txtAnswer.getSelectionStart();
        final int endSelection = txtAnswer.getSelectionEnd();

        String selectedText = txtAnswer.getText().toString().substring(startSelection, endSelection);

        String boldedText = "<b>" + selectedText + "</b>";

        final String newText = currentText.replace(selectedText, boldedText);

        txtAnswer.setText(Html.fromHtml(newText));
    }

    public void setTextToUnderline() {
        /*
        EditText et=(EditText)findViewById(R.id.edit);

        int startSelection=et.getSelectionStart();
        int endSelection=et.getSelectionEnd();

        String selectedText = et.getText().toString().substring(startSelection, endSelection);
         */
        final String currentText = Html.toHtml(txtAnswer.getText());
        final int startSelection = txtAnswer.getSelectionStart();
        final int endSelection = txtAnswer.getSelectionEnd();

        String selectedText = txtAnswer.getText().toString().substring(startSelection, endSelection);

        String boldedText = "<u>" + selectedText + "</u>";

        final String newText = currentText.replace(selectedText, boldedText);

        txtAnswer.setText(Html.fromHtml(newText));
    }

    public void setTextToItalic() {
        /*
        EditText et=(EditText)findViewById(R.id.edit);

        int startSelection=et.getSelectionStart();
        int endSelection=et.getSelectionEnd();

        String selectedText = et.getText().toString().substring(startSelection, endSelection);
         */
        final String currentText = Html.toHtml(txtAnswer.getText());
        final int startSelection = txtAnswer.getSelectionStart();
        final int endSelection = txtAnswer.getSelectionEnd();

        String selectedText = txtAnswer.getText().toString().substring(startSelection, endSelection);

        String boldedText = "<i>" + selectedText + "</i>";

        final String newText = currentText.replace(selectedText, boldedText);

        txtAnswer.setText(Html.fromHtml(newText));
    }

    public void updateNumberOfAnswers(final String questionKey) {
        questionsRef.document(questionKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int answers;
                String nAnswers = documentSnapshot.get("number_of_answers").toString();
                answers = Integer.parseInt(nAnswers) + 1;

                Map<String, Object> updateMap = new ArrayMap<>();
                updateMap.put("number_of_answers", answers);

                questionsRef.document(questionKey).set(updateMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(QuestionsActivity.this, "Number of answers updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(QuestionsActivity.this, "Number of answers not updated.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}

/*
BOOKMARK STATUS IN YOU FRAGMENT
AALISIN YUGN TAB SA ILALIM NG MATH FRAGMENT
PALABASIN ANG REPLY CARD SA CLICK ACIITY
 */
