package com.learning.firhan.aquacare.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learning.firhan.aquacare.AquariumDetailActivity;
import com.learning.firhan.aquacare.Interfaces.IMainActivity;
import com.learning.firhan.aquacare.MainActivity;
import com.learning.firhan.aquacare.Models.AquariumModel;
import com.learning.firhan.aquacare.R;

import java.io.IOException;
import java.util.ArrayList;

public class AquariumListAdapter extends RecyclerView.Adapter<AquariumListAdapter.ViewHolder> {
    private static final String TAG = "AquariumListAdapter";
    ArrayList<AquariumModel> aquariumModels;
    Context context;
    IMainActivity iMainActivity;

    public AquariumListAdapter(ArrayList<AquariumModel> aquariumModels, Context context) {
        this.aquariumModels = aquariumModels;
        this.context = context;

        iMainActivity = (MainActivity)context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_item_aquarium, viewGroup, false);

        ViewHolder viewHolder = new AquariumListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final AquariumModel aquariumModel = aquariumModels.get(i);

        String thumbnail = aquariumModel.getImageThumbnailUri();

        if(thumbnail!=null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inSampleSize = 16;
                final Bitmap aquariumBitmap = BitmapFactory.decodeFile(aquariumModel.getImageThumbnailUri(), options);

                viewHolder.listItemAquariumPhoto.setVisibility(View.VISIBLE);

                /* Glide */
                RequestOptions cropOptions = new RequestOptions().circleCrop();
                Glide
                        .with(viewHolder.itemView)
                        .load(aquariumBitmap)
                        .apply(cropOptions)
                        .into(viewHolder.listItemAquariumPhoto);
            } catch (Exception ex) {
                Log.d(TAG, "onBindViewHolder: " + ex.getMessage());
            }
        }
        viewHolder.listItemAquariumName.setText(aquariumModel.getName());
        viewHolder.listItemAquariumDesc.setText(aquariumModel.getDescription());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start aquarium detail activity
                Intent aquariumDetailIntent = new Intent(context, AquariumDetailActivity.class);
                aquariumDetailIntent.putExtra("aquarium", aquariumModel);
                context.startActivity(aquariumDetailIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return aquariumModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView listItemAquariumPhoto;
        TextView listItemAquariumName,listItemAquariumDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemAquariumPhoto = (ImageView)itemView.findViewById(R.id.listItemAquariumPhoto);
            listItemAquariumName = (TextView)itemView.findViewById(R.id.listItemAquariumName);
            listItemAquariumDesc = (TextView)itemView.findViewById(R.id.listItemAquariumDesc);
        }
    }
}
