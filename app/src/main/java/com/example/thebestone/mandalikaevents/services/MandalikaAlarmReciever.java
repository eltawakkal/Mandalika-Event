package com.example.thebestone.mandalikaevents.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.thebestone.mandalikaevents.R;
import com.example.thebestone.mandalikaevents.activity.AktifitasUtama;

public class MandalikaAlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        context.startService(new Intent(context, MandalikaService.class));

        Intent intentHome = new Intent(context, AktifitasUtama.class);

        intentHome.putExtra("STATUS", 1);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentHome, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo_app)
                        .setContentTitle("Pengingat Agenda")
                        .setContentText("Ketuk Untuk Melihat List Agendamu")
                        .setContentIntent(pendingIntent);

        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());

    }
}
