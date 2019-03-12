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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Books.ClickBookActivity;
import com.example.avery.chamberofwizards.Books.Favorites;
import com.example.avery.chamberofwizards.Books.ReadBookActivity;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    //Firebase Variables
    private FirebaseAuth mAuth;
    private DatabaseReference favoritesRef;
    private DatabaseReference booksRef;
    private String currentUserID;
    private Button btnRead;

    private RecyclerView favoritesContainer;
    private GridLayoutManager gridLayoutManager;

    private View mView;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_favorites, container, false);

        //Firebase declarations
        mAuth = FirebaseAuth.getInstance();
        favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites");
        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");
        currentUserID = mAuth.getCurrentUser().getUid();

        gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        favoritesContainer = mView.findViewById(R.id.favorite_books_container);
        favoritesContainer.setHasFixedSize(true);
        favoritesContainer.setLayoutManager(gridLayoutManager);

        loadFavorites();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void goToRead() {
        Intent intent = new Intent(getActivity(), ReadBookActivity.class);
        startActivity(intent);
    }

    public void loadFavorites() {

        Query query = favoritesRef.child(currentUserID);

        FirebaseRecyclerOptions<Favorites> options = new FirebaseRecyclerOptions.Builder<Favorites>().setQuery(query,Favorites.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Favorites, FavoritesViewHolder>(options) {

            @NonNull
            @Override
            public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_books_layout,parent,false);
                return new FavoritesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FavoritesViewHolder viewHolder, int position, @NonNull Favorites model) {
                final String bookKey = getRef(position).getKey();

                viewHolder.setBook_cover(model.getBook_cover());
                viewHolder.setBook_title(model.getBook_title());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToClickBook(bookKey);
                    }
                });
            }
        };
        firebaseRecyclerAdapter.startListening();
        favoritesContainer.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FavoritesViewHolder extends RecyclerView.ViewHolder {

        public View mView;

        public FavoritesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setBook_cover(String book_cover) {

            ImageView imageView = mView.findViewById(R.id.read_imgBookCover);
            Picasso.get().load(book_cover).into(imageView);

        }

        public void setBook_title(String book_title) {

            TextView textView = mView.findViewById(R.id.read_txtBookTitle);
            textView.setText(book_title);

        }
    }

    public void goToClickBook(String bookKey) {

        Intent intent = new Intent(getActivity(), ClickBookActivity.class);
        intent.putExtra("bookKey",bookKey);
        startActivity(intent);


    }

}
