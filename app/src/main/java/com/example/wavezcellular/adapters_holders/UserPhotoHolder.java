package com.example.wavezcellular.adapters_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wavezcellular.R;

import com.google.android.material.imageview.ShapeableImageView;

public class UserPhotoHolder extends RecyclerView.ViewHolder {

    ShapeableImageView photo;
    ProgressBar progress;
    RelativeLayout layout;


    public UserPhotoHolder(@NonNull View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photo_IMG_picture);
        progress = itemView.findViewById(R.id.photo_PRB_progressBar);
        layout = itemView.findViewById(R.id.photo_REL_layout);
    }
}
