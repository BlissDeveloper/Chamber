package com.example.avery.chamberofwizards.Books;

import android.content.Intent;
import android.content.pm.LabeledIntent;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class TopRatedMoreActivity extends AppCompatActivity {

    //Firebase Variables
    private DatabaseReference booksRef;

    private RecyclerView moreTopRatedBooksContainer;
    private Toolbar mToolbar;

    private FirebaseRecyclerAdapter<MoreBooks, MoreBooksViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_rated_more);

        //Firebase Declarations
        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");
        booksRef.keepSynced(true);

        mToolbar = findViewById(R.id.toolbar_top_rated);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Top Rated");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        moreTopRatedBooksContainer = findViewById(R.id.top_more_books);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TopRatedMoreActivity.this);
        linearLayoutManager.setReverseLayout(true); //Reverses the layout
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        moreTopRatedBooksContainer.setLayoutManager(linearLayoutManager);
        moreTopRatedBooksContainer.addItemDecoration(new DividerItemDecoration(TopRatedMoreActivity.this, DividerItemDecoration.VERTICAL)); //Adding divider

        load();
    }

    public void load() {

        /*
         firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MoreBooks, MoreBooksViewHolder>(
                MoreBooks.class,
                R.layout.more_books_layout,
                MoreBooksViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(MoreBooksViewHolder viewHolder, MoreBooks model, int position) {
                final String bookKey = getRef(position).getKey();
                final int itemCount = firebaseRecyclerAdapter.getItemCount();

                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setAverage_rating(model.getAverage_rating());
                viewHolder.setBook_title(model.getBook_title());
                viewHolder.setUsername(model.getUsername());

                viewHolder.setRating(bookKey);

                //Kapag pinindot ng user ang isang item sa recycler view.

                viewHolder.V.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToClickBook(bookKey);
                    }
                });


            }
        };
         */
        Query query = booksRef.orderByChild("average_rating");

        FirebaseRecyclerOptions<MoreBooks> options = new FirebaseRecyclerOptions.Builder<MoreBooks>().setQuery(query, MoreBooks.class).build();

        FirebaseRecyclerAdapter<MoreBooks, MoreBooksViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MoreBooks, MoreBooksViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MoreBooksViewHolder viewHolder, int position, @NonNull MoreBooks model) {
                final String bookKey = getRef(position).getKey();

                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setAverage_rating(model.getAverage_rating());
                viewHolder.setBook_title(model.getBook_title());
                viewHolder.setUsername(model.getUsername());

                viewHolder.setRating(bookKey);

                //Kapag pinindot ng user ang isang item sa recycler view.

                viewHolder.V.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToClickBook(bookKey);
                    }
                });
            }

            @NonNull
            @Override
            public MoreBooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_books_layout, parent, false);
                return new MoreBooksViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        moreTopRatedBooksContainer.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MoreBooksViewHolder extends RecyclerView.ViewHolder {

        private DatabaseReference booksRef2;

        public static View V;

        private RatingBar ratingBar;
        private TextView rank;

        public MoreBooksViewHolder(View itemView) {
            super(itemView);
            V = itemView;
            booksRef2 = FirebaseDatabase.getInstance().getReference().child("Book Requests");
            ratingBar = V.findViewById(R.id.more_book_rating_bar);
        }

        public void setBook_cover(String book_cover) {
            ImageView imageView = V.findViewById(R.id.more_book_cover);
            Picasso.get().load(book_cover).into(imageView);
        }

        public void setBook_title(String book_title) {
            TextView textView = V.findViewById(R.id.more_book_title);
            textView.setText(book_title);
        }

        public void setUsername(String username) {
            TextView textView = V.findViewById(R.id.more_book_author);
            textView.setText(username);
        }

        public void setAverage_rating(float average_rating) {
            String rating = "";
            TextView textView = V.findViewById(R.id.more_book_rating);
            rating = Float.toString(average_rating);
            textView.setText(rating);
        }

        public void setRating(String book_key) {
            booksRef2.child(book_key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String rating = dataSnapshot.child("average_rating").getValue().toString();
                        float rating_float = Float.parseFloat(rating);
                        ratingBar.setRating(rating_float);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public void goToClickBook(String book_key) {
        Intent intent = new Intent(TopRatedMoreActivity.this,ClickBookActivity.class);
        intent.putExtra("bookKey", book_key);
        startActivity(intent);
    }
}
