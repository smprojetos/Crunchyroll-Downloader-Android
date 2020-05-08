package com.hama.crdl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<ExampleItem> mExampleList;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public ProgressBar mProgressbar1;


    public ExampleViewHolder(View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.imageView);
        mTextView1 = itemView.findViewById(R.id.textView);
        mTextView2 = itemView.findViewById(R.id.textView2);
        mProgressbar1 = itemView.findViewById(R.id.Progressbar1);

    }
    }
    public ExampleAdapter(ArrayList<ExampleItem> exampleList) {
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.example_item, viewGroup, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder exampleViewHolder, int i) {
        ExampleItem currentItem = mExampleList.get(i);
        Picasso.get().load(currentItem.getImageResource()).into( exampleViewHolder.mImageView);
        //exampleViewHolder.mImageView.setImageResource(currentItem.getImageResource());
        exampleViewHolder.mTextView1.setText(currentItem.getTextTitle());
        exampleViewHolder.mTextView2.setText(currentItem.getTextProgess());
        exampleViewHolder.mProgressbar1.setProgress(currentItem.getmIntProgess());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
