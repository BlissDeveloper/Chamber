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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class LatestFragment extends Fragment
{

    private View mView;

    private DatabaseReference booksRef;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager gridLayoutManager;

    public LatestFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_latest, container, false);

        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");

        recyclerView = mView.findViewById(R.id.latestBooksContainer);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        swipeRefreshLayout = mView.findViewById(R.id.latestRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                load();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return mView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        load();
    }

    public void load()
    {
        Query query = booksRef;

        FirebaseRecyclerOptions<Books> options = new FirebaseRecyclerOptions.Builder<Books>().setQuery(query, Books.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Books,ReadFragment.BooksViewHolder>(options) {

            @NonNull
            @Override
            public ReadFragment.BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_books_layout,parent,false);
                return new ReadFragment.BooksViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ReadFragment.BooksViewHolder viewHolder, int position, @NonNull Books model) {
                final String bookKey = getRef(position).getKey();

                viewHolder.setBook_title(model.getBook_title());
                viewHolder.setBook_cover(model.getBook_cover());

                viewHolder.V.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        goToClickBook(bookKey);
                    }
                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void goToClickBook(String bookKey)
    {
        Intent intent = new Intent(getActivity(),ClickBookActivity.class);
        intent.putExtra("bookKey",bookKey);
        startActivity(intent);
    }

}
