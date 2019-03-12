package com.example.avery.chamberofwizards.Questions.QuestionsFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.avery.chamberofwizards.Questions.Question;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentUserAnswers extends Fragment {

    private FirebaseAuth mAuth;
    private String currentUserID;
    private CollectionReference answersRef;
    private FirestoreRecyclerAdapter<Question, UserAnswersViewHolder> firestoreRecyclerAdapter;

    private View mView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    public FragmentUserAnswers() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_fragment_user_answers, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        answersRef = FirebaseFirestore.getInstance().collection("Answers");

        recyclerView = mView.findViewById(R.id.userAnswersRecyclerVIew);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        displayUserAnswers();
    };

    @Override
    public void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

    public void displayUserAnswers() {
        Query query = answersRef.whereEqualTo("respondent_id", currentUserID);

        FirestoreRecyclerOptions<Question> options = new FirestoreRecyclerOptions.Builder<Question>().setQuery(query, Question.class).build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Question, UserAnswersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserAnswersViewHolder holder, int position, @NonNull Question model) {
                final String answerKey = getSnapshots().getSnapshot(position).getId();

                /*

        private TextView answererIsBestAnswer;
        private RatingBar answererAnswerRating;
        private CircleImageView answererImage;
        private TextView answererName;
        private TextView answererCourse;
        private TextView answererAnswer;
        private ImageView answererAnswerImage;
                 */
                holder.setIs_best_answer(model.isIs_best_answer());
                holder.setBest_answer_rating(model.getBest_answer_rating());
                holder.setRespondent_image(model.getRespondent_image());
                holder.setRespondent(model.getRespondent());
                holder.setRespondent_course(model.getRespondent_course());
                holder.setAnswer(model.getAnswer());
                holder.setAnswer_image(model.getAnswer_image());

            }

            @NonNull
            @Override
            public UserAnswersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_you_all_answers_layout, parent, false);
                return new UserAnswersViewHolder(view);
            }
        };
        firestoreRecyclerAdapter.startListening();
        recyclerView.setAdapter(firestoreRecyclerAdapter);
    }

    public static class UserAnswersViewHolder extends RecyclerView.ViewHolder {

        private View V;

        private TextView answererIsBestAnswer;
        private RatingBar answererAnswerRating;
        private CircleImageView answererImage;
        private TextView answererName;
        private TextView answererCourse;
        private TextView answererAnswer;
        private ImageView answererAnswerImage;

        public UserAnswersViewHolder(@NonNull View itemView) {
            super(itemView);
            V = itemView;

            answererIsBestAnswer = V.findViewById(R.id.answerLayoutIsBestAnswer);
            answererAnswerRating = V.findViewById(R.id.answerLayoutAnswererAnswerRating);
            answererImage = V.findViewById(R.id.answerLayoutAnswererImage);
            answererName = V.findViewById(R.id.answerLayoutAnswererName);
            answererCourse = V.findViewById(R.id.answerLayoutAnswererCourse);
            answererAnswer = V.findViewById(R.id.answerLayoutAnswererAnswer);
            answererAnswerImage = V.findViewById(R.id.answerLayoutAnswererAnswerImage);
        }

        public void setIs_best_answer(int is_best_answer) {
            if (is_best_answer == 1) {
                answererIsBestAnswer.setVisibility(View.VISIBLE);

            } else {
                answererIsBestAnswer.setVisibility(View.GONE);
            }
        }

        public void setBest_answer_rating(float best_answer_rating) {
            if (best_answer_rating <= 0) {
                //Wala pang rating
                answererAnswerRating.setVisibility(View.GONE);
            } else {
                answererAnswerRating.setRating(best_answer_rating);
                answererAnswerRating.setVisibility(View.GONE);
            }
        }

        public void setRespondent_image(String respondent_image) {
            Picasso.get().load(respondent_image).into(answererImage);
        }

        public void setRespondent(String respondent) {
            answererName.setText(respondent);
        }

        public void setRespondent_course(String respondent_course) {
            answererCourse.setText(respondent_course);
        }

        public void setAnswer(String answer) {
            answererAnswer.setText(answer);
        }

        public void setAnswer_image(String answer_image) {
            if(answer_image == null) {
                answererAnswerImage.setVisibility(View.GONE);
            }
            else {
                Picasso.get().load(answer_image).into(answererAnswerImage);
                answererAnswerImage.setVisibility(View.VISIBLE);
            }
        }

    }
}
