package com.example.wavezcellular.adapters_holders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;



import com.example.wavezcellular.Interfaces.PhotoListener;
import com.example.wavezcellular.R;

import java.util.List;

public class UserPhotoAdapter extends RecyclerView.Adapter<UserPhotoHolder>{
    Context context;
    private List<String> userImages;
    private PhotoListener photoListener;
    private int selectedItemPosition = -1;

    public UserPhotoAdapter(Context context, List<String> imageNames, PhotoListener photoListener) {
        this.context = context;
        this.userImages = imageNames;
        this.photoListener = photoListener;
    }


    @NonNull
    @Override
    public UserPhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserPhotoHolder(LayoutInflater.from(context).inflate(R.layout.user_photo_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserPhotoHolder holder, int position) {
        // Set the background color based on the selected item position
        if (selectedItemPosition == position) {
            holder.layout.setBackgroundResource(R.drawable.rounded_square_selected); // Change to your desired color
        } else {
            holder.layout.setBackgroundResource(R.drawable.rounded_square_background); // Replace with your original background drawable
        }

        String imageName = userImages.get(position);
        int resourceId = holder.itemView.getContext().getResources().getIdentifier(imageName, "drawable", holder.itemView.getContext().getPackageName());
        holder.photo.setImageResource(resourceId);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the selected item position
                int previousSelectedItemPosition = selectedItemPosition;
                selectedItemPosition = holder.getAdapterPosition(); // Use getAdapterPosition() to retrieve the position
                // Notify the adapter of the item changes
                notifyItemChanged(previousSelectedItemPosition);
                notifyItemChanged(selectedItemPosition);
                // Retrieve the image name using the adapter position
                String imageName = userImages.get(holder.getAdapterPosition());
                photoListener.onPhotoClick(imageName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userImages.size();
    }
}
