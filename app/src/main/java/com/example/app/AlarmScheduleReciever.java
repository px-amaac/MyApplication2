package com.example.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by ShaDynastys on 2/28/14.
 */
public class AlarmScheduleReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctxt, Intent i){
        scheduleAlarms(ctxt);
    }

    static void scheduleAlarms(Context ctxt){
        AlarmManager manager = (AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ctxt, NotifyService.class);
        PendingIntent pi = PendingIntent.getService(ctxt, 0, i, 0); //context, requestcode, pending intent, intent flags.
        //manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000* 100, pi);
        Toast.makeText(ctxt, "AlarmScheduled", Toast.LENGTH_SHORT).show();
    }
}
