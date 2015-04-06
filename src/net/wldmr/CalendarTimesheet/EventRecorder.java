package net.wldmr.CalendarTimesheet;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Locale;

import android.database.Cursor;

import android.content.ContentResolver;
import android.content.ContentValues;

import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

import android.net.Uri;

class EventRecorder {

    private static Cursor eventCursor = null;
    private static final String defaultEventName = ".";
    
    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    static enum CalendarColumns {
        id (Calendars._ID),
        account_name (Calendars.ACCOUNT_NAME),
        display_name (Calendars.CALENDAR_DISPLAY_NAME),
        owner_account (Calendars.OWNER_ACCOUNT),
        access_level (Calendars.CALENDAR_ACCESS_LEVEL),
        color (Calendars.CALENDAR_COLOR);

        private final String column_string;

        public static final String[] names = new String[CalendarColumns.values().length];

        static {
            CalendarColumns[] cols = CalendarColumns.values();
            for (int i=0; i<cols.length; i++) {
                names[i] = cols[i].toString();
            }
        }

        CalendarColumns(String column_string) {
            this.column_string = column_string;
        }

        public String toString() {
            return column_string;
        }

    }

    static Cursor queryCalendars(ContentResolver cr) {
        // Run query
        Cursor cur = null;
        Uri uri = Calendars.CONTENT_URI;

        String selection = "(" + CalendarColumns.access_level + " = ?)";

        String[] selectionArgs = new String[] {String.valueOf(Calendars.CAL_ACCESS_OWNER)};

        // Submit the query and get a Cursor object back. 
        cur = cr.query(uri, CalendarColumns.names, selection, selectionArgs, null);
        return cur;
    }


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
        values.put(CalendarContract.Events.TITLE, defaultEventName);
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

    static void updateEndTime(ContentResolver cr, Uri event) {
        long endMillis = Calendar.getInstance().getTimeInMillis();
        updateEndTime(cr, event, endMillis);
    }

    static void updateEndTime(ContentResolver cr, Uri event, long endMillis) {
        ContentValues values = new ContentValues();
        values.put(Events.DTEND, endMillis); 
        cr.update(event, values, null, null);
    }

}
