package it.lorenzo.clw.chooser;

/**
 * Created by lorenzo on 17/02/15.
 */
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import it.lorenzo.clw.R;

import java.io.File;

public class FileChooser extends ListActivity {

    // private int id;
    private SimpleCursorAdapter mAdapter;
    private File current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_file_chooser);
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        String[] from = {"name", "absolutePath", "img"};
        int[] to = {R.id.fileName, R.id.fileName, R.id.imageView1};
        mAdapter = new SimpleCursorAdapter(this, R.layout.line, null, from, to,
                0);
        mAdapter.setViewBinder(new ViewBinder() {
            @Override
            public boolean setViewValue(View aView, Cursor aCursor,
                                        int aColumnIndex) {
                if (aColumnIndex == 1) {
                    String tag = aCursor.getString(aColumnIndex);
                    aView.setTag(tag);
                    return true;
                }
                return false;
            }
        });
        setListAdapter(mAdapter);
        setPath("/");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.file_chooser, menu);
        return true;
    }

    public void setPath(String path) {
        String[] col = {"name", "absolutePath", "img", "_id"};
        MatrixCursor data = new MatrixCursor(col);
        File dir = new File(path);
        current = dir;
        File[] files = dir.listFiles();
        if (files != null) {
            int n = 0;
            for (File children : files) {
                Object[] row = new Object[4];
                row[3] = "" + ++n;
                row[0] = children.getName();
                row[1] = children.getAbsolutePath();
                if (children.isDirectory())
                    row[2] = R.drawable.folder;
                else
                    row[2] = R.drawable.unknown;
                data.addRow(row);
            }
        }
        mAdapter.swapCursor(data);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        File file = new File((String) ((GridLayout) v).getChildAt(1).getTag());

        // File file = new File((String) v.getTag());
        if (file.isDirectory())
            setPath(file.getAbsolutePath());
        else {
            Intent intent = new Intent(this, FileSelect.class);
            intent.putExtra("path", file.getAbsolutePath());
            // intent.putExtra("appWidgetId", id);
            setResult(100, intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_parent:
                String parent = current.getParent();
                if (parent != null)
                    setPath(parent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
