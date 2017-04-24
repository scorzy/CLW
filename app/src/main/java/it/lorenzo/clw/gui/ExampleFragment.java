package it.lorenzo.clw.gui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.lorenzo.clw.R;
import it.lorenzo.clw.chooser.Example;

public class ExampleFragment extends Fragment {

	@BindView(R.id.exampleStatus)
	TextView exampleStatus;
	@BindView(R.id.exampleCreate)
	Button exampleCreate;
	@BindView(R.id.exampleBrowse)
	Button exampleBrowse;

	public ExampleFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_example, container, false);
		ButterKnife.bind(this, view);

		File clwDir = new File(Environment.getExternalStorageDirectory(), "CLW-examples");
		exampleStatus.setText(clwDir.exists() ?
				"Examples found in " + clwDir.getAbsolutePath() :
				"No examples found. You can create it with the button");
		return view;
	}

	@OnClick(R.id.exampleCreate)
	public void createExamples(View view) {
		Example.createExamples(view.getContext());
	}

	@OnClick(R.id.exampleBrowse)
	public void openFolder(View view) {
		File clwDir = new File(Environment.getExternalStorageDirectory(), "CLW-examples");
		if (clwDir.exists()) {
			Uri selectedUri;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				selectedUri = FileProvider.getUriForFile(view.getContext(),
						view.getContext().getPackageName() + ".provider", clwDir);
			} else
				selectedUri = Uri.fromFile(clwDir);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(selectedUri, "resource/folder");
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			if (intent.resolveActivityInfo(view.getContext().getPackageManager(), 0) != null) {
				startActivity(intent);
			} else {
				Toast.makeText(view.getContext(), "Please install a File Manager.", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(view.getContext(), "Example folder not found.", Toast.LENGTH_LONG).show();
		}
	}

}
