package it.lorenzo.clw.core.modules;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import it.lorenzo.clw.core.Core;
import it.lorenzo.clw.core.modules.Utility.BitmapWithPosition;

/**
 * Created by lorenzo on 24/03/15.
 */
public class Agenda extends AbstractModule {

	private static final String[] EVENTS = new String[]{
			Calendars._ID,                            // 0
			CalendarContract.Instances.TITLE,           // 1
			CalendarContract.Instances.DESCRIPTION,         // 2
			CalendarContract.Instances.BEGIN, // 3
			CalendarContract.Instances.END,   //4
			CalendarContract.Instances.ALL_DAY    //5
	};

	private static final String FUTURE = "future";
	private static final String AGENDA = "agenda";

	private static final String TITLE = "title";

	private static final String STARTNUM = "dt_start_num";
	private static final String STARTDAYSHORT = "dt_start_day_short";
	private static final String STARTDAYLONG = "dt_start_day_long";
	private static final String STARTMOUNT = "dt_start_mounth";
	private static final String STARTHH = "dt_start_hh";
	private static final String STARTMM = "dt_start_mm";
	private static final String STARTCUSTOM = "dt_start_custom";
	private static final String STARTHM = "dt_start_hm";

	private static final String ENDNUM = "dt_end_num";
	private static final String ENDDAYSHORT = "dt_end_day_short";
	private static final String ENDDAYLONG = "dt_end_day_long";
	private static final String ENDMOUNT = "dt_end_mounth";
	private static final String ENDHH = "dt_end_hh";
	private static final String ENDMM = "dt_end_mm";
	private static final String ENDHM = "dt_end_hm";
	private static final String ENDCUSTOM = "dt_end_custom";

	private static final String CALENDARSIDS = "calendars_ids";

	private int future = 28;
	private String calendarQuery;
	private Cursor cursor;

	public Agenda(Core core) {
		super(core);
		calendarQuery = "";
		keys.put(AGENDA, Result.string);
		keys.put(FUTURE, Result.settings);
		keys.put(CALENDARSIDS, Result.settings);
	}

	@Override
	public String getString(String key, String[] params, Context context) {
		initializeIfNeeded(context);
		if (key.equals(AGENDA)) {
			if (cursor == null) {
				readCalendarEvent(context);
			}
			if (cursor.moveToPosition(Integer.parseInt(params[1]))) {
				boolean allDay = cursor.getString(5).equals("1");
				GregorianCalendar start = new GregorianCalendar();
				start.setTimeInMillis(Long.parseLong(cursor.getString(3)));
				GregorianCalendar end = new GregorianCalendar();
				end.setTimeInMillis(Long.parseLong(cursor.getString(4)));

				Locale current;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					current = context.getResources().getConfiguration().getLocales().get(0);
				} else {
					current = context.getResources().getConfiguration().locale;
				}

				SimpleDateFormat dayShort = new SimpleDateFormat("E", current);
				SimpleDateFormat dayLong = new SimpleDateFormat("EEEE", current);

				String custom = "";
				for (int i = 2; i < params.length; i++) {
					custom += params[i] + " ";
				}

				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", current);

				switch (params[0]) {
					case TITLE:
						return cursor.getString(1);
					case STARTNUM:
						return "" + start.get(Calendar.DAY_OF_MONTH);
					case ENDNUM:
						return "" + end.get(Calendar.DAY_OF_MONTH);
					case STARTDAYSHORT:
						return dayShort.format(start.getTimeInMillis());
					case ENDDAYSHORT:
						return dayShort.format(end.getTimeInMillis());
					case STARTDAYLONG:
						return dayLong.format(start.getTimeInMillis());
					case ENDDAYLONG:
						return dayLong.format(end.getTimeInMillis());
					case STARTMOUNT:
						return "" + start.get(Calendar.MONTH);
					case ENDMOUNT:
						return "" + end.get(Calendar.MONTH);
					case STARTCUSTOM:
						SimpleDateFormat startCustom = new SimpleDateFormat(custom, current);
						return startCustom.format(start.getTimeInMillis());
					case ENDCUSTOM:
						SimpleDateFormat endCustom = new SimpleDateFormat(custom, current);
						return endCustom.format(end.getTimeInMillis());
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
							return sdf.format(start.getTime());
					case ENDHM:
						if (!allDay)
							return sdf.format(end.getTime());
				}
			}
		}
		return "";
	}

	@Override
	public void changeSetting(String key, String[] params, Context context) {
	}

	@Override
	public BitmapWithPosition GetBmp(String key, String[] params, int maxWidth, Context context) {
		return null;
	}

	@Override
	public void initialize(Context context) {
		if (!calendarQuery.isEmpty()) {
			readCalendarEvent(context);
		}
	}

	@Override
	public void setDefaults(String key, String[] params, Context context) {
		switch (key) {
			case (FUTURE):
				future = Integer.parseInt(params[0]);
				break;
			case (CALENDARSIDS):
				calendarQuery = "calendar_id = ";
				for (int i = 0; i < params.length; i++) {
					calendarQuery += params[i];
					if (i + 1 < params.length)
						calendarQuery += " OR calendar_id = ";
				}
				break;
		}
	}

	private void readCalendarEvent(Context context) {
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
		cursor = context.getContentResolver().query(eventsUri, EVENTS, " ( " + calendarQuery + " ) ", null, CalendarContract.Instances.BEGIN + " ASC");
	}

	@Override
	protected void finalize() {
		if (cursor != null && !cursor.isClosed())
			cursor.close();
	}
}
