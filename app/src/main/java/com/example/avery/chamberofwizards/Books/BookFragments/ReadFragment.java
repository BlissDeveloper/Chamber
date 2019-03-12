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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.avery.chamberofwizards.Books.Books;
import com.example.avery.chamberofwizards.Books.ClickBookActivity;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadFragment extends Fragment {

    View mView;

    private DatabaseReference booksRef;

    private RecyclerView booksContainer;
    private GridLayout gridLayout;
    private GridLayoutManager gridLayoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    public ReadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_read, container, false);
        setRetainInstance(true);

        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");

        gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        booksContainer = mView.findViewById(R.id.read_booksContainer);
        booksContainer.setHasFixedSize(true);
        booksContainer.setLayoutManager(gridLayoutManager);

        swipeRefreshLayout = mView.findViewById(R.id.mostPopularContainer);

        loadBooks();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBooks();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return mView;
    }

    public void loadBooks() {

        /*
           final String bookKey = getRef(position).getKey();

                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setBook_title(model.getBook_title());

                viewHolder.V.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        sendToClickBook(bookKey);

                    }
                });
         */

        Query query = booksRef.orderByChild("number_of_comments");

        FirebaseRecyclerOptions<Books> options = new FirebaseRecyclerOptions.Builder<Books>().setQuery(query, Books.class).build();

        FirebaseRecyclerAdapter<Books, BooksViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Books, BooksViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BooksViewHolder viewHolder, int position, @NonNull Books model) {
                final String bookKey = getRef(position).getKey();

                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setBook_title(model.getBook_title());

                viewHolder.V.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendToClickBook(bookKey);
                    }
                });
            }

            @NonNull
            @Override
            public BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_books_layout, parent, false);
                return new BooksViewHolder(view);
            }
        };

        booksContainer.setAdapter(firebaseRecyclerAdapter);
    }

    public void sendToClickBook(String bookKey) {

        Intent intent = new Intent(getActivity(), ClickBookActivity.class);
        intent.putExtra("bookKey", bookKey);
        startActivity(intent);

    }

    public static class BooksViewHolder extends RecyclerView.ViewHolder {

        View V;

        public BooksViewHolder(View itemView) {
            super(itemView);
            V = itemView;
        }

        public void setBook_cover(String book_cover) {

            ImageView imageView = V.findViewById(R.id.read_imgBookCover);
            Picasso.get().load(book_cover).into(imageView);

        }

        public void setBook_title(String book_title) {

            TextView textView = V.findViewById(R.id.read_txtBookTitle);
            textView.setText(book_title);

        }
    }

}
