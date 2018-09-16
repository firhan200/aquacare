package com.learning.firhan.aquacare.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learning.firhan.aquacare.AddAquariumActivity;
import com.learning.firhan.aquacare.AddFishActivity;
import com.learning.firhan.aquacare.AquariumDetailActivity;
import com.learning.firhan.aquacare.Constants.ActivityResultsCode;
import com.learning.firhan.aquacare.FishDetailActivity;
import com.learning.firhan.aquacare.Helpers.CommonHelper;
import com.learning.firhan.aquacare.Helpers.FishHelper;
import com.learning.firhan.aquacare.Helpers.FishSQLiteHelper;
import com.learning.firhan.aquacare.Models.FishModel;
import com.learning.firhan.aquacare.R;

import java.util.ArrayList;

public class FishListAdapter extends RecyclerView.Adapter<FishListAdapter.ViewHolder> {
    private static final String TAG = "FishListAdapter";
    public ArrayList<FishModel> fishModels;
    Context context;

    //sqlite
    FishSQLiteHelper fishSQLiteHelper;

    //helpers
    CommonHelper commonHelper;
    FishHelper fishHelper;

    public FishListAdapter(ArrayList<FishModel> fishModels, Context context) {
        this.fishModels = fishModels;
        this.context = context;

        fishSQLiteHelper = new FishSQLiteHelper(context);
        commonHelper = new CommonHelper();
        fishHelper = new FishHelper();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d(TAG, "onAttachedToRecyclerView: ");
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: ");

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_item_fish, viewGroup, false);

        ViewHolder viewHolder = new FishListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final FishModel fishModel = fishModels.get(i);

        viewHolder.listItemFishName.setText(fishModel.getName());
        viewHolder.listItemFishType.setText(fishModel.getType());
        viewHolder.listItemFishAge.setText(fishHelper.getAgeInDays(fishModel.getPurchaseDate()));

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

        //set menu
        viewHolder.listItemFishMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_aquarium, popupMenu.getMenu());
                popupMenu.show();
                setPopUpMenuOnClickListener(fishModel, popupMenu);
            }
        });

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

    private void setPopUpMenuOnClickListener(final FishModel fishModel, PopupMenu popupMenu){
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.aquariumEditMenu:
                        //Goto Edit Fish
                        Intent intent = new Intent(context, AddFishActivity.class);
                        intent.putExtra("fishModel", fishModel);
                        ((Activity)context).startActivityForResult(intent, ActivityResultsCode.EDIT_FISH);
                        break;
                    case R.id.aquariumDeleteMenu:
                        String messageText = "Delete "+fishModel.getName()+"?";
                        showDeleteDialog("Yes", "Cancel", messageText, fishModel.getId());
                        break;
                }
                return false;
            }
        });
    }

    private void showDeleteDialog(String yesLabel, String noLabel, String messageText, final int parameterId){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme_Dialog));
        builder.setMessage(messageText)
                .setPositiveButton(yesLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteFishTask(parameterId).execute();
                    }
                })
                .setNegativeButton(noLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    @Override
    public int getItemCount() {
        return fishModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageButton listItemFishMenu;
        TextView listItemFishName,listItemFishType,listItemFishAge;
        ImageView listItemFishPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemFishName = (TextView)itemView.findViewById(R.id.listItemFishName);
            listItemFishType = (TextView)itemView.findViewById(R.id.listItemFishType);
            listItemFishAge = (TextView)itemView.findViewById(R.id.listItemFishAge);
            listItemFishPhoto = (ImageView)itemView.findViewById(R.id.listItemfishPhoto);
            listItemFishMenu = (ImageButton)itemView.findViewById(R.id.listItemFishMenu);
        }
    }

    class DeleteFishTask extends AsyncTask{
        private int fishId;
        private Boolean isSuccess;

        public DeleteFishTask(int fishId) {
            this.fishId = fishId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSuccess = false;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            //delete fish
            int deletedId = fishSQLiteHelper.deleteData(fishId);
            if(deletedId > 0){
                isSuccess = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if(isSuccess){
                //show toast
                commonHelper.showToast(context, "Delete fish success.");

                //refresh list
                ((AquariumDetailActivity)context).populateFish(true);
            }else{
                //show toast
                commonHelper.showToast(context, "Delete fish failed.");
            }
        }
    }
}
