package edu.washington.tchin94.awty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    private String phoneNumber;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        phoneNumber = intent.getStringExtra("phoneNumber");
        Toast.makeText(context, phoneNumber + ": Are we there yet?", Toast.LENGTH_SHORT).show();
    }
}
