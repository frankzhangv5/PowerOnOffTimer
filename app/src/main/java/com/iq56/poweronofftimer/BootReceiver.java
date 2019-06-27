package com.iq56.poweronofftimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.iq56.poweronofftimer.Utils.TAG;

public class BootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.i(TAG, "onReceive: " + intent);

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Log.i(TAG, "starting AlarmService ...");
            context.startService(new Intent(context, AlarmService.class));
        }
    }

}
