package it.lorenzo.clw.chooser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import it.lorenzo.clw.R;

/**
 * Created by lorenzo on 14/10/16.
 */

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.MyViewHolder> {


    private Context context;
    private ArrayList<Example> examples;

    public ExampleAdapter(Context context, ArrayList<Example> examples) {
        this.context = context;
        this.examples = examples;
    }

    @Override
    public int getItemCount() {
        return examples.size();
    }


    @Override
    public ExampleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.example_row, parent, false);
        return new MyViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Example example = examples.get(position);
        holder.setData(example);
    }

    static public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private Button button;

        public MyViewHolder(View itemView) {

            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            button = (Button) itemView.findViewById(R.id.button3);
        }

        public void setData(Example example) {

            if (example != null) {
                button.setText(example.caption);
                imageView.setImageResource(example.img);
                button.setTag(example.path);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        File path = (File) view.getTag();
                        Intent intent = new Intent(view.getContext(), FileSelect.class);
                        intent.putExtra("path", path);
                        intent.setData(Uri.fromFile(path));
                        ((Activity) view.getContext()).setResult(100, intent);
                        ((Activity) view.getContext()).finish();
                    }
                });
            }
        }
    }

}
