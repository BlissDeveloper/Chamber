package com.example.avery.chamberofwizards.Books.BookFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avery.chamberofwizards.Books.Books;
import com.example.avery.chamberofwizards.Books.ReadBookActivity;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadsFragment extends Fragment {

    //Firebase Variables
    private DatabaseReference downloadedBooksRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    //XML File Views
    private RecyclerView downloadedBooksContainer;
    private GridLayoutManager gridLayoutManager;

    private View mView;

    public DownloadsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_downloads, container, false);

        //Firebase Declarations
        downloadedBooksRef = FirebaseDatabase.getInstance().getReference().child("Downloaded Books");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        //XML File Declarations
        downloadedBooksContainer = mView.findViewById(R.id.downloaded_books_container);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        downloadedBooksContainer.setLayoutManager(gridLayoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBooks();
    }

    public void loadBooks() {

        Query query =  downloadedBooksRef.child(currentUserID);

        FirebaseRecyclerOptions<Books> options = new FirebaseRecyclerOptions.Builder<Books>().setQuery(query, Books.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Books,FavoritesFragment.FavoritesViewHolder>(options) {

            @NonNull
            @Override
            public FavoritesFragment.FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_books_layout,parent, false);
                return new FavoritesFragment.FavoritesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FavoritesFragment.FavoritesViewHolder viewHolder, int position, @NonNull Books model) {
                final String bookKey = getRef(position).getKey();

                viewHolder.setBook_title(model.getBook_title());
                viewHolder.setBook_cover(model.getBook_cover());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        readTheBook(bookKey);
                    }
                });
            }
        };
        firebaseRecyclerAdapter.startListening();
        downloadedBooksContainer.setAdapter(firebaseRecyclerAdapter);
    }

    public void readTheBook(String book_key) {
        Intent intent = new Intent(getActivity(), ReadBookActivity.class);
        intent.putExtra("bookKey", book_key);
        intent.putExtra("readCode", 0);
        startActivity(intent);
    }

}
