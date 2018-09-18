package com.learning.firhan.aquacare.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.learning.firhan.aquacare.FishDetailActivity;
import com.learning.firhan.aquacare.Helpers.FishHelper;
import com.learning.firhan.aquacare.Models.FishModel;
import com.learning.firhan.aquacare.R;

import java.util.ArrayList;

public class SearchFishListAdapter extends RecyclerView.Adapter<SearchFishListAdapter.ViewHolder> {
    private static final String TAG = "SearchFishListAdapter";
    
    Context context;
    ArrayList<FishModel> fishModels;

    FishHelper fishHelper;

    public SearchFishListAdapter(Context context, ArrayList<FishModel> fishModels) {
        this.context = context;
        this.fishModels = fishModels;

        fishHelper = new FishHelper();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_item_search_fish, viewGroup, false);

        ViewHolder viewHolder = new SearchFishListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final FishModel fishModel = fishModels.get(i);

        //bind to view
        viewHolder.listItemFishName.setText(fishModel.getName());
        viewHolder.listItemFishType.setText(fishModel.getType());
        viewHolder.listItemFishAge.setText(fishHelper.getAgeInDays(fishModel.getPurchaseDate()));

        //bind image
        String thumbnail = fishModel.getImageThumbnailUri();

        if(thumbnail!=null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inSampleSize = 16;
                final Bitmap fishBitmap = BitmapFactory.decodeFile(fishModel.getImageThumbnailUri(), options);

                viewHolder.listItemFishPhoto.setVisibility(View.VISIBLE);

                /* Glide */
                RequestOptions cropOptions = new RequestOptions().circleCrop();
                Glide
                        .with(viewHolder.itemView)
                        .load(fishBitmap)
                        .apply(cropOptions)
                        .into(viewHolder.listItemFishPhoto);
            } catch (Exception ex) {
                Log.d(TAG, "onBindViewHolder: " + ex.getMessage());
            }
        }

        //fish tap
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to fish detail activity
                Intent intent = new Intent(context, FishDetailActivity.class);
                intent.putExtra("fish", fishModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fishModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView listItemFishPhoto;
        TextView listItemFishName,listItemFishType,listItemFishAge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemFishPhoto = (ImageView)itemView.findViewById(R.id.listItemFishPhoto);
            listItemFishName = (TextView) itemView.findViewById(R.id.listItemFishName);
            listItemFishType = (TextView) itemView.findViewById(R.id.listItemFishType);
            listItemFishAge = (TextView) itemView.findViewById(R.id.listItemFishAge);
        }
    }
}
