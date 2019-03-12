package com.example.avery.chamberofwizards.Questions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnswerCommentRepliesActivity extends AppCompatActivity {

    public static Context myContext;

    //Firebase
    private FirebaseAuth mAuth;
    private CollectionReference commentsRef;
    private CollectionReference repliesRef;
    private CollectionReference commentsUpvotesRef;
    private DatabaseReference usersRef;
    private StorageReference storageRootRef;
    private String currentUserID;

    //Views
    private Toolbar mToolbar;

    //Cardview Comment
    private CircleImageView imgCommenter;
    private TextView txtViewCommenter;
    private TextView txtViewCommenterCourse;
    private TextView txtViewComment;
    private Button btnPostReply;
    private TextView txtViewNumberOfUpvotes;
    private ImageButton imgBtnUpvoteComment;
    private ImageView imgViewComment;

    //Recycler View
    private RecyclerView recylerViewReplies;
    private LinearLayoutManager linearLayoutManager;

    //Post reply
    private CardView cardViewPostReply;
    private ImageButton imgBtnClose;
    private Toolbar toolbarPostReply;
    private ProgressBar progressBarPostReply;
    private TextView txtViewDisplayComment;
    private EditText editTextReply;
    private Button btnSubmitReply;
    private ImageView imgViewReplyImage;
    private ImageButton imgBtnAddImage;

    private String commentKey; //Eto yung na-received sa ClickQuestionActivity
    private String answerKey;
    private String questionKey;

    //Flag for opening the gallery
    private final int comment_reply_flag = 69;

    //Uri for gallery pick
    private Uri commentReplyImageUri;

    //Uploaded Image URL
    private String commentReplyImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_comment_replies);

        myContext = AnswerCommentRepliesActivity.this;

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        commentsRef = FirebaseFirestore.getInstance().collection("Comments");
        repliesRef = FirebaseFirestore.getInstance().collection("Replies");
        commentsUpvotesRef = FirebaseFirestore.getInstance().collection("Up");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storageRootRef = FirebaseStorage.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();

        mToolbar = findViewById(R.id.repliesToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Replies");

        //Cardview Comment:
        imgCommenter = findViewById(R.id.repliesCommenterImg);
        txtViewCommenter = findViewById(R.id.repliesTxtViewCommenterName);
        txtViewCommenterCourse = findViewById(R.id.repliesTxtViewCommenterCourse);
        txtViewComment = findViewById(R.id.repliesTxtViewComment);
        btnPostReply = findViewById(R.id.repliesBtnReply);
        txtViewNumberOfUpvotes = findViewById(R.id.repliesTxtViewNumberOfUpvotes);
        imgBtnUpvoteComment = findViewById(R.id.repliesBtnUpvote);
        imgViewComment = findViewById(R.id.repliesImageViewComment);

        //Recycler View:
        recylerViewReplies = findViewById(R.id.recylerViewRepliesContainer);
        linearLayoutManager = new LinearLayoutManager(AnswerCommentRepliesActivity.this, LinearLayoutManager.VERTICAL, false);
        recylerViewReplies.setLayoutManager(linearLayoutManager);

        //Post Reply Cardview:
        cardViewPostReply = findViewById(R.id.cardviewPostReply);
        imgBtnClose = findViewById(R.id.repliesImgBtnClose);
        toolbarPostReply = findViewById(R.id.toolbarPostReply);
        progressBarPostReply = findViewById(R.id.progressBarPostReply);
        txtViewDisplayComment = findViewById(R.id.txtViewDisplayComment);
        editTextReply = findViewById(R.id.editTextReply);
        btnSubmitReply = findViewById(R.id.repliesBtnPostReply);
        imgViewReplyImage = findViewById(R.id.imgViewCommentReplyImg);
        imgBtnAddImage = findViewById(R.id.repliesImgBtnAddImage);

        //Comment key received from ClickQuestionActivity
        commentKey = getIntent().getExtras().get("comment_id").toString();
        answerKey = getIntent().getExtras().get("answer_id").toString();
        questionKey = getIntent().getExtras().get("question_id").toString();

        btnSubmitReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarPostReply.setVisibility(View.VISIBLE);
                if (commentReplyImageUri != null) {
                    //May image na inadd
                    saveCommentReplyImageToFirebaseStorage();
                } else {
                    //Walang image na inadd
                    saveCommentReplyToFirestore();
                }
            }
        });

        imgBtnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(comment_reply_flag);
            }
        });

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewPostReply.setVisibility(View.GONE);
                cardViewPostReply.setEnabled(false);
            }
        });

        editTextReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().trim().length() < 5) {
                    btnSubmitReply.setVisibility(View.GONE);
                    btnSubmitReply.setEnabled(false);
                } else {
                    btnSubmitReply.setVisibility(View.VISIBLE);
                    btnSubmitReply.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() < 5) {
                    btnSubmitReply.setVisibility(View.GONE);
                    btnSubmitReply.setEnabled(false);
                } else {
                    btnSubmitReply.setVisibility(View.VISIBLE);
                    btnSubmitReply.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnPostReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewPostReply.setVisibility(View.VISIBLE);
            }
        });

        imgBtnUpvoteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upvoteComment();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Snanpshot listener
        applyUpvoteSnapshotListener();
        applyUpvoteStatusSnapshotListener();

        progressBarPostReply.setVisibility(View.GONE);

        btnSubmitReply.setVisibility(View.GONE);
        btnSubmitReply.setEnabled(false);

        cardViewPostReply.setVisibility(View.GONE);
        loadCommentViewsData();

        loadAllReplies();
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        private CircleImageView replierImage;
        private TextView replierName;
        private TextView replierCourse;
        private TextView replierReply;
        private ImageView replierReplyImage;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            //Layout views:
            replierImage = mView.findViewById(R.id.replyLayoutReplierImage);
            replierName = mView.findViewById(R.id.replyLayoutReplierName);
            replierCourse = mView.findViewById(R.id.replyLayoutReplierCourse);
            replierReply = mView.findViewById(R.id.replyLayoutReplierReply);
            replierReplyImage = mView.findViewById(R.id.replyLayoutReplierReplyImage);
        }

        public void setReplier_course(String replier_course) {
            TextView textView = mView.findViewById(R.id.replyLayoutReplierName);
            textView.setText(replier_course);
        }

        public void setReplier_image(String replier_image) {
            CircleImageView circleImageView = mView.findViewById(R.id.replyLayoutReplierImage);
            Picasso.get().load(replier_image).into(circleImageView);
        }

        public void setReplier_name(String replier_name) {
            TextView textView = mView.findViewById(R.id.replyLayoutReplierName);
            textView.setText(replier_name);
        }

        public void setReply(String reply) {
            TextView textView = mView.findViewById(R.id.replyLayoutReplierReply);
            textView.setText(reply);
        }

        public void setReply_image(String reply_image) {
            ImageView imageView = mView.findViewById(R.id.replyLayoutReplierReplyImage);
            Picasso.get().load(reply_image).into(imageView);
        }
    }

    public void loadAllReplies() {
        Query query = repliesRef.whereEqualTo("comment_id", commentKey);

        FirestoreRecyclerOptions<Replies> options = new FirestoreRecyclerOptions.Builder<Replies>().setQuery(query, Replies.class).build();

        FirestoreRecyclerAdapter<Replies, ReplyViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Replies, ReplyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReplyViewHolder holder, int position, @NonNull Replies model) {
                final String replyKey = getSnapshots().getSnapshot(position).getId();
                holder.setReplier_course(model.getReplier_course());
                holder.setReplier_image(model.getReplier_image());
                holder.setReplier_name(model.getReplier_name());
                holder.setReply(model.getReply());
                holder.setReply_image(model.getReply_image());
            }

            @NonNull
            @Override
            public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_replies_layout, parent, false);
                return new ReplyViewHolder(view);
            }
        };
        firestoreRecyclerAdapter.startListening();
        recylerViewReplies.setAdapter(firestoreRecyclerAdapter);
    }

    public void resetCardViewReplyToDefaults() {
        commentReplyImageUri = null;
        commentReplyImageURL = null;
        editTextReply.setText("");
        imgViewReplyImage.setImageURI(null);
        imgViewReplyImage.setVisibility(View.GONE);
    }

    public void saveCommentReplyToFirestore() {
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username, course, image;
                    username = dataSnapshot.child("fullname").getValue().toString();
                    course = dataSnapshot.child("course").getValue().toString();
                    image = dataSnapshot.child("profile_image").getValue().toString();

                    DocumentReference d = repliesRef.document();
                    String id = d.getId();

                    Map<String, Object> replyMap = new ArrayMap<>();
                    replyMap.put("answer_id", answerKey);
                    replyMap.put("comment_id", commentKey);
                    replyMap.put("question_id", questionKey);
                    replyMap.put("replier", currentUserID);
                    replyMap.put("reply", editTextReply.getText().toString());
                    replyMap.put("reply_id", id);
                    replyMap.put("replier_name", username);
                    replyMap.put("replier_course", course);
                    replyMap.put("replier_image", image);

                    if (commentReplyImageURL != null) {
                        replyMap.put("reply_image", commentReplyImageURL);
                    } else {
                        replyMap.put("reply_image", null);
                    }

                    repliesRef.document(id).set(replyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AnswerCommentRepliesActivity.this, "Reply posted.", Toast.LENGTH_SHORT).show();
                                resetCardViewReplyToDefaults();
                            } else {
                                Toast.makeText(AnswerCommentRepliesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBarPostReply.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void saveCommentReplyImageToFirebaseStorage() {
        String uniqueID = UUID.randomUUID().toString();

        final StorageReference filePath = storageRootRef.child(uniqueID);

        filePath.putFile(commentReplyImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            commentReplyImageURL = uri.toString();
                        }
                    });
                } else {
                    Toast.makeText(AnswerCommentRepliesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == comment_reply_flag && resultCode == RESULT_OK && data != null) {
            commentReplyImageUri = data.getData();
            imgViewReplyImage.setImageURI(commentReplyImageUri);
            imgViewReplyImage.setVisibility(View.VISIBLE);
        }
    }

    public void openGallery(int flag) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, flag);
    }


    public void applyUpvoteStatusSnapshotListener() {
        Query query = commentsUpvotesRef
                .whereEqualTo("comment_id", commentKey)
                .whereEqualTo("upvoted_by", currentUserID);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        //May upvote
                        imgBtnUpvoteComment.setImageResource(R.drawable.upvoted_icon);
                    } else {
                        //Walang upvote
                        imgBtnUpvoteComment.setImageResource(R.drawable.upvote_icon_none);
                    }
                }
            }
        });
    }

    public void applyUpvoteSnapshotListener() {
        commentsRef.document(commentKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (documentSnapshot.exists()) {
                        String n = documentSnapshot.get("number_of_upvotes").toString();
                        txtViewNumberOfUpvotes.setText(n);
                    }
                }
            }
        });
    }

    public void upvoteComment() {
        //Check muna kung nasa upvotes ref yung upvote document na may comment_key kagaya dito at current user id:
        Query query = commentsUpvotesRef
                .whereEqualTo("comment_id", commentKey)
                .whereEqualTo("upvoted_by", currentUserID);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    //May upvote
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        String id = d.getId();
                        commentsUpvotesRef.document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    updateNumberOfUpvotes();
                                } else {
                                    Toast.makeText(AnswerCommentRepliesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    //Walang upvote
                    Map<String, Object> upvoteMap = new ArrayMap<>();
                    upvoteMap.put("question_id", questionKey);
                    upvoteMap.put("answer_id", answerKey);
                    upvoteMap.put("comment_id", commentKey);
                    upvoteMap.put("upvoted_by", currentUserID);

                    commentsUpvotesRef.document().set(upvoteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                updateNumberOfUpvotes();
                            } else {
                                Toast.makeText(AnswerCommentRepliesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void updateNumberOfUpvotes() {
        //Hahanapin lahat ng docs na may comment_id na kagaya ng comment id kapag kinclick yung comment:
        Query query = commentsUpvotesRef.whereEqualTo("comment_id", commentKey);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int count = 0;
                for (DocumentSnapshot d : queryDocumentSnapshots) {
                    count++;
                }

                Map<String, Object> map = new ArrayMap<>();
                map.put("number_of_upvotes", count);

                commentsRef.document(commentKey).update(map);
            }
        });
    }

    public void loadCommentViewsData() {
        commentsRef.document(commentKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String commenter_image = documentSnapshot.getString("commenter_image");
                    String commenter_name = documentSnapshot.getString("commenter_name");
                    String commenter_course = documentSnapshot.getString("commenter_course");
                    String comment = documentSnapshot.getString("comment");
                    String comment_image = documentSnapshot.getString("comment_image");

                    if (comment_image == null) {
                        Picasso.get().load(comment_image).into(imgViewComment);
                        imgViewComment.setVisibility(View.VISIBLE);
                    } else {
                        imgViewComment.setVisibility(View.GONE);
                    }

                    Picasso.get().load(commenter_image).into(imgCommenter);
                    txtViewCommenter.setText(commenter_name);
                    txtViewCommenterCourse.setText(commenter_course);
                    txtViewComment.setText(comment);
                    Picasso.get().load(comment_image).into(imgViewComment);
                    imgViewComment.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
