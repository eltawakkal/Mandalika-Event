package com.example.thebestone.mandalikaevents.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Iterator;
import java.util.Set;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
// To get set of all registered ids on boot.
        Set<String> set = SimpleAlarmManager.getAllRegistrationIds(context);
        for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
            int id = Integer.parseInt(it.next());
            //to initialize with registreation id
            SimpleAlarmManager.initWithId(context, id).start();
        }
    }
}
