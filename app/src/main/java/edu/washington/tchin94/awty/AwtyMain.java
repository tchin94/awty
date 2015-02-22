package edu.washington.tchin94.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class AwtyMain extends ActionBarActivity {

    private PendingIntent pendingIntent;
    private boolean started;
    private int interval;
    private boolean validInput;
    private boolean validPhone;
    private boolean validInterval;
    private EditText timeInterval;
    private EditText phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awty_main);

        started = isStarted();

        //gets view
        final Button actionBtn = (Button) findViewById(R.id.action_btn);
        phoneNumber = (EditText) findViewById(R.id.phone_number_value);
        timeInterval = (EditText) findViewById(R.id.time_interval_value);

        //sets action button
        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create alarm manager
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                validateInput();
                //if there isn't an alarm, create an alarm
                if (!started && validInput) {
                    actionBtn.setText("Stop");
                    started = true;
                    setPendingIntent();
                    setInterval();
                    //sets the alarm
                    manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
                } else if (started) {
                    //turns off alarm
                    actionBtn.setText("Start");
                    manager.cancel(pendingIntent);
                    pendingIntent.cancel();
                    started = false;
                } else if (validPhone && !validInterval) {
                    Toast.makeText(AwtyMain.this, "Invalid Interval", Toast.LENGTH_SHORT).show();
                } else if (!validPhone && validInterval) {
                    Toast.makeText(AwtyMain.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AwtyMain.this, "Invalid Phone Number and Interval", Toast.LENGTH_SHORT).show();
                }
            }
        });
        phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //sets the prefs
        SharedPreferences shared = getSharedPreferences("info",MODE_PRIVATE);
        if (shared != null) {
            phoneNumber.setText(shared.getString("phoneNumber", ""));
            timeInterval.setText(shared.getString("timeInterval", ""));
        }

        //sets the action button
        if (!started) {
            actionBtn.setText("Start");
        } else {
            actionBtn.setText("Stop");
            //sets the pending intent
            Intent alarmIntent = new Intent(AwtyMain.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(AwtyMain.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref;
        pref = getSharedPreferences("info", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("phoneNumber", phoneNumber.getText().toString());
        editor.putString("timeInterval", timeInterval.getText().toString());
        editor.commit();
    }

    //returns interval
    private void setInterval() {
        interval = Integer.parseInt(timeInterval.getText().toString()) * 1000 * 60;
    }

    //makes sure the input is correct
    private void validateInput() {
        String p = phoneNumber.getText().toString();
        String i = timeInterval.getText().toString();
        validPhone = p.matches("\\(\\d{3}\\)\\s\\d{3}\\-\\d{4}");
        validInterval = !i.isEmpty() && !i.matches("0+");
        validInput = validPhone && validInterval;
    }

    //sets pending intent
    private void setPendingIntent() {
        Intent alarmIntent = new Intent(AwtyMain.this, AlarmReceiver.class);
        alarmIntent.putExtra("phoneNumber", phoneNumber.getText().toString());
        pendingIntent = PendingIntent.getBroadcast(AwtyMain.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //returns whether or not an alarm is already running
    private Boolean isStarted() {
        boolean alarmUp = (PendingIntent.getBroadcast(AwtyMain.this, 0,
                new Intent(AwtyMain.this, AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        return  alarmUp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_awty_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
