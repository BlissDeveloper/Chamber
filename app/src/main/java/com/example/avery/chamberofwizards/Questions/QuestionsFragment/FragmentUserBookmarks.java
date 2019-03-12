package com.example.avery.chamberofwizards.Questions.QuestionsFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Questions.ClickQuestionActivity;
import com.example.avery.chamberofwizards.Questions.PostedQuestions;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class FragmentUserBookmarks extends Fragment {

    private FirebaseAuth mAuth;
    private String currentUserID;
    private CollectionReference questionsRef;
    private FirestoreRecyclerAdapter<PostedQuestions, FragmentUserQuestions.FragmentYouQuestionsViewHolder> firestoreRecyclerAdapter;
    private CollectionReference bookmarksRef;

    private View mView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    public FragmentUserBookmarks() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_fragment_user_bookmarks, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        questionsRef = FirebaseFirestore.getInstance().collection("Questions");
        bookmarksRef = FirebaseFirestore.getInstance().collection("Bookmarks");

        recyclerView = mView.findViewById(R.id.userBookmarksRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        displayBookmarkedQuestions();
    }

    @Override
    public void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

    public void displayBookmarkedQuestions() {
        Query query = bookmarksRef.whereEqualTo("bookmarked_by", currentUserID);

        FirestoreRecyclerOptions<PostedQuestions> options = new FirestoreRecyclerOptions.Builder<PostedQuestions>().setQuery(query, PostedQuestions.class).build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<PostedQuestions, FragmentUserQuestions.FragmentYouQuestionsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FragmentUserQuestions.FragmentYouQuestionsViewHolder holder, int position, @NonNull PostedQuestions model) {
                final String questionKey = getSnapshots().getSnapshot(position).getId();

                holder.setAsker(model.getAsker());
                holder.setAsker_image(model.getAsker_image());
                holder.setCategory_code(model.getCategory_code());
                holder.setQuestions_image(model.getQuestions_image());
                holder.setQuestion(model.getQuestion());
                holder.setAsker_image(model.getAsker_image());
                holder.setCourse(model.getCourse());
                holder.setNumber_of_answers(model.getNumber_of_answers());

                holder.askerQuestionQuestionStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bookmarksRef.document(questionKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String bookmarkKey = documentSnapshot.getString("question_id");
                                    if (bookmarkKey != null) {
                                            Intent intent = new Intent(getActivity(), ClickQuestionActivity.class);
                                            intent.putExtra("question_key", bookmarkKey);
                                            startActivity(intent);
                                    } else {
                                        Toast.makeText(getActivity(), "Bookmark post key cannot be null", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public FragmentUserQuestions.FragmentYouQuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_you_all_question_layout, parent, false);
                return new FragmentUserQuestions.FragmentYouQuestionsViewHolder(view);
            }
        };
        firestoreRecyclerAdapter.startListening();
        recyclerView.setAdapter(firestoreRecyclerAdapter);
    }

}
