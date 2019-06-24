package com.iq56.poweronofftimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, String.format("Module : %s, Version : %s, Date : %s, Publisher : %s, Revision : %s",
                "AlarmService", "1.0", "2019-6-21", "frank.zhang", "1"));

        sharedPreferences = getSharedPreferences(this.getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();

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
        if(!allowPowerOnOff) {
            Log.e(TAG, "not allowed to power on/off");
            return;
        }

        String mode = sharedPreferences.getString(KEY_MODE, "");

        if (MODE_DAILY.equals(mode)) {

            setDailyAlarm();

        } else if (MODE_WEEKLY.equals(mode)) {

            for (int i = 0; i < 7; i++) {
                boolean ret = sharedPreferences.getBoolean("repeat_" + WEEKDAYS[i], false);
                if (ret) {
                    setWeeklyAlarm(i + 1);
                }
            }
        } else {
            Toast.makeText(AlarmService.this, "Repeat mode set to daily default !", Toast.LENGTH_SHORT).show();

            editor.putString(KEY_MODE, MODE_DAILY);
            editor.commit();

            setDailyAlarm();
        }

        int powerOnHour = sharedPreferences.getInt(KEY_POWER_ON_HOUR, 7);
        int powerOnMinute = sharedPreferences.getInt(KEY_POWER_ON_MINUTE, 30);
        int powerOffHour = sharedPreferences.getInt(KEY_POWER_OFF_HOUR, 21);
        int powerOffMinute = sharedPreferences.getInt(KEY_POWER_OFF_MINUTE, 30);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int[] poweronTime = {year, month , day, powerOnHour, powerOnMinute};
        int[] poweroffTime = {year, month , day, powerOffHour, powerOffMinute};
        new Api(AlarmService.this).sendSetPowerOnOffBroadcast(poweronTime, poweroffTime);

        sharedPreferences.edit().putString(KEY_POWERON_TIME, Arrays.toString(poweronTime)).commit();
        sharedPreferences.edit().putString(KEY_POWEROFF_TIME, Arrays.toString(poweroffTime)).commit();
    }

    public void clearAllAlarmByService() {
        Utils.dump(sharedPreferences, "beforeClearAllAlarm");
        Intent powerOnIntent = new Intent(AlarmService.this, PowerOnOffAlarm.class);
        powerOnIntent.setAction(ACTION_POWER_ON);
        PendingIntent powerOnSender = PendingIntent.getBroadcast(AlarmService.this,
                REQUEST_CODE_POWER_ON_OFF, powerOnIntent, PendingIntent.FLAG_NO_CREATE);

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) AlarmService.this
                .getSystemService(Context.ALARM_SERVICE);
        am.cancel(powerOnSender);

        Intent powerOffIntent = new Intent(AlarmService.this, PowerOnOffAlarm.class);
        powerOffIntent.setAction(ACTION_POWER_OFF);
        PendingIntent powerOffSender = PendingIntent.getBroadcast(AlarmService.this,
                REQUEST_CODE_POWER_ON_OFF, powerOffIntent, PendingIntent.FLAG_NO_CREATE);

        am.cancel(powerOffSender);

        new Api(AlarmService.this).sendClearPowerOnOffBroadcast();
    }

    private void setDailyAlarm() {
        int powerOnHour = sharedPreferences.getInt(KEY_POWER_ON_HOUR, 7);
        int powerOnMinute = sharedPreferences.getInt(KEY_POWER_ON_MINUTE, 30);
        int powerOffHour = sharedPreferences.getInt(KEY_POWER_OFF_HOUR, 21);
        int powerOffMinute = sharedPreferences.getInt(KEY_POWER_OFF_MINUTE, 30);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, powerOnHour);
        calendar.set(Calendar.MINUTE, powerOnMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent powerOnIntent = new Intent(AlarmService.this, PowerOnOffAlarm.class);
        powerOnIntent.setAction(ACTION_POWER_ON);
        PendingIntent powerOnSender = PendingIntent.getBroadcast(AlarmService.this,
                REQUEST_CODE_POWER_ON_OFF, powerOnIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) AlarmService.this
                .getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                ONE_DAY_INTERVAL, powerOnSender);

        Intent powerOffIntent = new Intent(AlarmService.this, PowerOnOffAlarm.class);
        powerOffIntent.setAction(ACTION_POWER_OFF);
        PendingIntent powerOffSender = PendingIntent.getBroadcast(AlarmService.this,
                REQUEST_CODE_POWER_ON_OFF, powerOffIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        calendar.set(Calendar.HOUR_OF_DAY, powerOffHour);
        calendar.set(Calendar.MINUTE, powerOffMinute);

        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                ONE_DAY_INTERVAL, powerOffSender);


    }

    private void setWeeklyAlarm(int dayOfWeek) {
        int powerOnHour = sharedPreferences.getInt(KEY_POWER_ON_HOUR, 7);
        int powerOnMinute = sharedPreferences.getInt(KEY_POWER_ON_MINUTE, 30);
        int powerOffHour = sharedPreferences.getInt(KEY_POWER_OFF_HOUR, 21);
        int powerOffMinute = sharedPreferences.getInt(KEY_POWER_OFF_MINUTE, 30);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, powerOnHour);
        calendar.set(Calendar.MINUTE, powerOnMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent powerOnIntent = new Intent(AlarmService.this, PowerOnOffAlarm.class);
        powerOnIntent.setAction(ACTION_POWER_ON);
        PendingIntent powerOnSender = PendingIntent.getBroadcast(AlarmService.this,
                REQUEST_CODE_POWER_ON_OFF, powerOnIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) AlarmService.this
                .getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                ONE_WEEK_INTERVAL, powerOnSender);


        calendar.set(Calendar.HOUR_OF_DAY, powerOffHour);
        calendar.set(Calendar.MINUTE, powerOffMinute);

        Intent powerOffIntent = new Intent(AlarmService.this, PowerOnOffAlarm.class);
        powerOffIntent.setAction(ACTION_POWER_OFF);
        PendingIntent powerOffSender = PendingIntent.getBroadcast(AlarmService.this,
                REQUEST_CODE_POWER_ON_OFF, powerOffIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                ONE_WEEK_INTERVAL, powerOffSender);
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
