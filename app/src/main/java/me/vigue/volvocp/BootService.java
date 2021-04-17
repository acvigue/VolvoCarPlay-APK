package me.vigue.volvocp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.atomic.AtomicBoolean;

public class BootService extends Service {
    public BootService() {
    }

    private AtomicBoolean working = new AtomicBoolean(true);
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private Handler communicator;
    private View volume_dialog;
    private View audioOff_dialog;
    private View recirculatedAir_dialog;
    private View outsideAir_dialog;
    private View autoClimate_dialog;
    private View manualClimate_dialog;
    private View ecoClimate_dialog;
    private View tempAdjust_dialog;
    private final int SHOW_DIALOG = 0;
    private final int HIDE_DIALOG = 1;
    private final int VOLUME_LEVEL_UPDATE = 5;
    private final int TEMP_UPDATE = 6;
    private enum DIALOGS{VOLUME,AUDIO_OFF,RECIRCULATED_AIR,OUTSIDE_AIR,AUTO_CLIMATE,MANUAL_CLIMATE,ECO_CLIMATE,TEMP_ADJUST};

    private Runnable runnable = () -> {
        boolean ran = false;
        while(working.get()) {
            if(!ran) {
                SystemClock.sleep(3000);

                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int max_vol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max_vol, 0);

                //max call vol
                max_vol = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, max_vol, 0);

                //max ring
                max_vol = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, max_vol, 0);
                ran = true;
            } else {
                SystemClock.sleep(100);
            }
            /*
            Message m = communicator.obtainMessage(HIDE, SystemClock.uptimeMillis());
            m.sendToTarget();
            */
        }
    };

    @Override
    public void onCreate() {
        // start new thread and you your work there

        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        params = new WindowManager.LayoutParams((int)(0.5 * width), (int)(0.35 * height), WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER;

        communicator = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if(message.what == SHOW_DIALOG) {
                    //Show overlay
                    //windowManager.addView(overlay, params);
                } else if(message.what == HIDE_DIALOG) {
                    //windowManager.removeView(overlay);
                }else if(message.what == VOLUME_LEVEL_UPDATE) {
                    ProgressBar volumeBar = volume_dialog.findViewById(R.id.vol_bar);
                    volumeBar.setProgress(5*message.arg1);
                }else if(message.what == TEMP_UPDATE) {

                }
            }
        };

        new Thread(runnable).start();

        volume_dialog = View.inflate(getApplicationContext(), R.layout.volume_view, null);
        audioOff_dialog = View.inflate(getApplicationContext(), R.layout.audio_off, null);

        String NOTIFICATION_CHANNEL_ID = "me.vigue.volvocp";
        String channelName = "Volvo Integration";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Volvo Integration")
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