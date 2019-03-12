package com.example.avery.chamberofwizards.Notes;

import android.support.annotation.NonNull;
import android.support.v4.app.SupportActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import javax.annotation.Nullable;

public class NewNoteActivity extends AppCompatActivity {

    //Firebase variables
    private FirebaseAuth mAuth;
    private CollectionReference notesRef;
    private String currentUserID;

    Toolbar toolbar;

    private EditText noteTitle;
    private EditText noteContent;

    private String note_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        toolbar = findViewById(R.id.newNoteToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        notesRef = FirebaseFirestore.getInstance().collection("Notes");
        currentUserID = mAuth.getCurrentUser().getUid();

        noteTitle = findViewById(R.id.noteTitle);
        noteContent = findViewById(R.id.noteContent);

        if (getIntent().hasExtra("note_key")) {
            note_key = getIntent().getExtras().getString("note_key");

            loadSavedNote(note_key);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getIntent().hasExtra("note_key")) {
            saveUpdatedNote(note_key);
        } else {
            saveNoteToDatabase();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getIntent().hasExtra("note_key")) {
                    saveUpdatedNote(note_key);
                    return true;
                } else {
                    saveNoteToDatabase();
                    return true;
                }
            default:
                return false;
        }
    }

    public void loadSavedNote(final String note_key) {
        notesRef.document(note_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()) {
                    String title, body;
                    //title = dataSnapshot.child("note_title").getValue().toString();
                   // body = dataSnapshot.child("note_content").getValue().toString();

                    title = documentSnapshot.get("note_title").toString();
                    body = documentSnapshot.get("note_content").toString();

                    noteTitle.setText(title);
                    noteContent.setText(body);
                }
            }
        });
    }

    public void saveUpdatedNote(String note_key) {
        String title, content, saveCurrentDate, saveCurrentTime;

        title = noteTitle.getText().toString();
        content = noteContent.getText().toString();

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd", Locale.US);
        saveCurrentDate = currentDate.format(callForDate.getTime());
///
        //Getting current Time

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        HashMap<String, Object> notesMap = new HashMap<>();

        notesMap.put("note_title", title);
        notesMap.put("note_content", content);
        notesMap.put("note_date", saveCurrentDate);
        notesMap.put("note_time", saveCurrentTime);

        //Saving the note to the database
        notesRef.document(note_key).update(notesMap);

    }

    public void saveNoteToDatabase() {

        String title, content, saveCurrentDate, saveCurrentTime;

        //final DatabaseReference newNoteRef = notesRef.push();
        final String key = notesRef.document().get().toString();

        title = noteTitle.getText().toString();
        content = noteContent.getText().toString();

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd", Locale.US);
        saveCurrentDate = currentDate.format(callForDate.getTime());
///
        //Getting current Time

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        HashMap<String, Object> notesMap = new HashMap<>();

        //notesMap.put("uid", currentUserID);
        notesMap.put("note_title", title);
        notesMap.put("note_content", content);
        notesMap.put("note_date", saveCurrentDate);
        notesMap.put("note_time", saveCurrentTime);
        notesMap.put("is_shared", false);
        notesMap.put("note_by", currentUserID);
        notesMap.put("shared_by", null);
        notesMap.put("shared_to", null);

        //Saving the note to the database

        notesRef.document().set(notesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
}
