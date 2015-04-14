package it.lorenzo.clw.gui;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import it.lorenzo.clw.R;

public class CalendarIDS extends Activity {

	public static final String[] EVENT_PROJECTION = new String[]{
			Calendars._ID,                           // 0
			Calendars.ACCOUNT_NAME,                  // 1
			Calendars.CALENDAR_DISPLAY_NAME,         // 2
			Calendars.OWNER_ACCOUNT                  // 3
	};

	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_ids);

		// Run query
		Cursor cur = null;
		ContentResolver cr = getContentResolver();
		Uri uri = Calendars.CONTENT_URI;
		// Submit the query and get a Cursor object back.
		cur = cr.query(uri, EVENT_PROJECTION, "", null, null);

		// Use the cursor to step through the returned records
		String string = "";
		while (cur.moveToNext()) {
			long calID = 0;
			String displayName = null;
			String accountName = null;
			String ownerName = null;

			// Get the field values
			calID = cur.getLong(PROJECTION_ID_INDEX);
			displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
			accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
			ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

			Log.i("" + calID, displayName);
			string += calID + " " + displayName + "\n";
		}
		final TextView textView = (TextView) findViewById(R.id.calendarsIds);
		textView.setText(string);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menu_calendar_id, menu);
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
