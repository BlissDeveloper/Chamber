package com.example.avery.chamberofwizards.Books.BookFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avery.chamberofwizards.Books.Books;
import com.example.avery.chamberofwizards.Books.ClickBookActivity;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostDiscussed extends Fragment {

    //Firebase variables
    private FirebaseAuth mAuth;
    private DatabaseReference booksRef;
    private String currentUserID;

    private View mView;

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    int n;

    public MostDiscussed() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_most_discussed, container, false);
        setRetainInstance(true);

        //Firebase declarations:
        mAuth = FirebaseAuth.getInstance();
        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");
        currentUserID = mAuth.getCurrentUser().getUid();

        gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        recyclerView = mView.findViewById(R.id.most_discussed_container);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout = mView.findViewById(R.id.swipeRefresh);

        recyclerView.setLayoutManager(gridLayoutManager);

        loadBookQuery();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBookQuery();
                swipeRefreshLayout.setRefreshing(false);
            }

        });

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        loadBookQuery();
    }

    public void loadBookQuery() {

        /*
         final String bookKey = getRef(position).getKey();

                        viewHolder.setBook_cover(model.getBook_cover());
                        viewHolder.setBook_title(model.getBook_title());

                        //When a user clicks on a book:
                        viewHolder.V.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                sendToClickBookActivity(bookKey);
                            }
                        });


                          R.layout.all_books_layout,
         */

        Query query = booksRef.orderByChild("average_rating");

        FirebaseRecyclerOptions<Books> options = new FirebaseRecyclerOptions.Builder<Books>().setQuery(query, Books.class).build();

        FirebaseRecyclerAdapter<Books, ReadFragment.BooksViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Books, ReadFragment.BooksViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReadFragment.BooksViewHolder viewHolder, int position, @NonNull Books model) {
                final String bookKey = getRef(position).getKey();

                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setBook_title(model.getBook_title());

                //When a user clicks on a book:
                viewHolder.V.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendToClickBookActivity(bookKey);
                    }
                });
            }

            @NonNull
            @Override
            public ReadFragment.BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_books_layout, parent, false);
                return new ReadFragment.BooksViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void sendToClickBookActivity(String bookKey) {
        Intent intent = new Intent(getActivity(), ClickBookActivity.class);
        intent.putExtra("bookKey", bookKey);
        startActivity(intent);
    }
}
