package net.wldmr.CalendarTimesheet;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.ContentValues;

import android.provider.CalendarContract;

import android.net.Uri;

class EventRecorder {

    static void startEvent(ContentResolver cr, long calID) {
        long startMillis = 0;
        long endMillis = 0;
        int defaultIntervalType = Calendar.MINUTE;
        int defaultIntervalLength = 1;
        String timezone = TimeZone.getDefault().getDisplayName(Locale.getDefault());

        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = (Calendar) beginTime.clone();
        endTime.add(defaultIntervalType, defaultIntervalLength);

        startMillis = beginTime.getTimeInMillis();
        endMillis = endTime.getTimeInMillis();

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "");
        values.put(CalendarContract.Events.DESCRIPTION, "");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timezone);

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        // 
        // ... do something with event ID
        //
        //
    }
}
