package com.example.avery.chamberofwizards.Books;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SearchBookActivity extends AppCompatActivity {

    //Firebase Variables
    private DatabaseReference booksRef;

    private Toolbar searchToolbar;
    private EditText txtSearchBook;
    private RecyclerView searchBooksContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        //Firebase Declarations
        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");

        searchToolbar = findViewById(R.id.searchBooksToolbar);
        txtSearchBook = findViewById(R.id.searchBookAct);
        searchBooksContainer = findViewById(R.id.searchBooksContainer);

        setSupportActionBar(searchToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recycler View Customizations
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchBookActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchBooksContainer.setLayoutManager(linearLayoutManager);
        searchBooksContainer.addItemDecoration(new DividerItemDecoration(SearchBookActivity.this, DividerItemDecoration.VERTICAL)); //Divider for each item

        txtSearchBook.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchInput = txtSearchBook.getText().toString();
                loadSeach(searchInput);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchInput = txtSearchBook.getText().toString();
                loadSeach(searchInput);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void loadSeach(String search) {

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

        Query query = booksRef.orderByChild("book_title").startAt(search.toUpperCase()).endAt(search.toLowerCase() + "\uf8ff");

        FirebaseRecyclerOptions<MoreBooks> options = new FirebaseRecyclerOptions.Builder<MoreBooks>().setQuery(query, MoreBooks.class).build();

        FirebaseRecyclerAdapter<MoreBooks, TopRatedMoreActivity.MoreBooksViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MoreBooks, TopRatedMoreActivity.MoreBooksViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TopRatedMoreActivity.MoreBooksViewHolder viewHolder, int position, @NonNull MoreBooks model) {
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
            public TopRatedMoreActivity.MoreBooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_books_layout, parent, false);
                return new TopRatedMoreActivity.MoreBooksViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        searchBooksContainer.setAdapter(firebaseRecyclerAdapter);
    }

    TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            String searchInput = txtSearchBook.getText().toString();
            loadSeach(searchInput);
            return false;
        }
    };

    public void goToClickBook(String book_key) {
        Intent intent = new Intent(SearchBookActivity.this, ClickBookActivity.class);
        intent.putExtra("bookKey", book_key);
        startActivity(intent);
    }
}
