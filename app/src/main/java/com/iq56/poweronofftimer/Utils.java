package com.iq56.poweronofftimer;

import android.content.SharedPreferences;
import android.util.Log;


public class Utils {
    /**
     * adb logcat -s PowerOnOffTimer
     */
    public static final String TAG = "PowerOnOffTimer";

    public static final String KEY_ALLOWED_POWER_ONOFF = "allowed_power_onoff";

    public static final String KEY_POWERON_TIME = "poweron_time";
    public static final String KEY_POWEROFF_TIME = "poweroff_time";

    public static final String ACTION_POWER_ON = "com.iq56.poweronofftimer.poweron";
    public static final String ACTION_POWER_OFF = "com.iq56.poweronofftimer.poweroff";


    public static final String KEY_POWER_ON_HOUR = "poweron_hour";
    public static final String KEY_POWER_ON_MINUTE = "poweron_minute";
    public static final String KEY_POWER_OFF_HOUR = "poweroff_hour";
    public static final String KEY_POWER_OFF_MINUTE = "poweroff_minute";

    public static final String KEY_MODE = "timer_mode";
    public static final String MODE_DAILY = "daily";
    public static final String MODE_WEEKLY = "weekly";

    public static final String KEY_REPEAT = "timer_repeat";
    public static final String REPEAT_SUNDAY = "repeat_sunday";
    public static final String REPEAT_MONDAY = "repeat_monday";
    public static final String REPEAT_TUESDAY = "repeat_tuesday";
    public static final String REPEAT_WEDNESDAY = "repeat_wednesday";
    public static final String REPEAT_THURSAY = "repeat_thursday";
    public static final String REPEAT_FRIDAY = "repeat_friday";
    public static final String REPEAT_SATURDAY = "repeat_saturday";

    public static final String[] WEEKDAYS = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};

    public static final int REQUEST_CODE_POWER_ON_OFF = 101;

    public static final int ONE_DAY_INTERVAL = 1000 * 60 * 60 * 24;// 24h
    public static final int ONE_WEEK_INTERVAL = ONE_DAY_INTERVAL * 7;// A WEEK


    public static void dump(SharedPreferences sharedPreferences, String info) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("=====================================");
        sb.append("\n");

        sb.append(info);
        sb.append(":\n");

        sb.append(KEY_MODE);
        sb.append(":");
        sb.append(sharedPreferences.getString(KEY_MODE, ""));
        sb.append("\n");

        sb.append(REPEAT_SUNDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_SUNDAY, false));
        sb.append("\n");

        sb.append(REPEAT_MONDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_MONDAY, false));
        sb.append("\n");

        sb.append(REPEAT_TUESDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_TUESDAY, false));
        sb.append("\n");

        sb.append(REPEAT_WEDNESDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_WEDNESDAY, false));
        sb.append("\n");

        sb.append(REPEAT_THURSAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_THURSAY, false));
        sb.append("\n");

        sb.append(REPEAT_FRIDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_FRIDAY, false));
        sb.append("\n");

        sb.append(REPEAT_SATURDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_SATURDAY, false));
        sb.append("\n");

        sb.append(KEY_POWER_ON_HOUR);
        sb.append(":");
        sb.append(sharedPreferences.getInt(KEY_POWER_ON_HOUR, -1));
        sb.append("\n");

        sb.append(KEY_POWER_ON_MINUTE);
        sb.append(":");
        sb.append(sharedPreferences.getInt(KEY_POWER_ON_MINUTE, -1));
        sb.append("\n");

        sb.append(KEY_POWER_OFF_HOUR);
        sb.append(":");
        sb.append(sharedPreferences.getInt(KEY_POWER_OFF_HOUR, -1));
        sb.append("\n");

        sb.append(KEY_POWER_OFF_MINUTE);
        sb.append(":");
        sb.append(sharedPreferences.getInt(KEY_POWER_OFF_MINUTE, -1));
        sb.append("\n");

        sb.append(KEY_ALLOWED_POWER_ONOFF);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(KEY_ALLOWED_POWER_ONOFF, false));
        sb.append("\n");

        sb.append(KEY_POWERON_TIME);
        sb.append(":");
        sb.append(sharedPreferences.getString(KEY_POWERON_TIME, ""));
        sb.append("\n");

        sb.append(KEY_POWEROFF_TIME);
        sb.append(":");
        sb.append(sharedPreferences.getString(KEY_POWEROFF_TIME, ""));
        sb.append("\n");

        sb.append("=====================================");
        sb.append("\n");
        sb.append("\n");

        Log.i(TAG, sb.toString());
    }
}
