package com.example.avery.chamberofwizards.Books;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avery.chamberofwizards.Books.BookFragments.MostDiscussed;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MostDiscussedMoreActivity extends AppCompatActivity {

    //Firebase Variables
    private DatabaseReference booksRef;

    private Toolbar mToolbar;
    private RecyclerView most_discussed_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_discussed_more);

        //Firebase Declarations
        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");

        mToolbar = findViewById(R.id.toolbar_most_discussed);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Most Discussed");

        most_discussed_container = findViewById(R.id.discussed_more_books);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MostDiscussedMoreActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        most_discussed_container.setLayoutManager(linearLayoutManager);
        most_discussed_container.addItemDecoration(new DividerItemDecoration(MostDiscussedMoreActivity.this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onStart() {
        super.onStart();
        load();
    }

    public void load() {
        /*
        FirebaseRecyclerAdapter<MoreBooks,TopRatedMoreActivity.MoreBooksViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MoreBooks, TopRatedMoreActivity.MoreBooksViewHolder>(
                MoreBooks.class,
                R.layout.more_books_layout,
                TopRatedMoreActivity.MoreBooksViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(TopRatedMoreActivity.MoreBooksViewHolder viewHolder, MoreBooks model, int position) {

                final String bookKey = getRef(position).getKey();

                viewHolder.setBook_title(model.getBook_title());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setAverage_rating(model.getAverage_rating());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setRating(bookKey);
            }
        };
         */
        Query query = booksRef.orderByChild("number_of_comments");

        FirebaseRecyclerOptions<MoreBooks> options = new FirebaseRecyclerOptions.Builder<MoreBooks>().setQuery(query, MoreBooks.class).build();

        FirebaseRecyclerAdapter<MoreBooks, TopRatedMoreActivity.MoreBooksViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MoreBooks, TopRatedMoreActivity.MoreBooksViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TopRatedMoreActivity.MoreBooksViewHolder viewHolder, int position, @NonNull MoreBooks model) {
                final String bookKey = getRef(position).getKey();

                viewHolder.setBook_title(model.getBook_title());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setAverage_rating(model.getAverage_rating());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setRating(bookKey);
            }

            @NonNull
            @Override
            public TopRatedMoreActivity.MoreBooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_books_layout, parent, false);
                return new TopRatedMoreActivity.MoreBooksViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        most_discussed_container.setAdapter(firebaseRecyclerAdapter);
    }
}
