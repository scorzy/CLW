package it.lorenzo.clw.chooser;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lorenzo on 14/10/16.
 */

public class Example {
    public String caption;
    public int img;
    public int file;
    public File path;


    public void saveToDisk(Context context) {
        File clwDir = new File(Environment.getExternalStorageDirectory(), "CLW-examples");
        if (clwDir.isDirectory() || clwDir.mkdirs()) {
            String filename = caption + ".txt";
            File file2 = new File(clwDir, filename);
            try {
                CopyRAWtoSDCard(this.file, file2.getAbsolutePath(), context);
            } catch (IOException ioException) {
                CharSequence text = "Can't write example file: " + filename;
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            path = file2;
        }
    }

    private void CopyRAWtoSDCard(int id, String path, Context context) throws IOException {
        InputStream in = context.getResources().openRawResource(id);
        FileOutputStream out = new FileOutputStream(path);
        byte[] buff = new byte[1024];
        int read;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}