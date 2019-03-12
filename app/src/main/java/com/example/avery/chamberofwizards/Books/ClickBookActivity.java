package com.example.avery.chamberofwizards.Books;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avery.chamberofwizards.Books.Services.DownloadBookService;
import com.example.avery.chamberofwizards.DatabaseHelper;
import com.example.avery.chamberofwizards.Forum.CommentsActivity;
import com.example.avery.chamberofwizards.Forum.EditPostActivity;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClickBookActivity extends AppCompatActivity {

    DownloadBookService mService;

    private DatabaseReference rootRef;
    private DatabaseReference booksRef;
    private FirebaseAuth mAuth;
    private DatabaseReference favoritesRef;
    private StorageReference pdfRef;
    private StorageReference newRef;
    private ValueEventListener bookDownloadListener;
    private String currentUserID;
    private ValueEventListener favoriteListener;
    private StorageReference rootStorageRef;
    private DatabaseReference usersRef;
    private ValueEventListener btnReviewListener;

    private final int Gallery_Pick = 26;
    private Uri uriImageBookReview;
    private String imageBookReviewDownloadURL;
    private static final int EDIT_FLAG_BOOK_REVIEW = 1;

    //Local database instance or object
    //DatabaseHelper localDB;
    //private SQLiteDatabase mDatabase;

    private DownloadManager downloadManager;

    private String bookKey;
    String pdfURL;
    Uri file;

    private Toolbar clickBookToolbar;

    private ImageView clickBook_imgBookCover;
    private TextView user_name, book_title, book_author, clickBookCourse;
    private RecyclerView commentsContainer;
    private TextView txtNumberOfReviews;
    private Button btnRead;
    private Button btnFavorite;
    private Button btnDownload;
    private ProgressBar progressBar;
    private TextView txtDownloadProgress;

    public static String DATABASE_NAME = "Downloaded_Books.db";

    private CircleImageView btnReview;

    private List<String> keys;
    private List<Float> ratings;

    private RatingBar ratingBar;

    String[] ratingKey;

    private ProgressDialog progressDialog;

    int j;

    float sum;

    float summation, numberOfComments, avg;

    //XML views for cardview ratring
    private CardView cardviewBookRating;
    private ImageButton imageButtonCloseBookReview;
    private Button btnSubmitBookReview;
    private RatingBar ratingBarBookReview;
    private EditText editTextBookReview;
    private ImageView imageViewBookReview;
    private ImageButton imageButtonAddImageToBookReview;
    private static ProgressBar progressBarBookReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_book);

        //localDB = new DatabaseHelper(ClickBookActivity.this);

        booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");
        mAuth = FirebaseAuth.getInstance();
        favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites");
        currentUserID = mAuth.getCurrentUser().getUid();
        pdfRef = FirebaseStorage.getInstance().getReference().child("Uploads");
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootStorageRef = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        bookKey = getIntent().getExtras().get("bookKey").toString();

        clickBookToolbar = findViewById(R.id.clickBookToolbar);
        setSupportActionBar(clickBookToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clickBook_imgBookCover = findViewById(R.id.clickBook_imgBookCover);
        user_name = findViewById(R.id.clickBook_txtUsername);
        book_title = findViewById(R.id.clickBook_txtTitle);
        book_author = findViewById(R.id.clickBook_txtUsername);
        clickBookCourse = findViewById(R.id.clickBook_txtCourse);
        btnReview = findViewById(R.id.btnReview);
        txtNumberOfReviews = findViewById(R.id.txtNumberOfReviews);

        //Buttons
        btnRead = findViewById(R.id.clickBookBtnRead);
        btnFavorite = findViewById(R.id.clickBookBtnFavorite);
        btnDownload = findViewById(R.id.clickBookBtnDownload);

        commentsContainer = findViewById(R.id.commentsContainer);
        commentsContainer.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentsContainer.setLayoutManager(linearLayoutManager);

        //XML Views for the rating card
        cardviewBookRating = findViewById(R.id.cardviewBookReview);
        imageButtonCloseBookReview = findViewById(R.id.imageButtonCloseBookReview);
        btnSubmitBookReview = findViewById(R.id.btnSubmitBookRating);
        ratingBarBookReview = findViewById(R.id.ratingBarBookRating);
        editTextBookReview = findViewById(R.id.editTextBookReview);
        imageViewBookReview = findViewById(R.id.imageViewBookReviewImage);
        imageButtonAddImageToBookReview = findViewById(R.id.imageButtonAddImageToBookReview);
        progressBarBookReview = findViewById(R.id.progressBarClickBook);
        imageBookReviewDownloadURL = null;
        uriImageBookReview = null;

        ratingBar = findViewById(R.id.ratingBar);

        sum = 0;
        j = 0;

        loadBook(bookKey);

        keys = new ArrayList<>();

        imageButtonCloseBookReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardviewBookRating.setVisibility(View.GONE);
            }
        });

        btnSubmitBookReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarBookReview.setVisibility(View.VISIBLE);
                if (uriImageBookReview == null) {
                    addBookReviewToFirebaseDatabase(ratingBarBookReview.getRating());
                } else {
                    uploadBookReviewImageToFirebaseStorage(ratingBarBookReview.getRating());
                }
            }
        });

        imageButtonAddImageToBookReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        editTextBookReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().trim().length() > 5) {
                    btnSubmitBookReview.setVisibility(View.VISIBLE);
                } else {
                    btnSubmitBookReview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 5) {
                    btnSubmitBookReview.setVisibility(View.VISIBLE);
                } else {
                    btnSubmitBookReview.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ito yung background task:

                if (ContextCompat.checkSelfPermission(ClickBookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    booksRef.child(bookKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String url = dataSnapshot.child("book_url").getValue().toString();
                                String book_title = dataSnapshot.child("book_title").getValue().toString();
                                String book_cover = dataSnapshot.child("book_cover").getValue().toString();
                                if (url != null && book_title != null && book_cover != null) {

                                    //    new DownloadBookTask().execute(url, book_title, book_cover, currentUserID);
                                    //   mDatabase.execSQL(sql, new String[]{book_key, book_path, book_title, book_cover, user_id});
                                    // Result, book_key, path, title, cover, user_id

                                    new DownloadBookTask(ClickBookActivity.this).execute(url, book_title, book_cover, bookKey, currentUserID);


                                } else {
                                    Log.d("Avery", "Book url cannot be null");
                                }
                            } else {
                                Log.d("Avery", "Datasnapshot does not exists");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    ActivityCompat.requestPermissions(ClickBookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9);
                }


            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite(bookKey);
            }
        });

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookKey != null) {
                    goToRead(bookKey);
                } else {
                    Toast.makeText(ClickBookActivity.this, "Book key cannot be null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goToWriteReview(bookKey);
                cardviewBookRating.setVisibility(View.VISIBLE);
            }
        });

        clickBook_imgBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getSumOfReviews(bookKey);
            }
        });

        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null); //Creates or opens a database
        // createTable(); //Creates the table. Refer to the method below
    }

    public void uploadBookReviewImageToFirebaseStorage(final float r) {
        String uniqueId = UUID.randomUUID().toString();

        final StorageReference filePath = rootStorageRef.child("Book Reviews").child(uniqueId);
        filePath.putFile(uriImageBookReview).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageBookReviewDownloadURL = uri.toString();
                            addBookReviewToFirebaseDatabase(r);
                        }
                    });
                }
            }
        });
    }

    public void setDefaults() {
        ratingBarBookReview.setRating(0);
        editTextBookReview.setText("");
        imageViewBookReview.setImageURI(null);
        imageViewBookReview.setVisibility(View.GONE);
    }

    public void updateBookData(final float rating) {
        booksRef.child(bookKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    float number_of_reviews = 0, average_rating = 0, summation_of_reviews = 0;
                    float new_n_reviews = 0, new_average_rating = 0, new_summation_of_reviews = 0;

                    number_of_reviews = Float.parseFloat(dataSnapshot.child("number_of_comments").getValue().toString());
                    average_rating = Float.parseFloat(dataSnapshot.child("average_rating").getValue().toString());
                    summation_of_reviews = Float.parseFloat(dataSnapshot.child("summation_of_comments").getValue().toString());

                    number_of_reviews = number_of_reviews + 1;
                    summation_of_reviews = summation_of_reviews + rating;

                    average_rating = summation_of_reviews / number_of_reviews;

                    Log.d("Avery", "Rating of the rating bar: " + String.valueOf(rating));
                    Log.d("Avery", "Number of reviews: " + String.valueOf(number_of_reviews));
                    Log.d("Avery", "Summation of reviews: " + String.valueOf(summation_of_reviews));
                    Log.d("Avery", "Average rating: " + String.valueOf(average_rating));

                    Map<String, Object> map = new ArrayMap<>();
                    map.put("number_of_comments", String.valueOf(number_of_reviews));
                    map.put("summation_of_comments", summation_of_reviews);
                    map.put("average_rating", average_rating);

                    booksRef.child(bookKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ClickBookActivity.this, "Review posted successfully!", Toast.LENGTH_SHORT).show();
                                setDefaults();
                            } else {
                                Toast.makeText(ClickBookActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBarBookReview.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addBookReviewToFirebaseDatabase(final float r) {
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username, user_image;

                    username = dataSnapshot.child("fullname").getValue().toString();
                    user_image = dataSnapshot.child("profile_image").getValue().toString();

                    //Getting current date
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd");
                    final String saveCurrentDate = currentDate.format(cal.getTime());

                    //Getting current time
                    Calendar calTime = Calendar.getInstance();
                    SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                    final String saveCurrentTime = currentTime.format(calTime.getTime());


                    Map<String, Object> reviewMap = new ArrayMap<>();

                    reviewMap.put("uid", currentUserID);
                    reviewMap.put("username", username);
                    reviewMap.put("user_image", user_image);
                    reviewMap.put("date", saveCurrentDate);
                    reviewMap.put("time", saveCurrentTime);
                    reviewMap.put("review", editTextBookReview.getText().toString());
                    reviewMap.put("rating", r);
                    reviewMap.put("book_key", bookKey);
                    reviewMap.put("book_review_image", imageBookReviewDownloadURL);

                    booksRef.child(bookKey).child("Reviews").push().updateChildren(reviewMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                updateBookData(r);
                            } else {
                                Log.e("Avery", task.getException().getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            uriImageBookReview = data.getData();
            imageViewBookReview.setImageURI(uriImageBookReview);
        }
    }

    public class DownloadBookTask extends AsyncTask<String, Integer, String[]> {

        NotificationCompat.Builder notification;
        NotificationChannel mChannel;

        private Context mContexct;

        private SQLiteDatabase mDatabase;
        private String DATABASE = "Downloaded_Books.db";

        private NotificationManager notificationManager;

        final int NOTIFY_ID = 26;
        final String CHANNEL_ID = "chamber_id_01";
        CharSequence name = getString(R.string.chamber_channel);

        DownloadBookTask(ClickBookActivity clickBookActivityContext) {
            mContexct = clickBookActivityContext;
        }

        public void insertIntoFirebaseDB(final String book_Key, final String book_title, final String book_cover, final String user_id, final String[] s) {
            HashMap<String, Object> bookMap = new HashMap<>();


            /*
                    s[0] = "n";
                    s[1] = strings[3]; Book key
                    s[2] = pdfFile.getAbsolutePath(); Path
                    s[3] = strings[1]; Title
                    s[4] = strings[2]; Cover
                    s[5] = strings[4]; ID

             */

            bookMap.put("book_title", book_title);
            bookMap.put("book_cover", book_cover);

            rootRef.child("Downloaded Books").child(currentUserID).child(book_Key).updateChildren(bookMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Avery", "Book data added to firebase");
                        addBookToLocalDatabase(s[1], s[2], s[3], s[4], s[5]);
                    } else {
                        Log.d("Avery", task.getException().getMessage());
                    }
                }
            });
        }

        public void addBookToLocalDatabase(String book_key, String book_path, String book_title, String book_cover, String user_id) {
            String sql = "INSERT INTO downloaded_books (BOOK_KEY, BOOK_PATH, BOOK_TITLE, BOOK_COVER, USER) VALUES (?,?,?,?,?)";
            mDatabase.execSQL(sql, new String[]{book_key, book_path, book_title, book_cover, user_id});
        }

        public void createTable() {
            String sql = "CREATE TABLE IF NOT EXISTS downloaded_books (BOOK_KEY TEXT PRIMARY KEY, BOOK_PATH TEXT, BOOK_TITLE TEXT, BOOK_COVER TEXT, USER TEXT )";
            mDatabase.execSQL(sql);
            Log.d("Avery", "Database created!");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mContexct == null) {
                Log.e("Avery", "No context");
            }

            mDatabase = openOrCreateDatabase(DATABASE, MODE_PRIVATE, null);

            Log.d("Avery", "Creating local DB");
            createTable();

            //Kaylangan ng ID for Oreo above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //Kapag oreo or above ang verion ng phone
                mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);

                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notification = new NotificationCompat.Builder(mContexct, CHANNEL_ID)
                        .setSmallIcon(R.drawable.download_icon)
                        .setProgress(0, 0, false);

                notificationManager.createNotificationChannel(mChannel);
            }


            progressBar = findViewById(R.id.clickBookProgressbar);
            txtDownloadProgress = findViewById(R.id.txtDownloadProgress);
            progressBar.setMax(100);
            progressBar.setVisibility(View.VISIBLE);
            txtDownloadProgress.setVisibility(View.VISIBLE);


        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);

            /*
                    s[0] = "n";
                    s[1] = strings[3]; Book key
                    s[2] = pdfFile.getAbsolutePath(); Path
                    s[3] = strings[1]; Title
                    s[4] = strings[2]; Cover
                    s[5] = strings[4]; ID

             */

            //Result, book_key, book_path, book_title, book_cover, user_id

            /*
            public void addBookToLocalDatabase(String book_key, String book_path, String book_title, String book_cover, String user_id) {
            String sql = "INSERT INTO downloaded_books (BOOK_KEY, BOOK_PATH, BOOK_TITLE, BOOK_COVER, USER) VALUES (?,?,?,?,?)";
            mDatabase.execSQL(sql, new String[]{book_key, book_path, book_title, book_cover, user_id});
        }
             */

            if (s[0].equals("y")) {
                Log.d("Avery", "Download Success");
                //addBookToLocalDatabase(s[1], s[2], s[3], s[4], s[5]);
                insertIntoFirebaseDB(s[1], s[3], s[4], s[5], s);

            } else if (s[0].equals("n")) {
                Log.d("Avery", "Download Failed");
            } else {
                Log.d("Avery", s[0]);
            }

            progressBar.setVisibility(View.GONE);
            txtDownloadProgress.setVisibility(View.GONE);

            notification.setContentText("Download complete").setProgress(0, 0, false);
            notificationManager.notify(NOTIFY_ID, notification.build());
        }


        @Override
        protected String[] doInBackground(String... strings) {

            Log.d("Avery", "Starting Async");

            File pdfFile;

            final String path = Environment.getExternalStorageDirectory() + "//Download//" + "//ChamberDownloads//Books/" + "/" + strings[1] + "//" + strings[1] + ".pdf";
            final String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            Log.d("Avery", Environment.DIRECTORY_DOWNLOADS);
            int count;

            try {
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                pdfFile = new File(absolutePath, strings[1] + ".pdf");

                if (pdfFile.createNewFile()) {
                    pdfFile.createNewFile();
                }

                int lengthOfFile = connection.getContentLength();
                Log.d("Avery", "Length of file: " + lengthOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(pdfFile);

                byte data[] = new byte[lengthOfFile];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    long progress_temp = Math.abs((total * 100) / lengthOfFile);
                    publishProgress((int) progress_temp);
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                String s[] = new String[6];

                if (pdfFile.exists()) {
                    Log.d("Avery", "File downloaded.");
                    //    new DownloadBookTask().execute(url, book_title, book_cover, currentUserID);
                    //   mDatabase.execSQL(sql, new String[]{book_key, book_path, book_title, book_cover, user_id});
                    //new DownloadBookTask().execute(url, book_title, book_cover, bookKey, currentUserID);


                    //Result, book_key, book_path, book_title, book_cover, user_id
                    s[0] = "y";
                    s[1] = strings[3];
                    s[2] = pdfFile.getAbsolutePath();
                    s[3] = strings[1];
                    s[4] = strings[2];
                    s[5] = strings[4];

                    return s;
                } else {
                    Log.d("Avery", "File not downloaded.");

                    s[0] = "n";
                    s[1] = strings[3];
                    s[2] = pdfFile.getAbsolutePath();
                    s[3] = strings[1];
                    s[4] = strings[2];
                    s[5] = strings[4];

                    return s;
                }
            } catch (Exception e) {
                Log.e("Avery", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("Avery", values[0] + "");
            progressBar.setProgress(values[0]);
            txtDownloadProgress.setText(String.valueOf(values[0]));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification.setProgress(100, values[0], false);
                notificationManager.notify(NOTIFY_ID, notification.build());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        maintainAverage();
        loadReviews(bookKey);
        displayNumberOfReviews(bookKey);
        maintainDownloadButton();
        maintainFavoriteButton();
        cardviewBookRating.setVisibility(View.GONE);
        //btnSubmitBookReview.setVisibility(View.GONE);
        maintainReviewButton();
    }

    public void maintainAverage() {
        booksRef.child(bookKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    float avg;
                    avg = Float.parseFloat(dataSnapshot.child("average_rating").getValue().toString());
                    Log.d("Avery", String.valueOf(avg));
                    ratingBar.setRating(avg);
                } else {
                    Log.e("Avery", "No datasnapshot exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    public void maintainReviewButton() {
        Query query = booksRef.child(bookKey).child("Reviews").orderByChild("uid").equalTo(currentUserID);

        btnReviewListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Meron nang review yung user
                    btnReview.setVisibility(View.GONE);
                    btnReview.setEnabled(false);
                    Log.d("Avery", "Meron nangf review si user");
                } else {
                    btnReview.setVisibility(View.VISIBLE);
                    btnReview.setEnabled(true);
                    Log.d("Avery", "Wala pang review si user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void maintainFavoriteButton() {
        favoriteListener = favoritesRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(bookKey)) {
                        //Favorited
                        btnFavorite.setText("Favorited");
                        btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_icon, 0, 0, 0);
                        btnFavorite.setEnabled(false);
                    } else {
                        btnFavorite.setText("Favorite");
                        btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_icon_outline, 0, 0, 0);
                        btnFavorite.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void maintainDownloadButton() {
        bookDownloadListener = rootRef.child("Downloaded Books").child(currentUserID).child(bookKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    btnDownload.setText("Downloaded");
                    btnDownload.setEnabled(false);
                } else {
                    btnDownload.setText("Download");
                    btnDownload.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_book_favorite:
                //favorite(bookKey);
                break;

            case R.id.clickBookDownload:
                if (ContextCompat.checkSelfPermission(ClickBookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ActivityCompat.requestPermissions(ClickBookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9);
                }
                break;

            case R.id.read_book_menu:
                //goToRead(bookKey);
                break;
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //download(bookKey);
            //startDownloadServie(bookKey);
        } else {
            Toast.makeText(this, "Kindly provide permission.", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToRead(String book_key) {

        Intent intent = new Intent(ClickBookActivity.this, ReadBookActivity.class);
        intent.putExtra("bookKey", book_key);
        intent.putExtra("readCode", 1);
        startActivity(intent);

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

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setTitle(book_title)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator + book_title + ".pdf");

                    queueID = downloadManager.enqueue(request);

                    DownloadManager.Request imageRequest = new DownloadManager.Request(imageURI);
                    imageRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/ChamberDownloads/Books" + book_title, File.separator + book_title + "_cover" + ".jpg");

                    long id = downloadManager.enqueue(imageRequest);

                    String pdfPath = Environment.getExternalStorageDirectory() + Environment.DIRECTORY_DOWNLOADS + "//ChamberDownloads//Books/" + "/" + book_title + "//" + book_title + ".pdf";
                    String imgPath = Environment.getExternalStorageDirectory() + Environment.DIRECTORY_DOWNLOADS + "/ChamberDownloads/Books/" + "/" + book_title + "/" + book_title + "_cover" + ".jpg";

                    //addBookToLocalDatabase(book_key, pdfPath, book_title, imgPath, currentUserID);
                    // insertIntoFirebaseDB(book_key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
        downloadManager  = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse();
         */

    }

    public void favorite(final String bookKey) {

        favoritesRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild(bookKey)) {
                        Toast.makeText(ClickBookActivity.this, "Book is already in your favorites!", Toast.LENGTH_SHORT).show();
                    } else {

                        booksRef.child(bookKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    String book_title, book_cover;

                                    book_title = dataSnapshot.child("book_title").getValue().toString();
                                    book_cover = dataSnapshot.child("book_cover").getValue().toString();

                                    HashMap<String, Object> favoritesMap = new HashMap<>();
                                    favoritesMap.put("book", bookKey);
                                    favoritesMap.put("book_title", book_title);
                                    favoritesMap.put("book_cover", book_cover);

                                    favoritesRef.child(mAuth.getCurrentUser().getUid()).child(bookKey).updateChildren(favoritesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(ClickBookActivity.this, "Book added to favorites!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ClickBookActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                } else {

                    booksRef.child(bookKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                String book_title, book_cover;

                                book_title = dataSnapshot.child("book_title").getValue().toString();
                                book_cover = dataSnapshot.child("book_cover").getValue().toString();

                                HashMap<String, Object> favoritesMap = new HashMap<>();
                                favoritesMap.put("book", bookKey);
                                favoritesMap.put("book_title", book_title);
                                favoritesMap.put("book_cover", book_cover);

                                favoritesRef.child(mAuth.getCurrentUser().getUid()).child(bookKey).updateChildren(favoritesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(ClickBookActivity.this, "Book added to favorites!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ClickBookActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.click_book_menu, menu);
        return true;
    }

    public void loadBook(String bookKey) {

        booksRef.child(bookKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String bookCover, authorName, userCourse, title;

                    bookCover = dataSnapshot.child("book_cover").getValue().toString();
                    authorName = dataSnapshot.child("username").getValue().toString();
                    userCourse = dataSnapshot.child("course").getValue().toString();
                    title = dataSnapshot.child("book_title").getValue().toString();

                    Picasso.get().load(bookCover).into(clickBook_imgBookCover);
                    user_name.setText(authorName);
                    book_title.setText(title);
                    book_author.setText(authorName);
                    clickBookCourse.setText(userCourse);

                    getSupportActionBar().setTitle(title);
                } else {
                    Toast.makeText(ClickBookActivity.this, "Book not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
    }

    public void goToWriteReview(String bookKey) {
        Intent intent = new Intent(ClickBookActivity.this, WriteReviewActivity.class);
        intent.putExtra("bookKey", bookKey);
        startActivity(intent);
    }

    public void getSumOfReviews(final String bookKey) {
        final Query query = booksRef.child(bookKey).child("Reviews");


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataKeys : dataSnapshot.getChildren()) {
                    keys.add(dataKeys.getKey());
                    Toast.makeText(ClickBookActivity.this, keys.get(j), Toast.LENGTH_SHORT).show();

                    booksRef.child(bookKey).child("Reviews").child(keys.get(j)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot d) {

                        }

                        @Override
                        public void onCancelled(DatabaseError d) {

                        }
                    });
                    j++;
                }
                keys.clear();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public float setAverageRatingOfBook(final String bookKey) {

        booksRef.child(bookKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("number_of_comments").exists()) {
                    summation = Float.parseFloat(dataSnapshot.child("summation_of_comments").getValue().toString());
                    numberOfComments = Float.parseFloat(dataSnapshot.child("number_of_comments").getValue().toString());

                    avg = (summation / numberOfComments); //* -1;

                    ratingBar.setRating(avg);

                    if (Float.isInfinite(avg) || Float.isNaN(avg)) {
                        booksRef.child(bookKey).child("average_rating").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ClickBookActivity.this, "Average rating uploaded!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    } else {
                        float av = avg;
                        booksRef.child(bookKey).child("average_rating").setValue(av).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ClickBookActivity.this, "Average rating uploaded!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return avg;
    }


    public void loadReviews(final String bookKey) {
        Query query = booksRef.child(bookKey).child("Reviews");

        FirebaseRecyclerOptions<Reviews> options = new FirebaseRecyclerOptions.Builder<Reviews>().setQuery(query, Reviews.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Reviews, ReviewsViewHolder>(options) {

            @NonNull
            @Override
            public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_reviews_layout, parent, false);
                return new ReviewsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ReviewsViewHolder viewHolder, int position, @NonNull final Reviews model) {
                final String reviewKey = getRef(position).getKey();

                viewHolder.imageButtonBookReviewDots.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.deleteReview(bookKey, reviewKey, model.getRating(), ClickBookActivity.this);
                    }
                });

                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setRating(model.getRating());
                viewHolder.setReview(model.getReview());
                viewHolder.setUser_image(model.getUser_image());
                viewHolder.setUsername(model.getUsername());
            }
        };

        firebaseRecyclerAdapter.startListening();
        commentsContainer.setAdapter(firebaseRecyclerAdapter);
    }

    public void displayNumberOfReviews(String bookKey) {

        booksRef.child(bookKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("number_of_comments").exists()) {

                    String n;
                    float nValue = 0;

                    nValue = Float.parseFloat(dataSnapshot.child("number_of_comments").getValue().toString()) * 1;
                    n = Integer.toString((int) nValue);
                    txtNumberOfReviews.setText(n + " Reviews");
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
                                Toast.makeText(ClickBookActivity.this, "Added to Firebase DB", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ClickBookActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ClickBookActivity.this, "Error inserting book.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class ReviewsViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        ImageButton imageButtonBookReviewDots;

        private DatabaseReference booksRef;
        private FirebaseAuth mAuth;
        private String currentUserID;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            booksRef = FirebaseDatabase.getInstance().getReference().child("Book Requests");
            mAuth = FirebaseAuth.getInstance();
            currentUserID = mAuth.getCurrentUser().getUid();

            imageButtonBookReviewDots = mView.findViewById(R.id.imageButtonBookReviewOptions);
        }

        public void updateBookDataFromDeletion(final String bookKey, final String reviewKey, final float rating, final Context context) {
            booksRef.child(bookKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        float number_of_reviews = 0, summation_of_reviews = 0, average_rating = 0;
                        number_of_reviews = Float.parseFloat(dataSnapshot.child("number_of_comments").getValue().toString());
                        summation_of_reviews = Float.parseFloat(dataSnapshot.child("summation_of_comments").getValue().toString());
                        average_rating = Float.parseFloat(dataSnapshot.child("average_rating").getValue().toString());

                        number_of_reviews = number_of_reviews - 1;
                        summation_of_reviews = summation_of_reviews - rating;

                        if (number_of_reviews == 0) {
                            average_rating = 0;
                        } else {
                            average_rating = summation_of_reviews / number_of_reviews;
                        }

                        Map<String, Object> map = new ArrayMap<>();
                        map.put("number_of_comments", number_of_reviews);
                        map.put("summation_of_comments", summation_of_reviews);
                        map.put("average_rating", average_rating);

                        booksRef.child(bookKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Review deleted successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("Avery", task.getException().getMessage());
                                }
                                progressBarBookReview.setVisibility(View.GONE);
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void deleteReview(final String bookKey, final String reviewKey, final float rating, final Context context) {

            CharSequence options[] = new CharSequence[]{
                    "Edit Review",
                    "Delete Review"

            };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete review?");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent(context, EditPostActivity.class);
                            intent.putExtra("key", reviewKey);
                            intent.putExtra("book_key", bookKey);
                            intent.putExtra("edit_flag", EDIT_FLAG_BOOK_REVIEW);
                            context.startActivity(intent);
                            break;
                        case 1:
                            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case Dialog.BUTTON_NEGATIVE:
                                            booksRef.child(bookKey).child("Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        if (dataSnapshot.hasChild(reviewKey)) {
                                                            progressBarBookReview.setVisibility(View.VISIBLE);
                                                            booksRef.child(bookKey).child("Reviews").child(reviewKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        //Toast.makeText(context, "Review deleted successfully!", Toast.LENGTH_SHORT).show();
                                                                        updateBookDataFromDeletion(bookKey, reviewKey, rating, context);
                                                                    } else {
                                                                        Log.e("Avery", task.getException().getMessage());
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            break;
                                        case Dialog.BUTTON_POSITIVE:
                                            dialog.dismiss();
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                            alertBuilder.setPositiveButton("Cancel", clickListener)
                                    .setNegativeButton("Delete", clickListener)
                                    .show();
                            break;

                    }

                }
            }).show();
        }

        public void setDate(String date) {
            TextView textView = mView.findViewById(R.id.comment_date);
            textView.setText(date);
        }

        public void setRating(long rating) {
            RatingBar ratingBar = mView.findViewById(R.id.review_rating);
            ratingBar.setRating(rating);
        }

        public void setReview(String review) {
            TextView textView = mView.findViewById(R.id.comment_text);
            textView.setText(review);
        }

        public void setTime(String time) {
            TextView textView = mView.findViewById(R.id.comment_time);
            textView.setText(" at " + time);
        }

        public void setUser_image(String user_image) {
            CircleImageView circleImageView = mView.findViewById(R.id.commentUserImg);
            Picasso.get().load(user_image).into(circleImageView);
        }

        public void setUsername(String username) {
            TextView textView = mView.findViewById(R.id.comment_username);
            textView.setText(username);
        }
    }
}
