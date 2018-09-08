package com.learning.firhan.aquacare;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learning.firhan.aquacare.Adapters.AquariumListAdapter;
import com.learning.firhan.aquacare.Adapters.FishListAdapter;
import com.learning.firhan.aquacare.Constants.ActivityResultsCode;
import com.learning.firhan.aquacare.Fragments.CameraOrGalleryDialogFragment;
import com.learning.firhan.aquacare.Fragments.HomeFragment;
import com.learning.firhan.aquacare.Helpers.AquariumSQLiteHelper;
import com.learning.firhan.aquacare.Helpers.CommonHelper;
import com.learning.firhan.aquacare.Helpers.FishHelper;
import com.learning.firhan.aquacare.Helpers.FishSQLiteHelper;
import com.learning.firhan.aquacare.Helpers.StorageHelper;
import com.learning.firhan.aquacare.Models.AquariumModel;
import com.learning.firhan.aquacare.Models.FishModel;

import java.io.File;
import java.util.ArrayList;

public class AquariumDetailActivity extends AppCompatActivity {
    private static final String TAG = "AquariumDetailActivity";
    //sqlite
    public FishSQLiteHelper fishSQLiteHelper;
    public AquariumSQLiteHelper aquariumSQLiteHelper;

    //helpers
    FishHelper fishHelper;
    StorageHelper storageHelper;

    //View
    LayoutInflater layoutInflater;
    AquariumModel aquariumModel;
    Toolbar aquariumDetailToolbar;
    ImageView aquariumPhoto;
    FloatingActionButton addFishButton;
    public RecyclerView fishListRecyclerView;
    public FishListAdapter fishListAdapter;
    public ArrayList<FishModel> fishModels;
    TextView detailAquariumName, detailAquariumDescription, detailLength, detailHeight, detailWide;

    //globar variable
    private Boolean isNewFish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aquarium_detail);

        initIds();

        setToolbar();

        //bring add fish button to front
        addFishButton.bringToFront();
        addFishButton.invalidate();

        storageHelper = new StorageHelper();
        fishHelper = new FishHelper();
        layoutInflater = getLayoutInflater();
        fishSQLiteHelper = new FishSQLiteHelper(getApplicationContext());
        aquariumSQLiteHelper = new AquariumSQLiteHelper(getApplicationContext());

        setAddFishButtonListener();

        //check aquarium model from intent
        getAquariumFromIntentExtras(getIntent());

        populateFish(false);
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

    protected void getAquariumFromIntentExtras(Intent intent){
        if(intent!=null){
            try {
                aquariumModel = getIntent().getParcelableExtra("aquarium");
                //populte aquarium detail
                populateAquariumDetail(aquariumModel);
            }catch (Exception ex){
                Log.d(TAG, "populateAquariumDetail: "+ex.getMessage());
            }
        }
    }

    private void rePopulateAquariumDetail(int id){
        Log.d(TAG, "rePopulateAquariumDetail: "+id);
        aquariumModel = aquariumSQLiteHelper.getAquariumById(id);
        populateAquariumDetail(aquariumModel);
    }

    private void populateAquariumDetail(AquariumModel aquariumModel){
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
        }else{
            Log.d(TAG, "populateAquariumDetail: Aquarium Model is NULL");
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
            isNewFish = true;
            populateFish(true);
        }else if(requestCode==ActivityResultsCode.EDIT_FISH && resultCode==Activity.RESULT_OK){
            populateFish(true);
        }else if(requestCode==ActivityResultsCode.EDIT_AQUARIUM && resultCode==Activity.RESULT_OK){
            rePopulateAquariumDetail(aquariumModel.getId());
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

    @Override
    public void onBackPressed() {
        finishWithResult();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: "+item.getItemId()+" = "+android.R.id.home);
        switch (item.getItemId()){
            case android.R.id.home:
                finishWithResult();
                break;
            case R.id.aquariumEditMenu:
                //Goto Edit Aquarium
                Intent intent = new Intent(getApplicationContext(), AddAquariumActivity.class);
                intent.putExtra("aquariumModel", aquariumModel);
                startActivityForResult(intent, ActivityResultsCode.EDIT_AQUARIUM);
                break;
            case R.id.aquariumDeleteMenu:
                String messageText = "Delete "+aquariumModel.getName()+"? (All fish inside this aquarium will also be deleted)";
                showDeleteDialog("Delete Anyway", "Cancel", messageText, aquariumModel.getId());
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu_aquarium, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void finishWithResult(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        setResult(ActivityResultsCode.REFRESH_LATEST_FISH, mainIntent);
        finish();
    }

    private void showDeleteDialog(String yesLabel, String noLabel, String messageText, final int parameterId){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AppTheme_Dialog));
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

    public void populateFish(Boolean isRefreshing){
        new PopulateFishTask(isRefreshing).execute();
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
                FishModel fishModel = fishHelper.convertCursorToModel(fishes);
                fishModels.add(fishModel);

                Log.d(TAG, "Fish Name: "+fishes.getString(5));
            }
        }

        private void setFishRecyclerView(ArrayList<FishModel> fishModels){
            Log.d(TAG, "setFishRecyclerView: ");
            
            //set layout manager
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);

            //set adapter
            fishListAdapter = new FishListAdapter(fishModels, AquariumDetailActivity.this);

            fishListRecyclerView.setHasFixedSize(false);
            fishListRecyclerView.setLayoutManager(gridLayoutManager);
            fishListRecyclerView.setAdapter(fishListAdapter);
        }

        @Override
        protected void onPostExecute(Object o) {
            if(!isRefreshList){
                //first populate
                setFishRecyclerView(fishModels);
            }else{
                fishListAdapter.notifyDataSetChanged();
            }
        }
    }

    class DeleteAquariumTask extends AsyncTask{
        AquariumSQLiteHelper aquariumSQLiteHelper;
        FishSQLiteHelper fishSQLiteHelper;
        Boolean isSuccess = false;
        private int aquariumId;

        public DeleteAquariumTask(int aquariumId) {
            aquariumSQLiteHelper = new AquariumSQLiteHelper(getApplicationContext());
            fishSQLiteHelper = new FishSQLiteHelper(getApplicationContext());
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

            if(isSuccess){
                CommonHelper commonHelper = new CommonHelper();
                commonHelper.showToast(getApplicationContext(), "Deleting Success!");

                //goto main activity
                finishWithResult();
            }
        }
    }
}
