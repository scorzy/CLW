package it.lorenzo.clw.gui;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import it.lorenzo.clw.R;


public class Settings extends PreferenceFragmentCompat {

	public Settings() {
	}

	public static Settings newInstance() {
		Settings fragment = new Settings();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);
	}

}
