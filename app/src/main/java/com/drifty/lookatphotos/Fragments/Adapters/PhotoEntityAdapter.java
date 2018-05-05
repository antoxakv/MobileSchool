package com.drifty.lookatphotos.Fragments.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.drifty.lookatphotos.Fragments.ViewHolders.LoadingViewHolder;
import com.drifty.lookatphotos.Fragments.ViewHolders.PhotoViewHolder;
import com.drifty.lookatphotos.LoadPhotos.Tools.PhotoEntity;
import com.drifty.lookatphotos.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoEntityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private List<PhotoEntity> photoEntities;
    private ArrayList<String> photosUrls;
    private boolean isPortrait;
    private Context context;
    private boolean isClickable;

    public final static int PHOTO_VIEW = 0;
    public final static int LOADING_VIEW = 1;

    public PhotoEntityAdapter(Context context, final List<PhotoEntity> photoEntities, final boolean isPortrait) {
        inflater = LayoutInflater.from(context);
        this.photoEntities = photoEntities;
        this.isPortrait = isPortrait;
        this.context = context;
        photosUrls = new ArrayList<>(photoEntities.size());
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                photosUrls.clear();
                isClickable = true;
                for (PhotoEntity pe : photoEntities) {
                    photosUrls.add(pe.getOrigUrl());
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (itemCount == 1) {
                    isClickable = false;
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (itemCount == 1) {
                    isClickable = true;
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case PHOTO_VIEW:
                viewHolder = new PhotoViewHolder(inflater.inflate(R.layout.icon_of_photo, parent, false), context, photosUrls, this);
                break;
            case LOADING_VIEW:
                viewHolder = new LoadingViewHolder(inflater.inflate(R.layout.item_loading, parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder pvh = (PhotoViewHolder) holder;
            PhotoEntity photo = photoEntities.get(position);
            pvh.setPosition(position);
            pvh.setPhoto(isPortrait ? photo.getPortraitIcon() : photo.getLandscapeIcon());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return photoEntities.get(position) == null ? LOADING_VIEW : PHOTO_VIEW;
    }

    @Override
    public int getItemCount() {
        return photoEntities.size();
    }

    public boolean isClickable() {
        return isClickable;
    }
}