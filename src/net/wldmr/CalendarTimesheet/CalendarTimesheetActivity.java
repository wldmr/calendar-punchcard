package net.wldmr.CalendarTimesheet;

import android.app.Activity;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.ArrayAdapter;

import android.provider.CalendarContract.Calendars;

import android.database.Cursor;
import android.content.ContentResolver;
import android.net.Uri;

import android.widget.AdapterView;
import android.view.View;

import android.widget.Toast;

import android.util.Log;

public class CalendarTimesheetActivity extends Activity
{
    private Cursor cur;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        cur = queryCalendars();
        String[] items = new String[cur.getCount()];

        // Use the cursor to step through the returned records
        Log.d("CalendarTimesheetActivity", "Iterating over calendars");
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(Columns.id.ordinal());
            displayName = cur.getString(Columns.display_name.ordinal());
            accountName = cur.getString(Columns.account_name.ordinal());
            ownerName = cur.getString(Columns.owner_account.ordinal());

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

        listView.setOnItemClickListener(mMessageClickedHandler); 
    }

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    private enum Columns {
        id (Calendars._ID),
        account_name (Calendars.ACCOUNT_NAME),
        display_name (Calendars.CALENDAR_DISPLAY_NAME),
        owner_account (Calendars.OWNER_ACCOUNT),
        access_level (Calendars.CALENDAR_ACCESS_LEVEL),
        color (Calendars.CALENDAR_COLOR);

        private final String column_string;

        public static final String[] names = new String[Columns.values().length];

        static {
            Columns[] cols = Columns.values();
            for (int i=0; i<cols.length; i++) {
                names[i] = cols[i].toString();
            }
        }

        Columns(String column_string) {
            this.column_string = column_string;
        }

        public String toString() {
            return column_string;
        }

    }

    public Cursor queryCalendars() {
        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = Calendars.CONTENT_URI;

        String selection = "(" + Columns.access_level + " = ?)";

        String[] selectionArgs = new String[] {String.valueOf(Calendars.CAL_ACCESS_OWNER)};

        // Submit the query and get a Cursor object back. 
        cur = cr.query(uri, Columns.names, selection, selectionArgs, null);
        return cur;
    }


    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id)
        {
            // Display a messagebox.
            cur.moveToPosition(position);
            String s = cur.getString(Columns.color.ordinal());
            Toast.makeText(getApplicationContext(), "Item: " + s, Toast.LENGTH_SHORT).show();

            // TODO: Insert event that extends one hour into the future.
        }
    };

}
