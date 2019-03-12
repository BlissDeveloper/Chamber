package com.example.avery.chamberofwizards.Questions.QuestionsFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avery.chamberofwizards.R;


public class UserBookMarksFragment extends Fragment {


    public UserBookMarksFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_book_marks, container, false);
    }

}
