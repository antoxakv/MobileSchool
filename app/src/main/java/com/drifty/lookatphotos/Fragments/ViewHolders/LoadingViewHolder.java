package com.drifty.lookatphotos.Fragments.ViewHolders;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ProgressBar;

import com.drifty.lookatphotos.R;

public class LoadingViewHolder extends ViewHolder {
    private ProgressBar progressBar;

    public LoadingViewHolder(View view) {
        super(view);
        progressBar = view.findViewById(R.id.progressBar);
    }
}
