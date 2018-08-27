package com.learning.firhan.aquacare.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learning.firhan.aquacare.Models.FishModel;
import com.learning.firhan.aquacare.R;

import java.util.ArrayList;

public class FishListAdapter extends RecyclerView.Adapter<FishListAdapter.ViewHolder> {
    private static final String TAG = "FishListAdapter";
    public ArrayList<FishModel> fishModels;

    public FishListAdapter(ArrayList<FishModel> fishModels) {
        this.fishModels = fishModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_item_fish, viewGroup, false);

        ViewHolder viewHolder = new FishListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        FishModel fishModel = fishModels.get(i);
        viewHolder.listItemFishName.setText(fishModel.getName());
        viewHolder.listItemFishType.setText(fishModel.getType());

        String thumbnail = fishModel.getImageThumbnailUri();

        if(thumbnail!=null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inSampleSize = 16;
                final Bitmap aquariumBitmap = BitmapFactory.decodeFile(fishModel.getImageThumbnailUri(), options);

                viewHolder.listItemFishPhoto.setVisibility(View.VISIBLE);

                /* Glide */
                RequestOptions cropOptions = new RequestOptions().centerCrop();
                Glide
                        .with(viewHolder.itemView)
                        .load(aquariumBitmap)
                        .apply(cropOptions)
                        .into(viewHolder.listItemFishPhoto);
            } catch (Exception ex) {
                Log.d(TAG, "onBindViewHolder: " + ex.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return fishModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView listItemFishName,listItemFishType;
        ImageView listItemFishPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemFishName = (TextView)itemView.findViewById(R.id.listItemFishName);
            listItemFishType = (TextView)itemView.findViewById(R.id.listItemFishType);
            listItemFishPhoto = (ImageView)itemView.findViewById(R.id.listItemfishPhoto);
        }
    }
}
