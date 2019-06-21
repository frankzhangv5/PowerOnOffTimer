package com.iq56.poweronofftimer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Api {

    private static final String TAG = Api.class.getSimpleName();

    private static final String INTENT_SET_POWERONOFF = "android.56iq.intent.action.setpoweronoff";

    private static final String EXTRA_TIME_ON = "timeon";

    private static final String EXTRA_TIME_OFF = "timeoff";

    private static final String EXTRA_ENABLE = "enable";

    private Context mContext;

    public Api(Context context) {
        mContext = context;
    }

    /**
     * @param datetime [year,month,day, hour,minute]
     */
    public void sendSetPowerOnBroadCast(int[] datetime) {
        Log.i(TAG, "sendSetPowerOnBroadCast : " + datetime);
        Intent intent = new Intent(INTENT_SET_POWERONOFF);
        intent.putExtra(EXTRA_TIME_ON,datetime);
        intent.putExtra( EXTRA_ENABLE ,true);
        mContext.sendBroadcast(intent);
    }

    /**
     * @param datetime [year,month,day, hour,minute]
     */
    public void sendSetPowerOffBroadCast(int[] datetime) {
        Log.i(TAG, "sendSetPowerOnBroadCast : " + datetime);
        Intent intent = new Intent(INTENT_SET_POWERONOFF);
        intent.putExtra(EXTRA_TIME_OFF,datetime);
        intent.putExtra( EXTRA_ENABLE ,true);
        mContext.sendBroadcast(intent);
    }
}
