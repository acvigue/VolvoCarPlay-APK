package me.vigue.volvocp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.atomic.AtomicBoolean;

public class BootService extends Service {
    public BootService() {
    }

    private AtomicBoolean working = new AtomicBoolean(true);

    private Runnable runnable = () -> {
        while(working.get()) {
            SystemClock.sleep(1000);
        }
    };

    @Override
    public void onCreate() {
        // start new thread and you your work there
        //new Thread(runnable).start();

        //max music vol
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max_vol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max_vol, 0);

        //max call vol
        max_vol = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, max_vol, 0);

        //max ring
        max_vol = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, max_vol, 0);

        String NOTIFICATION_CHANNEL_ID = "me.vigue.volvocp";
        String channelName = "ATSAMD Serial Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setContentTitle("volvocp")
                .setContentIntent(pendingIntent).build();
        startForeground(1337, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        working.set(false);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}