package com.example.avery.chamberofwizards.Games.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.avery.chamberofwizards.Books.ClickBookActivity;
import com.example.avery.chamberofwizards.BuildConfig;
import com.example.avery.chamberofwizards.Forum.MainActivity;
import com.example.avery.chamberofwizards.Games.GameReviews;
import com.example.avery.chamberofwizards.Games.Games;
import com.example.avery.chamberofwizards.Games.MainGamesActivity;
import com.example.avery.chamberofwizards.Games.MyFileProvider;
import com.example.avery.chamberofwizards.Games.Screenshots;
import com.example.avery.chamberofwizards.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class AllGamesFragment extends Fragment {
    private View mView;

    private CollectionReference gamesRef;
    private CollectionReference downloadedGamesRef;
    private FirestoreRecyclerAdapter<Games, GamesViewHolder> firestoreRecyclerAdapter;
    public static FirestoreRecyclerAdapter<Screenshots, GamesViewHolder.ScreenshotsViewHolder> screenshotsRecyclerAdapter;
    private static ListenerRegistration ratingBarListener;
    private static ListenerRegistration numberOfReviewsListener;

    //Views
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager reviewsLayoutManager;

    static final int NOTIFY_ID = 26;
    static final String CHANNEL_ID = "chamber_id_01";
    static CharSequence name;

    static NotificationCompat.Builder notification;
    static NotificationChannel mChannel;

    private String gameK;

    private static NotificationManager notificationManager;


    public AllGamesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_all_games, container, false);

        gamesRef = FirebaseFirestore.getInstance().collection("Games");
        downloadedGamesRef = FirebaseFirestore.getInstance().collection("Downloaded Games");

        //Views
        recyclerView = mView.findViewById(R.id.recyclerViewGames);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        reviewsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        //Recycler view setup
        recyclerView.setLayoutManager(linearLayoutManager);

        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        name = getString(R.string.chamber_channel);

        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d("Avery", "Install success");
            addInstalledAppIntoFirestore();

        } else {
            Log.d("Avery", "Install unsucessfull");
        }
    }

    public void addInstalledAppIntoFirestore() {
        String android_id = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        final Map<String, Object> map = new ArrayMap<>();
        map.put("installed_on", android_id);

        Query query = downloadedGamesRef.whereEqualTo("game_id", gameK);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        String id = d.getId();
                        downloadedGamesRef.document(id).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Avery", "Success");
                                } else {
                                    Log.d("Avery", task.getException().getMessage());
                                }
                            }
                        });
                    }
                } else {
                    Log.e("Avery", "Query document snapshots does not exist");
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        loadGames();
    }

    @Override
    public void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();

        if (ratingBarListener != null) {
            ratingBarListener.remove();
        }
    }

    public static class GamesViewHolder extends RecyclerView.ViewHolder {
        View V;

        CollectionReference screenshotsRef;
        CollectionReference downloadedGameRef;
        CollectionReference gameRef;
        CollectionReference gameReviewsRef;
        private FirebaseAuth mAuth;
        private String currentUserID;

        ImageView imgViewGameTitle;
        CardView cardviewGameInfo;
        RecyclerView screenshotsRecyclerView;
        LinearLayoutManager layoutManager;
        CircleImageView btnAddGameReview;
        RatingBar ratingBarGameRating;
        Button btnGameReviews;
        CardView cardviewGameScreenshots;
        CardView cardviewGameReviews;
        TextView textViewNumberOfReviews;
        Button btnGameScreenshots;

        RecyclerView recyclerViewGameReviews;
        LinearLayoutManager linearLayoutManagerGameReviews;
        LinearLayoutManager linear;

        Button btnDownloadGame;


        public GamesViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);
            V = itemView;

            mAuth = FirebaseAuth.getInstance();
            currentUserID = mAuth.getCurrentUser().getUid();

            screenshotsRef = FirebaseFirestore.getInstance().collection("Screenshots");
            downloadedGameRef = FirebaseFirestore.getInstance().collection("Downloaded Games");
            gameRef = FirebaseFirestore.getInstance().collection("Games");
            gameReviewsRef = FirebaseFirestore.getInstance().collection("Game Reviews");

            imgViewGameTitle = V.findViewById(R.id.gamesLayoutImgViewTitle);
            cardviewGameInfo = V.findViewById(R.id.cardViewGameInfo);
            screenshotsRecyclerView = V.findViewById(R.id.recyclerViewGameScreeshots);
            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            btnAddGameReview = V.findViewById(R.id.btnAddGameReview);
            ratingBarGameRating = V.findViewById(R.id.ratingBarGameRating);
            btnGameReviews = V.findViewById(R.id.btnGameComments);
            cardviewGameScreenshots = V.findViewById(R.id.cardviewScreenshots);
            cardviewGameReviews = V.findViewById(R.id.cardviewComments);
            recyclerViewGameReviews = V.findViewById(R.id.recyclerViewGameReviews);
            linearLayoutManagerGameReviews = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            textViewNumberOfReviews = V.findViewById(R.id.txtGameNumberOfReviews);
            btnGameScreenshots = V.findViewById(R.id.btnGameScreenshots);

            btnDownloadGame = V.findViewById(R.id.btnGameDownload);

            //Recycler setup
            screenshotsRecyclerView.setLayoutManager(layoutManager);
            recyclerViewGameReviews.setLayoutManager(linearLayoutManagerGameReviews);
        }

        public static class GameReviewsViewHolder extends RecyclerView.ViewHolder {
            private View V;

            CircleImageView reviewerImage;
            TextView reviewerName;
            TextView reviewerReview;
            RatingBar reviewerGameRating;

            public GameReviewsViewHolder(@NonNull View itemView) {
                super(itemView);
                V = itemView;

                reviewerImage = V.findViewById(R.id.reviewer_image);
                reviewerName = V.findViewById(R.id.reviewer_name);
                reviewerGameRating = V.findViewById(R.id.reviewer_game_rating);
                reviewerReview = V.findViewById(R.id.reviewer_review);
            }

            public void setGame_review(String game_review) {
                reviewerReview.setText(game_review);
            }

            public void setGame_reviewer_image(String game_reviewer_image) {
                Picasso.get().load(game_reviewer_image).into(reviewerImage);
            }

            public void setGame_reviewer_name(String game_reviewer_name) {
                reviewerName.setText(game_reviewer_name);
            }

            public void setGame_rating(float game_rating) {
                reviewerGameRating.setRating(game_rating);
            }
        }

        public void maintainNumberOfReviews(String gameKey) {
            numberOfReviewsListener = gameRef.document(gameKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists() && e == null) {
                        int n = Integer.parseInt(documentSnapshot.get("number_of_reviews").toString());
                        if (n > 0) {
                            if (n > 1) {
                                textViewNumberOfReviews.setText(String.valueOf(n) + " reviews");
                            } else {
                                textViewNumberOfReviews.setText(String.valueOf(n) + " review");
                            }
                        } else {
                            textViewNumberOfReviews.setText("No reviews yet");
                        }
                    }
                }
            });
        }

        public void loadReviews(String gameKey) {
            Query query = gameReviewsRef.whereEqualTo("gameKey", gameKey);

            FirestoreRecyclerOptions<GameReviews> options = new FirestoreRecyclerOptions.Builder<GameReviews>().setQuery(query, GameReviews.class).build();

            FirestoreRecyclerAdapter<GameReviews, GameReviewsViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<GameReviews, GameReviewsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull GameReviewsViewHolder holder, int position, @NonNull GameReviews model) {
                    holder.setGame_rating(model.getGame_rating());
                    holder.setGame_review(model.getGame_review());
                    holder.setGame_reviewer_image(model.getGame_reviewer_image());
                    holder.setGame_reviewer_name(model.getGame_reviewer_name());
                }

                @NonNull
                @Override
                public GameReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_game_reviews_layout, parent, false);
                    return new GameReviewsViewHolder(view);
                }
            };
            firestoreRecyclerAdapter.startListening();
            recyclerViewGameReviews.setAdapter(firestoreRecyclerAdapter);
        }

        public void maintainRatingBar(String gameKey) {
            ratingBarListener = gameRef.document(gameKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists() && e == null) {
                        Float rating = Float.parseFloat(documentSnapshot.get("average_rating").toString());

                        ratingBarGameRating.setRating(rating);
                    } else {
                        Log.e("Avery", e.getMessage());
                    }
                }
            });
        }

        public static final class DownloadGameTask extends AsyncTask<String, Integer, String[]> {
            private Context mContext;
            private CollectionReference gameRef;
            private CollectionReference downloadedGameRef;
            private FirebaseAuth mAuth;
            private String currentUserID;
            private Fragment mFragment;

            private final int APP_INSTALL_REQUEST = 23;

            DownloadGameTask(Context context, Fragment fragment) {
                mContext = context;
                mFragment = fragment;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                //Kaylangan ng ID for Oreo above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //Kapag oreo or above ang verion ng phone
                    mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

                    mChannel.setVibrationPattern(new long[]{0});
                    mChannel.enableVibration(true);

                    notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                            .setContentTitle("Chamber of Wizards")
                            .setContentText("Downloading game")
                            .setVibrate(new long[]{0L})
                            .setSmallIcon(R.drawable.download_icon)
                            .setProgress(0, 0, false);

                    notificationManager.createNotificationChannel(mChannel);
                } else {
                    notification = new NotificationCompat.Builder(mContext)
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                            .setContentTitle("Chamber of Wizards")
                            .setContentText("Downloading game")
                            .setVibrate(new long[]{0L})
                            .setSmallIcon(R.drawable.download_icon)
                            .setProgress(0, 0, false);

                    notificationManager.notify(NOTIFY_ID, notification.build());
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

                Log.d("Avery", values[0] + "");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    mChannel.setVibrationPattern(new long[]{0});
                    mChannel.enableVibration(true);

                    notification.setProgress(100, values[0], false);

                    //notificationManager.createNotificationChannel(mChannel);

                    notificationManager.notify(NOTIFY_ID, notification.build());
                } else {
                    notification.setProgress(100, values[0], false);
                    notificationManager.notify(NOTIFY_ID, notification.build());
                }
            }

            @Override
            protected String[] doInBackground(String... strings) {
                Log.d("Avery", "Starting Async");

                File gameFile;

                //final String path = Environment.getExternalStorageDirectory() + "//Download//" + "//ChamberDownloads//Books/" + "/" + strings[0] + "//" + strings[0] + ".apk";
                final String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String fileSize = strings[1]; //Eto yung galing sa params ng Asynctask
                Log.d("Avery", Environment.DIRECTORY_DOWNLOADS);
                int count;

                try {
                    URL url = new URL(strings[0]);
                    URLConnection connection = url.openConnection();

                    gameFile = new File(absolutePath, strings[2] + ".apk");

                    if (!gameFile.exists()) {
                        gameFile.getParentFile().mkdirs();
                        gameFile.createNewFile();
                        Log.d("Avery", "APK does not exists");
                    } else {
                        Log.d("Avery", "APK  exists");
                    }

                    //int lengthOfFile = connection.getContentLength();
                    long lengthOfFile = (long) connection.getContentLength();
                    Log.d("Avery", "Length of file: " + lengthOfFile);

                    InputStream inputStream = new BufferedInputStream(url.openStream());
                    OutputStream outputStream = new FileOutputStream(gameFile);

                    byte data[] = new byte[(int) lengthOfFile]; ///

                    long total = 0;
                    int progress = 0;

                    while ((count = inputStream.read(data)) != -1) {
                        Math.abs(total += count);
                        long progress_temp = Math.abs((total * 100) / lengthOfFile);

                        publishProgress((int) progress_temp);
                        // Log.d("Avery", String.valueOf(total));
                        outputStream.write(data, 0, count);
                    }

                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    Log.e("Avery", e.getMessage());
                }

                String s[] = new String[3];
                s[0] = "finished";
                s[1] = strings[2];
                s[2] = absolutePath;

                return s;
            }

            @Override
            protected void onPostExecute(String s[]) {
                super.onPostExecute(s);

                Log.d("Avery", "Postexecute");

                gameRef = FirebaseFirestore.getInstance().collection("Games");
                downloadedGameRef = FirebaseFirestore.getInstance().collection("Downloaded Games");
                mAuth = FirebaseAuth.getInstance();
                currentUserID = mAuth.getCurrentUser().getUid();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (s[0].equals("finished")) {

                        mChannel.setVibrationPattern(new long[]{0});
                        mChannel.enableVibration(true);

                        notification.setProgress(100, 0, false)
                                .setContentTitle("Chamber of Wizards")
                                .setContentText("Download finished");

                        //notificationManager.createNotificationChannel(mChannel);

                        notificationManager.notify(NOTIFY_ID, notification.build());

                        saveDownloadedGameToFirestore(s[1], s[2]);
                    }
                }
            }

            public void saveDownloadedGameToFirestore(final String gameKey, final String path) {
                gameRef.document(gameKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            final String id = downloadedGameRef.document().getId();
                            downloadedGameRef.document(id).set(documentSnapshot.getData()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                                                Settings.Secure.ANDROID_ID);

                                        Map<String, Object> map = new ArrayMap<>();
                                        map.put("downloaded_by", currentUserID);
                                        map.put("game_id", gameKey);
                                        map.put("downloaded_on", android_id);
                                        map.put("installed_on", null);
                                        downloadedGameRef.document(id).update(map);

                                        showAlertDialog(gameKey, path);
                                    }
                                }
                            });
                        }
                    }
                });
            }

            public void showAlertDialog(final String gameKey, final String path) {
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Install
                                installGame(gameKey, path);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //Later
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Install game")
                        .setPositiveButton("Install", clickListener)
                        .setNegativeButton("Install later", clickListener)
                        .show();
            }

            public void installGame(String gameKey, final String path) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    /*
                    FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".HelperClasses.GenericFileProvider"
                , file);
                     */
                    final String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File apkFile = new File(absolutePath, gameKey + ".apk");

                    if (apkFile.exists()) {
                        Log.d("Avery", "APK File exists.");
                        Uri apkUri = FileProvider.getUriForFile(mContext.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", apkFile);
                        // Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(downloaded_apk,
                        //                                "application/vnd.android.package-archive");
                        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE).setDataAndType(apkUri, "application/vnd.android.package-archive");
                        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        mFragment.startActivityForResult(intent, APP_INSTALL_REQUEST);
                        //java.lang.IllegalStateException: Fragment AllGamesFragment{3bbdfb0} not attached to Activity
                    } else {
                        Log.d("Avery", "APK File does not exists.");
                    }


                } else {

                }
            }
        }

        public static class ScreenshotsViewHolder extends RecyclerView.ViewHolder {
            View view;

            ImageView imgViewScreenshot;

            public ScreenshotsViewHolder(@NonNull View item) {
                super(item);
                view = item;

                imgViewScreenshot = view.findViewById(R.id.imgViewScreenshotLayout);
            }

            public void setScreenshot_url(String screenshot_url) {
                Picasso.get().load(screenshot_url).into(imgViewScreenshot);
            }
        }

        public void openGame(String gameKey, final Fragment mFragment) {
            gameRef.document(gameKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String package_name = documentSnapshot.getString("package_name");

                        try {
                            PackageManager pm = mFragment.getActivity().getPackageManager();
                            Intent intent = pm.getLaunchIntentForPackage(package_name);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            mFragment.startActivity(intent);
                        } catch (Exception e) {
                            Log.e("Avery", e.getMessage());
                        }
                    }
                }
            });
        }

        public void installGame(final String gameKey, final Fragment mFragment, final Context mContext) {
            final int APP_INSTALL_REQUEST = 23;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    /*
                    FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".HelperClasses.GenericFileProvider"
                , file);
                     */
                final String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File apkFile = new File(absolutePath, gameKey + ".apk");

                if (apkFile.exists()) {
                    Log.d("Avery", "APK File exists.");
                    Uri apkUri = FileProvider.getUriForFile(mContext.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", apkFile);
                    // Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(downloaded_apk,
                    //                                "application/vnd.android.package-archive");
                    Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE).setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    mFragment.startActivityForResult(intent, APP_INSTALL_REQUEST);
                    //java.lang.IllegalStateException: Fragment AllGamesFragment{3bbdfb0} not attached to Activity
                } else {
                    Log.d("Avery", "APK File does not exists.");
                }


            } else {

            }
        }

        public void loadScreenshots(String game_id) {
            Query query = screenshotsRef.whereEqualTo("game_id", game_id);

            FirestoreRecyclerOptions<Screenshots> options = new FirestoreRecyclerOptions.Builder<Screenshots>().setQuery(query, Screenshots.class).build();

            screenshotsRecyclerAdapter = new FirestoreRecyclerAdapter<Screenshots, ScreenshotsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ScreenshotsViewHolder holder, int position, @NonNull Screenshots model) {
                    holder.setScreenshot_url(model.getScreenshot_url());
                }

                @NonNull
                @Override
                public ScreenshotsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_screenshots_layout, parent, false);
                    return new ScreenshotsViewHolder(view);
                }
            };
            screenshotsRecyclerAdapter.startListening();
            screenshotsRecyclerView.setAdapter(screenshotsRecyclerAdapter);
        }

        public void maintainDownloadButton(String game_id, final Context context, final String package_name) {
            final Query query = downloadedGameRef.whereEqualTo("game_id", game_id).whereEqualTo("downloaded_by", currentUserID);


            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                String mId;

                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        mId = d.getId();

                        downloadedGameRef.document(mId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (isAppInstalled(context, package_name)) {
                                    //Installed na
                                    btnDownloadGame.setText("Open");
                                    btnDownloadGame.setCompoundDrawablesWithIntrinsicBounds(R.drawable.open_icon, 0, 0, 0);
                                } else {
                                    if (documentSnapshot.exists() && e == null) {
                                        String installed_on = documentSnapshot.getString("installed_on");
                                        String downloaded_on = documentSnapshot.getString("downloaded_on");

                                        if (installed_on == null) {
                                            //It means na di pa naiinstall sa device, pero na-download na, kasi meron nang data sa database eh.
                                            if (downloaded_on == null) {
                                                //Hindi installed and downloaded
                                                btnDownloadGame.setText("Download");
                                                btnDownloadGame.setCompoundDrawablesWithIntrinsicBounds(R.drawable.install_icon, 0, 0, 0);
                                            } else {
                                                //Hindi installed, pero downloaded.
                                                btnDownloadGame.setText("Install");
                                            }
                                        } else {
                                            //Installed
                                            btnDownloadGame.setText("Open");
                                            btnDownloadGame.setCompoundDrawablesWithIntrinsicBounds(R.drawable.open_icon, 0, 0, 0);
                                        }
                                    } else {
                                        Log.e("Avery", e.getMessage());
                                    }
                                }
                            }
                        });
                    }


                }
            });
        }

        public static boolean isAppInstalled(Context context, String packageName) {
            try {
                context.getPackageManager().getApplicationInfo(packageName, 0);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }

        public void hideGameInfo() {
            cardviewGameInfo.setVisibility(View.GONE);
        }

        public void showGameInfo() {
            cardviewGameInfo.setVisibility(View.VISIBLE);
        }

        public void setTitle_screen_image(String title_screen_image) {
            Picasso.get().load(title_screen_image).into(imgViewGameTitle);
        }
    }

    public void loadGames() {
        Query query = gamesRef;

        FirestoreRecyclerOptions<Games> options = new FirestoreRecyclerOptions.Builder<Games>().setQuery(query, Games.class).build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Games, GamesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final GamesViewHolder holder, int position, final @NonNull Games model) {
                final String gameKey = getSnapshots().getSnapshot(position).getId();

                holder.loadScreenshots(gameKey);

                holder.btnGameScreenshots.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.cardviewGameScreenshots.setVisibility(View.VISIBLE);
                        holder.cardviewGameReviews.setVisibility(View.GONE);
                        holder.loadScreenshots(gameKey);
                    }
                });

                holder.maintainNumberOfReviews(gameKey);

                holder.btnGameReviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.cardviewGameScreenshots.setVisibility(View.GONE);
                        holder.cardviewGameReviews.setVisibility(View.VISIBLE);
                        holder.loadReviews(gameKey);
                    }
                });

                holder.maintainRatingBar(gameKey);

                holder.maintainDownloadButton(gameKey, getActivity().getApplicationContext(), model.getPackage_name());

                holder.btnAddGameReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainGamesActivity.cardviewGameReview.setVisibility(View.VISIBLE);
                        MainGamesActivity.selectedGsmeKey = gameKey;
                        Log.d("Avery", MainGamesActivity.selectedGsmeKey);

                    }
                });

                holder.btnDownloadGame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.btnDownloadGame.getText().equals("Open")) {
                            holder.openGame(gameKey, (Fragment) AllGamesFragment.this);

                        } else if (holder.btnDownloadGame.getText().equals("Download")) {
                            Dialog.OnClickListener listener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case Dialog.BUTTON_POSITIVE:
                                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                                holder.btnDownloadGame.setEnabled(false);
                                                gameK = gameKey;
                                                holder.btnDownloadGame.setText("Downloading");
                                                Log.d("Avery", model.getDownload_link());
                                                new GamesViewHolder.DownloadGameTask(getActivity(), (Fragment) AllGamesFragment.this).execute(model.getDownload_link(), Long.toString(model.getFileSize()), gameKey);
                                                holder.btnDownloadGame.setEnabled(true);
                                            } else {
                                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9);
                                            }
                                            break;
                                        case Dialog.BUTTON_NEGATIVE:
                                            dialog.dismiss();
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                            b.setTitle("Download Game")
                                    .setNegativeButton("Cancel", listener)
                                    .setPositiveButton("Download", listener)
                                    .show();
                        } else if (holder.btnDownloadGame.getText().equals("Install")) {
                            holder.installGame(gameKey, (Fragment) AllGamesFragment.this, getActivity());
                        }
                    }
                });

                holder.imgViewGameTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.cardviewGameInfo.getVisibility() == View.GONE) {
                            //Naka-hide and card
                            holder.showGameInfo();
                            holder.loadScreenshots(gameKey);
                        } else if (holder.cardviewGameInfo.getVisibility() == View.VISIBLE) {
                            //Naka-show yung card:
                            holder.hideGameInfo();
                        }
                    }
                });

                holder.setTitle_screen_image(model.getTitle_screen_image());
            }

            @NonNull
            @Override
            public GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_game_titles_layout, parent, false);
                return new GamesViewHolder(view, getActivity());
            }
        };
        firestoreRecyclerAdapter.startListening();
        recyclerView.setAdapter(firestoreRecyclerAdapter);
    }
}
