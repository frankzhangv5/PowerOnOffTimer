package com.iq56.poweronofftimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements OnClickListener, CompoundButton.OnCheckedChangeListener {


    private static final String TAG = "PowerOnOffAlarm";

    private Button btnSelectPowerOnTime, btnSelectPowerOffTime, btnSubmit, btnClear;

    private RadioGroup radioGroupMode;

    private CheckBox btnSunday, btnMonday, btnTuesDay, btnWednesday, btnThursday, btnFriday, btnSaturday;

    private TextView txtPowerOnTime, txtPowerOffTime;

    public static final String ACTION_POWER_ON = "com.iq56.poweronofftimer.poweron";
    public static final String ACTION_POWER_OFF = "com.iq56.poweronofftimer.poweroff";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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

    private static final int ONE_DAY_INTERVAL = 1000 * 60 * 60 * 24;// 24h
    private static final int ONE_WEEK_INTERVAL = ONE_DAY_INTERVAL * 7;// A WEEK

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(this.getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnSelectPowerOnTime = (Button)findViewById(R.id.btn_select_poweron_time);
        btnSelectPowerOffTime = (Button)findViewById(R.id.btn_select_poweroff_time);

        btnSelectPowerOnTime.setOnClickListener(this);
        btnSelectPowerOffTime.setOnClickListener(this);

        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnClear = (Button)findViewById(R.id.btn_clear);

        btnSubmit.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        radioGroupMode = (RadioGroup)findViewById(R.id.mode_group);

        btnSunday = (CheckBox)findViewById(R.id.cb_sunday);
        btnMonday = (CheckBox)findViewById(R.id.cb_monday);
        btnTuesDay = (CheckBox)findViewById(R.id.cb_tuesday);
        btnWednesday = (CheckBox)findViewById(R.id.cb_wednesday);
        btnThursday = (CheckBox)findViewById(R.id.cb_thursday);
        btnFriday = (CheckBox)findViewById(R.id.cb_friday);
        btnSaturday = (CheckBox)findViewById(R.id.cb_saturday);

        final CheckBox[] weekdayCheckBoxs = {btnSunday, btnMonday, btnTuesDay, btnWednesday, btnThursday, btnFriday, btnSaturday};

        String mode = sharedPreferences.getString(KEY_MODE, "");
        if(MODE_DAILY.equals(mode)) {
            radioGroupMode.check(R.id.btn_daily);
        } else if(MODE_WEEKLY.equals(mode)) {
            radioGroupMode.check(R.id.btn_weekly);
        } else {
            radioGroupMode.check(R.id.btn_daily);
        }

        radioGroupMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(R.id.btn_weekly == checkedId) {

                    for(int i = 0; i < 7; i++) {
                        weekdayCheckBoxs[i].setClickable(true);
                    }

                    editor.putString(KEY_MODE, MODE_WEEKLY);
                    editor.commit();
                } else {
                    for(int i = 0; i < 7; i++) {
                        weekdayCheckBoxs[i].setClickable(false);
                    }

                    editor.putString(KEY_MODE, MODE_DAILY);
                    editor.commit();
                }
            }
        });



        for(int i = 0; i < 7; i++) {
            weekdayCheckBoxs[i].setOnCheckedChangeListener(this);
        }

        for(int i = 0; i < 7; i++) {
            boolean ret = sharedPreferences.getBoolean("repeat_" + WEEKDAYS[i], false);
            if(ret) {
                weekdayCheckBoxs[i].setChecked(true);
            }
        }


        if(! MODE_WEEKLY.equals(mode)) {
            for(int i = 0; i < 7; i++) {
                weekdayCheckBoxs[i].setClickable(false);
            }
        }


        txtPowerOnTime = (TextView) findViewById(R.id.txt_poweron_time);
        txtPowerOffTime = (TextView) findViewById(R.id.txt_poweroff_time);

        int powerOnHour = sharedPreferences.getInt(KEY_POWER_ON_HOUR, 7);
        int powerOnMinute = sharedPreferences.getInt(KEY_POWER_ON_MINUTE, 30);
        int powerOffHour = sharedPreferences.getInt(KEY_POWER_OFF_HOUR, 21);
        int powerOffMinute = sharedPreferences.getInt(KEY_POWER_OFF_MINUTE, 30);

        txtPowerOnTime.setText(String.format("%2d:%2d", powerOnHour, powerOnMinute));
        txtPowerOffTime.setText(String.format("%2d:%2d", powerOffHour, powerOffMinute));

        dump("onCreated");
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, buttonView.getText() + ".isChecked:" + isChecked);
        if(!isChecked) {
            switch (buttonView.getId()) {
                case R.id.cb_sunday: {
                    editor.remove(REPEAT_SUNDAY);
                    break;
                }
                case R.id.cb_monday: {
                    editor.remove(REPEAT_MONDAY);
                    break;
                }
                case R.id.cb_tuesday: {
                    editor.remove(REPEAT_TUESDAY);
                    break;
                }
                case R.id.cb_wednesday: {
                    editor.remove(REPEAT_WEDNESDAY);
                    break;
                }
                case R.id.cb_thursday: {
                    editor.remove(REPEAT_THURSAY);
                    break;
                }
                case R.id.cb_friday: {
                    editor.remove(REPEAT_FRIDAY);
                    break;
                }
                case R.id.cb_saturday: {
                    editor.remove(REPEAT_SATURDAY);
                    break;
                }
            }
            editor.commit();
        } else {
            switch (buttonView.getId()) {
                case R.id.cb_sunday: {
                    editor.putBoolean(REPEAT_SUNDAY,true);
                    break;
                }
                case R.id.cb_monday: {
                    editor.putBoolean(REPEAT_MONDAY,true);
                    break;
                }
                case R.id.cb_tuesday: {
                    editor.putBoolean(REPEAT_TUESDAY,true);
                    break;
                }
                case R.id.cb_wednesday: {
                    editor.putBoolean(REPEAT_WEDNESDAY,true);
                    break;
                }
                case R.id.cb_thursday: {
                    editor.putBoolean(REPEAT_THURSAY,true);
                    break;
                }
                case R.id.cb_friday: {
                    editor.putBoolean(REPEAT_FRIDAY,true);
                    break;
                }
                case R.id.cb_saturday: {
                    editor.putBoolean(REPEAT_SATURDAY,true);
                    break;
                }
            }
            editor.commit();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_poweron_time: {
                TimePickerDialog time=new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtPowerOnTime.setText(String.format("%2d:%2d", hourOfDay, minute));

                        editor.putInt(KEY_POWER_ON_HOUR, hourOfDay);
                        editor.putInt(KEY_POWER_ON_MINUTE, minute);
                        editor.commit();
                    }
                }, 7, 30, true);
                time.show();
                break;
            }
            case R.id.btn_select_poweroff_time: {
                TimePickerDialog time=new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtPowerOffTime.setText(String.format("%2d:%2d", hourOfDay, minute));

                        editor.putInt(KEY_POWER_OFF_HOUR, hourOfDay);
                        editor.putInt(KEY_POWER_OFF_MINUTE, minute);
                        editor.commit();
                    }
                }, 22, 00, true);
                time.show();
                break;
            }

            case R.id.btn_submit: {
                dump("beforeSubmit");
                String mode = sharedPreferences.getString(KEY_MODE, "");

                if(MODE_DAILY.equals(mode)) {

                    setDailyAlarm();

                } else if(MODE_WEEKLY.equals(mode)) {

                    for(int i = 0; i < 7; i++) {
                        boolean ret = sharedPreferences.getBoolean("repeat_" + WEEKDAYS[i], false);
                        if(ret) {
                            setWeeklyAlarm(i + 1);
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Repeat mode set to daily default !", Toast.LENGTH_SHORT).show();

                    editor.putString(KEY_MODE, MODE_DAILY);
                    editor.commit();

                    setDailyAlarm();
                }

                break;
            }
            case R.id.btn_clear: {
                dump("beforeClearAll");
                cancleAllAlarm();
            }
        }
    }

    private void cancleAllAlarm() {
        Intent powerOnIntent = new Intent(MainActivity.this, PowerOnOffAlarm.class);
        powerOnIntent.setAction(ACTION_POWER_ON);
        PendingIntent powerOnSender = PendingIntent.getBroadcast(MainActivity.this,
                REQUEST_CODE_POWER_ON_OFF, powerOnIntent, PendingIntent.FLAG_NO_CREATE);

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) MainActivity.this
                .getSystemService(Context.ALARM_SERVICE);
        am.cancel(powerOnSender);

        Intent powerOffIntent = new Intent(MainActivity.this, PowerOnOffAlarm.class);
        powerOffIntent.setAction(ACTION_POWER_OFF);
        PendingIntent powerOffSender = PendingIntent.getBroadcast(MainActivity.this,
                REQUEST_CODE_POWER_ON_OFF, powerOffIntent, PendingIntent.FLAG_NO_CREATE);


        am.cancel(powerOffSender);
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

        Intent powerOnIntent = new Intent(MainActivity.this, PowerOnOffAlarm.class);
        powerOnIntent.setAction(ACTION_POWER_ON);
        PendingIntent powerOnSender = PendingIntent.getBroadcast(MainActivity.this,
                REQUEST_CODE_POWER_ON_OFF, powerOnIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) MainActivity.this
                .getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                ONE_DAY_INTERVAL, powerOnSender);

        Intent powerOffIntent = new Intent(MainActivity.this, PowerOnOffAlarm.class);
        powerOffIntent.setAction(ACTION_POWER_OFF);
        PendingIntent powerOffSender = PendingIntent.getBroadcast(MainActivity.this,
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

        Intent powerOnIntent = new Intent(MainActivity.this, PowerOnOffAlarm.class);
        powerOnIntent.setAction(ACTION_POWER_ON);
        PendingIntent powerOnSender = PendingIntent.getBroadcast(MainActivity.this,
                REQUEST_CODE_POWER_ON_OFF, powerOnIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) MainActivity.this
                .getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                ONE_WEEK_INTERVAL, powerOnSender);


        calendar.set(Calendar.HOUR_OF_DAY, powerOffHour);
        calendar.set(Calendar.MINUTE, powerOffMinute);

        Intent powerOffIntent = new Intent(MainActivity.this, PowerOnOffAlarm.class);
        powerOffIntent.setAction(ACTION_POWER_OFF);
        PendingIntent powerOffSender = PendingIntent.getBroadcast(MainActivity.this,
                REQUEST_CODE_POWER_ON_OFF, powerOffIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                ONE_WEEK_INTERVAL, powerOffSender);
    }

    private void dump(String title) {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append(":\n");

        sb.append(KEY_MODE);
        sb.append(":");
        sb.append(sharedPreferences.getString(KEY_MODE,""));
        sb.append("\n");

        sb.append(REPEAT_SUNDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_SUNDAY,false));
        sb.append("\n");

        sb.append(REPEAT_MONDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_MONDAY,false));
        sb.append("\n");

        sb.append(REPEAT_TUESDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_TUESDAY,false));
        sb.append("\n");

        sb.append(REPEAT_WEDNESDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_WEDNESDAY,false));
        sb.append("\n");

        sb.append(REPEAT_THURSAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_THURSAY,false));
        sb.append("\n");

        sb.append(REPEAT_FRIDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_FRIDAY,false));
        sb.append("\n");

        sb.append(REPEAT_SATURDAY);
        sb.append(":");
        sb.append(sharedPreferences.getBoolean(REPEAT_SATURDAY,false));
        sb.append("\n");

        sb.append(KEY_POWER_ON_HOUR);
        sb.append(":");
        sb.append(sharedPreferences.getInt(KEY_POWER_ON_HOUR,-1));
        sb.append("\n");

        sb.append(KEY_POWER_ON_MINUTE);
        sb.append(":");
        sb.append(sharedPreferences.getInt(KEY_POWER_ON_MINUTE,-1));
        sb.append("\n");

        sb.append(KEY_POWER_OFF_HOUR);
        sb.append(":");
        sb.append(sharedPreferences.getInt(KEY_POWER_OFF_HOUR,-1));
        sb.append("\n");

        sb.append(KEY_POWER_OFF_MINUTE);
        sb.append(":");
        sb.append(sharedPreferences.getInt(KEY_POWER_OFF_MINUTE,-1));
        sb.append("\n");

        Log.i(TAG, sb.toString());
    }
}
