package it.lorenzo.clw.gui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import it.lorenzo.clw.R;

public class CalendarIDS extends AppCompatActivity {

	public static final String[] EVENT_PROJECTION = new String[]{
			Calendars._ID,                           // 0
			Calendars.ACCOUNT_NAME,                  // 1
			Calendars.CALENDAR_DISPLAY_NAME,         // 2
			Calendars.OWNER_ACCOUNT                  // 3
	};

	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_ids);

		String string = "";
		final TextView textView = (TextView) findViewById(R.id.calendarsIds);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
			Cursor cur;
			ContentResolver cr = getContentResolver();
			Uri uri = Calendars.CONTENT_URI;

			cur = cr.query(uri, EVENT_PROJECTION, "", null, null);
			if (cur != null) {
				while (cur.moveToNext()) {
					long calID;
					String displayName;

					calID = cur.getLong(PROJECTION_ID_INDEX);
					displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);

					string += calID + " " + displayName + "\n";
				}
				cur.close();
			}
		} else {
			string = "Please grant Read Calendar permission.";
		}
		textView.setText(string);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		return id == R.id.action_settings || super.onOptionsItemSelected(item);
	}
}
