package com.drifty.lookatphotos.Fragments.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.drifty.lookatphotos.Activities.ShowPhoto;
import com.drifty.lookatphotos.Fragments.TableOfPhotos;
import com.drifty.lookatphotos.R;

public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView photoView;
    private String url;
    private Context context;

    public PhotoViewHolder(View itemView, Context context) {
        super(itemView);
        photoView = itemView.findViewById(R.id.photo);
        itemView.setOnClickListener(this);
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, ShowPhoto.class);
        intent.putExtra(TableOfPhotos.URL, url);
        context.startActivity(intent);
    }

    public void setPhoto(Bitmap photoBitmap) {
        photoView.setImageBitmap(photoBitmap);
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
