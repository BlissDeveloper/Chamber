package com.example.avery.chamberofwizards.Prelims;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.strictmode.CleartextNetworkViolation;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.avery.chamberofwizards.Books.MainBooksActivity;
import com.example.avery.chamberofwizards.Events.EventsMainActivity;
import com.example.avery.chamberofwizards.Forum.MainActivity2;
import com.example.avery.chamberofwizards.Forum.VerifiyStudentNumberActivity;
import com.example.avery.chamberofwizards.Forum.login_activity;
import com.example.avery.chamberofwizards.Forum.setup_activity;
import com.example.avery.chamberofwizards.Games.MainGamesActivity;
import com.example.avery.chamberofwizards.Notes.MainNotesActivity;
import com.example.avery.chamberofwizards.Questions.QuestionsActivity;
import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.internal.schedulers.NewThreadWorker;

public class HomePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference usersRef;

    private CircleImageView circleForum, circleNotes, circleEPub, circleEvents, circleQnA, circleGames, circleSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        circleForum = (CircleImageView) findViewById(R.id.circleForum);
        circleNotes = (CircleImageView) findViewById(R.id.circleNotes);
        circleSignOut = (CircleImageView) findViewById(R.id.circleLogout);
        circleEPub = (CircleImageView) findViewById(R.id.circlePublication);
        circleEvents = (CircleImageView) findViewById(R.id.circleEvents);
        circleQnA = (CircleImageView) findViewById(R.id.circleQnA);
        circleGames = (CircleImageView) findViewById(R.id.circleGames);

        circleForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToForum();
            }
        });

        circleNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToNotes();
            }
        });

        circleEPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToEPub();
            }
        });

        circleEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToEvents();
            }
        });

        circleQnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToQnA();
            }
        });

        circleGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToGames();
            }
        });

        circleSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);

                Dialog.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case Dialog.BUTTON_POSITIVE:
                                //Cancel
                                dialog.dismiss();
                                break;
                            case Dialog.BUTTON_NEGATIVE:
                                //Logout
                                mAuth.signOut();
                                signOut();
                                break;
                        }
                    }
                };

                builder.setTitle("Are you sure you want to sign out?")
                        .setPositiveButton("Cancel", clickListener)
                        .setNegativeButton("Sign out", clickListener)
                        .show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUserID == null) {
            sendToLogIn();
        } else {
            checkUserExistenceInDB();
        }
    }

    public void sendToForum() {
        Intent intent = new Intent(HomePageActivity.this, MainActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void sendToNotes() {
        Intent intent = new Intent(HomePageActivity.this, MainNotesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void sendToEPub() {
        Intent intent = new Intent(HomePageActivity.this, MainBooksActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void sendToEvents() {
        Intent intent = new Intent(HomePageActivity.this, EventsMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void sendToQnA() {
        Intent intent = new Intent(HomePageActivity.this, QuestionsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void sendToGames() {
        Intent intent = new Intent(HomePageActivity.this, MainGamesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void signOut() {
        Intent intent = new Intent(HomePageActivity.this, login_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void sendToSetupAccount() {
        Intent intent = new Intent(HomePageActivity.this, setup_activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void sendToEmailVerification() {
        Intent intent = new Intent(HomePageActivity.this, VerifyEmailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void sendToVerificationOfStudentNumber() {
        Intent intent = new Intent(HomePageActivity.this, VerifiyStudentNumberActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void sendToLogIn() {
        Intent intent = new Intent(HomePageActivity.this, login_activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void checkUserExistenceInDB() {
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                mAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            //Verified email
                            if (dataSnapshot.hasChild("student_number")) { //Checheck kung verified na yung student number:
                                if (!dataSnapshot.hasChild("fullname")) {
                                    sendToSetupAccount();
                                }
                            } else {
                                //Hindi pa verified ang student number
                                sendToVerificationOfStudentNumber();
                            }
                        } else {
                            //Hindi pa verified and email
                            sendToEmailVerification();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*
     usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!dataSnapshot.hasChild(currentUserID)) {
                        //Wala sa database ang user
                       sendToVerificationOfStudentNumber();
                    }
                    else {
                        if(mAuth.getCurrentUser().isEmailVerified()) {
                            usersRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        if(dataSnapshot.hasChild("student_number")) {
                                            //Nakapag-verify na:
                                            sendToSetupAccount();
                                        }
                                        else {
                                            sendToVerificationOfStudentNumber();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            sendToEmailVerification();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
     */
}
