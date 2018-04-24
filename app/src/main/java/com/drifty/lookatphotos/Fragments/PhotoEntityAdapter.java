package com.drifty.lookatphotos.Fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drifty.lookatphotos.LoadPhotos.PhotoEntity;
import com.drifty.lookatphotos.R;

import java.util.List;

public class PhotoEntityAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private LayoutInflater inflater;
    private List<PhotoEntity> photoEntities;
    private boolean isPortrait;
    private Context context;

    public PhotoEntityAdapter(Context context, List<PhotoEntity> photoEntities, boolean isPortrait) {
        inflater = LayoutInflater.from(context);
        this.photoEntities = photoEntities;
        this.isPortrait = isPortrait;
        this.context = context;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.icon_of_photo, parent, false);
        return new PhotoViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        PhotoEntity photo = photoEntities.get(position);
        holder.setUrl(photo.getOrigUrl());
        holder.setPhoto(isPortrait ? photo.getPortraitIcon() : photo.getLandscapeIcon());
    }

    @Override
    public int getItemCount() {
        return photoEntities.size();
    }
}
