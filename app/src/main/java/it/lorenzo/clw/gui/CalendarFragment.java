package it.lorenzo.clw.gui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.lorenzo.clw.R;

public class CalendarFragment extends Fragment {

	public static final String[] EVENT_PROJECTION = new String[]{
			Calendars._ID,                           // 0
			Calendars.ACCOUNT_NAME,                  // 1
			Calendars.CALENDAR_DISPLAY_NAME,         // 2
			Calendars.OWNER_ACCOUNT                  // 3
	};
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;

	@BindView(R.id.calendarView)
	TextView calendarView;

	public CalendarFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_calendar, container, false);
		ButterKnife.bind(this, view);

		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR)
				== PackageManager.PERMISSION_GRANTED) {
			update();
		} else {
			if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
					Manifest.permission.READ_CONTACTS)) {
				Toast.makeText(getContext(), "Required for read agenda", Toast.LENGTH_LONG);
			} else {
				ActivityCompat.requestPermissions(getActivity(),
						new String[]{Manifest.permission.READ_CONTACTS},
						119);
			}
		}

		return view;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		if (requestCode == 119 && grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED)
			update();
	}

	private void update() {
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR)
				== PackageManager.PERMISSION_GRANTED) {
			String string = "";
			Cursor cur;
			ContentResolver cr = getActivity().getContentResolver();
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
			calendarView.setText(string);
		} else
			calendarView.setText("Grand Read Calendar permissions");
	}

}
