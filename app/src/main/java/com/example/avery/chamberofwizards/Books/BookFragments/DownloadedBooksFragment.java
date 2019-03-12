package com.example.avery.chamberofwizards.Books.BookFragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avery.chamberofwizards.Books.ClickBookActivity;
import com.example.avery.chamberofwizards.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadedBooksFragment extends Fragment {

    private FirebaseAuth mAuth;
    private String currentUserID;

    SQLiteDatabase mDatabase;

    private View mView;


    public DownloadedBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_downloaded_books, container, false);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

       mDatabase = SQLiteDatabase.openOrCreateDatabase(ClickBookActivity.DATABASE_NAME,null);

       loadBooks(currentUserID);

       return mView;
    }

    public void loadBooks(String user_id) {

        String sql = "SELECT * FROM downloaded_books WHERE USER = ?";

        Cursor cursor = mDatabase.rawQuery(sql, new String[]{user_id}); //For selecting

        if(cursor.moveToFirst()) {

        }
    }
}
