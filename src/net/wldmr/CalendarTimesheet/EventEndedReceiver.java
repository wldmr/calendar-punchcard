package net.wldmr.CalendarTimesheet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.widget.Toast;
import android.util.Log;

public class EventEndedReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Intent: " + intent, Toast.LENGTH_LONG).show();
        Log.d("EventEndedReceiver", "Can I see the end from: " + intent);
    }
}
