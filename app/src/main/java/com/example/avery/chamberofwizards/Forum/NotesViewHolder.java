package com.example.avery.chamberofwizards.Forum;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotesViewHolder extends RecyclerView.ViewHolder {
    View mView;

    CollectionReference notesRef;

    public NotesViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

        notesRef = FirebaseFirestore.getInstance().collection("Notes");
    }

    public void deleteNote(String note_key, final Context context) {
        notesRef.document(note_key).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(context, "Note successfully deleted!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setNote_content(String note_content) {
        TextView textView = mView.findViewById(R.id.note_content);
        textView.setText(note_content);
    }

    public void setNote_title(String note_title) {
        TextView textView = mView.findViewById(R.id.note_title);
        textView.setText(note_title);
    }
}
