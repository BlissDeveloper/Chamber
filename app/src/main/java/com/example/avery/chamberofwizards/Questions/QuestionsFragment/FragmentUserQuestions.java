package com.example.avery.chamberofwizards.Questions.QuestionsFragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Questions.ClickQuestionActivity;
import com.example.avery.chamberofwizards.Questions.PostedQuestions;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentUserQuestions extends Fragment {

    private static Context context;
    private static ListenerRegistration bookmarkSnapshot;

    private FirestoreRecyclerAdapter<PostedQuestions, FragmentYouQuestionsViewHolder> firestoreRecyclerAdapter;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private CollectionReference questionsRef;
    private CollectionReference bookmarksRef;

    private View mView;
    private RecyclerView recyclerViewUserQuestions;
    private LinearLayoutManager linearLayoutManager;

    public FragmentUserQuestions() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        context = getActivity();

        mView = inflater.inflate(R.layout.fragment_fragment_user_questions, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        questionsRef = FirebaseFirestore.getInstance().collection("Questions");
        bookmarksRef = FirebaseFirestore.getInstance().collection("Bookmarks");

        recyclerViewUserQuestions = mView.findViewById(R.id.recyclerViewUserQuestions);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewUserQuestions.setLayoutManager(linearLayoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadUserQuestions();
    }

    @Override
    public void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

    public void loadUserQuestions() {
        Query query = questionsRef.whereEqualTo("asker_id", currentUserID);

        FirestoreRecyclerOptions<PostedQuestions> options = new FirestoreRecyclerOptions.Builder<PostedQuestions>().setQuery(query, PostedQuestions.class).build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<PostedQuestions, FragmentYouQuestionsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final @NonNull FragmentYouQuestionsViewHolder holder, int position, @NonNull PostedQuestions model) {
                final String questionKey = getSnapshots().getSnapshot(position).getId();

                holder.setAsker(model.getAsker());
                holder.setAsker_image(model.getAsker_image());
                holder.setCategory_code(model.getCategory_code());
                holder.setQuestions_image(model.getQuestions_image());
                holder.setQuestion(model.getQuestion());
                holder.setAsker_image(model.getAsker_image());
                holder.setCourse(model.getCourse());
                holder.setNumber_of_answers(model.getNumber_of_answers());

                holder.mantainBookmarkStatus(questionKey, currentUserID);

                holder.btnBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.btnBookmark.setEnabled(false);
                        holder.bookmarkQuestion(questionKey, currentUserID);
                    }
                });

                holder.askerQuestionQuestionStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ClickQuestionActivity.class);
                        intent.putExtra("question_key", questionKey);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FragmentYouQuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_you_all_question_layout, parent, false);
                return new FragmentYouQuestionsViewHolder(view);
            }
        };
        firestoreRecyclerAdapter.startListening();
        recyclerViewUserQuestions.setAdapter(firestoreRecyclerAdapter);
    }

    public static class FragmentYouQuestionsViewHolder extends RecyclerView.ViewHolder {

        View V;

        CollectionReference bookmarksRef;
        CollectionReference questionsRef;

        CircleImageView askerImage;
        TextView askerQuestionCategory;
        TextView askerName;
        TextView askerCourse;
        TextView askerQuestion;
        ImageButton questionDots;
        ImageView askerQuestionImage;
        TextView askerQuestionQuestionStatus;
        Button btnBookmark;

        public static String questionId;

        public FragmentYouQuestionsViewHolder(@NonNull View itemView) {
            super(itemView);
            V = itemView;

            bookmarksRef = FirebaseFirestore.getInstance().collection("Bookmarks");
            questionsRef = FirebaseFirestore.getInstance().collection("Questions");

            askerImage = V.findViewById(R.id.questionLayoutAskerImage);
            askerQuestionCategory = V.findViewById(R.id.questionLayoutQuestionCategory);
            askerName = V.findViewById(R.id.questionsLayoutAskerName);
            askerCourse = V.findViewById(R.id.questionLayoutAskerCourse);
            askerQuestion = V.findViewById(R.id.questionLayoutQuestionQuestion);
            questionDots = V.findViewById(R.id.questionLayoutDots);
            askerQuestionImage = V.findViewById(R.id.questionLayoutQuestionImage);
            askerQuestionQuestionStatus = V.findViewById(R.id.txtAnswerStatus);
            btnBookmark = V.findViewById(R.id.questionLayoutBtnBookmark);
        }

        public String getQuestionId(String bookmarkPostKey, String currentUserID) {
            bookmarksRef.document(bookmarkPostKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        questionId = documentSnapshot.getString("question_id");
                    }
                }
            });
            if (questionId != null) {
                return questionId;
            } else {
                return null;
            }
        }

        public void mantainBookmarkStatus(final String questionKey, final String currentUserID) {
            Query query = bookmarksRef.whereEqualTo("question_id", questionKey).whereEqualTo("bookmarked_by", currentUserID);

            bookmarkSnapshot = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        //May bookmark
                        btnBookmark.setText("Bookmarked");
                        btnBookmark.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bookmar_icon, 0, 0, 0);
                    } else {
                        //Walang bookmark
                        btnBookmark.setText("Bookmark");
                        btnBookmark.setCompoundDrawablesWithIntrinsicBounds(R.drawable.not_yet_bookmarked_icon, 0, 0, 0);
                    }
                }
            });
        }

        public void removeBookmark(final String questionKey, final String currentUserID) {
            bookmarksRef.document(questionKey).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void addBookmark(final String questionKey, final String currentUserID) {
            questionsRef.document(questionKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Map toCopyFromQuestionsRef = documentSnapshot.getData();
                        if (!toCopyFromQuestionsRef.isEmpty()) {
                            //Eto na yung pag-save sa Firestore ng bookmark, data coming from the questions ref:
                            toCopyFromQuestionsRef.put("bookmarked_by", currentUserID);
                            toCopyFromQuestionsRef.put("question_id", questionKey);

                            bookmarksRef.document().set(toCopyFromQuestionsRef).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Bookmark added!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void bookmarkQuestion(final String questionKey, final String currentUserID) {
            bookmarksRef.document(questionKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (!documentSnapshot.exists()) {
                        //Wala pang bookmark
                        addBookmark(questionKey, currentUserID);
                    } else {
                        // May book mark na
                        removeBookmark(questionKey, currentUserID);
                    }
                    btnBookmark.setEnabled(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setNumber_of_answers(int number_of_answers) {
            if (number_of_answers > 0) {
                askerQuestionQuestionStatus.setText(String.valueOf(number_of_answers) + " Answers");
            } else {
                askerQuestionQuestionStatus.setText("Be the first to answer!");
            }
        }

        public void setAsker(String asker) {
            askerName.setText(asker);
        }

        public void setCourse(String course) {
            askerCourse.setText(course);
        }

        public void setQuestion(String question) {
            askerQuestion.setText(question);
        }

        public void setQuestions_image(String questions_image) {
            if (questions_image != null) {
                Picasso.get().load(questions_image).into(askerQuestionImage);
                askerQuestionImage.setVisibility(View.VISIBLE);
            } else {
                askerQuestionImage.setVisibility(View.GONE);
            }
        }

        public void setAsker_image(String asker_image) {
            Picasso.get().load(asker_image).into(askerImage);
            askerImage.setVisibility(View.VISIBLE);
        }

        public void setCategory_code(int category_code) {
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
                    askerQuestionCategory.setText("Mathematics");
                    askerQuestionCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.math_icon_triadic, 0, 0, 0);
                    break;
                case 2:
                    askerQuestionCategory.setText("Business");
                    askerQuestionCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.business_icon_triadic, 0, 0, 0);
                    break;
                case 3:
                    askerQuestionCategory.setText("Computer Science");
                    askerQuestionCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cs_icon_post, 0, 0, 0);
                    break;
                case 4:
                    askerQuestionCategory.setText("Biology");
                    askerQuestionCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bio_icon_triadic, 0, 0, 0);
                    break;
                case 5:
                    askerQuestionCategory.setText("Environmental Science");
                    askerQuestionCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.envi_scie_triadic, 0, 0, 0);
                    break;
            }
        }

    }
}
