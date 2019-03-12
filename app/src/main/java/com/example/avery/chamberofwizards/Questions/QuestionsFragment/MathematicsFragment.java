package com.example.avery.chamberofwizards.Questions.QuestionsFragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Questions.ClickQuestionActivity;
import com.example.avery.chamberofwizards.Questions.PostedQuestions;
import com.example.avery.chamberofwizards.Questions.QuestionsActivity;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MathematicsFragment extends Fragment {

    public static Context context;

    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference usersRef;
    private CollectionReference questionsReference;

    private RecyclerView mathRecyclerView;
    private View V;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar progressBar;

    private Boolean isTapped;

    public MathematicsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        V = inflater.inflate(R.layout.fragment_mathematics, container, false);

        context = getActivity();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        questionsReference = FirebaseFirestore.getInstance().collection("Questions");

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mathRecyclerView = V.findViewById(R.id.recyclerViewMath);
        mathRecyclerView.setLayoutManager(linearLayoutManager);
        progressBar = V.findViewById(R.id.progressBarFragment);

        isTapped = false;

        displayAllQuestions();

        return V;
    }

    public void displayAllQuestions() {
        Query mQuery = questionsReference;

        FirestoreRecyclerOptions<PostedQuestions> options = new FirestoreRecyclerOptions.Builder<PostedQuestions>()
                .setQuery(mQuery, PostedQuestions.class)
                .build();

        FirestoreRecyclerAdapter<PostedQuestions, QuestionsViewHolder> adapter = new FirestoreRecyclerAdapter<PostedQuestions, QuestionsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final QuestionsViewHolder holder, final int position, @NonNull final PostedQuestions model) {
                final String postKey = getSnapshots().getSnapshot(position).getId();
                final String currentQuestion = getSnapshots().get(position).getQuestion();

                holder.setAsker(model.getAsker());
                holder.setAsker_image(model.getAsker_image());
                holder.setCourse(model.getCourse());
                holder.setQuestion(model.getQuestion());
                holder.setCategory_code(model.getCategory_code());
                //holder.setNumber_of_answers(model.getNumber_of_answers());
                holder.ifWillDisplayAnswerButton(postKey, getActivity(), currentUserID);
                holder.ifBookmarkedOrNot(currentUserID, postKey);

                holder.maintainDotsButton(currentUserID, model.getAsker_id());

                holder.dotsHorizontalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "Delete Question",
                                "Edit Question"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int choice) {
                                switch (choice) {
                                    case 0:
                                        //Delete
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        progressBar.setVisibility(View.VISIBLE);
                                                        holder.deleteQuestion(postKey);
                                                        progressBar.setVisibility(View.GONE);
                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        dialog.dismiss();
                                                        break;
                                                }
                                            }
                                        };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setMessage("Confirm")
                                                .setPositiveButton("Delete", dialogClickListener)
                                                .setNegativeButton("Cancel", dialogClickListener)
                                                .show();
                                        break;
                                    case 1:
                                        Toast.makeText(getActivity(), "Edit Question", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                        builder.show();
                    }
                });

                if (model.getQuestions_image() != null) {
                    holder.setQuestions_image(model.getQuestions_image());
                }

                holder.btnAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CardView cardView = getActivity().findViewById(R.id.cardToAnswer);
                        final TextView textView = getActivity().findViewById(R.id.txtQuestionToBeAnswered); //Eto yung naka-display
                        cardView.setVisibility(View.VISIBLE);
                        textView.setText(currentQuestion);
                        QuestionsActivity.currentQuestionID = postKey;
                    }
                });

                holder.txtNumberOfAnswers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ClickQuestionActivity.class);
                        intent.putExtra("question_key", postKey);
                        startActivity(intent);
                    }
                });


                holder.btnBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isTapped = true;
                        if (isTapped) {
                            holder.bookmarkQuestion(currentUserID, postKey, model.getQuestion());
                        }
                    }
                });

                holder.maintainNumberOfAnswers(postKey);

            }

            @NonNull
            @Override
            public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_post_layout, parent, false);
                return new QuestionsViewHolder(view);
            }
        };
        adapter.startListening();
        mathRecyclerView.setAdapter(adapter);
    }

    public static class QuestionsViewHolder extends RecyclerView.ViewHolder {

        private CollectionReference bookmarksRef;
        private CollectionReference qRef;
        private DatabaseReference usersRef;
        private CollectionReference answersRef;
        private CollectionReference upvotesRef;
        private CollectionReference answerCommentsRef;
        private CollectionReference repliesRef;
        private CollectionReference commentUpvotesRef;

        View mView;
        private Button btnAnswer;
        private CardView askCard;
        private CardView answerCardEditText;
        private TextView txtNumberOfAnswers;
        private Button btnBookmark;
        private ImageButton dotsHorizontalButton;
        private ProgressBar prog;

        public QuestionsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            qRef = FirebaseFirestore.getInstance().collection("Questions");
            answersRef = FirebaseFirestore.getInstance().collection("Answers");
            upvotesRef = FirebaseFirestore.getInstance().collection("Upvotes");
            answerCommentsRef = FirebaseFirestore.getInstance().collection("Comments");
            repliesRef = FirebaseFirestore.getInstance().collection("Replies");
            commentUpvotesRef = FirebaseFirestore.getInstance().collection("Up");

            btnAnswer = mView.findViewById(R.id.questionLayoutBtnAnswer);
            askCard = mView.findViewById(R.id.cardToAnswer);
            answerCardEditText = mView.findViewById(R.id.cardViewEditHolder);
            txtNumberOfAnswers = mView.findViewById(R.id.txtNumberOfAnswers);
            btnBookmark = mView.findViewById(R.id.btnBookmarkQuestion);
            dotsHorizontalButton = mView.findViewById(R.id.question_dots_horizontal);
        }


        public void setAsker(String asker) {
            TextView textView = mView.findViewById(R.id.txtAskerName);
            textView.setText(asker);
        }

        public void setCourse(String course) {
            TextView textView = mView.findViewById(R.id.txtAskerCourse);
            textView.setText(course);
        }

        public void setDate(String date) {

        }

        public void setQuestion(String question) {
            TextView textView = mView.findViewById(R.id.txtPostedQuestion);
            textView.setText(question);
        }

        public void maintainNumberOfAnswers(String question_id) {
            if (question_id != null) {
                qRef.document(question_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            String n = documentSnapshot.get("number_of_answers").toString();
                            int num = Integer.parseInt(n);
                            TextView textView = mView.findViewById(R.id.txtNumberOfAnswers);
                            if (num == 0) {
                                textView.setText("Be the first to answer!");
                            } else {
                                if (num == 1) {
                                    textView.setText(num + " Answer");
                                } else {
                                    textView.setText(num + " Answers");
                                }
                            }
                        }
                    }
                });
            }
        }


        public void setQuestions_image(String questions_image) {
            ImageView circleImageView = mView.findViewById(R.id.imgQuestionImage);

            if (questions_image != null) {
                //May image yung question
                Picasso.get().load(questions_image).into(circleImageView);
                circleImageView.setVisibility(View.VISIBLE);
            } else {
                //Walang image
                circleImageView.setVisibility(View.GONE);
            }
        }

        public void setNumber_of_answers(int number_of_answers) {
            TextView textView = mView.findViewById(R.id.txtNumberOfAnswers);

            if (number_of_answers == 0) {
                textView.setText("No answers yet. Be the first to answer!");
            } else if (number_of_answers == 1) {
                textView.setText(number_of_answers + " Answer");
            } else {
                textView.setText(number_of_answers + " Answers");
            }
        }

        public void setTime(String time) {

        }

        public void setCategory_code(int category_code) {
            TextView textView = mView.findViewById(R.id.txtCategory);
             /*
                0. Category
                1. Math
                2. Business
                3. Computer Science
                4. Biology
                5. Envi Scie
                 */
            switch (category_code) {
                case 1:
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.math_icon_triadic, 0, 0, 0);
                    textView.setText("Mathematics");
                    break;
                case 2:
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.business_icon_triadic, 0, 0, 0);
                    textView.setText("Business");
                    break;
                case 3:
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.cs_icon_post, 0, 0, 0);
                    textView.setText("Computer Science");
                    break;
                case 4:
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.bio_icon_triadic, 0, 0, 0);
                    textView.setText("Biology");
                    break;
                case 5:
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.envi_scie_triadic, 0, 0, 0);
                    textView.setText("Environmental Science");
                    break;
            }
        }

        public void setAsker_image(String asker_image) {
            CircleImageView circleImageView = mView.findViewById(R.id.imgAskerImage);
            Picasso.get().load(asker_image).into(circleImageView);
        }

        public void maintainDotsButton(String user_id, String asker_id) {
            if (asker_id.equals(user_id)) {
                dotsHorizontalButton.setVisibility(View.VISIBLE);
                dotsHorizontalButton.setEnabled(true);
            } else {
                dotsHorizontalButton.setVisibility(View.GONE);
                dotsHorizontalButton.setEnabled(false);
            }
        }

        public void deleteQuestion(final String q_id) {
            qRef.document(q_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        deleteAnswersFromQuestion(q_id);
                        deleteUpvotesFromQuestion(q_id);
                        deleteAnswerCommentsFromQuestion(q_id);
                        deleteRepliesFromQuestion(q_id);
                        deleteCommentUpvotesFromQuestion(q_id);
                    } else {

                    }
                }
            });
        }

        public void deleteCommentUpvotesFromQuestion(final String q_id) {
            Query query = commentUpvotesRef.whereEqualTo("question_id", q_id);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(e == null && !queryDocumentSnapshots.isEmpty()) {
                        for(DocumentSnapshot d : queryDocumentSnapshots) {
                            String id = d.getId();
                            commentUpvotesRef.document(id).delete();
                        }
                    }
                }
            });
        }

        public void deleteRepliesFromQuestion(final String q_id) {
            Query query = repliesRef.whereEqualTo("question_id", q_id);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(!queryDocumentSnapshots.isEmpty()) {
                        for(DocumentSnapshot d: queryDocumentSnapshots) {
                            String id = d.getId();
                            repliesRef.document(id).delete();
                        }
                    }
                }
            });
        }

        public void deleteAnswersFromQuestion(String q_id) {
            Query query = answersRef.whereEqualTo("question_id", q_id);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(!queryDocumentSnapshots.isEmpty()) {
                        for(DocumentSnapshot d : queryDocumentSnapshots) {
                            String id = d.getId();
                            answersRef.document(id).delete();
                        }
                    }
                }
            });
        }

        public void deleteUpvotesFromQuestion(String q_id) {
            Query query = upvotesRef.whereEqualTo("question_id", q_id);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(!queryDocumentSnapshots.isEmpty()) {
                        for(DocumentSnapshot d : queryDocumentSnapshots) {
                            String id = d.getId();
                            upvotesRef.document(id).delete();
                        }
                    }
                }
            });
        }

        public void deleteAnswerCommentsFromQuestion(String q_id) {
            Query query = answerCommentsRef.whereEqualTo("question_id", q_id);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(!queryDocumentSnapshots.isEmpty()) {
                       for(DocumentSnapshot d : queryDocumentSnapshots) {
                           String id = d.getId();
                           answerCommentsRef.document(id).delete();
                       }
                    }
                }
            });
        }

        public void ifBookmarkedOrNot(String c, String q_id) {
            bookmarksRef = FirebaseFirestore.getInstance().collection("Bookmarks");

            Query checkBookmark = bookmarksRef.whereEqualTo("question_id", q_id).whereEqualTo("bookmarked_by", c);

            checkBookmark.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots.isEmpty()) {
                        btnBookmark.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.not_yet_bookmarked_icon, 0, 0, 0);
                        btnBookmark.setText("Bookmark");
                    } else {
                        btnBookmark.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.bookmar_icon, 0, 0, 0);
                        btnBookmark.setText("Bookmarked");
                    }
                }
            });
        }

        public void ifWillDisplayAnswerButton(String q_id, final Context context, final String uid) {
            qRef.document(q_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String asker = documentSnapshot.getString("asker_id");
                        if (uid.equals(asker)) {
                            disableAnswerButton();
                        } else {
                            enableAnswerButton();
                        }
                    }
                }
            });
        }

        public void disableAnswerButton() {
            btnAnswer.setVisibility(View.GONE);
            btnAnswer.setEnabled(false);
        }

        public void enableAnswerButton() {
            btnAnswer.setVisibility(View.VISIBLE);
            btnAnswer.setEnabled(true);
        }

        public void bookmarkQuestion(final String currentUID, final String q_id, final String q) {
            //Questions.document(key).collection(Bookmarks).document(userID).set(ArrayMap);
            bookmarksRef = FirebaseFirestore.getInstance().collection("Bookmarks");

            /*
             Map<String, Object> bookmarkMap = new ArrayMap<>();
            bookmarkMap.put("question_id", q_id);
            bookmarkMap.put("question", q);
             */

            Query isAlreadyBookmarked = bookmarksRef.whereEqualTo("question_id", q_id).whereEqualTo("bookmarked_by", currentUID);

            isAlreadyBookmarked.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()) {
                        addBookmarkToFirestore(q_id, currentUID, q);
                    } else {
                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                            q.getReference().delete();
                        }
                    }
                }
            });
        }

        public void addBookmarkToFirestore(final String q_id, final String c, String q) {
           /*
            Map<String, Object> bookmarkMap = new ArrayMap<>();
            bookmarkMap.put("question_id", q_id);
            bookmarkMap.put("bookmarked_by", c);
            bookmarkMap.put("question", q);

            bookmarksRef.document().set(bookmarkMap);
            */
           qRef.document(q_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   if(task.isSuccessful()) {
                       DocumentSnapshot d = task.getResult();
                        if(d != null) {
                           DocumentReference path = bookmarksRef.document();
                           final String id = path.getId();
                            bookmarksRef.document(id).set(d.getData()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Map<String, Object> map = new ArrayMap<>();
                                        map.put("bookmarked_by", c);
                                        map.put("question_id", q_id);
                                            bookmarksRef.document(id).update(map);
                                    }
                                    else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                   }
               }
           });
        }
    }
}
