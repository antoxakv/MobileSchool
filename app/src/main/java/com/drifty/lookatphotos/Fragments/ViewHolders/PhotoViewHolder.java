package com.drifty.lookatphotos.Fragments.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.drifty.lookatphotos.Activities.ShowPhoto;
import com.drifty.lookatphotos.Fragments.Adapters.PhotoEntityAdapter;
import com.drifty.lookatphotos.R;

import java.util.ArrayList;

public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView photoView;
    private int position;
    private Context context;
    private ArrayList<String> photosUrls;
    private PhotoEntityAdapter pea;

    public PhotoViewHolder(View itemView, Context context, ArrayList<String> photosUrls, PhotoEntityAdapter pea) {
        super(itemView);
        photoView = itemView.findViewById(R.id.photo);
        itemView.setOnClickListener(this);
        this.context = context;
        this.photosUrls = photosUrls;
        this.pea = pea;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, ShowPhoto.class);
        //Индекс url, фото которого необходимо отобразить.
        intent.putExtra(ShowPhoto.POSITION, position);
        //Передаем url'и полных размеров для swipe'ов фотографий в ShowPhoto.
        intent.putExtra(ShowPhoto.URLS, photosUrls);
        context.startActivity(intent);
    }

    public void setPhoto(Bitmap photoBitmap) {
        photoView.setImageBitmap(photoBitmap);
    }

    public void setPosition(int position) {
        this.position = position;
    }

}