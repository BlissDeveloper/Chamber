package com.example.avery.chamberofwizards.Notes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersViewHolder extends RecyclerView.ViewHolder {
    View mView;
    Context c;

    CollectionReference notesRef;
    CollectionReference sharedNotesRef;
    FirebaseAuth mAuth;
    String currentUserID;

    CircleImageView circleImageViewShareNoteLayout;
    TextView textViewShareNoteUserFullname;
    TextView textViewShareNoteUserCourse;
    public static Button buttonShareNote;

    public UsersViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        mView = itemView;

        c = context;

        notesRef = FirebaseFirestore.getInstance().collection("Notes");
        sharedNotesRef = FirebaseFirestore.getInstance().collection("Shared Notes");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        circleImageViewShareNoteLayout = mView.findViewById(R.id.circleImageViewToShareUserImage);
        textViewShareNoteUserFullname = mView.findViewById(R.id.textViewToShareUserFullName);
        textViewShareNoteUserCourse = mView.findViewById(R.id.textViewToShareUserCourse);
        buttonShareNote = mView.findViewById(R.id.buttonToShareNote);
    }

    public void maintainShareButton(final String noteKey, final String currentUserID, final String sharesToUserID) {
        final Query query = notesRef.whereEqualTo("is_shared", true).whereEqualTo("note_key", noteKey);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty() && e == null) {
                    for(DocumentSnapshot d : queryDocumentSnapshots) {
                        String shared_by = d.getString("shared_by");
                        String shared_to = d.getString("shared_to");

                        if(shared_by.equals(currentUserID) && shared_to.equals(sharesToUserID)) {
                            buttonShareNote.setText("Shared");
                            buttonShareNote.setEnabled(false);
                        }
                        else {
                            buttonShareNote.setText("Share mo pa");
                            buttonShareNote.setEnabled(true);
                        }
                    }
                } else {

                }
            }
        });
    }

    /*
           buttonShareNote.setText("Shared");
                    buttonShareNote.setEnabled(false);
                } else {
                    buttonShareNote.setText("Share mo pa");
                    buttonShareNote.setEnabled(true);
                }
     */

    public void shareNote(final String noteKey, final String userKey) {
        notesRef.document(noteKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    final Map<String, Object> map = documentSnapshot.getData();
                    map.remove("shared_by");
                    map.remove("shared_to");
                    map.remove("note_by");
                    map.remove("is_shared");

                    map.put("is_shared", true);
                    map.put("shared_by", currentUserID);
                    map.put("shared_to", userKey);
                    map.put("note_by", userKey);
                    map.put("note_key", noteKey);

                    final String sharedNOteID = sharedNotesRef.document().getId();

                    sharedNotesRef.document(sharedNOteID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                notesRef.document(sharedNOteID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(c, "Shared successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(c, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {

                            }
                        }
                    });
                }
            }
        });
    }

    public void setCourse(String course) {
        textViewShareNoteUserCourse.setText(course);
    }

    public void setFullname(String fullname) {
        textViewShareNoteUserFullname.setText(fullname);
    }

    public void setProfile_image(String profile_image) {
        Picasso.get().load(profile_image).into(circleImageViewShareNoteLayout);
    }
}
