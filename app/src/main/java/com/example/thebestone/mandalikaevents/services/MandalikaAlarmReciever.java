package com.example.thebestone.mandalikaevents.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MandalikaAlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Alarm", Toast.LENGTH_LONG).show();

        context.startService(new Intent(context, MandalikaService.class));

    }
}
