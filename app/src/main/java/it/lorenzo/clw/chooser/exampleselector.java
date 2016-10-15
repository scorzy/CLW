package it.lorenzo.clw.chooser;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import it.lorenzo.clw.R;

public class exampleselector extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exampleselector);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<Example> exampleList = new ArrayList<Example>();

        Example example = new Example();
        example.caption = "Example";
        example.img = R.drawable.example_img;
        example.file = R.raw.example;
        example.saveToDisk(this);
        if (example.path != null && example.path.canRead())
            exampleList.add(example);

        Example example2 = new Example();
        example2.caption = "TextSample";
        example2.img = R.drawable.text;
        example2.file = R.raw.text;
        example2.saveToDisk(this);
        if (example2.path != null && example2.path.canRead())
            exampleList.add(example2);


        ExampleAdapter adapter = new ExampleAdapter(this, exampleList);
        mRecyclerView.setAdapter(adapter);
    }


}

