package it.lorenzo.clw.chooser;

/**
 * Created by lorenzo on 17/02/15.
 */

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import it.lorenzo.clw.R;

public class FileSelect extends Activity {

	private int id = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);

		setContentView(R.layout.activity_file_select);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			id = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

	}

	private void save(String path) {
		SharedPreferences sharedPref = this.getSharedPreferences(
				getString(R.string.preference), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("" + id, path);
		editor.commit();
		Intent intent = new Intent();
		intent.setAction("it.lorenzo.clw.intent.action.CHANGE_PICTURE");
		int[] ids = new int[1];
		ids[0] = id;
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
		sendBroadcast(intent);
		setResult(RESULT_OK, intent);

		this.finish();
	}

	public void saveSetting(View view) {
		EditText editText = (EditText) findViewById(R.id.editText1);
		String path = editText.getText().toString();
		save(path);
	}

	public void browse(View view) {
		Intent intent = new Intent(this, FileChooser.class);
		this.startActivityForResult(intent, 100);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
									Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == 100) {
			String path = intent.getStringExtra("path");
			if (path != null) {
				// EditText editText = (EditText) findViewById(R.id.editText1);
				// editText.setText(path);
				save(path);
			}
		}

	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file_select, menu);
		return true;
	}
*/
}
