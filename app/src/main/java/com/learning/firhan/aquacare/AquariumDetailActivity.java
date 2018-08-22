package com.learning.firhan.aquacare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.learning.firhan.aquacare.Constants.ActivityResultsCode;
import com.learning.firhan.aquacare.Fragments.CameraOrGalleryDialogFragment;
import com.learning.firhan.aquacare.Helpers.StorageHelper;
import com.learning.firhan.aquacare.Models.AquariumModel;

import java.io.File;

public class AquariumDetailActivity extends AppCompatActivity {
    private static final String TAG = "AquariumDetailActivity";
    LayoutInflater layoutInflater;
    AquariumModel aquariumModel;
    Toolbar aquariumDetailToolbar;
    ImageView aquariumPhoto;
    FloatingActionButton addFishButton;
    StorageHelper storageHelper;
    TextView detailAquariumName, detailAquariumDescription, detailLength, detailHeight, detailWide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aquarium_detail);

        //init id
        aquariumDetailToolbar = (Toolbar)findViewById(R.id.aquariumDetailToolbar);
        aquariumPhoto = (ImageView)findViewById(R.id.detailAquariumPhoto);
        detailAquariumName = (TextView)findViewById(R.id.detailAquariumName);
        detailAquariumDescription = (TextView)findViewById(R.id.detailAquariumDescription);
        detailLength = (TextView)findViewById(R.id.detailLength);
        detailHeight = (TextView)findViewById(R.id.detailHeight);
        detailWide = (TextView)findViewById(R.id.detailWide);
        addFishButton = (FloatingActionButton)findViewById(R.id.addFishButton);

        //set custom toolbar
        setSupportActionBar(aquariumDetailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //bring add fish button to front
        addFishButton.bringToFront();
        addFishButton.invalidate();

        storageHelper = new StorageHelper();
        layoutInflater = getLayoutInflater();

        //set listener
        addFishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraOrGalleryDialogFragment cameraOrGalleryDialogFragment = new CameraOrGalleryDialogFragment();
                cameraOrGalleryDialogFragment.show(getSupportFragmentManager(), "CameraOrGallery");
            }
        });

        populateAquariumDetail();
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

            //goto add aquarium activity
            Intent addFistIntent = new Intent(this, AddFishActivity.class);
            addFistIntent.putExtra("image", photo);
            startActivityForResult(addFistIntent, ActivityResultsCode.ADD_FISH);
        }else if(requestCode==ActivityResultsCode.GALLERY_REQUEST && resultCode==Activity.RESULT_OK){
            Uri uri = data.getData();

            try {
                // Get the path from the Uri
                final String path = storageHelper.getPathFromURI(uri, getContentResolver());
                if (path != null) {
                    File file = new File(path);
                    uri = Uri.fromFile(file);
                }

                //goto add aquarium activity
                Intent addFistIntent = new Intent(this, AddFishActivity.class);
                addFistIntent.putExtra("imageUri", uri.toString());
                startActivityForResult(addFistIntent, ActivityResultsCode.ADD_FISH);
            } catch (Exception ex) {
                Log.d(TAG, "onActivityResult: "+ex.getMessage());
            }
        }
    }
}
