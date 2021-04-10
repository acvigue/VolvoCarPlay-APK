package me.vigue.volvocp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Set max volume

        //Start service for USB HID
        Intent serviceIntent = new Intent(context, BootService.class);
        context.startForegroundService(serviceIntent);
    }
}