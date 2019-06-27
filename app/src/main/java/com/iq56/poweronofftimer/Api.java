package com.iq56.poweronofftimer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;

import static com.iq56.poweronofftimer.Utils.KEY_POWEROFF_TIME;
import static com.iq56.poweronofftimer.Utils.KEY_POWERON_TIME;
import static com.iq56.poweronofftimer.Utils.TAG;

public class Api {

    private static final String ACTION_SET_POWERONOFF = "android.56iq.intent.action.setpoweronoff";

    private static final String EXTRA_TIME_ON = "timeon";

    private static final String EXTRA_TIME_OFF = "timeoff";

    private static final String EXTRA_ENABLE = "enable";

    private Context mContext;

    public Api(Context context) {
        mContext = context;
    }

    /**
     * @param poweronTime [year,month,day, hour,minute]
     */
    public void sendSetPowerOnOffBroadcast(int[] poweronTime, int[] poweroffTime) {
        Log.i(TAG, "sendSetPowerOnOffBroadcast : on: " + Arrays.toString(poweronTime) + ", off: " + Arrays.toString(poweroffTime));
        Intent intent = new Intent(ACTION_SET_POWERONOFF);
        intent.putExtra(EXTRA_TIME_ON, poweronTime);
        intent.putExtra(EXTRA_TIME_OFF, poweroffTime);
        intent.putExtra(EXTRA_ENABLE, true);
        mContext.sendBroadcast(intent);
    }

    public void sendClearPowerOnOffBroadcast() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);

        String poweronTimeString = sharedPreferences.getString(KEY_POWERON_TIME, "");
        String poweroffTimeString = sharedPreferences.getString(KEY_POWEROFF_TIME, "");

        if (!TextUtils.isEmpty(poweronTimeString) && !TextUtils.isEmpty(poweroffTimeString)) {
            poweronTimeString = poweronTimeString.replace("[", "");
            poweronTimeString = poweronTimeString.replace("]", "");
            String[] timeArr = poweronTimeString.split(",");

            if (null != timeArr && timeArr.length >= 5) {
                int year = Integer.parseInt(timeArr[0].trim());
                int month = Integer.parseInt(timeArr[1].trim());
                int day = Integer.parseInt(timeArr[2].trim());
                int hour = Integer.parseInt(timeArr[3].trim());
                int minute = Integer.parseInt(timeArr[4].trim());

                int poweronTime[] = {year, month, day, hour, minute};

                poweroffTimeString = poweroffTimeString.replace("[", "");
                poweroffTimeString = poweroffTimeString.replace("]", "");
                timeArr = poweroffTimeString.split(",");

                if (null != timeArr && timeArr.length >= 5) {
                    year = Integer.parseInt(timeArr[0].trim());
                    month = Integer.parseInt(timeArr[1].trim());
                    day = Integer.parseInt(timeArr[2].trim());
                    hour = Integer.parseInt(timeArr[3].trim());
                    minute = Integer.parseInt(timeArr[4].trim());

                    int poweroffTime[] = {year, month, day, hour, minute};
                    Log.i(TAG, "sendClearPowerOnOffBroadcast : on: " + Arrays.toString(poweronTime) + ", off: " + Arrays.toString(poweroffTime));
                    Intent intent = new Intent(ACTION_SET_POWERONOFF);
                    intent.putExtra(EXTRA_TIME_ON, poweronTime);
                    intent.putExtra(EXTRA_TIME_OFF, poweroffTime);
                    intent.putExtra(EXTRA_ENABLE, false);
                    mContext.sendBroadcast(intent);
                }
            }
        }
    }
}
