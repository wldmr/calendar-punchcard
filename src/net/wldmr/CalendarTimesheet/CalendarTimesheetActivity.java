package net.wldmr.CalendarTimesheet;

import android.app.Activity;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.ArrayAdapter;

public class CalendarTimesheetActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String[] dummyItems = {"One, potato", "Two, potato"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        R.layout.calendar_list_item, dummyItems);

        ListView listView = (ListView) findViewById(R.id.calendar_list);
        listView.setAdapter(adapter);
    }
}
