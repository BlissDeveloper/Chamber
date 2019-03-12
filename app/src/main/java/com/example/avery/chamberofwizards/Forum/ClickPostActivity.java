package com.example.avery.chamberofwizards.Forum;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    //Firebase
    private DatabaseReference postsRef;
    private FirebaseAuth auth;
    private String currentUserID;
    private String dbUserID;

    private ImageView postImage;
    private TextView txtDesc;
    private Button btnEdit;
    private Button btnDel;
    private String postKey;
    private String desc;
    private String imageURL;


    @Override
    public <T extends View> T findViewById(int id) {
        return super.findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        postKey = getIntent().getExtras().get("postKey").toString();

        //Firebase
        postsRef = FirebaseDatabase.getInstance().getReference().child("All Posts").child(postKey);
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();

        postImage = (ImageView) findViewById(R.id.imgPost);
        txtDesc = findViewById(R.id.txtDesc);
        btnEdit = findViewById(R.id.btnEdit);
        btnDel = findViewById(R.id.btnDelPost);

        btnEdit.setVisibility(View.INVISIBLE);
        btnDel.setVisibility(View.INVISIBLE);

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    desc = dataSnapshot.child("description").getValue().toString();
                    imageURL = dataSnapshot.child("image_url").getValue().toString();
                    dbUserID = dataSnapshot.child("uid").getValue().toString();

                    if (currentUserID.equals(dbUserID)) {
                        btnEdit.setVisibility(View.VISIBLE);
                        btnDel.setVisibility(View.VISIBLE);
                    }

                    btnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editCurrentPost(desc);
                        }
                    });


                    txtDesc.setText(desc);
                    Picasso.get().load(imageURL).into(postImage);
                } else {
                    Toast.makeText(ClickPostActivity.this, "Error retrieving post.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void editCurrentPost(String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post:");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Update the description of the post.
                postsRef.child("description").setValue(inputField.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ClickPostActivity.this, "Post updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ClickPostActivity.this, "Error updating post. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);

    }
}
