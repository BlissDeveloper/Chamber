package com.example.avery.chamberofwizards.Books.Services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Books.ClickBookActivity;
import com.example.avery.chamberofwizards.Books.ReadBookActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;

public class DownloadBookService extends Service {

    private Context mContext;

    private DatabaseReference rootRef;
    private DatabaseReference booksRef;
    private FirebaseAuth mAuth;
    private DatabaseReference favoritesRef;
    private StorageReference pdfRef;
    private StorageReference newRef;
    private String currentUserID;

    private DownloadManager downloadManager;

    private SQLiteDatabase mDatabase;
    public static String DATABASE_NAME = "Downloaded_Books.db";

    private final IBinder mBinder = new MyLocalBinder();

    private String bookKey;

    public DownloadBookService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");
        mAuth = FirebaseAuth.getInstance();
        favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites");
        currentUserID = mAuth.getCurrentUser().getUid();
        pdfRef = FirebaseStorage.getInstance().getReference().child("Uploads");
        rootRef = FirebaseDatabase.getInstance().getReference();

        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null); //Creates or opens a database


        createTable();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        bookKey = intent.getExtras().get("book_key").toString();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.d("Avery", "Starting Service...");
                download(bookKey);
            }
        };

        Thread thread = new Thread(r);
        thread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyLocalBinder extends Binder {
        public DownloadBookService getService() {
            return DownloadBookService.this;
        }
    }


    public void download(final String book_key) {

        booksRef.child(book_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long queueID;
                    String bookUrl = dataSnapshot.child("book_url").getValue().toString();
                    String book_title = dataSnapshot.child("book_title").getValue().toString();
                    String book_cover = dataSnapshot.child("book_cover").getValue().toString();
                    Uri imageURI;

                    imageURI = Uri.parse(book_cover);
                    Uri uri = Uri.parse(bookUrl);


                    downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setTitle(book_title)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator + book_title + ".pdf");

                    queueID = downloadManager.enqueue(request);

                    String pdfPath = Environment.getExternalStorageDirectory() + Environment.DIRECTORY_DOWNLOADS + "//ChamberDownloads//Books/" + "/" + book_title + "//" + book_title + ".pdf";
                    String imgPath = Environment.getExternalStorageDirectory() + Environment.DIRECTORY_DOWNLOADS + "/ChamberDownloads/Books/" + "/" + book_title + "/" + book_title + "_cover" + ".jpg";

                    addBookToLocalDatabase(book_key, pdfPath, book_title, imgPath, currentUserID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void insertIntoFirebaseDB(final String book_Key) {
        booksRef.child(book_Key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String book_cover = dataSnapshot.child("book_cover").getValue().toString();
                    String book_title = dataSnapshot.child("book_title").getValue().toString();

                    HashMap<String, Object> bookMap = new HashMap<>();

                    bookMap.put("book_title", book_title);
                    bookMap.put("book_cover", book_cover);

                    rootRef.child("Downloaded Books").child(currentUserID).child(book_Key).updateChildren(bookMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                insertIntoFirebaseDB(book_Key);
                            } else {
                                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addBookToLocalDatabase(String book_key, String book_path, String book_title, String book_cover, String user_id) {
        String sql = "INSERT INTO downloaded_books (BOOK_KEY, BOOK_PATH, BOOK_TITLE, BOOK_COVER, USER) VALUES (?,?,?,?,?)";
        mDatabase.execSQL(sql, new String[]{book_key, book_path, book_title, book_cover, user_id});

        Toast.makeText(this, "Employee added!", Toast.LENGTH_SHORT).show();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS downloaded_books (BOOK_KEY TEXT PRIMARY KEY, BOOK_PATH TEXT, BOOK_TITLE TEXT, BOOK_COVER TEXT, USER TEXT )";
        mDatabase.execSQL(sql);
    }


}
