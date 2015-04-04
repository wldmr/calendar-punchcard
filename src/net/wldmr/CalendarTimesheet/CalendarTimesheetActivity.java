package net.wldmr.CalendarTimesheet;

import android.app.Activity;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.ArrayAdapter;

import android.provider.CalendarContract.Calendars;

import android.database.Cursor;
import android.content.ContentResolver;
import android.net.Uri;

import android.util.Log;

public class CalendarTimesheetActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Cursor cur = queryCalendars();
        String[] items = new String[cur.getCount()];

        // Use the cursor to step through the returned records
        Log.d("CalendarTimesheetActivity", "Iterating over calendars");
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(Columns.id.index);
            displayName = cur.getString(Columns.display_name.index);
            accountName = cur.getString(Columns.account_name.index);
            ownerName = cur.getString(Columns.owner_account.index);

            Log.d("CalendarTimesheetActivity", "calID = " + calID);
            Log.d("CalendarTimesheetActivity", "  displayName = " + displayName);
            Log.d("CalendarTimesheetActivity", "  accountName = " + accountName);
            Log.d("CalendarTimesheetActivity", "  ownerName   = " + ownerName);

            int pos = cur.getPosition();
            items[pos] = displayName;

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        R.layout.calendar_list_item, items);

        ListView listView = (ListView) findViewById(R.id.calendar_list);
        listView.setAdapter(adapter);
    }

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    private enum Columns {
        id (Calendars._ID, 0),
        account_name (Calendars.ACCOUNT_NAME, 1),
        display_name (Calendars.CALENDAR_DISPLAY_NAME, 2),
        owner_account (Calendars.OWNER_ACCOUNT, 3),
        access_level (Calendars.CALENDAR_ACCESS_LEVEL, 4);

        public final String name;
        public final int index;

        public static final String[] names = new String[Columns.values().length];
        public static final int[] indices = new int[Columns.values().length];

        static {
            Columns[] cols = Columns.values();
            for (int i=0; i<cols.length; i++) {
                names[i] = cols[i].name;
                indices[i] = cols[i].index;
            }
        }

        Columns(String name, int index) {
            this.name = name;
            this.index = index;
        }

    }

    public Cursor queryCalendars() {
        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = Calendars.CONTENT_URI;

        String selection = "(" + Columns.access_level.name + " = ?)";

        String[] selectionArgs = new String[] {String.valueOf(Calendars.CAL_ACCESS_OWNER)};

        // Submit the query and get a Cursor object back. 
        cur = cr.query(uri, Columns.names, selection, selectionArgs, null);
        return cur;
    }
}
