package com.iq56.poweronofftimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;


public class PowerOnOffAlarm extends BroadcastReceiver {

    private static final String TAG = PowerOnOffAlarm.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "onReceive: " + intent);

        if(MainActivity.ACTION_POWER_ON.equals(action)) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            int poweronTime[] = {year, month, day, hour, minute};
            new Api(context).sendSetPowerOnBroadCast(poweronTime);
        } else if(MainActivity.ACTION_POWER_OFF.equals(action)) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE) + 1;

            int poweroffTime[] = {year, month, day, hour, minute};
            new Api(context).sendSetPowerOffBroadCast(poweroffTime);
        }
    }
}
