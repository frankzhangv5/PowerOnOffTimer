package com.iq56.poweronofftimer;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;

import static com.iq56.poweronofftimer.Utils.*;

public class AlarmService extends Service {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Api mApi;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, String.format("Module : %s, Version : %s, Date : %s, Publisher : %s, Revision : %s",
                "AlarmService", "1.0", "2019-6-21", "frank.zhang", "1"));

        sharedPreferences = getSharedPreferences(this.getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mApi = new Api(this);

        Utils.dump(sharedPreferences, "AlarmService onCreate");

        setAlarmByService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {

        return new IAlarmServiceInterface.Stub() {

            @Override
            public void setAlarm() {
                Log.d(TAG, "setAlarm called");
                setAlarmByService();
            }

            @Override
            public void clearAllAlarm() {
                Log.d(TAG, "clearAllAlarm called");
                clearAllAlarmByService();
            }
        };
    }

    private void setAlarmByService() {
        Utils.dump(sharedPreferences, "beforeSetAlarm");

        boolean allowPowerOnOff = sharedPreferences.getBoolean(KEY_ALLOWED_POWER_ONOFF, false);
        if (!allowPowerOnOff) {
            Log.e(TAG, "not allowed to power on/off");
            return;
        }

        String mode = sharedPreferences.getString(KEY_MODE, "");

        if (MODE_DAILY.equals(mode)) {

            setDailyAlarm();

        } else if (MODE_WEEKLY.equals(mode)) {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            for (int i = 0; i < 7; i++) {
                int index = (i + dayOfWeek - 1) % 7;
                boolean checked = sharedPreferences.getBoolean("repeat_" + WEEKDAYS[index], false);
                if (checked) {
                    setWeeklyAlarm(i);
                    break;
                }
            }
        } else {
            Toast.makeText(AlarmService.this, "Repeat mode set to daily default !", Toast.LENGTH_SHORT).show();

            editor.putString(KEY_MODE, MODE_DAILY);
            editor.commit();

            setDailyAlarm();
        }
    }

    public void clearAllAlarmByService() {
        Utils.dump(sharedPreferences, "beforeClearAllAlarm");
        mApi.sendClearPowerOnOffBroadcast();
    }

    private void setDailyAlarm() {
        int powerOnHour = sharedPreferences.getInt(KEY_POWER_ON_HOUR, 7);
        int powerOnMinute = sharedPreferences.getInt(KEY_POWER_ON_MINUTE, 30);
        int powerOffHour = sharedPreferences.getInt(KEY_POWER_OFF_HOUR, 21);
        int powerOffMinute = sharedPreferences.getInt(KEY_POWER_OFF_MINUTE, 30);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        int[] poweronTime = {year, month, day, powerOnHour, powerOnMinute};
        int[] poweroffTime = {year, month, day, powerOffHour, powerOffMinute};

        if (powerOnHour < hour || (powerOnHour == hour && powerOnMinute <= minute)) {
            poweronTime[2] += 1;
        }

        if (powerOffHour < hour || (powerOffHour == hour && powerOffMinute < minute)) {
            poweroffTime[2] += 1;
        }

        mApi.sendClearPowerOnOffBroadcast();

        sharedPreferences.edit().putString(KEY_POWERON_TIME, Arrays.toString(poweronTime)).commit();
        sharedPreferences.edit().putString(KEY_POWEROFF_TIME, Arrays.toString(poweroffTime)).commit();

        mApi.sendSetPowerOnOffBroadcast(poweronTime, poweroffTime);
    }

    private void setWeeklyAlarm(int deltaDays) {
        int powerOnHour = sharedPreferences.getInt(KEY_POWER_ON_HOUR, 7);
        int powerOnMinute = sharedPreferences.getInt(KEY_POWER_ON_MINUTE, 30);
        int powerOffHour = sharedPreferences.getInt(KEY_POWER_OFF_HOUR, 21);
        int powerOffMinute = sharedPreferences.getInt(KEY_POWER_OFF_MINUTE, 30);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH) + deltaDays;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        int[] poweronTime = {year, month, day, powerOnHour, powerOnMinute};
        int[] poweroffTime = {year, month, day, powerOffHour, powerOffMinute};

        if (deltaDays == 0) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int delta = 0;
            for (int i = 0; i < 7; i++) {
                int index = (i + dayOfWeek - 1) % 7;
                boolean checked = sharedPreferences.getBoolean("repeat_" + WEEKDAYS[index], false);
                if (checked && i > 0) {
                    delta = i;
                    break;
                }
            }
            if (powerOnHour < hour || (powerOnHour == hour && powerOnMinute <= minute)) {
                poweronTime[2] += delta;
            }

            if (powerOffHour < hour || (powerOffHour == hour && powerOffMinute < minute)) {
                poweroffTime[2] += delta;
            }
        }

        mApi.sendClearPowerOnOffBroadcast();

        sharedPreferences.edit().putString(KEY_POWERON_TIME, Arrays.toString(poweronTime)).commit();
        sharedPreferences.edit().putString(KEY_POWEROFF_TIME, Arrays.toString(poweroffTime)).commit();

        mApi.sendSetPowerOnOffBroadcast(poweronTime, poweroffTime);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "on destroy");

        super.onDestroy();
    }
}
