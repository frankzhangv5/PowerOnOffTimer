package com.iq56.poweronofftimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;

import static com.iq56.poweronofftimer.Utils.KEY_ALLOWED_POWER_ONOFF;
import static com.iq56.poweronofftimer.Utils.TAG;
import static com.iq56.poweronofftimer.Utils.ACTION_POWER_ON;
import static com.iq56.poweronofftimer.Utils.ACTION_POWER_OFF;
import static com.iq56.poweronofftimer.Utils.KEY_POWERON_TIME;
import static com.iq56.poweronofftimer.Utils.KEY_POWEROFF_TIME;

public class PowerOnOffAlarm extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive: " + intent);

        if (ACTION_POWER_ON.equals(action)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
            boolean allowPowerOnOff = sharedPreferences.getBoolean(KEY_ALLOWED_POWER_ONOFF, false);
            if (!allowPowerOnOff) {
                Log.e(TAG, "not allowed to power on/off");
                return;
            }

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            int poweronTime[] = {year, month, day, hour, minute};

            sharedPreferences.edit().putString(KEY_POWERON_TIME, Arrays.toString(poweronTime)).commit();

            new Api(context).sendSetPowerOnBroadcast(poweronTime);
        } else if (ACTION_POWER_OFF.equals(action)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
            boolean allowPowerOnOff = sharedPreferences.getBoolean(KEY_ALLOWED_POWER_ONOFF, false);
            if (!allowPowerOnOff) {
                Log.e(TAG, "not allowed to power on/off");
                return;
            }

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE) + 1;

            int poweroffTime[] = {year, month, day, hour, minute};

            sharedPreferences.edit().putString(KEY_POWEROFF_TIME, Arrays.toString(poweroffTime)).commit();

            new Api(context).sendSetPowerOffBroadcast(poweroffTime);
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Log.i(TAG, "starting AlarmService ...");
            context.startService(new Intent(context, AlarmService.class));
        }
    }

}
