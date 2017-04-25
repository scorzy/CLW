package it.lorenzo.clw.chooser;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import it.lorenzo.clw.R;

public class ExampleSelector extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_exampleselector);

		RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

		mRecyclerView.setHasFixedSize(true);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
				this,
				LinearLayoutManager.HORIZONTAL,
				false
		);
		mRecyclerView.setLayoutManager(mLayoutManager);

		ArrayList<Example> exampleList = Example.createExamples(this);

		if (exampleList != null) {
			ExampleAdapter adapter = new ExampleAdapter(exampleList);
			mRecyclerView.setAdapter(adapter);
		}

	}
}

