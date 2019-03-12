package com.example.avery.chamberofwizards.Books.BookFragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.avery.chamberofwizards.Books.Books;
import com.example.avery.chamberofwizards.Books.ClickBookActivity;
import com.example.avery.chamberofwizards.Books.MoreBooks;
import com.example.avery.chamberofwizards.Books.MostDiscussedMoreActivity;
import com.example.avery.chamberofwizards.Books.TopRatedMoreActivity;
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
public class MainBooksScreenFragment extends Fragment {

    //Firebase Variables
    private DatabaseReference booksRef;

    private View mView;
    private Query topRatedQuery;

    private RecyclerView top_rated_container;
    private RecyclerView most_discussed_container;
    private RecyclerView most_popular_container;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ConstraintLayout constraintLayout;
    private ConstraintLayout most_discussed_view;

    public MainBooksScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_main_books_screen, container, false);

        //Firebase Declarations
        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");
        booksRef.keepSynced(true);
        topRatedQuery = null;

        swipeRefreshLayout = mView.findViewById(R.id.swipe_book);

        constraintLayout = mView.findViewById(R.id.topRatedMore);
        most_discussed_view = mView.findViewById(R.id.most_discussed_view);

        top_rated_container = mView.findViewById(R.id.top_rated_container);
        most_discussed_container = mView.findViewById(R.id.most_discussed_container);
        most_popular_container = mView.findViewById(R.id.most_popular_container);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);

        top_rated_container.setLayoutManager(linearLayoutManager);
        most_discussed_container.setLayoutManager(linearLayoutManager1);
        most_popular_container.setLayoutManager(linearLayoutManager2);

        loadTopRatedBooks();
        loadMostDiscussedBooks();
        loadLatestBooks();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTopRatedBooks();
                loadMostDiscussedBooks();
                loadLatestBooks();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMoreTopRated();
            }
        });

        most_discussed_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMostDiscussed();
            }
        });

        return mView;
    }

    public void loadTopRatedBooks() {
        topRatedQuery = booksRef.orderByChild("average_rating");

        FirebaseRecyclerOptions<Books> options = new FirebaseRecyclerOptions.Builder<Books>().setQuery(topRatedQuery, Books.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Books,MainBooksViewHolder>(options) {

            @NonNull
            @Override
            public MainBooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_book_layout,parent,false);
                return new MainBooksViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MainBooksViewHolder viewHolder, int position, @NonNull Books model) {
                final String bookKey = getRef(position).getKey();

                viewHolder.setBook_title(model.getBook_title());
                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setUsername(model.getUsername());

                viewHolder.V.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToClickBook(bookKey);
                    }
                });
            }
        };
        firebaseRecyclerAdapter.startListening();
        top_rated_container.setAdapter(firebaseRecyclerAdapter);
        //most_discussed_container.setAdapter(firebaseRecyclerAdapter);
        //most_popular_container.setAdapter(firebaseRecyclerAdapter);
    }

    public void loadMostDiscussedBooks() {
        Query mostDiscussedQuery = booksRef.orderByChild("number_of_comments");

        //Books, MainBooksVIew, main_book_Layout

        /*
         final String bookKey1 = getRef(position).getKey();

                viewHolder.setBook_title(model.getBook_title());
                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setUsername(model.getUsername());

                viewHolder.V.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToClickBook(bookKey1);
                    }
                });
         */

        FirebaseRecyclerOptions<Books> options = new FirebaseRecyclerOptions.Builder<Books>().setQuery(mostDiscussedQuery, Books.class).build();

        FirebaseRecyclerAdapter mostDiscussedAdapter = new FirebaseRecyclerAdapter<Books, MainBooksViewHolder>(options) {

            @NonNull
            @Override
            public MainBooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_book_layout, parent,false);
                return new MainBooksViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MainBooksViewHolder viewHolder, int position, @NonNull Books model) {
                final String bookKey1 = getRef(position).getKey();

                viewHolder.setBook_title(model.getBook_title());
                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setUsername(model.getUsername());

                viewHolder.V.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToClickBook(bookKey1);
                    }
                });
            }
        };
        mostDiscussedAdapter.startListening();
        most_discussed_container.setAdapter(mostDiscussedAdapter);
    }

    /*
     final String bookKey2 = getRef(position).getKey();

                viewHolder.setBook_title(model.getBook_title());
                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setUsername(model.getUsername());

                viewHolder.V.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToClickBook(bookKey2);
                    }
                });
     */

    public void loadLatestBooks() {

        Query query = booksRef;

        FirebaseRecyclerOptions<Books> options = new FirebaseRecyclerOptions.Builder<Books>().setQuery(query, Books.class).build();

        FirebaseRecyclerAdapter latestAdapter = new FirebaseRecyclerAdapter<Books,MainBooksViewHolder>(options) {

            @NonNull
            @Override
            public MainBooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_book_layout,parent,false);
                return new MainBooksViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MainBooksViewHolder viewHolder, int position, @NonNull Books model) {
                final String bookKey2 = getRef(position).getKey();
                viewHolder.setBook_title(model.getBook_title());
                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setUsername(model.getUsername());

                viewHolder.V.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToClickBook(bookKey2);
                    }
                });
            }
        };
        latestAdapter.startListening();
        most_popular_container.setAdapter(latestAdapter);
    }

    public static class MainBooksViewHolder extends RecyclerView.ViewHolder {
        private View V;

        public MainBooksViewHolder(View itemView) {
            super(itemView);
            V = itemView;
        }

        public void setBook_title(String book_title) {
            TextView textView = V.findViewById(R.id.txt_book_title);
            textView.setText(book_title);
        }

        public void setBook_cover(String book_cover) {
            ImageView imageView = V.findViewById(R.id.img_book_cover);
            Picasso.get().load(book_cover).into(imageView);
        }

        public void setUsername(String username) {
            TextView textViewUser = V.findViewById(R.id.txt_book_author);
            textViewUser.setText(username);
        }
    }

    public void goToClickBook(String book_key) {
        Intent intent = new Intent(getActivity(), ClickBookActivity.class);
        intent.putExtra("bookKey", book_key);
        startActivity(intent);
    }

    public void goToMoreTopRated() {
        Intent intent = new Intent(getActivity(), TopRatedMoreActivity.class);
        startActivity(intent);
    }

    public void goToMostDiscussed() {
        Intent intent = new Intent(getActivity(),MostDiscussedMoreActivity.class);
        startActivity(intent);
    }
}
