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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learning.firhan.aquacare.AddAquariumActivity;
import com.learning.firhan.aquacare.AquariumDetailActivity;
import com.learning.firhan.aquacare.Constants.ActivityResultsCode;
import com.learning.firhan.aquacare.Fragments.HomeFragment;
import com.learning.firhan.aquacare.Helpers.AquariumSQLiteHelper;
import com.learning.firhan.aquacare.Helpers.CommonHelper;
import com.learning.firhan.aquacare.Helpers.FishSQLiteHelper;
import com.learning.firhan.aquacare.Interfaces.IConfirmationDialog;
import com.learning.firhan.aquacare.Interfaces.IMainActivity;
import com.learning.firhan.aquacare.MainActivity;
import com.learning.firhan.aquacare.Models.AquariumModel;
import com.learning.firhan.aquacare.R;

import java.util.ArrayList;

public class AquariumListAdapter extends RecyclerView.Adapter<AquariumListAdapter.ViewHolder> implements IConfirmationDialog {
    private static final String TAG = "AquariumListAdapter";
    ArrayList<AquariumModel> aquariumModels;
    Context context;
    IMainActivity iMainActivity;
    HomeFragment homeFragment;

    public AquariumListAdapter(ArrayList<AquariumModel> aquariumModels, Context context, HomeFragment homeFragment) {
        this.aquariumModels = aquariumModels;
        this.context = context;
        this.homeFragment = homeFragment;

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
                ((Activity)context).startActivityForResult(aquariumDetailIntent, ActivityResultsCode.AQUARIUM_DETAIL);
            }
        });

        viewHolder.listItemAquariumMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_aquarium, popupMenu.getMenu());
                popupMenu.show();
                setPopUpMenuOnClickListener(aquariumModel, popupMenu);
            }
        });
    }

    private void setPopUpMenuOnClickListener(final AquariumModel aquariumModel,PopupMenu popupMenu){
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.aquariumEditMenu:
                        //Goto Edit Aquarium
                        Intent intent = new Intent(context, AddAquariumActivity.class);
                        intent.putExtra("aquariumModel", aquariumModel);
                        ((Activity)context).startActivityForResult(intent, ActivityResultsCode.EDIT_AQUARIUM);
                        break;
                    case R.id.aquariumDeleteMenu:
                        String messageText = "Delete "+aquariumModel.getName()+"? (All fish inside this aquarium will also be deleted)";
                        showDeleteDialog("Delete Anyway", "Cancel", messageText, aquariumModel.getId());
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return aquariumModels.size();
    }

    @Override
    public void positiveAction(int parameterId) {
        CommonHelper commonHelper = new CommonHelper();
        commonHelper.showToast(context,"deleting "+parameterId);
    }

    @Override
    public void negativeAction() {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView listItemAquariumPhoto;
        TextView listItemAquariumName,listItemAquariumDesc;
        ImageButton listItemAquariumMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemAquariumPhoto = (ImageView)itemView.findViewById(R.id.listItemAquariumPhoto);
            listItemAquariumName = (TextView)itemView.findViewById(R.id.listItemAquariumName);
            listItemAquariumDesc = (TextView)itemView.findViewById(R.id.listItemAquariumDesc);
            listItemAquariumMenu = (ImageButton)itemView.findViewById(R.id.listItemAquariumMenu);
        }
    }

    private void showDeleteDialog(String yesLabel, String noLabel, String messageText, final int parameterId){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(messageText)
                .setPositiveButton(yesLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DeleteAquariumTask(parameterId).execute();
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

    class DeleteAquariumTask extends AsyncTask {
        AquariumSQLiteHelper aquariumSQLiteHelper;
        FishSQLiteHelper fishSQLiteHelper;
        Boolean isSuccess = false;
        private int aquariumId;

        public DeleteAquariumTask(int aquariumId) {
            aquariumSQLiteHelper = new AquariumSQLiteHelper(context);
            fishSQLiteHelper = new FishSQLiteHelper(context);
            this.aquariumId = aquariumId;
        }

        @Override
        protected void onPreExecute() {
            //getDialog().dismiss();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            int deleteRowID = aquariumSQLiteHelper.deleteData(aquariumId);
            if(deleteRowID > 0){
                //deleting fish
                fishSQLiteHelper.deleteFishByAquariumId(aquariumId);

                isSuccess = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            CommonHelper commonHelper = new CommonHelper();
            commonHelper.showToast(context, "Deleting Success!");
            homeFragment.populateAquariumList(true);
            homeFragment.populateFishList(true);
        }
    }
}
