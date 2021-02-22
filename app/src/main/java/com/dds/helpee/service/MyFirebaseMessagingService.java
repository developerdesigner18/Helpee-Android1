package com.dds.helpee.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.dds.helpee.R;
import com.dds.helpee.activities.HomeActivity;
import com.dds.helpee.activities.MainActivity;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.Response;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String TAG = "notification";
    SharedPreferences pref;
    SharedPreferences.Editor et;
    int userId = 0;
    String msgId = null , title , body, loginToken = null;
    String channel_id = "HelpeeChannelId" , channel_name = "HelpeeChannelName";

    boolean showNotification = true;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        pref = getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        showNotification = pref.getBoolean(Const.NOTIFICATION, true);


        if(remoteMessage.getData() != null)
        {
            msgId = remoteMessage.getData().get("messageId");
//
            Log.e("ti" , ""+remoteMessage.getData().get("title"));
        }
        else
        {
            msgId = remoteMessage.getData().get("messageId");
        }
        title = remoteMessage.getNotification().getTitle();
        body = remoteMessage.getNotification().getBody();

        Log.e(TAG, "onMessageReceived: " + remoteMessage.getNotification().getTitle()+ " " +remoteMessage.getMessageType());
        Log.e(TAG, "onMessageReceived: " + remoteMessage.getNotification().getBody());

        Log.e("actuion",  ""+remoteMessage.getNotification().getClickAction());
        Log.e("msgId",  ""+msgId);

        if (remoteMessage.getNotification() != null)
        {
            if(showNotification == true)
            {
                sendNotiication(remoteMessage);
            }
            else
            {

            }
            Log.e(TAG, "onMessageReceived: title" + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "onMessageReceived: body" + remoteMessage.getNotification().getBody());
        }

        title = remoteMessage.getNotification().getTitle();
        body = remoteMessage.getNotification().getBody();
    }

    @Override
    public void onNewToken(String fcmtoken)
    {
        super.onNewToken(fcmtoken);

        Log.e("newToken", fcmtoken);

        pref = getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();


        userId = pref.getInt(Const.USER_ID, 0);
        loginToken = pref.getString(Const.TOKEN , null);

        et.putString(Const.FCM_TOKEN, fcmtoken);
        et.commit();
        et.apply();

        if(loginToken != null)
        {
            saveFCMToken(fcmtoken);
        }

    }
    public void saveFCMToken(String fcmtoken)
    {
        Call<Response> call = ApiClient.create_InstanceAuth(loginToken).SaveFCMToken(userId, fcmtoken);
        call.enqueue(new Callback<Response>()
        {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
            {
                if(response != null)
                {
                    if(response.isSuccessful() && response.body() != null)
                    {
                        Log.e("fcmResponse", ""+new Gson().toJson(response.body()));

                        String message = (String) response.body().getMessage();

//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String message = (String) response.body().getMessage();

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t)
            {
                Log.e("token failure", t.toString());
//                String message = (String) response.body().getMessage();
//
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void sendNotiication(RemoteMessage remoteMessage)
    {
        String action = remoteMessage.getNotification().getClickAction();
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Log.e("action" , ""+action);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.putExtra("messageId" , remoteMessage.getData().get("messageId"));
        intent.putExtra("title" , remoteMessage.getData().get("title"));
        intent.putExtra("body" , remoteMessage.getData().get("body"));

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(channel_id, channel_name,
                    importance);
            mChannel.setDescription(body);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mBuilder.setChannelId(channel_id);
            mNotifyManager.createNotificationChannel(mChannel);
        }
        mBuilder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setChannelId(channel_id)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mNotifyManager.notify(123, mBuilder.build());
    }
    public void sendNotiication1(RemoteMessage remoteMessage)
    {
        String action = remoteMessage.getNotification().getClickAction();
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Log.e("action" , ""+action);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.putExtra("messageId" , remoteMessage.getData().get("messageId"));
        intent.putExtra("title" , remoteMessage.getData().get("title"));
        intent.putExtra("body" , remoteMessage.getData().get("body"));

//        PendingIntent pendingIntent = PendingIntent.getActivity(this ,
//                0, intent,PendingIntent.FLAG_UPDATE_CURRENT);


        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(GetEmergency.class);
//        stackBuilder.addNextIntent(intent);

//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
//                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//For Android Version Orio and greater than orio.

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channel_id,channel_name, importance);
            mChannel.setDescription(body);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mNotifyManager.createNotificationChannel(mChannel);
        }
//For Android Version lower than oreo.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel_id);
        mBuilder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
//                .setColor(getResources().getColor(R.color.bk_gray))
                .setContentIntent(pendingIntent)
                .setChannelId(channel_id)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        mNotifyManager.notify(0, mBuilder.build());
    }
}
