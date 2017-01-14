package it.lorenzo.clw.chooser;

/**
 * Created by lorenzo on 17/02/15.
 */

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import it.lorenzo.clw.R;

public class FileSelect extends AppCompatActivity {

	private int id = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);

		setContentView(R.layout.activity_file_select);

		requirePermission();

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			id = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
	}

	private void requirePermission() {
		if (Build.VERSION.SDK_INT > 16) {
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED ||
					ContextCompat.checkSelfPermission(this,
							Manifest.permission.READ_EXTERNAL_STORAGE)
							!= PackageManager.PERMISSION_GRANTED ||
					ContextCompat.checkSelfPermission(this,
							Manifest.permission.READ_CALENDAR)
							!= PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{
								Manifest.permission.WRITE_EXTERNAL_STORAGE,
								Manifest.permission.READ_EXTERNAL_STORAGE,
								Manifest.permission.READ_CALENDAR},
						115);
			}
		} else if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this,
						Manifest.permission.READ_CALENDAR)
						!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{
							Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.READ_CALENDAR},
					115);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String permissions[], @NonNull int[] grantResults) {
		if (requestCode == 115) {
			if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_DENIED) {
				Toast.makeText(this, "CLW will not work without Read external storage permission !", Toast.LENGTH_LONG).show();
			}
			if (grantResults.length > 1
					&& grantResults[1] == PackageManager.PERMISSION_DENIED) {
				Toast.makeText(this, "Example will not work without Write external storage permission !", Toast.LENGTH_LONG).show();

			}
			if (grantResults.length > 2
					&& grantResults[2] == PackageManager.PERMISSION_DENIED) {
				Toast.makeText(this, "Agenda will not work !", Toast.LENGTH_LONG).show();
			}
		}
	}


	private void save(String path) {
		boolean saved = false;
		try {
			File file = new File(path);
			if (file.exists() && file.canRead()) {

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

				Toast.makeText(this, "Using \n" + path, Toast.LENGTH_LONG).show();
				saved = true;
				this.finish();
			}
		} catch (Exception ex) {
		}
		if (!saved)
			Toast.makeText(this, "Please select a valid file", Toast.LENGTH_LONG).show();

	}

	public void saveSetting(View view) {
		EditText editText = (EditText) findViewById(R.id.editText1);
		String path = editText.getText().toString();
		save(path);
	}

	public void browse(View view) {

		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_GRANTED) {

			requirePermission();
			Intent intent = new Intent(this, FileChooser.class);
			this.startActivityForResult(intent, 100);

		} else {
			Toast.makeText(this, "External storage permission required !", Toast.LENGTH_LONG).show();
			requirePermission();
		}
	}

	public void browse_ext(View view) {

		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_GRANTED) {


			DialogProperties properties = new DialogProperties();
			properties.selection_mode = DialogConfigs.SINGLE_MODE;
			properties.selection_type = DialogConfigs.FILE_SELECT;
			properties.root = new File(DialogConfigs.DEFAULT_DIR);
			properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
			properties.extensions = null;
			FilePickerDialog dialog = new FilePickerDialog(this, properties);
			dialog.setTitle("Select a File");
			dialog.setDialogSelectionListener(new DialogSelectionListener() {
				@Override
				public void onSelectedFilePaths(String[] files) {
					//files is the array of the paths of files selected by the Application User.
					save(files[0]);
				}
			});
			dialog.show();

		} else {
			Toast.makeText(this, "External storage permission required !", Toast.LENGTH_LONG).show();
			requirePermission();
		}
//		requirePermission();
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.setType("*/*");
//		intent.addCategory(Intent.CATEGORY_OPENABLE);
//		try {
//			startActivityForResult(intent, 100);
//		} catch (android.content.ActivityNotFoundException ex) {
//			Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_LONG).show();
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
									Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if ((resultCode == 100 || resultCode == RESULT_OK) && intent != null) {
			String path = intent.getData().getPath();
			if (path != null) {
				save(path);
			}
		}
	}

	public void useExample(View view) {

		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_GRANTED) {

			Intent intent = new Intent(this, ExampleSelector.class);
		this.startActivityForResult(intent, 100);
		} else {
			Toast.makeText(this, "External storage permission required !", Toast.LENGTH_LONG).show();
			requirePermission();
		}
	}

}
