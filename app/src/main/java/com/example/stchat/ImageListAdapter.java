package com.example.stchat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {

    private Context context;
    private ArrayList<String> mImageURIList;
    public ImageListAdapter(Context context, ArrayList<String> mImageURIList){
        this.context = context;
        this.mImageURIList = mImageURIList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, null, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(layoutView);


        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        Glide.with(context).load(Uri.parse(mImageURIList.get(position))).into(holder.mMediaItem);
        
    }

    @Override
    public int getItemCount() {
        return mImageURIList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView mMediaItem;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mMediaItem = itemView.findViewById(R.id.mediaItem);
        }
    }
}
