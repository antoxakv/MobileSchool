package com.drifty.lookatphotos.Fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drifty.lookatphotos.LoadPhotos.PhotoEntity;
import com.drifty.lookatphotos.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

    private LayoutInflater inflater;
    private List<PhotoEntity> photoEntities;
    private boolean isPortrait;

    public MyAdapter(Context context, List<PhotoEntity> photoEntities, boolean isPortrait) {
        inflater = LayoutInflater.from(context);
        this.photoEntities = photoEntities;
        this.isPortrait = isPortrait;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.icon_of_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhotoEntity photo = photoEntities.get(position);
        holder.getProgressBar().setVisibility(View.INVISIBLE);
        holder.getPhoto().setImageBitmap(isPortrait ? photo.getPortraitIcon() : photo.getLandscapeIcon());
    }

    @Override
    public int getItemCount() {
        return photoEntities.size();
    }

    public void setPhotoEntities(List<PhotoEntity> photoEntities) {
        this.photoEntities = photoEntities;
        notifyDataSetChanged();
    }
}
