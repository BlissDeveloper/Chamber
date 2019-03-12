package com.example.avery.chamberofwizards.Books;

import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.avery.chamberofwizards.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class ReadBookActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference booksRef;
    private StorageReference bookStorage;
    private FirebaseStorage rootStorageRef;

    private String path;

    private String bookKey;
    private File bookFile;
    private Uri bookUri;

    private PDFView pdfView;
    private Toolbar mToolbar;
    private ProgressBar progressBar;

    private DownloadManager downloadManager;

    private SQLiteDatabase mDatabase;

    private String book_title;

    private String DATABASE_NAME;

    private int readCode;

    private boolean isTapped;
    private boolean isToolbarVisible;
    private boolean isDarkModeActivated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);

        isTapped = false;
        isToolbarVisible = true;

        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");
        rootStorageRef = FirebaseStorage.getInstance();

        DATABASE_NAME = "Downloaded_books.db";

        pdfView = findViewById(R.id.pdfViewer);
        mToolbar = findViewById(R.id.readBookToolbar);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        bookKey = getIntent().getExtras().get("bookKey").toString();
        readCode = Integer.parseInt(getIntent().getExtras().get("readCode").toString());

        isDarkModeActivated = false;

        mDatabase = openOrCreateDatabase(ClickBookActivity.DATABASE_NAME, MODE_PRIVATE, null);

        if (readCode == 0) { //Offline Reading
            loadBook(bookKey, false);
        } else if (readCode == 1) { //Online Reading

            ReadBookFromURLTask readBookFromURLTask = new ReadBookFromURLTask(this);
            readBookFromURLTask.execute(bookKey, "false");
        }

        pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTapped = true;

                if (isTapped) {
                    if (isToolbarVisible) {
                        Toast.makeText(ReadBookActivity.this, "Toolbar Invisible", Toast.LENGTH_SHORT).show();
                        mToolbar.setVisibility(View.INVISIBLE);
                        isToolbarVisible = false;
                    } else {
                        //Di Visible
                        Toast.makeText(ReadBookActivity.this, "Toolbar Visible", Toast.LENGTH_SHORT).show();
                        mToolbar.setVisibility(View.VISIBLE);
                        isToolbarVisible = true;
                    }
                }
                isTapped = false;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.read_book_activity_menu_dark_mode:
                if (isDarkModeActivated == false) {
                    //Hindi pa activated ang dark mode, so gagawin nang dark mode yung pdfView.
                    enableDarkMode(item);
                    isDarkModeActivated = true;
                } else {
                    //Activated na 'yung dark mode, so ide-deactivate naman dito:
                    disableDarkMode(item);
                    isDarkModeActivated = false;
                }
                return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_book_activity_menu, menu);

        return true;
    }

    public void enableDarkMode(MenuItem menuItem) {
        menuItem.setIcon(R.drawable.dark_mode_activated_icon);
        if (readCode == 0) {
            //Offline
            loadBook(bookKey, true);
        } else if (readCode == 1) {
            ReadBookFromURLTask readBookFromURLTask = new ReadBookFromURLTask(this);
            readBookFromURLTask.execute(bookKey, "true");
        }
    }

    public void disableDarkMode(MenuItem menuItem) {
        menuItem.setIcon(R.drawable.dark_mode_deactivated_icon);
        pdfView.setNightMode(false);
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS downloaded_books (BOOK_KEY TEXT PRIMARY KEY, BOOK_PATH TEXT, BOOK_TITLE TEXT, BOOK_COVER TEXT, USER TEXT )";
        mDatabase.execSQL(sql);
    }

    public void load() {
        String rootPath = Environment.getExternalStorageDirectory().getPath();
        String downloadPath = rootPath + "/" + Environment.DIRECTORY_DOWNLOADS;
        String pdfPath = downloadPath + "/My Hero.pdf";

        File file = new File(pdfPath);

        if (file.exists()) {
            //File does exists, pero hindi naglo-load sa pdf
            pdfView.fromFile(file).load(); //.load() lang pala ang kulang.
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadBook(String book_key, boolean nightMode) {
        String sql = "SELECT BOOK_TITLE FROM downloaded_books WHERE BOOK_KEY = ?";
        Cursor cursor = mDatabase.rawQuery(sql, new String[]{book_key}); //.rawQuery for selection, execSQL for Inserting, Updating and Deleting

        if (cursor.moveToFirst()) { //Checks if the rawQuery has results
            do {
                book_title = cursor.getString(0); //Parameter is the position. Zero kasi isa lang sinelect ko sa table, computer counting starts with zero
            }
            while (cursor.moveToNext());

            String rootPath = Environment.getExternalStorageDirectory().getPath();
            String downloadPath = rootPath + "/" + Environment.DIRECTORY_DOWNLOADS;
            String pdfPath = downloadPath + "/" + book_title + ".pdf";

            Toast.makeText(this, pdfPath, Toast.LENGTH_LONG).show();


            File file = new File(pdfPath);

            if (file.exists()) {
                //Toast.makeText(this, "PDF EXISTS", Toast.LENGTH_SHORT).show();
                if (nightMode) {
                    //true
                    pdfView.fromFile(file)
                            .enableSwipe(true)
                            .nightMode(true)
                            .load();
                } else {
                    pdfView.fromFile(file)
                            .enableSwipe(true)
                            .load();
                }
            } else {
                Toast.makeText(this, book_title + " PDF not found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }

    }


    private class ReadBookFromURLTask extends AsyncTask<String, Integer, String> {

        private WeakReference<ReadBookActivity> activityWeakReference;

        ReadBookFromURLTask(ReadBookActivity activity) {
            activityWeakReference = new WeakReference<ReadBookActivity>(activity);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            progressBar = findViewById(R.id.readBookProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            ReadBookActivity readBookActivity = activityWeakReference.get();

            if (readBookActivity == null || readBookActivity.isFinishing()) {
                return;
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... strings) {

            readBookFromURL(strings[0], strings[1]);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ReadBookActivity readBookActivity = activityWeakReference.get();

            if (readBookActivity == null || readBookActivity.isFinishing()) {
                return;
            }

            progressBar.setVisibility(View.GONE);
        }

        public void readBookFromURL(String book_key, final String nightMode) {
            final long ONE_MEGABYTE = 1024 * 1024 * 20;

            booksRef.child(book_key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String url = dataSnapshot.child("book_url").getValue().toString();

                        if (url != null) {
                            bookStorage = rootStorageRef.getReferenceFromUrl(url);

                            if (bookStorage != null) {
                                Toast.makeText(ReadBookActivity.this, "Storage Path exist", Toast.LENGTH_SHORT).show();

                                bookStorage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(ReadBookActivity.this, "Successfully downloaded the bytes", Toast.LENGTH_SHORT).show();

                                        if (bytes != null) {
                                            if (nightMode.equals("true")) {
                                                pdfView.fromBytes(bytes)
                                                        .nightMode(true)
                                                        .load();
                                            } else if (nightMode.equals("false")) {
                                                pdfView.fromBytes(bytes)
                                                        .load();
                                            }
                                        } else {
                                            Toast.makeText(ReadBookActivity.this, "Bytes does not exist.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            } else {
                                Toast.makeText(ReadBookActivity.this, "Storage path does not exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ReadBookActivity.this, "Book URL does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
