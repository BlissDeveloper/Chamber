package com.example.avery.chamberofwizards.Prelims;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.avery.chamberofwizards.R;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String message_title = remoteMessage.getNotification().getTitle();
        String message_body = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();
        String post_key = null;
        CharSequence name = getString(R.string.chamber_channel);
        //For forum data:
        if (remoteMessage.getData().get("postKey") != null) {
            post_key = remoteMessage.getData().get("postKey");
        }

        int notification_id = (int) System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Device is in oreo or higher
            NotificationChannel mChannel = new NotificationChannel(getString(R.string.chamber_channel), name, NotificationManager.IMPORTANCE_DEFAULT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.chamber_channel))
                    .setSmallIcon(R.mipmap.application_logo_trans)
                    .setContentTitle("Chamber of Wizards")
                    .setContentText(message_body);

            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("postKey", post_key);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(mChannel);
            notificationManager.notify(notification_id, mBuilder.build());
        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.application_logo_trans)
                    .setContentTitle("Chamber of Wizards")
                    .setContentText(message_body);

            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("postKey", post_key);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notification_id, mBuilder.build());
        }
    }
}
