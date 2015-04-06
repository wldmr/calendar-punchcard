package net.wldmr.CalendarTimesheet;

import android.app.Activity;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import android.provider.CalendarContract.Calendars;

import android.database.Cursor;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;

import android.widget.AdapterView;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import android.util.Log;

import android.graphics.Bitmap;
import android.graphics.Color;

public class CalendarTimesheetActivity extends Activity
{
    private Cursor cur;

    private class MyCursorAdapter extends SimpleCursorAdapter {

        MyCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // FIXME: Sooooper inefficient, probably. There must be a better 
            // way to set the color on that view, and even if not, we shouldn't 
            // regenerate the bitmap every time the view is rendered. Oh well.
            View view = super.getView(position, convertView, parent);
            ImageView img = (ImageView) view.findViewById(R.id.calendar_color);
            Bitmap bitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
            Cursor c = getCursor();
            c.moveToPosition(position);
            String color = c.getString(5); // FIXME: A more robust way of getting the column
            bitmap.eraseColor(Integer.parseInt(color));
            img.setImageBitmap(bitmap);
            return view;
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ContentResolver cr = getContentResolver();
        cur = EventRecorder.queryCalendars(cr);
        String[] items = new String[cur.getCount()];

        String[] fromColumns = {
            Calendars.CALENDAR_DISPLAY_NAME,
            Calendars.CALENDAR_COLOR,
        };
        int[] toViews = {
            R.id.display_name,
            R.id.calendar_color,
        };

        SimpleCursorAdapter adapter = new MyCursorAdapter(this,
                R.layout.calendar_list_item, cur, fromColumns, toViews, 0);
        ListView listView = (ListView) findViewById(R.id.calendar_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(mMessageClickedHandler); 
    }

    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id)
        {
            // Display a messagebox.
            cur.moveToPosition(position);

            String s = cur.getString(EventRecorder.CalendarColumns.display_name.ordinal());
            long CalendarID = cur.getLong(EventRecorder.CalendarColumns.id.ordinal());

            ContentResolver cr = getContentResolver();
            Uri event = EventRecorder.getRecordingEvent(cr, CalendarID);
            if (event == null) {
                Toast.makeText(getApplicationContext(), "Starting event in " + s, Toast.LENGTH_SHORT).show();
                EventRecorder.startEvent(cr, CalendarID);
            } else {
                Toast.makeText(getApplicationContext(), "Ending event in " + s, Toast.LENGTH_SHORT).show();
                EventRecorder.updateEndTime(cr, event);

                // Start an activity to review the event.
                // FIXME: I'd like to use ACTION_EDIT, but that does not, in 
                // fact, open an activity that lets me edit the event at all.
                // See <https://code.google.com/p/android/issues/detail?id=39402>,
                // where this issue is addressed, but also marked as 
                // "obsolete", whatever that means.
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(event);
                startActivity(intent);
            }

        }
    };

}
