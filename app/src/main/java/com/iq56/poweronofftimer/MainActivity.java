package com.iq56.poweronofftimer;

import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
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

import static com.iq56.poweronofftimer.Utils.*;

public class MainActivity extends AppCompatActivity implements OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button btnSelectPowerOnTime, btnSelectPowerOffTime, btnSubmit, btnClear;

    private RadioGroup radioGroupMode;

    private CheckBox btnSunday, btnMonday, btnTuesDay, btnWednesday, btnThursday, btnFriday, btnSaturday;

    private TextView txtPowerOnTime, txtPowerOffTime;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private IAlarmServiceInterface mService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            mService = IAlarmServiceInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(this, AlarmService.class);
        serviceIntent.setAction(IAlarmServiceInterface.class.getName());
        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);

        sharedPreferences = getSharedPreferences(this.getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnSelectPowerOnTime = (Button) findViewById(R.id.btn_select_poweron_time);
        btnSelectPowerOffTime = (Button) findViewById(R.id.btn_select_poweroff_time);

        btnSelectPowerOnTime.setOnClickListener(this);
        btnSelectPowerOffTime.setOnClickListener(this);

        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnClear = (Button) findViewById(R.id.btn_clear);

        btnSubmit.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        radioGroupMode = (RadioGroup) findViewById(R.id.mode_group);

        btnSunday = (CheckBox) findViewById(R.id.cb_sunday);
        btnMonday = (CheckBox) findViewById(R.id.cb_monday);
        btnTuesDay = (CheckBox) findViewById(R.id.cb_tuesday);
        btnWednesday = (CheckBox) findViewById(R.id.cb_wednesday);
        btnThursday = (CheckBox) findViewById(R.id.cb_thursday);
        btnFriday = (CheckBox) findViewById(R.id.cb_friday);
        btnSaturday = (CheckBox) findViewById(R.id.cb_saturday);

        txtPowerOnTime = (TextView) findViewById(R.id.txt_poweron_time);
        txtPowerOffTime = (TextView) findViewById(R.id.txt_poweroff_time);

        initUI();

        Utils.dump(sharedPreferences, "onCreated");
    }

    private void initUI() {
        final CheckBox[] weekdayCheckBoxs = {btnSunday, btnMonday, btnTuesDay, btnWednesday, btnThursday, btnFriday, btnSaturday};

        String mode = sharedPreferences.getString(KEY_MODE, "");

        if (MODE_DAILY.equals(mode)) {
            radioGroupMode.check(R.id.btn_daily);
        } else if (MODE_WEEKLY.equals(mode)) {
            radioGroupMode.check(R.id.btn_weekly);
        } else {
            radioGroupMode.check(R.id.btn_daily);
        }

        radioGroupMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (R.id.btn_weekly == checkedId) {

                    for (int i = 0; i < 7; i++) {
                        weekdayCheckBoxs[i].setClickable(true);
                    }

                    editor.putString(KEY_MODE, MODE_WEEKLY);
                    editor.commit();
                } else {
                    for (int i = 0; i < 7; i++) {
                        weekdayCheckBoxs[i].setClickable(false);
                    }

                    editor.putString(KEY_MODE, MODE_DAILY);
                    editor.commit();
                }
            }
        });

        for (int i = 0; i < 7; i++) {
            weekdayCheckBoxs[i].setOnCheckedChangeListener(this);
        }

        for (int i = 0; i < 7; i++) {
            boolean ret = sharedPreferences.getBoolean("repeat_" + WEEKDAYS[i], false);
            if (ret) {
                weekdayCheckBoxs[i].setChecked(true);
            }
        }

        if (!MODE_WEEKLY.equals(mode)) {
            for (int i = 0; i < 7; i++) {
                weekdayCheckBoxs[i].setClickable(false);
            }
        }

        int powerOnHour = sharedPreferences.getInt(KEY_POWER_ON_HOUR, 7);
        int powerOnMinute = sharedPreferences.getInt(KEY_POWER_ON_MINUTE, 30);
        int powerOffHour = sharedPreferences.getInt(KEY_POWER_OFF_HOUR, 21);
        int powerOffMinute = sharedPreferences.getInt(KEY_POWER_OFF_MINUTE, 30);

        txtPowerOnTime.setText(String.format("%2d:%2d", powerOnHour, powerOnMinute));
        txtPowerOffTime.setText(String.format("%2d:%2d", powerOffHour, powerOffMinute));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        Log.d(TAG, buttonView.getText() + ".isChecked:" + isChecked);

        if (!isChecked) {
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
                    editor.putBoolean(REPEAT_SUNDAY, true);
                    break;
                }
                case R.id.cb_monday: {
                    editor.putBoolean(REPEAT_MONDAY, true);
                    break;
                }
                case R.id.cb_tuesday: {
                    editor.putBoolean(REPEAT_TUESDAY, true);
                    break;
                }
                case R.id.cb_wednesday: {
                    editor.putBoolean(REPEAT_WEDNESDAY, true);
                    break;
                }
                case R.id.cb_thursday: {
                    editor.putBoolean(REPEAT_THURSAY, true);
                    break;
                }
                case R.id.cb_friday: {
                    editor.putBoolean(REPEAT_FRIDAY, true);
                    break;
                }
                case R.id.cb_saturday: {
                    editor.putBoolean(REPEAT_SATURDAY, true);
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
                TimePickerDialog time = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {

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
                TimePickerDialog time = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {

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

                editor.putBoolean(KEY_ALLOWED_POWER_ONOFF, true);
                editor.commit();

                if (null != mService) {
                    try {
                        mService.setAlarm();
                    } catch (RemoteException e) {
                        Toast.makeText(MainActivity.this, "Set Alarm Failed", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Service NOT connected", Toast.LENGTH_LONG).show();
                }

                break;
            }
            case R.id.btn_clear: {

                if (null != mService) {
                    try {
                        mService.clearAllAlarm();
                    } catch (RemoteException e) {
                        Toast.makeText(MainActivity.this, "Clear Alarm Failed", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Service NOT connected", Toast.LENGTH_LONG).show();
                }

                editor.clear();
                editor.commit();

                initUI();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
