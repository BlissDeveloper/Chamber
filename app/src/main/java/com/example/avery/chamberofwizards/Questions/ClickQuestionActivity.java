package com.example.avery.chamberofwizards.Questions;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Forum.VerifiyStudentNumberActivity;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.internal.operators.observable.ObservableElementAt;

public class ClickQuestionActivity extends AppCompatActivity {

    private CollectionReference questionsRef;
    private CollectionReference upvotesRef;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private CollectionReference answersRef;
    private StorageReference answersCommentStorageRef;
    private StorageReference filePath;
    private CollectionReference commentsRef;
    private CollectionReference repliesRef;
    private StorageReference commentRepliesStorageRef;

    //Eto yung naglo-load sa sort by spinner;
    private ArrayList<SortBySpinnerItem> sortBySpinnerItems;
    //Adapter ng mga sort by menu
    private SortBySpinnerItemAdapter sortBySpinnerItemAdapter;

    private android.support.v7.widget.Toolbar clickQuestionToolbar;
    private String questionID;

    private TextView txtCategory;
    private TextView txtAskerName;
    private TextView txtAskerCourse;
    private CircleImageView imgAskerImage;
    private TextView txtQuestion;
    private TextView txtNumberOfAnswerrs;
    private ImageView imgviewQuestionImage;
    private RecyclerView answersContainer;
    private LinearLayoutManager linearLayoutManager;
    private CardView cardviewAnswerComment;
    private TextView txtAnswerComment;
    private TextView questionsCommentTxtComment;
    private Button questionsCommentBtnSubmit;
    private ImageButton answerCommentAddImage;
    private ImageView answerCommentImg;
    private ProgressBar progressBar;
    private ImageButton questionsCommentBtnClose;
    private ProgressBar replyProgress;
    private Button btnFilter;
    private Spinner sortBySpinner;

    private Toolbar commentToolbar;

    //Reply Cardview
    public static CardView commentReplyCardview;
    public static ImageButton commentReplyImgBtnClose;
    public static TextView commentReplyTextComment;
    public static EditText commentReplyEditTextReply;
    public static Button commentReplyButtonSubmit;
    public static ImageButton commentReplyAddImage;
    public static ImageView commentReplyImageView;
    public static Toolbar commentReplyToolbar;

    private Boolean isTapped;
    private String currentAnswerID;
    public static String currentComment;

    public static String currentCommentID;

    private final int Gallery_code = 26;
    private final int Open_Gallery_Comment_Reply = 123;
    Uri uri;
    Uri replyCommentImageUri;
    Uri answerCommentImageUri;
    String answerCommentImageURL;
    String comment_name, commenter_image;

    public static Context mContext;

    Query displayAnswerQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_question);

        mContext = this;

        questionsRef = FirebaseFirestore.getInstance().collection("Questions");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        answersRef = FirebaseFirestore.getInstance().collection("Answers");
        answersCommentStorageRef = FirebaseStorage.getInstance().getReference().child("Answer Comments");
        commentsRef = FirebaseFirestore.getInstance().collection("Comments");
        repliesRef = FirebaseFirestore.getInstance().collection("Replies");
        commentRepliesStorageRef = FirebaseStorage.getInstance().getReference().child("Comment Replies");

        txtCategory = findViewById(R.id.txtClickQuestionCategory);
        txtAskerName = findViewById(R.id.txtClickQuestionAsker);
        txtAskerCourse = findViewById(R.id.txtClickQuestionAskerCourse);
        imgAskerImage = findViewById(R.id.imgClickQuestionAskerImage);
        txtQuestion = findViewById(R.id.txtClickQuestionQuestion);
        txtNumberOfAnswerrs = findViewById(R.id.txtClickQuestionNumberOfAnswers);
        imgviewQuestionImage = findViewById(R.id.imgClickQuestionQuestionImage);
        answersContainer = findViewById(R.id.recyclerviewClickQuestion);
        btnFilter = findViewById(R.id.clickQuestionBtnFilter);

        loadSortBySpinnerItems();
        sortBySpinner = findViewById(R.id.spinnerSortBy);
        sortBySpinnerItemAdapter = new SortBySpinnerItemAdapter(ClickQuestionActivity.this, sortBySpinnerItems);
        sortBySpinner.setAdapter(sortBySpinnerItemAdapter);

        cardviewAnswerComment = findViewById(R.id.cardviewAnswerComment);
        questionsCommentTxtComment = findViewById(R.id.questionsCommentTxtComment);
        questionsCommentBtnSubmit = findViewById(R.id.questionBtnComment);
        answerCommentAddImage = findViewById(R.id.questionAnswerCommentImgBtnAddImg);
        answerCommentImg = findViewById(R.id.imageviewImgAnswerComment);
        progressBar = findViewById(R.id.commentsProgress);
        questionsCommentBtnClose = findViewById(R.id.questionsCommentImgButtonClose);
        commentToolbar = findViewById(R.id.questionsCommentToolbar);

        replyProgress = findViewById(R.id.replyProgress);

        //Reply Cardview
        commentReplyCardview = findViewById(R.id.cardviewCommentReply);
        commentReplyImgBtnClose = findViewById(R.id.replyCommentImgBtnClose);
        commentReplyTextComment = findViewById(R.id.commentReplyComment);
        commentReplyEditTextReply = findViewById(R.id.replyCommentTxtComment);
        commentReplyButtonSubmit = findViewById(R.id.replyBtnComment);
        commentReplyAddImage = findViewById(R.id.commentReplyImageButtonAddImg);
        commentReplyImageView = findViewById(R.id.imageviewCommentReply);

        linearLayoutManager = new LinearLayoutManager(ClickQuestionActivity.this, LinearLayoutManager.VERTICAL, false);
        answersContainer.setLayoutManager(linearLayoutManager);

        clickQuestionToolbar = findViewById(R.id.clickQuestionToolbar);
        setSupportActionBar(clickQuestionToolbar);
        getSupportActionBar().setTitle("Answers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commentReplyToolbar = findViewById(R.id.replyCommentToolbar);

        setSupportActionBar(commentReplyToolbar);
        getSupportActionBar().setTitle("");

        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Comment");

        questionID = getIntent().getExtras().get("question_key").toString();

        if (questionID == null) {
            Toast.makeText(this, "Error retrieving question ID.", Toast.LENGTH_SHORT).show();
        }

        isTapped = false;

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = parent.getSelectedItemPosition();

                switch (pos) {
                    case 1:
                        //Sort by best answer first

                        break;
                    case 2:
                        //Sort by upvotes
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        commentReplyImgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentReplyCardview.setVisibility(View.GONE);
                commentReplyCardview.setEnabled(false);
            }
        });

        commentReplyButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyProgress.setVisibility(View.VISIBLE);
                if (commentReplyImageView.getDrawable() == null) {
                    //Walang laman ang image
                    saveCommentReplyToFirestore(null, questionID, currentAnswerID, currentCommentID, currentUserID, commentReplyEditTextReply.getText().toString());
                } else {
                    //May laman ang image
                    saveCommentReplyImageToFirebaseStorage(replyCommentImageUri, questionID, currentAnswerID, currentCommentID, currentUserID, commentReplyEditTextReply.getText().toString());
                }
            }
        });

        commentReplyAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(Open_Gallery_Comment_Reply);
            }
        });

        commentReplyEditTextReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().trim().length() >= 5) {
                    commentReplyButtonSubmit.setVisibility(View.VISIBLE);
                    commentReplyButtonSubmit.setEnabled(true);
                } else {
                    commentReplyButtonSubmit.setVisibility(View.GONE);
                    commentReplyButtonSubmit.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() >= 5) {
                    commentReplyButtonSubmit.setVisibility(View.VISIBLE);
                    commentReplyButtonSubmit.setEnabled(true);
                } else {
                    commentReplyButtonSubmit.setVisibility(View.GONE);
                    commentReplyButtonSubmit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        answerCommentAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(Gallery_code);
            }
        });

        questionsCommentBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCommentCardview();
            }
        });

        questionsCommentTxtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int t = s.toString().trim().length();
                if (t > 0) {
                    questionsCommentBtnSubmit.setVisibility(View.VISIBLE);
                    questionsCommentBtnSubmit.setEnabled(true);
                } else {
                    questionsCommentBtnSubmit.setVisibility(View.GONE);
                    questionsCommentBtnSubmit.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        questionsCommentBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (answerCommentImageUri != null) {
                    //May image:
                    saveAnswerCommentImageToFirebaseStorage(uri, questionID, currentAnswerID, currentUserID, questionsCommentTxtComment.getText().toString());
                } else {
                    //Walang image:
                    saveAnswerCommentToFirestore(null, questionID, currentAnswerID, currentUserID, questionsCommentTxtComment.getText().toString());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        commentReplyButtonSubmit.setVisibility(View.GONE);
        commentReplyButtonSubmit.setEnabled(false);
        commentReplyCardview.setVisibility(View.GONE);
        commentReplyCardview.setEnabled(false);

        progressBar.setVisibility(View.GONE);

        displayQuestionInformation(questionID);
        hideImage();
        displayAnswers(questionID, currentUserID);
    }

    public void loadSortBySpinnerItems() {
        sortBySpinnerItems = new ArrayList<>();

        sortBySpinnerItems.add(new SortBySpinnerItem("Sort By", R.drawable.sort_icon_blue));
        sortBySpinnerItems.add(new SortBySpinnerItem("Best Answer First", R.drawable.star_icon));
        sortBySpinnerItems.add(new SortBySpinnerItem("Upvotes", R.drawable.upvoted_icon));
    }

    public void hideCommentCardview() {
        cardviewAnswerComment.setVisibility(View.GONE);
        cardviewAnswerComment.setEnabled(false);
    }

    public void displayQuestionInformation(String qKey) {
        questionsRef.document(qKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int category, number_of_answers;
                String name, course, asker_image, question, question_image;

                category = Integer.parseInt(documentSnapshot.get("category_code").toString());
                number_of_answers = Integer.parseInt(documentSnapshot.get("number_of_answers").toString());

                name = documentSnapshot.getString("asker");
                course = documentSnapshot.getString("course");
                asker_image = documentSnapshot.getString("asker_image");
                question = documentSnapshot.getString("question");
                question_image = documentSnapshot.getString("questions_image");

                txtAskerName.setText(name);
                txtAskerCourse.setText(course);
                txtQuestion.setText(question);

                if (number_of_answers > 1) {
                    txtNumberOfAnswerrs.setText(number_of_answers + " Answers");
                } else if (number_of_answers == 1) {
                    txtNumberOfAnswerrs.setText(number_of_answers + " Answer");
                } else if (number_of_answers == 0) {
                    txtNumberOfAnswerrs.setText("No answers yet. Be the first to answer!");
                }

                /*
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.math_icon_triadic, 0, 0, 0);
                    textView.setText("Mathematics");

                    0. Category
                    1. Math
                    2. Business
                    3. Computer Science
                    4. Biology
                    5. Envi Scie
                 */
                switch (category) {
                    case 1:
                        txtCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.math_icon_triadic, 0, 0, 0);
                        txtCategory.setText("Mathematics");
                        break;
                    case 2:
                        txtCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.business_icon_triadic, 0, 0, 0);
                        txtCategory.setText("Business");
                        break;
                    case 3:
                        txtCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.cs_icon_post, 0, 0, 0);
                        txtCategory.setText("Computer Science");
                        break;
                    case 4:
                        txtCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.bio_icon_triadic, 0, 0, 0);
                        txtCategory.setText("Biology");
                        break;
                    case 5:
                        txtCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.envi_scie_triadic, 0, 0, 0);
                        txtCategory.setText("Environmental Science");
                        break;
                }

                if (question_image != null) {
                    Picasso.get().load(question_image).into(imgviewQuestionImage);
                    imgviewQuestionImage.setVisibility(View.VISIBLE);
                }
                Picasso.get().load(asker_image).into(imgAskerImage);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Gallery_code && resultCode == RESULT_OK && data != null) {
            answerCommentImageUri = data.getData();
            answerCommentImg.setImageURI(answerCommentImageUri);
            answerCommentImg.setVisibility(View.VISIBLE);
        } else if (requestCode == Open_Gallery_Comment_Reply && resultCode == RESULT_OK && data != null) {
            replyCommentImageUri = data.getData();
            commentReplyImageView.setImageURI(replyCommentImageUri);
            commentReplyImageView.setVisibility(View.VISIBLE);
        }
    }

    public void resetReplyCardviewToDefaults() {
        commentReplyEditTextReply.setText("");
        commentReplyImageView.setImageURI(null);
        commentReplyImageView.setVisibility(View.GONE);
        replyCommentImageUri = null;
    }

    public void saveCommentReplyImageToFirebaseStorage(final Uri uri, final String q_id, final String a_id, final String c_id, final String uid, final String r) {
        if (uri != null) {
            Date date = new Date();
            Timestamp ts = new Timestamp(date);
            String t = ts.toString();

            final StorageReference filePath = commentRepliesStorageRef.child(t);

            filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri theUri) {
                                saveCommentReplyToFirestore(theUri.toString(), q_id, a_id, c_id, uid, r);
                            }
                        });
                    }
                }
            });
        }
    }

    public void saveCommentReplyToFirestore(final String uri, final String q_id, final String a_id, final String c_id, final String uid, final String r) {
        usersRef.child(uid).addValueEventListener(new ValueEventListener() {
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
                    replyMap.put("question_id", q_id);
                    replyMap.put("answer_id", a_id);
                    replyMap.put("comment_id", c_id);
                    replyMap.put("reply_id", id);
                    replyMap.put("reply", r);
                    replyMap.put("replier", uid);
                    replyMap.put("replier_name", username);
                    replyMap.put("replier_course", course);
                    replyMap.put("replier_image", image);

                    if (uri != null) {
                        replyMap.put("reply_image", uri);
                    } else {
                        replyMap.put("reply_image", null);
                    }

                    repliesRef.document(id).set(replyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            replyProgress.setVisibility(View.GONE);
                            resetReplyCardviewToDefaults();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void resetCommentCardviewToDefaults() {
        questionsCommentTxtComment.setText("");
        answerCommentImg.setImageURI(null);
        answerCommentImg.setVisibility(View.GONE);
        answerCommentImageUri = null;
        answerCommentImageURL = null;
    }

    public void saveAnswerCommentImageToFirebaseStorage(final Uri u, final String q_id, final String a_id, final String uid, final String c) {

        Date date = new Date();
        Timestamp ts = new Timestamp(date);
        String t = ts.toString();

        filePath = answersCommentStorageRef.child(t);

        filePath.putFile(answerCommentImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                answerCommentImageURL = uri.toString();
                                saveAnswerCommentToFirestore(answerCommentImageURL, q_id, a_id, uid, questionsCommentTxtComment.getText().toString());
                            }
                        }
                    });
                } else {
                    Toast.makeText(ClickQuestionActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void saveAnswerCommentToFirestore(final String uri, final String q_id, final String a_id, final String uid, final String c) {
        usersRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    comment_name = dataSnapshot.child("fullname").getValue().toString();
                    commenter_image = dataSnapshot.child("profile_image").getValue().toString();
                    String course = dataSnapshot.child("course").getValue().toString();

                    if (answerCommentImageURL != null) {
                        //May image
                        Map<String, Object> commentMap = new ArrayMap<>();
                        commentMap.put("comment_by", uid);
                        commentMap.put("question_id", q_id);
                        commentMap.put("answer_id", a_id);
                        commentMap.put("comment", c);
                        commentMap.put("comment_image", answerCommentImageURL);
                        commentMap.put("commenter_name", comment_name);
                        commentMap.put("commenter_image", commenter_image);
                        commentMap.put("number_of_upvotes", 0);
                        commentMap.put("commenter_course", course);

                        commentsRef.document().set(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ClickQuestionActivity.this, "Comment successfully posted.", Toast.LENGTH_SHORT).show();
                                    resetCommentCardviewToDefaults();
                                } else {
                                    Toast.makeText(ClickQuestionActivity.this, "Comment not posted.", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        //Wala
                        Map<String, Object> commentMap = new ArrayMap<>();
                        commentMap.put("comment_by", uid);
                        commentMap.put("question_id", q_id);
                        commentMap.put("answer_id", a_id);
                        commentMap.put("comment", c);
                        commentMap.put("commenter_name", comment_name);
                        commentMap.put("commenter_image", commenter_image);
                        commentMap.put("number_of_upvotes", 0);
                        commentMap.put("commenter_course", course);

                        commentsRef.document().set(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ClickQuestionActivity.this, "Comment successfully posted.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ClickQuestionActivity.this, "Comment not posted.", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void hideImage() {
        imgviewQuestionImage.setVisibility(View.GONE);
        imgviewQuestionImage.setEnabled(false);
    }

    public void openGallery(int flag) {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, flag);
    }

    public void displayAnswers(final String id, final String uid) { //Question id
        questionsRef.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() { //On Success
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Toast.makeText(ClickQuestionActivity.this, "Configuring query", Toast.LENGTH_SHORT).show();
                Boolean has_best_answer = documentSnapshot.getBoolean("has_best_answer");

                if (has_best_answer) {
                    Toast.makeText(ClickQuestionActivity.this, "Has best answer", Toast.LENGTH_SHORT).show();
                    displayAnswerQuery = answersRef.whereEqualTo("question_id", id).orderBy("is_best_answer", Query.Direction.DESCENDING);
                } else {
                    displayAnswerQuery = answersRef.whereEqualTo("question_id", id);
                }

                FirestoreRecyclerOptions<Question> options = new FirestoreRecyclerOptions.Builder<Question>().setQuery(displayAnswerQuery, Question.class).build();

                FirestoreRecyclerAdapter<Question, AnswerViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Question, AnswerViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final AnswerViewHolder holder, int position, @NonNull final Question model) {
                        holder.setRespondent(model.getRespondent());
                        holder.setRespondent_image(model.getRespondent_image());
                        holder.setRespondent_course(model.getRespondent_course());
                        holder.setAnswer(model.getAnswer());
                        holder.setAnswer_image(model.getAnswer_image());
                        //holder.setNumber_of_upvotes(model.getNumber_of_upvotes());

                        holder.txtAnswerComment.setEnabled(false);

                        final String postKey = getSnapshots().getSnapshot(position).getId();

                        holder.setRespondent_id(model.getRespondent_id());

                        holder.setUpvoteStatus(questionID, currentUserID, postKey);

                        holder.maintainNumberOfUpvotes(questionID, postKey);

                        //Maintains the display of the button:
                        holder.ifWillDisplayButton(questionID);

                        holder.setIs_best_answer(model.isIs_best_answer(), model.getBest_answer_rating());

                        holder.cardviewComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(ClickQuestionActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                                currentAnswerID = postKey;
                                cardviewAnswerComment.setVisibility(View.VISIBLE);
                            }
                        });

                        holder.btnSubmitBestAnswerRating.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.validateAndSubmitRating(currentUserID, questionID, postKey, ClickQuestionActivity.this, model.getRespondent_id());
                            }
                        });


                        holder.btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.hideRatingCard();
                            }
                        });

                        holder.btnBestAnswer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.displayRatingCard();
                            }
                        });

                        holder.answersDotsButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Delete Answer"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(ClickQuestionActivity.this);
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                //Validate:
                                                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface d, int choice) {
                                                        switch (choice) {
                                                            case DialogInterface.BUTTON_POSITIVE:
                                                                //Dismiss
                                                                d.dismiss();
                                                                break;
                                                            case DialogInterface.BUTTON_NEGATIVE:
                                                                //Delete
                                                                holder.deleteAnswer(postKey, ClickQuestionActivity.this);
                                                                break;
                                                        }
                                                    }
                                                };

                                                AlertDialog.Builder b = new AlertDialog.Builder(ClickQuestionActivity.this);
                                                b.setTitle("Confirm Delete")
                                                        .setPositiveButton("Cancel", clickListener)
                                                        .setNegativeButton("Delete", clickListener)
                                                        .show();
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });


                        holder.maintainDotsButton(currentUserID, model.getRespondent_id(), ClickQuestionActivity.this);

                        holder.btnUpvote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isTapped = true;
                                if (isTapped) {
                                    holder.retainOrRemoveUpvote(currentUserID, questionID, postKey, ClickQuestionActivity.this);
                                    isTapped = false;
                                }
                            }
                        });

                        holder.circleImageViewDown.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                currentAnswerID = postKey;
                                if (!holder.answerCommentsHolder.isEnabled()) {
                                    //Hindi pa naka-show yung mga comments:
                                    holder.answerCommentsHolder.setEnabled(true);
                                    Toast.makeText(ClickQuestionActivity.this, "Comments shown", Toast.LENGTH_SHORT).show();
                                    holder.displayAnswerComments(questionID, postKey, ClickQuestionActivity.this, uid);
                                    holder.circleImageViewDown.setImageResource(R.drawable.pull_up);
                                } else {
                                    //Naka-show na yung mga comments
                                    holder.answerCommentsHolder.setEnabled(false);
                                    holder.commentsRecyclerAdapter.stopListening();
                                    holder.circleImageViewDown.setImageResource(R.drawable.drop_down);
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_answers_layout, parent, false);
                        return new AnswerViewHolder(view, ClickQuestionActivity.this);
                    }
                };
                firestoreRecyclerAdapter.startListening();
                answersContainer.setAdapter(firestoreRecyclerAdapter);
            }
        }); //On Success
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {

        private View V;
        private TextView txtIsBestAnswer;
        private CircleImageView imgCurrentUserImage;
        private TextView txtYouUpvotedThis;
        private Button btnBestAnswer;
        private Button btnUpvote;
        private CardView cardviewAnswerRating;
        //private CircleImageView currentUserImage;
        private ImageButton btnClose;
        private Button btnSubmitBestAnswerRating;
        private RatingBar ratingbarAnswerRating;
        private TextView txtNumberOfUpvotes;
        private TextView txtAnswerComment;
        private TextView questionsCommentTxtComment;
        private Button questionsCommentBtnSubmit;
        private CardView cardviewComment;
        private ImageButton answerCommentAddImage;
        private CircleImageView circleImageViewDown;
        private RecyclerView answerCommentsHolder;
        private LinearLayoutManager linearLayoutManager;
        private ImageButton answersDotsButton;
        private RatingBar answererAnswerRating;
        private ImageView imageviewAnswererBadge;

        private FirebaseAuth firebaseAuth;
        private DatabaseReference userRef;
        private CollectionReference questionRef;
        private String currentUID;
        private CollectionReference upvotesRef;
        private CollectionReference answersRef;
        private FirestoreRecyclerAdapter<Comments, AnswerCommentsViewHolder> commentsRecyclerAdapter;
        private CollectionReference commentsRef;
        private CollectionReference commentUpvotesRef;
        private CollectionReference commentRepliesRef;

        private Boolean isShown;

        public AnswerViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            V = itemView;
            txtIsBestAnswer = V.findViewById(R.id.txtIsBestAnswer);
            imgCurrentUserImage = V.findViewById(R.id.imgCurrentUserImage);
            txtYouUpvotedThis = V.findViewById(R.id.txtYouUpvotedThis);
            btnBestAnswer = V.findViewById(R.id.btnBestAnswer);
            btnUpvote = V.findViewById(R.id.btnUpvote);
            cardviewAnswerRating = V.findViewById(R.id.cardviewRating);
            //currentUserImage = V.findViewById(R.id.imgCurrentUserImage);
            btnClose = V.findViewById(R.id.answersImgBtnClose);
            btnSubmitBestAnswerRating = V.findViewById(R.id.btnSubmitRating);
            ratingbarAnswerRating = V.findViewById(R.id.ratingbarAnswerRating);
            txtNumberOfUpvotes = V.findViewById(R.id.txtNumberOfUpvotes);
            txtAnswerComment = V.findViewById(R.id.txtAnswerComment);
            cardviewComment = V.findViewById(R.id.cardviewComment);
            circleImageViewDown = V.findViewById(R.id.circleImageViewDown);
            answerCommentsHolder = V.findViewById(R.id.recyclerViewAnswerComments);
            linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            answersDotsButton = V.findViewById(R.id.answerDotsButton);
            imageviewAnswererBadge = V.findViewById(R.id.imageviewAnswererBadge);

            answererAnswerRating = V.findViewById(R.id.answererAnswerRating);

            answerCommentsHolder.setEnabled(false);
            isShown = false;

            answerCommentsHolder.setLayoutManager(linearLayoutManager);

            firebaseAuth = FirebaseAuth.getInstance();
            userRef = FirebaseDatabase.getInstance().getReference().child("Users");
            questionRef = FirebaseFirestore.getInstance().collection("Questions");
            upvotesRef = FirebaseFirestore.getInstance().collection("Upvotes");
            currentUID = firebaseAuth.getCurrentUser().getUid();
            answersRef = FirebaseFirestore.getInstance().collection("Answers");
            commentsRef = FirebaseFirestore.getInstance().collection("Comments");
            commentUpvotesRef = FirebaseFirestore.getInstance().collection("Up");
            commentRepliesRef = FirebaseFirestore.getInstance().collection("Replies");
        }

        public void setRespondent_id(String respondent_id) {
            userRef.child(respondent_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        double level;
                        level = Double.parseDouble(dataSnapshot.child("star_ratings").getValue().toString());

                        if (level >= 0 && level <= 20) {
                            if (level >= 0 && level <= 3) {
                                imageviewAnswererBadge.setImageResource(R.drawable.rookie_1);
                            } else if (level >= 4 && level <= 7) {
                                imageviewAnswererBadge.setImageResource(R.drawable.rookie_2);
                            } else if (level >= 8 && level <= 11) {
                                imageviewAnswererBadge.setImageResource(R.drawable.rookie_3);
                            } else if (level >= 12 && level <= 15) {
                                imageviewAnswererBadge.setImageResource(R.drawable.rookie_4);
                            } else if (level >= 16 && level <= 20) {
                                imageviewAnswererBadge.setImageResource(R.drawable.rookie_5);
                            }
                        } else if (level >= 21 && level <= 40) {
                            if (level >= 21 && level <= 24) {
                                imageviewAnswererBadge.setImageResource(R.drawable.intermediate_1);
                            } else if (level >= 25 && level <= 28) {
                                imageviewAnswererBadge.setImageResource(R.drawable.intermediate_2);
                            } else if (level >= 29 && level <= 32) {
                                imageviewAnswererBadge.setImageResource(R.drawable.intermediate_3);
                            } else if (level >= 32 && level <= 36) {
                                imageviewAnswererBadge.setImageResource(R.drawable.intermediate_4);
                            } else if (level >= 36 && level <= 40) {
                                imageviewAnswererBadge.setImageResource(R.drawable.intermediate_5);
                            }
                        } else if (level >= 41 && level <= 60) {
                            if (level >= 41 && level <= 44) {
                                imageviewAnswererBadge.setImageResource(R.drawable.proficient_1);
                            } else if (level >= 45 && level <= 48) {
                                imageviewAnswererBadge.setImageResource(R.drawable.proficient_2);
                            } else if (level >= 49 && level <= 52) {
                                imageviewAnswererBadge.setImageResource(R.drawable.proficient_3);
                            } else if (level >= 52 && level <= 56) {
                                imageviewAnswererBadge.setImageResource(R.drawable.proficient_4);
                            } else if (level >= 56 && level <= 60) {
                                imageviewAnswererBadge.setImageResource(R.drawable.proficient_5);
                            }
                        } else if (level >= 61 && level <= 80) {
                            if (level >= 61 && level <= 64) {
                                imageviewAnswererBadge.setImageResource(R.drawable.senior_1);
                            } else if (level >= 65 && level <= 68) {
                                imageviewAnswererBadge.setImageResource(R.drawable.senior_2);
                            } else if (level >= 69 && level <= 72) {
                                imageviewAnswererBadge.setImageResource(R.drawable.senior_3);
                            } else if (level >= 72 && level <= 76) {
                                imageviewAnswererBadge.setImageResource(R.drawable.senior_4);
                            } else if (level >= 76 && level <= 80) {
                                imageviewAnswererBadge.setImageResource(R.drawable.senior_5);
                            }
                        } else if (level >= 81 && level <= 100) {
                            if (level >= 81 && level <= 84) {
                                imageviewAnswererBadge.setImageResource(R.drawable.expert_1);
                            } else if (level >= 85 && level <= 88) {
                                imageviewAnswererBadge.setImageResource(R.drawable.expert_2);
                            } else if (level >= 89 && level <= 92) {
                                imageviewAnswererBadge.setImageResource(R.drawable.expert_3);
                            } else if (level >= 92 && level <= 96) {
                                imageviewAnswererBadge.setImageResource(R.drawable.expert_4);
                            } else if (level >= 96 && level <= 100) {
                                imageviewAnswererBadge.setImageResource(R.drawable.expert_5);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void setRespondent(String respondent) {
            TextView textView = V.findViewById(R.id.txtAnswerAnswerer);
            textView.setText(respondent);
        }

        public void setDate(String date) {

        }

        public void setTime(String time) {

        }

        public void setRespondent_image(String respondent_image) {
            CircleImageView circleImageView = V.findViewById(R.id.circleImageAnswererImage);
            Picasso.get().load(respondent_image).into(circleImageView);
        }

        public void setRespondent_course(String respondent_course) {
            TextView textView = V.findViewById(R.id.txtAnswererCourse);
            textView.setText(respondent_course);
        }

        public void setAnswer(String answer) {
            TextView textView = V.findViewById(R.id.txtAnswerAnswer);
            textView.setText(Html.fromHtml(answer));
        }

        public void setBadge(String badge) {

        }

        public void setAnswer_image(String answer_image) {
            ImageView imageView = V.findViewById(R.id.txtAnswerAnswerImage);

            if (answer_image != null) {
                Picasso.get().load(answer_image).into(imageView);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }

        public void setNumber_of_upvotes(int number_of_upvotes) {
            if (number_of_upvotes > 0) {
                txtNumberOfUpvotes.setText(Integer.toString(number_of_upvotes));
                txtNumberOfUpvotes.setVisibility(View.VISIBLE);
            } else {
                txtNumberOfUpvotes.setVisibility(View.GONE);
            }
        }

        public void maintainStarRatings() {

        }

        public void maintainDotsButton(String uid, String resp_id, final Context context) {
            if (uid.equals(resp_id)) {
                answersDotsButton.setVisibility(View.VISIBLE);
                answersDotsButton.setEnabled(true);
            } else {
                answersDotsButton.setVisibility(View.GONE);
                answersDotsButton.setEnabled(false);
            }
        }

        public void setBest_answer_rating(float best_answer_rating) {

        }

        public void setIs_best_answer(int is_best_answer, float rating) {
            if (is_best_answer > 0) {
                //Best answer
                txtIsBestAnswer.setVisibility(View.VISIBLE);

                answererAnswerRating.setRating(rating);
                answererAnswerRating.setVisibility(View.VISIBLE);

            } else {
                txtIsBestAnswer.setVisibility(View.GONE);
                answererAnswerRating.setVisibility(View.GONE);
            }
        }

        public void saveCommentToFireStore(String question_id, String answer_id) {

        }

        public void deleteAnswer(final String a_id, final Context context) {
            answersRef.document(a_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        deleteAnswerComments(a_id, context);
                        deleteAnswerUpvotes(a_id);
                        deleteAnswerCommentUpvotes(a_id);
                        deleteAnswerCommentReplies(a_id);
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void deleteAnswerCommentReplies(final String a_id) {
            Query query = commentRepliesRef.whereEqualTo("answer_id", a_id);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e == null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            String id = d.getId();
                            commentRepliesRef.document(id).delete();
                        }
                    }
                }
            });
        }

        public void deleteAnswerCommentUpvotes(final String a_id) {
            Query query = commentUpvotesRef.whereEqualTo("answer_id", a_id);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e == null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            String id = d.getId();
                            commentUpvotesRef.document(id).delete();
                        }
                    }
                }
            });
        }

        public void deleteAnswerUpvotes(final String a_id) {
            final Query query = upvotesRef.whereEqualTo("answer_id", a_id);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e == null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            String id = d.getId();
                            upvotesRef.document(id).delete();
                        }
                    }
                }
            });
        }

        public void deleteAnswerComments(String a_id, final Context context) {
            Query query = commentsRef.whereEqualTo("answer_id", a_id);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            String id = d.getId();
                            commentsRef.document(id).delete();
                        }
                    }
                }
            });
        }

        /////////////////////////////////////////////////////////////////////
        public void displayAnswerComments(final String q_id, final String a_id, final Context context, final String uid) {
            Toast.makeText(context, "Displaying comments", Toast.LENGTH_SHORT).show();
            Query query = commentsRef.whereEqualTo("question_id", q_id).whereEqualTo("answer_id", a_id);
            //FirestoreRecyclerOptions<Question> options = new FirestoreRecyclerOptions.Builder<Question>().setQuery(query, Question.class).build();
            FirestoreRecyclerOptions<Comments> options = new FirestoreRecyclerOptions.Builder<Comments>().setQuery(query, Comments.class).build();

            commentsRecyclerAdapter = new FirestoreRecyclerAdapter<Comments, AnswerCommentsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(final @NonNull AnswerCommentsViewHolder holder, int position, @NonNull final Comments model) {
                    final String commentKey = getSnapshots().getSnapshot(position).getId();

                    holder.setComment(model.getComment());
                    holder.setCommenter_image(model.getCommenter_image());
                    holder.setCommenter_name(model.commenter_name);
                    holder.setComment_image(model.comment_image);

                    holder.maintainDotsButton(model.getComment_by());

                    holder.commentLayoutDots.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            CharSequence options[] = new CharSequence[]{
                                    "Delete Comment"
                            };

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            //Confirming delete
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);

                                            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which) {
                                                        case DialogInterface.BUTTON_NEGATIVE:
                                                            holder.deleteCommentUpvotes(commentKey);
                                                            holder.deleteComment(commentKey);
                                                            holder.deleteCommentReplies(commentKey);
                                                            break;
                                                        case DialogInterface.BUTTON_POSITIVE:
                                                            dialog.dismiss();
                                                            break;
                                                    }
                                                }
                                            };

                                            builder1.setTitle("Confirm delete")
                                                    .setNegativeButton("Delete", clickListener)
                                                    .setPositiveButton("Cancel", clickListener)
                                                    .show();

                                            break;
                                    }
                                }
                            });
                            builder.show();


                        }
                    });

                    holder.answerReplyButtonUpvote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.isClicked = true;
                            if (holder.isClicked) {
                                holder.addOrRemoveUpvote(q_id, a_id, commentKey, holder.currentUserID, context);
                                //Toast.makeText(context, uid, Toast.LENGTH_LONG).show();
                                holder.isClicked = false;
                            }
                            // holder.addOrRemoveUpvote(q_id, a_id, commentKey, currentUID, context);
                        }
                    });

                    holder.answerReplyButtonReply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CardView cardView = ClickQuestionActivity.commentReplyCardview;
                            cardView.setEnabled(true);
                            cardView.setVisibility(View.VISIBLE);
                            commentReplyTextComment.setText(model.getComment());
                            currentCommentID = commentKey;
                            currentComment = model.getComment();
                        }
                    });

                    holder.btnViewReplies.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Kapag kinlick, mapupunta sa AnswerCommentRepliesActivity, ito yung magshow-show sa mga replies ng comment na yun
                            // Needs: comment ID
                            //mContext is declared above
                            Intent intent = new Intent(mContext, AnswerCommentRepliesActivity.class);
                            intent.putExtra("comment_id", commentKey);
                            intent.putExtra("question_id", q_id);
                            intent.putExtra("answer_id", a_id);
                            mContext.startActivity(intent);
                        }
                    });

                    holder.maintainUpvoteStatus(commentKey, holder.currentUserID, context);
                    holder.maintainNumberOfUpvotes(commentKey);
                }

                @NonNull
                @Override
                public AnswerCommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_answers_comment_layout, parent, false);
                    return new AnswerCommentsViewHolder(view);
                }
            };
            commentsRecyclerAdapter.startListening();
            answerCommentsHolder.setAdapter(commentsRecyclerAdapter);
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public static class AnswerCommentsViewHolder extends RecyclerView.ViewHolder {
            private View mView;

            CollectionReference commentRepliesRef;
            ImageButton answerReplyButtonUpvote;
            Button answerReplyButtonReply;
            TextView answerReplyTextNumberOfUpvotes;
            CollectionReference commentRepliesUpvotesRef;
            CollectionReference answerCommentsRef;
            TextView nUpvotes;
            Button btnViewReplies;
            ImageButton commentLayoutDots;

            FirebaseAuth mAuth;
            String currentUserID;

            Boolean isClicked;

            public AnswerCommentsViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView;

                answerReplyButtonReply = mView.findViewById(R.id.btnReply);
                answerReplyButtonUpvote = mView.findViewById(R.id.answerCommentUpvoteButton);
                answerReplyTextNumberOfUpvotes = mView.findViewById(R.id.txtAnswerCommentNumberOfUpvotes);
                nUpvotes = mView.findViewById(R.id.txtAnswerCommentNumberOfUpvotes);
                btnViewReplies = mView.findViewById(R.id.btnViewReplies);
                commentLayoutDots = mView.findViewById(R.id.commentLayoutDots);

                mAuth = FirebaseAuth.getInstance();
                currentUserID = mAuth.getCurrentUser().getUid();

                commentRepliesUpvotesRef = FirebaseFirestore.getInstance().collection("Up");
                answerCommentsRef = FirebaseFirestore.getInstance().collection("Comments");
                commentRepliesRef = FirebaseFirestore.getInstance().collection("Replies");

                isClicked = false;
            }


            public void setComment_image(String comment_image) {
                ImageView imageView = mView.findViewById(R.id.imageViewCommentImage);
                Picasso.get().load(comment_image).into(imageView);
                imageView.setVisibility(View.VISIBLE);
            }

            public void setComment(String comment) {
                TextView textView = mView.findViewById(R.id.textViewCommenterComment);
                textView.setText(comment);
            }

            public void setCommenter_image(String commenter_image) {
                CircleImageView circleImageView = mView.findViewById(R.id.circleImageViewCommentImage);
                Picasso.get().load(commenter_image).into(circleImageView);
            }

            public void setCommenter_name(String commenter_name) {
                TextView textView = mView.findViewById(R.id.textViewCommenterName);
                textView.setText(commenter_name);
            }

            public void maintainDotsButton(final String commenter_id) {
                if (commenter_id.equals(currentUserID)) {
                    //Show dots
                    commentLayoutDots.setVisibility(View.VISIBLE);
                    commentLayoutDots.setEnabled(true);
                } else {
                    //Dont show dots
                    commentLayoutDots.setVisibility(View.GONE);
                    commentLayoutDots.setEnabled(false);
                }
            }

            public void deleteCommentUpvotes(final String c_id) {
                Query query = commentRepliesUpvotesRef.whereEqualTo("comment_id", c_id);

                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e == null && !queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot d : queryDocumentSnapshots) {
                                String id = d.getId();
                                commentRepliesUpvotesRef.document(id).delete();
                            }
                        }
                    }
                });
            }

            public void deleteComment(final String c_id) {
                answerCommentsRef.document(c_id).delete();
            }

            public void deleteCommentReplies(final String c_id) {
                Query query = commentRepliesRef.whereEqualTo("comment_id", c_id);

                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e == null && !queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot d : queryDocumentSnapshots) {
                                String id = d.getId();
                                commentRepliesRef.document(id).delete();
                            }
                        }
                    }
                });
            }

            public void updateNumberOfUpvotes(final String q_id, final String a_id, final String c_id, final String uid, final Context context) {
                //Query na nagfi-filter kung ilan yung upvote for a specific comment in an answer, in a question:
                //Toast.makeText(context, "Updating upvote...", Toast.LENGTH_SHORT).show();
                Query numberOfUpvotes = commentRepliesUpvotesRef
                        .whereEqualTo("question_id", q_id)
                        .whereEqualTo("answer_id", a_id)
                        .whereEqualTo("comment_id", c_id);

                numberOfUpvotes.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            int c = 0;
                            int a = 0;

                            for (DocumentSnapshot d : queryDocumentSnapshots) {
                                c++;
                            }

                            Map<String, Object> map = new ArrayMap<>();
                            map.put("number_of_upvotes", c);

                            answerCommentsRef.document(c_id).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Upvote added.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Map<String, Object> map = new ArrayMap<>();
                            map.put("number_of_upvotes", 0);

                            answerCommentsRef.document(c_id).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Upvote added.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }

            public void maintainUpvoteStatus(final String c_id, final String uid, final Context context) {
                Query query = commentRepliesUpvotesRef.whereEqualTo("comment_id", c_id).whereEqualTo("upvoted_by", currentUserID);

                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot q, @Nullable FirebaseFirestoreException e) {
                        if (q.isEmpty()) {
                            answerReplyButtonUpvote.setImageResource(R.drawable.upvote_icon_none);
                        } else {
                            for (DocumentSnapshot d : q) {
                                if (d.exists()) {
                                    Toast.makeText(context, d.get("comment_id").toString(), Toast.LENGTH_SHORT).show();
                                    answerReplyButtonUpvote.setImageResource(R.drawable.upvoted_icon);
                                } else {
                                    answerReplyButtonUpvote.setImageResource(R.drawable.upvote_icon_none);
                                }
                            }
                        }
                    }
                });
            }

            public void maintainNumberOfUpvotes(final String c_id) {
                answerCommentsRef.document(c_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot d, @Nullable FirebaseFirestoreException e) {
                        if (d.exists()) {
                            String n = d.get("number_of_upvotes").toString();
                            nUpvotes.setText(n);
                        }
                    }
                });
            }

            public void addOrRemoveUpvote(final String question_id, final String answer_id, final String comment_id, final String current_uid, final Context context) {
                //Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                Query ifHasUpvoteQuery = commentRepliesUpvotesRef
                        .whereEqualTo("question_id", question_id)
                        .whereEqualTo("answer_id", answer_id)
                        .whereEqualTo("comment_id", comment_id)
                        .whereEqualTo("upvoted_by", current_uid);

                ifHasUpvoteQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Map<String, Object> map = new ArrayMap<>();
                            map.put("question_id", question_id);
                            map.put("answer_id", answer_id);
                            map.put("comment_id", comment_id);
                            map.put("upvoted_by", current_uid);

                            commentRepliesUpvotesRef.document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Toast.makeText(context, "Upvote added!", Toast.LENGTH_SHORT).show();
                                        updateNumberOfUpvotes(question_id, answer_id, comment_id, currentUserID, context);
                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            for (DocumentSnapshot d : queryDocumentSnapshots) {
                                String id = d.getId();
                                commentRepliesUpvotesRef.document(id).delete();
                            }
                            updateNumberOfUpvotes(question_id, answer_id, comment_id, currentUserID, context);
                        }

                    }
                });
            }
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        public void validateAndSubmitRating(String currentID, final String q_id, final String a_id, final Context context, final String answerer_id) {
            final float rating = ratingbarAnswerRating.getRating();

            //Check kung may rating or wala.
            if (rating > 0) {
                //Uploading the rating to the database and to the Answers Collection:

                Map<String, Object> map = new ArrayMap<>();
                map.put("is_best_answer", 1);
                map.put("best_answer_rating", rating);

                answersRef.document(a_id).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> m = new ArrayMap<>();
                            m.put("has_best_answer", true);

                            questionRef.document(q_id).update(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Toast.makeText(context, "Best rating successfully updated and uploaded!", Toast.LENGTH_SHORT).show();
                                        uploadRatingForUser(q_id, a_id, rating, context, answerer_id);
                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {

            }
        }


        public void hideRatingCard() {
            cardviewAnswerRating.setVisibility(View.GONE);
            cardviewAnswerRating.setEnabled(false);
        }

        public void displayRatingCard() {
            cardviewAnswerRating.setVisibility(View.VISIBLE);
            cardviewAnswerRating.setEnabled(true);
        }

        public void hideBestAnswerButton() {
            btnBestAnswer.setVisibility(View.GONE);
            btnBestAnswer.setEnabled(false);
        }

        public void displayBestAnswerButton() {
            btnBestAnswer.setVisibility(View.VISIBLE);
            btnBestAnswer.setEnabled(true);
        }

        public void displayUpvote() {
            btnUpvote.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.upvoted_icon, 0, 0, 0);
            btnUpvote.setText("Upvoted");
            btnUpvote.setTextColor(Color.parseColor("#4F4CAF"));

            userRef.child(currentUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String img = dataSnapshot.child("profile_image").getValue().toString();

                        Picasso.get().load(img).into(imgCurrentUserImage);
                        txtYouUpvotedThis.setVisibility(View.VISIBLE);
                        imgCurrentUserImage.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void maintainRatingHighOfUser(final float rating, final String answerer_id) {
            userRef.child(answerer_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        float current_high =0;
                        current_high = Float.parseFloat(dataSnapshot.child("answer_rating_high").getValue().toString());

                            if(rating > current_high) {
                                //May bago nang high
                                Map<String, Object> highMap = new ArrayMap<>();
                                highMap.put("answer_rating_high", rating);
                                userRef.child(answerer_id).updateChildren(highMap);
                            }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void uploadRatingForUser(String q_id, String a_id, final float rating, final Context context, final String answerer_id) {
            userRef.child(answerer_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //Adding current + new rate
                        float oldRating = Float.parseFloat(dataSnapshot.child("star_ratings").getValue().toString());
                        final float newRating = rating + oldRating;
                        //Updating user rating

                        Map<String, Object> map = new ArrayMap<>();
                        map.put("star_ratings", newRating);

                        userRef.child(answerer_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Rating updated for the user", Toast.LENGTH_SHORT).show();
                                    maintainLevelOfUserWithBestAnswer(answerer_id, newRating, context);
                                    maintainRatingHighOfUser(rating, answerer_id);
                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void maintainLevelOfUserWithBestAnswer(final String answerer_id, final float rating, final Context context) {
            userRef.child(answerer_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        double rank = 0.0;
                        double exp;
                        double newRank = 0.0;
                        rank = Double.parseDouble(dataSnapshot.child("level").getValue().toString());
                        exp = Double.parseDouble(dataSnapshot.child("star_ratings").getValue().toString());


                        if (exp >= 0 && exp <= 20) {
                            //Rookie
                            if (exp >= 0 && exp <= 3) {
                                newRank = 0.1;
                            } else if (exp >= 4 && exp <= 7) {
                                newRank = 0.2;
                            } else if (exp >= 8 && exp <= 11) {
                                newRank = 0.3;
                            } else if (exp >= 12 && exp <= 15) {
                                newRank = 0.4;
                            } else if (exp >= 16 && exp <= 20) {
                                newRank = 0.5;
                            }
                        } else if (exp >= 21 && exp <= 40) {
                            //Intermediate
                            if (exp >= 21 && exp <= 24) {
                                newRank = 1.1;
                            } else if (exp >= 25 && exp <= 28) {
                                newRank = 1.2;
                            } else if (exp >= 29 && exp <= 32) {
                                newRank = 1.3;
                            } else if (exp >= 32 && exp <= 36) {
                                newRank = 1.4;
                            } else if (exp >= 36 && exp <= 40) {
                                newRank = 1.5;
                            }
                        } else if (exp >= 41 && exp <= 60) {
                            //Proficient
                            if (exp >= 41 && exp <= 44) {
                                newRank = 2.1;
                            } else if (exp >= 45 && exp <= 48) {
                                newRank = 2.2;
                            } else if (exp >= 49 && exp <= 52) {
                                newRank = 2.3;
                            } else if (exp >= 52 && exp <= 56) {
                                newRank = 2.4;
                            } else if (exp >= 56 && exp <= 60) {
                                newRank = 2.5;
                            }
                        } else if (exp >= 61 && exp <= 80) {
                            //Senior
                            if (exp >= 61 && exp <= 64) {
                                newRank = 3.1;
                            } else if (exp >= 65 && exp <= 68) {
                                newRank = 3.2;
                            } else if (exp >= 69 && exp <= 72) {
                                newRank = 3.3;
                            } else if (exp >= 72 && exp <= 76) {
                                newRank = 3.4;
                            } else if (exp >= 76 && exp <= 80) {
                                newRank = 3.5;
                            }
                        } else if (exp >= 81 && exp <= 100) {
                            //Expert
                            if (exp >= 81 && exp <= 84) {
                                newRank = 4.1;
                            } else if (exp >= 85 && exp <= 88) {
                                newRank = 4.2;
                            } else if (exp >= 89 && exp <= 92) {
                                newRank = 4.3;
                            } else if (exp >= 92 && exp <= 96) {
                                newRank = 4.4;
                            } else if (exp >= 96 && exp <= 100) {
                                newRank = 4.5;
                            }
                        } else if (rating > 100) {

                        }

                        Map<String, Object> map = new ArrayMap<>();
                        map.put("level", newRank);

                        userRef.child(answerer_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Ranking updated.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        if (rank != newRank) {
                            //May pagbabago sa ranking
                            Toast.makeText(context, "User with the best answer has moved up into the rankings", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "User retains his/her ranking.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void displayNoUpvote() {
            btnUpvote.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.upvote_icon_none, 0, 0, 0);
            btnUpvote.setText("Upvote");

            txtYouUpvotedThis.setVisibility(View.GONE);
            imgCurrentUserImage.setVisibility(View.GONE);
        }

        public void setUpvoteStatus(String q_id, final String uid, final String key) {
            Query hasUpvotes = upvotesRef.whereEqualTo("question_id", q_id).whereEqualTo("answer_id", key).whereEqualTo("upvoted_by", uid);
            hasUpvotes.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots.isEmpty()) {
                        displayNoUpvote();
                    } else {
                        displayUpvote();
                    }
                }
            });
        }

        public void maintainNumberOfUpvotes(String question_id, String answer_id) {
            answersRef.document(answer_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        String n = documentSnapshot.get("number_of_upvotes").toString();
                        TextView textView = itemView.findViewById(R.id.txtNumberOfUpvotes);
                        int num = Integer.parseInt(n);
                        if (num > 0) {
                            textView.setText(n);
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            textView.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }

        public void ifWillDisplayButton(final String id) { // Question ID to
            questionRef.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String currentID = documentSnapshot.getString("asker_id");

                        if (currentUID.equals(currentID)) {
                            //The asker is equal to the current user:
                            questionRef.document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if (documentSnapshot.exists()) {
                                        boolean hasBestAnswer = documentSnapshot.getBoolean("has_best_answer");
                                        if (hasBestAnswer) {
                                            hideBestAnswerButton();
                                        } else {
                                            displayBestAnswerButton();
                                        }
                                    }
                                }
                            });
                        } else {
                            hideBestAnswerButton();
                        }
                    } else {

                    }
                }
            });
        }

        public void retainOrRemoveUpvote(final String uid, final String q_id, final String key, final Context context) {
            Query ifAlreadyUpvoted = upvotesRef.whereEqualTo("question_id", q_id).whereEqualTo("upvoted_by", uid).whereEqualTo("answer_id", key);
            ifAlreadyUpvoted.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    //Check muna kung may upvote:
                    if (queryDocumentSnapshots.isEmpty()) {
                        //Wala pang upvote
                        Map<String, Object> upvoteMap = new ArrayMap<>();
                        upvoteMap.put("question_id", q_id);
                        upvoteMap.put("answer_id", key);
                        upvoteMap.put("upvoted_by", uid);

                        upvotesRef.document().set(upvoteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    updateNumberOfUpvotes(q_id, key, context);
                                } else {

                                }
                            }
                        });
                    } else {
                        //Meron na
                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                            String id = q.getId();
                            upvotesRef.document(id).delete();
                        }
                        updateNumberOfUpvotes(q_id, key, context);
                    }
                }
            });
        }

        public void updateNumberOfUpvotes(final String q_id, final String a_id, final Context context) {

            Query query = upvotesRef.whereEqualTo("question_id", q_id).whereEqualTo("answer_id", a_id);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int count = 0;
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        count++;
                    }

                    Map<String, Object> m = new ArrayMap<>();
                    m.put("number_of_upvotes", count);

                    answersRef.document(a_id).set(m, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Upvote updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }
}
