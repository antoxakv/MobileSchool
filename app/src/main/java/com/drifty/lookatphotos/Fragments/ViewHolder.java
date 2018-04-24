package com.drifty.lookatphotos.Fragments;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.drifty.lookatphotos.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    private ImageView photo;
    private ProgressBar progressBar;

    public ViewHolder(View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photo);
        progressBar = itemView.findViewById(R.id.progressBar);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("debug", "test");
            }
        });
    }

    public ImageView getPhoto() {
        return photo;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
