package it.lorenzo.clw.core.modules;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import it.lorenzo.clw.core.Core;

/**
 * Created by lorenzo on 24/03/15.
 */
public class Agenda extends AbstractMobule {

	// Projection array. Creating indices for this array instead of doing
	// dynamic lookups improves performance.
	public static final String[] EVENT_PROJECTION = new String[]{
			Calendars._ID,                           // 0
			Calendars.ACCOUNT_NAME,                  // 1
			Calendars.CALENDAR_DISPLAY_NAME,         // 2
			CalendarContract.Calendars.OWNER_ACCOUNT // 3
	};
	public static final String[] EVENTS = new String[]{
			Calendars._ID,                            // 0
			CalendarContract.Instances.TITLE,           // 1
			CalendarContract.Instances.DESCRIPTION,         // 2
			CalendarContract.Instances.DTSTART, // 3
			CalendarContract.Instances.END,   //4
			CalendarContract.Instances.ALL_DAY    //5
	};

	public static final String FUTURE = "future";
	public static final String AGENDA = "agenda";

	public static final String TITLE = "title";

	public static final String STARTNUM = "dt_start_num";
	public static final String STARTDAYSHORT = "dt_start_day_short";
	public static final String STARTDAYLONG = "dt_start_day_long";
	public static final String STARTMOUNT = "dt_start_mounth";
	public static final String STARTHH = "dt_start_hh";
	public static final String STARTMM = "dt_start_mm";
	public static final String STARTCUSTOM = "dt_start_custom";
	public static final String STARTHM = "dt_start_hm";

	public static final String ENDNUM = "dt_end_num";
	public static final String ENDDAYSHORT = "dt_end_day_short";
	public static final String ENDDAYLONG = "dt_end_day_long";
	public static final String ENDMOUNT = "dt_end_mounth";
	public static final String ENDHH = "dt_end_hh";
	public static final String ENDMM = "dt_end_mm";
	public static final String ENDHM = "dt_end_hm";
	public static final String ENDCUSTOM = "dt_end_custom";

	public static final String CALENDARSIDS = "calendars_ids";

	// The indices for the projection array above.
	private int future = 28;
	private String calendarQuery;
	private Cursor cursor;
	private ArrayList<String> displayNames;


	public Agenda() {
		calendarQuery = "";
		keys.put(AGENDA, Result.string);
		keys.put(FUTURE, Result.settings);
		keys.put(CALENDARSIDS, Result.settings);
		displayNames = new ArrayList<String>();
	}

	@Override
	public String getString(String key, String[] params) {
		if (key.equals(AGENDA)) {
			if (cursor.moveToPosition(Integer.parseInt(params[1]))) {
				boolean allDay = cursor.getString(5).equals("1");
				GregorianCalendar start = new GregorianCalendar();
				start.setTimeInMillis(Long.parseLong(cursor.getString(3)));
				GregorianCalendar end = new GregorianCalendar();
				end.setTimeInMillis(Long.parseLong(cursor.getString(4)));
				Locale current = Core.getInstance().getContext().getResources().getConfiguration().locale;
				SimpleDateFormat dayShort = new SimpleDateFormat("E", current);
				SimpleDateFormat dayLong = new SimpleDateFormat("EEEE", current);

				String custom = "";
				for (int i = 2; i < params.length; i++) {
					custom += params[i] + " ";
				}

				switch (params[0]) {
					case TITLE:
						return cursor.getString(1);
					case STARTNUM:
						return "" + start.get(Calendar.DAY_OF_MONTH);
					case ENDNUM:
						return "" + end.get(Calendar.DAY_OF_MONTH);
					case STARTDAYSHORT:
						return dayShort.format(Long.parseLong(cursor.getString(3)));
					case ENDDAYSHORT:
						return dayShort.format(Long.parseLong(cursor.getString(4)));
					case STARTDAYLONG:
						return dayLong.format(Long.parseLong(cursor.getString(3)));
					case ENDDAYLONG:
						return dayLong.format(Long.parseLong(cursor.getString(4)));
					case STARTMOUNT:
						return "" + start.get(Calendar.MONTH);
					case ENDMOUNT:
						return "" + end.get(Calendar.MONTH);
					case STARTCUSTOM:
						SimpleDateFormat startCustom = new SimpleDateFormat(custom, current);
						return startCustom.format(Long.parseLong(cursor.getString(3)));
					case ENDCUSTOM:
						SimpleDateFormat endCustom = new SimpleDateFormat(custom, current);
						return endCustom.format(Long.parseLong(cursor.getString(4)));
					case STARTHH:
						return "" + start.get(Calendar.HOUR_OF_DAY);
					case STARTMM:
						return "" + start.get(Calendar.MINUTE);
					case ENDHH:
						return "" + end.get(Calendar.HOUR_OF_DAY);
					case ENDMM:
						return "" + end.get(Calendar.MINUTE);
					case STARTHM:
						if (!allDay)
							return "" + start.get(Calendar.HOUR_OF_DAY) + ":" + start.get(Calendar.MINUTE);
					case ENDHM:
						if (!allDay)
							return "" + end.get(Calendar.HOUR_OF_DAY) + ":" + end.get(Calendar.MINUTE);
				}

			}
		}
		return "";
	}

	@Override
	public void changeSetting(String key, String[] params) {
	}

	@Override
	public Bitmap GetBmp(String key, String[] params, int maxWidth) {
		return null;
	}

	@Override
	public void inizialize() {
		if (!calendarQuery.isEmpty()) {
			readCalendarEvent();
		}
	}

	@Override
	public void setDefaults(String[] elements) {
		switch (elements[0]) {
			case (FUTURE):
				future = Integer.parseInt(elements[1]);
				break;
			case (CALENDARSIDS):
				displayNames.clear();
				calendarQuery = "calendar_id = ";
				for (int i = 1; i < elements.length; i++) {
					calendarQuery += elements[i];
					if (i + 1 < elements.length)
						calendarQuery += " OR calendar_id = ";
				}
				break;
		}
	}

	private void readCalendarEvent() {
		long now = new Date(System.currentTimeMillis()).getTime();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now);
		c.add(Calendar.DATE, future);
		long max = c.getTimeInMillis();
		Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
				.buildUpon();
		ContentUris.appendId(eventsUriBuilder, now);
		ContentUris.appendId(eventsUriBuilder, max);
		Uri eventsUri = eventsUriBuilder.build();
		cursor = Core.getInstance().getContext().getContentResolver().query(eventsUri, EVENTS, " ( " + calendarQuery + " ) ", null, CalendarContract.Instances.BEGIN + " ASC");
	}
}
