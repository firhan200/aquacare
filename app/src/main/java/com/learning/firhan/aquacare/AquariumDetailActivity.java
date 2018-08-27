package com.learning.firhan.aquacare;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learning.firhan.aquacare.Adapters.FishListAdapter;
import com.learning.firhan.aquacare.Constants.ActivityResultsCode;
import com.learning.firhan.aquacare.Fragments.CameraOrGalleryDialogFragment;
import com.learning.firhan.aquacare.Helpers.FishSQLiteHelper;
import com.learning.firhan.aquacare.Helpers.StorageHelper;
import com.learning.firhan.aquacare.Models.AquariumModel;
import com.learning.firhan.aquacare.Models.FishModel;

import java.io.File;
import java.util.ArrayList;

public class AquariumDetailActivity extends AppCompatActivity {
    private static final String TAG = "AquariumDetailActivity";
    public FishSQLiteHelper fishSQLiteHelper;
    LayoutInflater layoutInflater;
    AquariumModel aquariumModel;
    Toolbar aquariumDetailToolbar;
    ImageView aquariumPhoto;
    FloatingActionButton addFishButton;
    StorageHelper storageHelper;
    public RecyclerView fishListRecyclerView;
    public FishListAdapter fishListAdapter;
    public ArrayList<FishModel> fishModels;
    TextView detailAquariumName, detailAquariumDescription, detailLength, detailHeight, detailWide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aquarium_detail);

        initIds();

        setToolbar();

        //bring add fish button to front
        addFishButton.bringToFront();
        addFishButton.invalidate();

        storageHelper = new StorageHelper();
        layoutInflater = getLayoutInflater();
        fishSQLiteHelper = new FishSQLiteHelper(getApplicationContext());

        setAddFishButtonListener();

        populateAquariumDetail();

        new PopulateFishTask(false).execute();
    }

    private void setAddFishButtonListener(){
        //set listener
        addFishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraOrGalleryDialogFragment cameraOrGalleryDialogFragment = new CameraOrGalleryDialogFragment();
                cameraOrGalleryDialogFragment.show(getSupportFragmentManager(), "CameraOrGallery");
            }
        });
    }

    private void setToolbar(){
        //set custom toolbar
        setSupportActionBar(aquariumDetailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initIds(){
        //init id
        aquariumDetailToolbar = (Toolbar)findViewById(R.id.aquariumDetailToolbar);
        aquariumPhoto = (ImageView)findViewById(R.id.detailAquariumPhoto);
        detailAquariumName = (TextView)findViewById(R.id.detailAquariumName);
        detailAquariumDescription = (TextView)findViewById(R.id.detailAquariumDescription);
        detailLength = (TextView)findViewById(R.id.detailLength);
        detailHeight = (TextView)findViewById(R.id.detailHeight);
        detailWide = (TextView)findViewById(R.id.detailWide);
        addFishButton = (FloatingActionButton)findViewById(R.id.addFishButton);
        fishListRecyclerView = (RecyclerView)findViewById(R.id.fishListRecyclerView);
    }

    private void populateAquariumDetail(){
        if(getIntent()!=null){
            try {
                aquariumModel = getIntent().getParcelableExtra("aquarium");
            }catch (Exception ex){
                Log.d(TAG, "populateAquariumDetail: "+ex.getMessage());
            }
            if(aquariumModel!=null){
                //populate to view
                try{
                    if(!aquariumModel.getImageUri().isEmpty()){
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        final Bitmap aquariumBitmap = BitmapFactory.decodeFile(aquariumModel.getImageUri(), options);

                        RequestOptions createOptions = new RequestOptions().centerCrop();
                        Glide
                                .with(getApplicationContext())
                                .load(aquariumBitmap)
                                .apply(createOptions)
                                .into(aquariumPhoto);
                    }
                }
                catch (Exception ex){
                    Log.d(TAG, "populateAquariumDetail: "+ex.getMessage());
                }


                detailAquariumName.setText(aquariumModel.getName());
                detailAquariumDescription.setText(aquariumModel.getDescription());
                detailLength.setText(String.valueOf(aquariumModel.getAquariumLength()));
                detailHeight.setText(String.valueOf(aquariumModel.getAquariumHeight()));
                detailWide.setText(String.valueOf(aquariumModel.getAquariumWide()));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityResultsCode.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            goToAddFishActivity(requestCode, null, photo, aquariumModel.getId());
        }else if(requestCode==ActivityResultsCode.GALLERY_REQUEST && resultCode==Activity.RESULT_OK){
            Uri uri = data.getData();

            try {
                // Get the path from the Uri
                final String path = storageHelper.getPathFromURI(uri, getContentResolver());
                if (path != null) {
                    File file = new File(path);
                    uri = Uri.fromFile(file);

                    goToAddFishActivity(requestCode, uri, null, aquariumModel.getId());
                }
            } catch (Exception ex) {
                Log.d(TAG, "onActivityResult: "+ex.getMessage());
            }
        }else if(requestCode==ActivityResultsCode.ADD_FISH && resultCode==Activity.RESULT_OK){
            //repopulate fish
            new PopulateFishTask(true).execute();
        }
    }

    private void goToAddFishActivity(int requestCode, Uri uri, Bitmap photo, int aquariumId){
        //goto add aquarium activity
        Intent addFistIntent = new Intent(this, AddFishActivity.class);

        //check if from gallery | Camera
        if(requestCode==ActivityResultsCode.CAMERA_REQUEST){
            addFistIntent.putExtra("image", photo);
        }else{
            addFistIntent.putExtra("imageUri", uri.toString());
        }

        addFistIntent.putExtra("aquariumId", aquariumModel.getId());
        startActivityForResult(addFistIntent, ActivityResultsCode.ADD_FISH);
    }

    class PopulateFishTask extends AsyncTask{
        private static final String TAG = "PopulateFishTask";
        private Boolean isRefreshList = false;

        public PopulateFishTask(Boolean isRefreshList) {
            this.isRefreshList = isRefreshList;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            getFishList();
            return null;
        }

        private void getFishList(){
            Log.d(TAG, "getFishList: ");

            if(isRefreshList){
                fishModels.clear();
            }else{
                fishModels = new ArrayList<>();
            }

            //sqlite query
            Cursor fishes = fishSQLiteHelper.getAllDataByAquariumId(aquariumModel.getId());
            while(fishes.moveToNext()){
                fishModels.add(new FishModel(
                        fishes.getInt(0),
                        fishes.getInt(1),
                        fishes.getString(2),
                        fishes.getString(3),
                        fishes.getString(4),
                        fishes.getString(5),
                        fishes.getString(6),
                        fishes.getString(7)
                ));

                Log.d(TAG, "Fish Name: "+fishes.getString(5));
            }

            if(!isRefreshList){
                //first populate
                setFishRecyclerView(fishModels);
            }else{
                fishListAdapter.notifyDataSetChanged();
            }

        }

        private void setFishRecyclerView(ArrayList<FishModel> fishModels){
            Log.d(TAG, "setFishRecyclerView: ");
            
            //set layout manager
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);

            //set adapter
            fishListAdapter = new FishListAdapter(fishModels);

            fishListRecyclerView.setHasFixedSize(false);
            fishListRecyclerView.setLayoutManager(gridLayoutManager);
            fishListRecyclerView.setAdapter(fishListAdapter);
        }
    }
}
