package it.lorenzo.clw.gui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;

import it.lorenzo.clw.R;
import it.lorenzo.clw.chooser.Example;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	private AppCompatDelegate mDelegate;


	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}


	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}


	private void bindPreferenceSummaryToInt(Preference preference) {
		preference.setOnPreferenceChangeListener(this);

		onPreferenceChange(preference,
				PreferenceManager
						.getDefaultSharedPreferences(preference.getContext())
						.getString(preference.getKey(), "30"));

	}

	private void bindPreferenceSummaryToBoolean(Preference preference) {
		preference.setOnPreferenceChangeListener(this);

		onPreferenceChange(preference,
				PreferenceManager
						.getDefaultSharedPreferences(preference.getContext())
						.getBoolean(preference.getKey(), false));

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getDelegate().installViewFactory();
		getDelegate().onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);

		Preference button = getPreferenceManager().findPreference("idsbutton");
		if (button != null) {
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					showCalendarIds();
					return true;
				}
			});
		}

		Preference textView = getPreferenceManager().findPreference("example_key");
		if (textView != null) {
			File clwDir = new File(Environment.getExternalStorageDirectory(), "CLW-examples");
			textView.setSummary(clwDir.getAbsolutePath());
			final Context context = this;
			textView.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					Example.createExamples(context);
					openFolder();
					return true;
				}
			});
		}
	}

	public void openFolder() {
		File clwDir = new File(Environment.getExternalStorageDirectory(), "CLW-examples");
		Uri selectedUri = Uri.fromFile(clwDir);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(selectedUri, "resource/folder");
		if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
			startActivity(intent);
		} else {
			Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_LONG).show();
		}
	}

	private void showCalendarIds() {
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_CALENDAR)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.READ_CALENDAR},
					116);

		} else {
			Intent intent = new Intent(this, CalendarIDS.class);
			startActivity(intent);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case 116: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Intent intent = new Intent(this, CalendarIDS.class);
					startActivity(intent);
				} else {
					Toast.makeText(this, "Please grant permission.", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object value) {
		String stringValue = value.toString();
		SharedPreferences sharedPref = this.getSharedPreferences(
				getString(R.string.preference), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();

		if (preference instanceof CheckBoxPreference) {
			editor.putBoolean(preference.getKey(), (Boolean) value);
			editor.commit();

		} else if (preference.getKey().equals("intervall_key")) {
			editor.putInt(preference.getKey(), Integer.parseInt(stringValue));
			editor.commit();

		}
		if (preference instanceof ListPreference) {
			// For list preferences, look up the correct display value in
			// the preference's 'entries' list.
			ListPreference listPreference = (ListPreference) preference;
			int index = listPreference.findIndexOfValue(stringValue);
			// Set the summary to reflect the new value.
			preference.setSummary(
					index >= 0
							? listPreference.getEntries()[index]
							: null);
		} else {
			// For all other preferences, set the summary to the value's
			// simple string representation.
			preference.setSummary(stringValue);
		}
		Intent intent = new Intent();
		intent.setAction("it.lorenzo.clw.intent.action.SETTINGS_CHANGED");
		sendBroadcast(intent);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setupSimplePreferencesScreen();
	}

	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}
		bindPreferenceSummaryToBoolean(findPreference("alarm_key"));
		bindPreferenceSummaryToInt(findPreference("intervall_key"));
		bindPreferenceSummaryToBoolean(findPreference("use_notification_key"));
	}


	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	private AppCompatDelegate getDelegate() {
		if (mDelegate == null) {
			mDelegate = AppCompatDelegate.create(this, null);
		}
		return mDelegate;
	}

	@Override
	public void setContentView(@LayoutRes int layoutResID) {
		getDelegate().setContentView(layoutResID);
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		getDelegate().setContentView(view, params);
	}

	@Override
	public void addContentView(View view, ViewGroup.LayoutParams params) {
		getDelegate().addContentView(view, params);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		getDelegate().onPostResume();
	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		super.onTitleChanged(title, color);
		getDelegate().setTitle(title);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		getDelegate().onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStop() {
		super.onStop();
		getDelegate().onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getDelegate().onDestroy();
	}


}
