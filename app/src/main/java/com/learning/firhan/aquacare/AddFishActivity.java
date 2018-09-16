package com.learning.firhan.aquacare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learning.firhan.aquacare.Constants.ActivityResultsCode;
import com.learning.firhan.aquacare.Fragments.CameraOrGalleryDialogFragment;
import com.learning.firhan.aquacare.Fragments.DatePickerDialogFragment;
import com.learning.firhan.aquacare.Helpers.CommonHelper;
import com.learning.firhan.aquacare.Helpers.FishSQLiteHelper;
import com.learning.firhan.aquacare.Helpers.StorageHelper;
import com.learning.firhan.aquacare.Interfaces.IDatePicker;
import com.learning.firhan.aquacare.Models.FishModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AddFishActivity extends AppCompatActivity implements IDatePicker {
    private static final String TAG = "AddFishActivity";

    //set global variable
    RelativeLayout addAquariumMainLayout;
    ImageView fishPhoto;
    int deviceWidth,_aquariumId;
    Bitmap fishBitmap;
    EditText fishName,fishType,fishDescription,purchaseDate;
    CommonHelper commonHelper;
    public Button addFishSubmitButton,fishChangePhotoButton;
    public ProgressBar addFishLoader;
    String purchaseDateString;
    Toolbar addFishToolbar;
    public FishModel fishModel;
    Boolean isPhotoChanged,isEdit;
    StorageHelper storageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fish);
        commonHelper = new CommonHelper();

        //initiate all ids on layout
        initIds();

        //get intent extra
        getIntentExtras();

        //set toolbar
        setAddFishToolbar();

        //check edit or add
        setAddOrEditFish();

        setFishPhoto();

        //set listener
        setFishNameListener();
        setFishTypeListener();
        setFishDescriptionListener();
        setPurchaseDateListener();
        setAddFishSubmitButtonListener();
    }

    private void getIntentExtras(){
        Intent intent = getIntent();
        if(intent!=null){
            //there is an extras
            _aquariumId = intent.getIntExtra("aquariumId", 0);
            Log.d(TAG, "getIntentExtras: aquariumId="+_aquariumId);
        }
    }

    private void setAddOrEditFish(){
        isPhotoChanged = false;

        Intent intent = getIntent();
        if(intent!=null){
            //check extra
            if(intent.getExtras()!=null){
                if(intent.getParcelableExtra("fishModel")!=null){
                    fishModel = intent.getParcelableExtra("fishModel");
                    isEdit = true;

                    //init storage helper for get image
                    storageHelper = new StorageHelper();

                    //show change photo button
                    fishChangePhotoButton.setVisibility(View.VISIBLE);
                    //set listener
                    setFishChangePhotoButtonListener();

                    populatePreviousData(fishModel);

                    setTitle("Edit Fish");
                }else{
                    isEdit = false;
                    isPhotoChanged = true;
                }
            }
        }
    }

    private void setFishChangePhotoButtonListener(){
        fishChangePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraOrGalleryDialogFragment cameraOrGalleryDialogFragment = new CameraOrGalleryDialogFragment();
                cameraOrGalleryDialogFragment.show(getSupportFragmentManager(), "CameraOrGallery");
            }
        });
    }

    private void setTitle(String title){
        try {
            getSupportActionBar().setTitle(title);
        }catch (Exception ex){
            Log.d(TAG, "setTitle: "+ex.getMessage());
        }
    }

    private void initIds(){
        //init id
        addAquariumMainLayout = (RelativeLayout)findViewById(R.id.addFishMainLayout);
        fishPhoto = (ImageView)findViewById(R.id.fishPhoto);
        fishName = (EditText)findViewById(R.id.fishName);
        fishType = (EditText)findViewById(R.id.fishType);
        fishDescription = (EditText)findViewById(R.id.fishDescription);
        purchaseDate = (EditText)findViewById(R.id.purchaseDate);
        addFishSubmitButton = (Button)findViewById(R.id.addFishSubmitButton);
        addFishToolbar = (Toolbar)findViewById(R.id.addFishToolbar);
        addFishLoader = (ProgressBar)findViewById(R.id.addFishLoader);
        fishChangePhotoButton = (Button)findViewById(R.id.fishChangePhotoButton);
    }

    private void populatePreviousData(FishModel fishModel){
        fishName.setText(fishModel.getName());
        fishType.setText(fishModel.getType());
        fishDescription.setText(fishModel.getDescription());
        purchaseDate.setText(fishModel.getPurchaseDate());
    }

    private void setAddFishToolbar(){
        //set toolbar
        setSupportActionBar(addFishToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //get image data from Intent then show it
    private void setFishPhoto(){
        if(isEdit){
            //show previous photo
            try{
                if(!fishModel.getImageUri().isEmpty()){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    fishBitmap = BitmapFactory.decodeFile(fishModel.getImageUri(), options);

                    RequestOptions createOptions = new RequestOptions().centerCrop();
                    Glide
                            .with(getApplicationContext())
                            .load(fishBitmap)
                            .apply(createOptions)
                            .into(fishPhoto);
                }
            }
            catch (Exception ex){
                Log.d(TAG, "populateFishDetail: "+ex.getMessage());
            }
        }else {
            //get intent extras
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().get("image") != null) {
                    fishBitmap = (Bitmap) getIntent().getExtras().get("image");
                    setImage(fishBitmap);
                } else if (getIntent().getExtras().get("imageUri") != null) {
                    String imageUri = getIntent().getExtras().getString("imageUri");
                    Uri uri = Uri.parse(imageUri);
                    fishBitmap = null;
                    try {
                        fishBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        setImage(fishBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void setFishNameListener(){
        fishName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    commonHelper.hideKeyboard(getApplicationContext(), v);
                }
            }
        });
    }

    private void setFishTypeListener(){
        fishType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    commonHelper.hideKeyboard(getApplicationContext(), v);
                }
            }
        });
    }

    private void setFishDescriptionListener(){
        fishDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    commonHelper.hideKeyboard(getApplicationContext(), v);
                }
            }
        });
    }

    private void setPurchaseDateListener(){
        purchaseDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    purchaseDate.clearFocus();
                    DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                    datePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });
    }

    private void setAddFishSubmitButtonListener(){
        addFishSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFishValidation(v);
            }
        });
    }

    private void saveFishValidation(View v){
        String fishNameValue = fishName.getText().toString();
        String fishTypeValue = fishType.getText().toString();
        String fishDescriptionValue = fishDescription.getText().toString();
        String purchaseDateValue = purchaseDate.getText().toString();

        if(fishNameValue.equals("")){
            commonHelper.showSnackBar(v, "Fish Name Cannot be Empty!");
            fishName.requestFocus();
            return;
        }

        if(fishTypeValue.equals("")){
            commonHelper.showSnackBar(v, "Fish Type Cannot be Empty!");
            fishType.requestFocus();
            return;
        }

        if(fishDescriptionValue.equals("")){
            commonHelper.showSnackBar(v, "Fish Description Cannot be Empty!");
            fishDescription.requestFocus();
            return;
        }

        if(purchaseDateValue.equals("")){
            commonHelper.showSnackBar(v, "Purchase Date Cannot be Empty!");
            purchaseDate.requestFocus();
            return;
        }

        //all input is OK ready to save fish
        if(isEdit){
            //use created object
            fishModel.setName(fishNameValue);
            fishModel.setType(fishTypeValue);
            fishModel.setDescription(fishDescriptionValue);
            fishModel.setPurchaseDate(purchaseDateValue);
        }else{
            //create new object
            fishModel = new FishModel(
                    0,
                    _aquariumId,
                    "",
                    "",
                    fishNameValue,
                    fishTypeValue,
                    fishDescriptionValue,
                    purchaseDateValue
            );
        }


        new SaveFishTask().execute();
    }

    private void setImage(Bitmap bitmap){
        RequestOptions requestOptions = new RequestOptions().centerCrop();

        Glide
                .with(getApplicationContext())
                .load(bitmap)
                .apply(requestOptions)
                .into(fishPhoto);
    }

    @Override
    public void setDate(String date) {
        purchaseDateString = date;
        purchaseDate.setText(purchaseDateString);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d(TAG, "onOptionsItemSelected: "+item.getItemId());
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ActivityResultsCode.CAMERA_REQUEST && resultCode==Activity.RESULT_OK){
            //get image from camera
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            setImage(fishBitmap);
            isPhotoChanged = true;
        }else if(requestCode==ActivityResultsCode.GALLERY_REQUEST && resultCode==Activity.RESULT_OK){
            //get image from gallery
            Uri uri = data.getData();

            try {
                // Get the path from the Uri
                final String path = storageHelper.getPathFromURI(uri, getContentResolver());
                if (path != null) {
                    File file = new File(path);
                    uri = Uri.fromFile(file);
                    fishBitmap = null;
                    try {
                        fishBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        setImage(fishBitmap);
                        isPhotoChanged = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                Log.d(TAG, "onActivityResult: "+ex.getMessage());
            }
        }
    }

    /*
    ASYNC TASK SECTION
     */
    class SaveFishTask extends AsyncTask{

        FishSQLiteHelper fishSQLiteHelper;
        Boolean isSuccess;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //set loading
            setLoading(true);

            fishSQLiteHelper = new FishSQLiteHelper(getApplicationContext());
            isSuccess = false;
        }

        private void setLoading(Boolean isLoading){
            if(isLoading){
                addFishSubmitButton.setVisibility(View.GONE);
                addFishLoader.setVisibility(View.VISIBLE);
            }else{
                addFishLoader.setVisibility(View.GONE);
                addFishSubmitButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if(isPhotoChanged){
                String imageUri = "";
                String imageThumbnailUri = "";

                //save image
                //get root dir
                String rootDirectory = Environment.getExternalStorageDirectory().toString();

                //create directory if not exist
                String path = rootDirectory + "/aquacare";
                File directory = new File(path);
                if(!directory.exists()){
                    directory.mkdir();
                }

                //generate filename
                String uuid = UUID.randomUUID().toString();
                String filename = commonHelper.generateFileName("Fish-", ".jpg");
                String thumbnailFilename = "Thumbnail-"+filename;

                //save image
                File file = new File(directory, filename);
                File fileThumnbail = new File(directory, thumbnailFilename);
                try {
                    //resize bitmap
                    Bitmap resizedBitmap = commonHelper.resizeBitmap(fishBitmap, 600);
                    Bitmap resizedThumbnailBitmap = resizedBitmap;

                    //save main image
                    commonHelper.saveImageJPEG(file, resizedBitmap);
                    //save thumnail image
                    commonHelper.saveImageJPEG(fileThumnbail, resizedThumbnailBitmap);

                    imageUri = directory+"/"+filename;
                    imageThumbnailUri = directory+"/"+thumbnailFilename;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                fishModel.setImageUri(imageUri);
                fishModel.setImageThumbnailUri(imageThumbnailUri);
            }

            //insert into sqlite
            long newRowId = 0;
            if(isEdit){
                //update data
                newRowId = fishSQLiteHelper.updateData(fishModel);
            }else{
                //insert data
                newRowId = fishSQLiteHelper.insertData(fishModel);
            }

            if(newRowId > 0){
                isSuccess = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //disable loading
            setLoading(false);

            if(isSuccess){
                commonHelper.showToast(getApplicationContext(), "Successfully Insert "+fishModel.getName());
                Intent aquariumDetailIntent = new Intent(getApplicationContext(), AquariumDetailActivity.class);
                setResult(Activity.RESULT_OK, aquariumDetailIntent);
                finish();
            }else{
                commonHelper.showToast(getApplicationContext(), "Failed to insert "+fishModel.getName());
            }
        }
    }
}
