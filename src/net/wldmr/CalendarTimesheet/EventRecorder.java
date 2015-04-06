package net.wldmr.CalendarTimesheet;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Locale;

import android.database.Cursor;

import android.content.Intent;
import android.content.ContentResolver;
import android.content.ContentValues;

import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

import android.net.Uri;

class EventRecorder {

    private static Cursor eventCursor = null;
    private static final String defaultEventName = "";

    /** Is there an event in the specified calendar that hasn't been finished yet?
     *
     * @return EventURI of currently running event, or null if none is 
     * currently running.
     * */
    static Uri getRecordingEvent (ContentResolver cr, long calID) {
        // Which means: Is there an event with no title?
        Uri uri = CalendarContract.Events.CONTENT_URI;

        String selection
            = "("
            + "(" + Events.CALENDAR_ID + " = ?)"
            + " AND "
            + "(" + Events.TITLE + " = ?)"
            + ")";

        String[] selectionArgs = new String[] {String.valueOf(calID), defaultEventName};

        String[] projection = {
            Events._ID,
        };

        eventCursor = cr.query(uri, projection, selection, selectionArgs, null);
        switch (eventCursor.getCount()) {
            case 0:
                return null;
            default: // TODO: Need to decide what to do when there are more than 1 result.
                eventCursor.moveToNext();
                long eventID = eventCursor.getLong(0);
                return Uri.withAppendedPath(uri, String.valueOf(eventID));
        }
    }

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

    static Intent createEditIntent(Uri event) {
        long endTime = Calendar.getInstance().getTimeInMillis();
        Intent intent = new Intent(Intent.ACTION_EDIT)
            .setData(event)
            .putExtra(Events.DTEND, endTime);
        return intent;
    }

}
