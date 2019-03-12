package com.example.avery.chamberofwizards.Notes.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.avery.chamberofwizards.Forum.NotesViewHolder;
import com.example.avery.chamberofwizards.Notes.Notes;
import com.example.avery.chamberofwizards.Notes.Users;
import com.example.avery.chamberofwizards.Notes.UsersViewHolder;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class YourNotesFragment extends Fragment {
    private View mView;

    private static String currentNoteID;

    private FirebaseAuth mAuth;
    private String currentUserID;
    private CollectionReference notesRef;
    private static FirestoreRecyclerAdapter<Notes, NotesViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference usersRef;

    private TextView textViewNoYourNotes;
    private RecyclerView recyclerViewYourNotes;
    private LinearLayoutManager linearLayoutManager;

    private CardView cardViewShareNote;
    private ImageButton imageButtonCloseShareNote;
    private LinearLayoutManager linearLayoutManager1;
    private RecyclerView recyclerViewShareNote;

    public YourNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_your_notes, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        notesRef = FirebaseFirestore.getInstance().collection("Notes");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        textViewNoYourNotes = mView.findViewById(R.id.textViewNoYourNotes);
        recyclerViewYourNotes = mView.findViewById(R.id.recyclerViewYourNotes);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewYourNotes.setLayoutManager(linearLayoutManager);

        cardViewShareNote = mView.findViewById(R.id.cardViewShareYourNotes);
        imageButtonCloseShareNote = mView.findViewById(R.id.imageButtonCloseShareYourNote);
        linearLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewShareNote = mView.findViewById(R.id.recyclerViewShareYourNote);
        recyclerViewShareNote.setLayoutManager(linearLayoutManager1);

        textViewNoYourNotes.setVisibility(View.GONE);

        maintainYourNotes();

        cardViewShareNote.setVisibility(View.GONE);
        cardViewShareNote.setEnabled(false);

        imageButtonCloseShareNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewShareNote.setVisibility(View.GONE);
                cardViewShareNote.setEnabled(false);
            }
        });

        return mView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void loadUsers() {
        Log.d("Avery", "Loading users...");
        com.google.firebase.database.Query query = usersRef.orderByChild("fullname");

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query, Users.class).build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder holder, int position, @NonNull Users model) {
                final String userKey = getRef(position).getKey();

                holder.maintainShareButton(currentNoteID, currentUserID, userKey);

                holder.buttonShareNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.shareNote(currentNoteID, userKey);
                    }
                });

                holder.setProfile_image(model.getProfile_image());
                holder.setCourse(model.getCourse());
                holder.setFullname(model.getFullname());
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_share_note_layout, parent, false);
                return new UsersViewHolder(view, getActivity());
            }
        };
        firebaseRecyclerAdapter1.startListening();
        recyclerViewShareNote.setAdapter(firebaseRecyclerAdapter1);
    }

    public void maintainYourNotes() {
        //final Query query = notesRef.orderByChild("is_shared").equalTo(false)
        // .orderByChild("uid").equalTo(currentUserID);

        final com.google.firebase.firestore.Query query = notesRef.whereEqualTo("is_shared", false).whereEqualTo("note_by", currentUserID);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    loadYourNotes(query);
                    Log.d("Avery", "Your notes not empty");
                } else {
                    textViewNoYourNotes.setVisibility(View.VISIBLE);
                    Log.d("Avery", "Your notes are empty");
                }
            }
        });
    }

    public void loadYourNotes(Query query) {
        FirestoreRecyclerOptions<Notes> options = new FirestoreRecyclerOptions.Builder<Notes>().setQuery(query, Notes.class).build();

        firebaseRecyclerAdapter = new FirestoreRecyclerAdapter<Notes, NotesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotesViewHolder viewHolder, int position, @NonNull Notes model) {
                final String noteKey = getSnapshots().getSnapshot(position).getId();

                viewHolder.setNote_content(model.getNote_content());
                viewHolder.setNote_title(model.getNote_title());

                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "Delete Note",
                                "Share Note"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        Dialog.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

                                        Dialog.OnClickListener click = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case Dialog.BUTTON_POSITIVE:
                                                        //Cancel
                                                        dialog.dismiss();
                                                        break;
                                                    case Dialog.BUTTON_NEGATIVE:
                                                        //Delete
                                                        viewHolder.deleteNote(noteKey, getActivity());
                                                        break;
                                                }
                                            }
                                        };

                                        b.setPositiveButton("Cancel", click)
                                                .setNegativeButton("Delete", click)
                                                .show();
                                        break;
                                    case 1:
                                        currentNoteID = noteKey;
                                        loadUsers();

                                        cardViewShareNote.setVisibility(View.VISIBLE);
                                        cardViewShareNote.setEnabled(true);
                                        break;
                                }
                            }
                        };
                        builder.setItems(options, clickListener)
                                .setIcon(R.drawable.delete_icon)
                                .show();

                        return true;
                    }
                });
            }

            @NonNull
            @Override
            public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_note_layout, parent, false);
                return new NotesViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerViewYourNotes.setAdapter(firebaseRecyclerAdapter);
    }
}
