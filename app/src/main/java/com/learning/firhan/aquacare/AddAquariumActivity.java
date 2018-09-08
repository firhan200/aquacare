package com.learning.firhan.aquacare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learning.firhan.aquacare.Constants.ActivityResultsCode;
import com.learning.firhan.aquacare.Fragments.CameraOrGalleryDialogFragment;
import com.learning.firhan.aquacare.Helpers.AquariumSQLiteHelper;
import com.learning.firhan.aquacare.Helpers.CommonHelper;
import com.learning.firhan.aquacare.Helpers.StorageHelper;
import com.learning.firhan.aquacare.Models.AquariumModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AddAquariumActivity extends AppCompatActivity {
    private static final String TAG = "AddAquariumActivity";

    AquariumModel aquariumModel;
    AquariumSQLiteHelper aquariumSQLiteHelper;
    ImageView aquariumPhoto;
    Bitmap aquariumBitmap;
    RelativeLayout addAquariumMainLayout;
    View mainView;
    EditText aquariumName, aquariumDescription, aquariumLength, aquariumHeight, aquariumWide;
    public Button submitButton, aquariumChangePhotoButton;
    public ProgressBar loader;
    double deviceWidth;
    CommonHelper commonHelper;
    Boolean isEdit,isPhotoChanged;
    StorageHelper storageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_aquarium);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init db
        aquariumSQLiteHelper = new AquariumSQLiteHelper(this);
        commonHelper = new CommonHelper();

        initIds();

        setAddOrEditAquarium();
        setAquariumPhoto();
        setAquariumNameListener();
        setAquariumDescriptionListener();
        setAquariumMeasuresListener();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAquarium(v);
            }
        });
    }

    private void setAddOrEditAquarium(){
        isPhotoChanged = false;

        Intent intent = getIntent();
        if(intent!=null){
            //check extra
            if(intent.getExtras()!=null){
                if(intent.getParcelableExtra("aquariumModel")!=null){
                    aquariumModel = intent.getParcelableExtra("aquariumModel");
                    isEdit = true;

                    //init storage helper for get image
                    storageHelper = new StorageHelper();

                    //show change photo button
                    aquariumChangePhotoButton.setVisibility(View.VISIBLE);
                    //set listener
                    setAquariumChangePhotoButtonListener();

                    populatePreviousData(aquariumModel);
                }else{
                    isEdit = false;
                    isPhotoChanged = true;
                }
            }
        }
    }

    private void populatePreviousData(AquariumModel aquariumModel){
        aquariumName.setText(aquariumModel.getName());
        aquariumDescription.setText(aquariumModel.getDescription());
        aquariumLength.setText(String.valueOf(aquariumModel.getAquariumLength()));
        aquariumWide.setText(String.valueOf(aquariumModel.getAquariumWide()));
        aquariumHeight.setText(String.valueOf(aquariumModel.getAquariumHeight()));
    }

    private void setAquariumMeasuresListener(){
        aquariumLength.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    commonHelper.hideKeyboard(getApplicationContext(), v);
                }
            }
        });

        aquariumHeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    commonHelper.hideKeyboard(getApplicationContext(), v);
                }
            }
        });

        aquariumWide.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    commonHelper.hideKeyboard(getApplicationContext(), v);
                }
            }
        });
    }

    private void setAquariumChangePhotoButtonListener(){
        aquariumChangePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraOrGalleryDialogFragment cameraOrGalleryDialogFragment = new CameraOrGalleryDialogFragment();
                cameraOrGalleryDialogFragment.show(getSupportFragmentManager(), "CameraOrGallery");
            }
        });
    }

    private void setAquariumDescriptionListener(){
        aquariumDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    commonHelper.hideKeyboard(getApplicationContext(), v);
                }
            }
        });
    }

    private void setAquariumNameListener(){
        //set listener
        aquariumName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    commonHelper.hideKeyboard(getApplicationContext(), v);
                }
            }
        });
    }

    private void setAquariumPhoto(){
        //get parent width
        addAquariumMainLayout.post(new Runnable() {
            @Override
            public void run() {
                deviceWidth = addAquariumMainLayout.getWidth();

                if(isEdit){
                    //show previous photo
                    try{
                        if(!aquariumModel.getImageUri().isEmpty()){
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            aquariumBitmap = BitmapFactory.decodeFile(aquariumModel.getImageUri(), options);

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
                }else{
                    //get intent extras
                    if(getIntent().getExtras()!=null){
                        if(getIntent().getExtras().get("image")!=null){
                            aquariumBitmap = (Bitmap) getIntent().getExtras().get("image");
                            setImage(aquariumBitmap);
                        }else if(getIntent().getExtras().get("imageUri")!=null){
                            String imageUri = getIntent().getExtras().getString("imageUri");
                            Uri uri = Uri.parse(imageUri);
                            aquariumBitmap = null;
                            try {
                                aquariumBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                setImage(aquariumBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    private void initIds(){
        //init id
        aquariumPhoto = (ImageView)findViewById(R.id.aquariumPhoto);
        addAquariumMainLayout = (RelativeLayout)findViewById(R.id.addAquariumMainLayout);
        aquariumName = (EditText)findViewById(R.id.aquariumName);
        aquariumDescription = (EditText)findViewById(R.id.aquariumDescription);
        aquariumHeight = (EditText)findViewById(R.id.aquariumHeight);
        aquariumLength = (EditText)findViewById(R.id.aquariumLength);
        aquariumWide = (EditText)findViewById(R.id.aquariumWide);
        submitButton = (Button)findViewById(R.id.addAquariumSubmitButton);
        loader = (ProgressBar)findViewById(R.id.loader);
        aquariumChangePhotoButton = (Button)findViewById(R.id.aquariumChangePhotoButton);
    }

    private void saveAquarium(View view){
        mainView = view;
        //get all data
        String aquariumNameValue = aquariumName.getText().toString();
        String aquariumDescriptionValue = aquariumDescription.getText().toString();

        //validating
        if(aquariumNameValue.isEmpty()){
            showSnackbar(view, "Name cannot be empty.");
            aquariumName.requestFocus();
            //enalble button
            submitButton.setEnabled(true);
            return;
        }

        if(aquariumDescriptionValue.isEmpty()){
            showSnackbar(view, "Description cannot be empty.");
            aquariumDescription.requestFocus();
            //enalble button
            submitButton.setEnabled(true);
            return;
        }

        //set default value
        double aquariumLengthValue = 0;
        double aquariumHeightValue = 0;
        double aquariumWideValue = 0;

        //get string
        String lengthString = aquariumLength.getText().toString();
        String heightString = aquariumHeight.getText().toString();
        String wideString = aquariumWide.getText().toString();

        if(lengthString.isEmpty()){
            showSnackbar(view, "Length cannot be empty.");
            aquariumLength.requestFocus();
            //enalble button
            submitButton.setEnabled(true);
            return;
        }
        if(heightString.isEmpty()){
            showSnackbar(view, "Height cannot be empty.");
            aquariumHeight.requestFocus();
            //enalble button
            submitButton.setEnabled(true);
            return;
        }
        if(wideString.isEmpty()){
            showSnackbar(view, "Wide cannot be empty.");
            aquariumWide.requestFocus();
            //enalble button
            submitButton.setEnabled(true);
            return;
        }

        //try parse to double
        if(lengthString!=null || lengthString!=""){
            try{
                aquariumLengthValue = Double.parseDouble(lengthString);
            }catch (Exception ex){
                Log.d(TAG, "onClick: "+ex.getMessage());
            }
        }
        if(heightString!=null || heightString!=""){
            try{
                aquariumHeightValue = Double.parseDouble(heightString);
            }catch (Exception ex){
                Log.d(TAG, "onClick: "+ex.getMessage());
            }
        }
        if(wideString!=null || wideString!=""){
            try{
                aquariumWideValue = Double.parseDouble(wideString);
            }catch (Exception ex){
                Log.d(TAG, "onClick: "+ex.getMessage());
            }
        }

        //insert into model
        if(!isEdit){
            //new aquarium init new model
            aquariumModel = new AquariumModel(0, "","", aquariumNameValue, aquariumDescriptionValue, aquariumLengthValue, aquariumHeightValue, aquariumWideValue);
        }else{
            aquariumModel.setName(aquariumNameValue);
            aquariumModel.setDescription(aquariumDescriptionValue);
            aquariumModel.setAquariumLength(aquariumLengthValue);
            aquariumModel.setAquariumHeight(aquariumHeightValue);
            aquariumModel.setAquariumWide(aquariumWideValue);
        }

        new SaveAquariumTask().execute();
    }

    private void setImage(Bitmap aquariumBitmap){
        RequestOptions requestOptions = new RequestOptions().centerCrop();

        Glide
                .with(getApplicationContext())
                .load(aquariumBitmap)
                .apply(requestOptions)
                .into(aquariumPhoto);

        isPhotoChanged = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d(TAG, "onOptionsItemSelected: "+item.getItemId()+" : "+android.R.id.home);
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: get request code: "+requestCode+", result code: "+resultCode);

        if (requestCode == ActivityResultsCode.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            aquariumBitmap = photo;
            setImage(photo);
            isPhotoChanged = true;
        }else if(requestCode==ActivityResultsCode.GALLERY_REQUEST && resultCode==Activity.RESULT_OK){
            Uri uri = data.getData();

            try {
                // Get the path from the Uri
                final String path = storageHelper.getPathFromURI(uri, getContentResolver());
                if (path != null) {
                    File file = new File(path);
                    uri = Uri.fromFile(file);

                    aquariumBitmap = null;
                    try {
                        aquariumBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        setImage(aquariumBitmap);
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

    public void showSnackbar(View v, String message){
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
    }

    public class SaveAquariumTask extends AsyncTask{
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //disable button
            submitButton.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(isSuccess){
                //success
                String message = getString(R.string.insert_data_success)+" "+aquariumModel.getName();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                setResult(Activity.RESULT_OK, mainIntent);
                finish();
            }else{
                //failed
                String message = getString(R.string.insert_data_failed)+" "+aquariumModel.getName();
                Snackbar.make(mainView, message, Snackbar.LENGTH_SHORT).show();
            }

            //enalble button
            loader.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            //check if new image / not
            if(isPhotoChanged) {

                String imageUri = "";
                String imageThumbnailUri = "";

                //save image
                //get root dir
                String rootDirectory = Environment.getExternalStorageDirectory().toString();

                //create directory if not exist
                String path = rootDirectory + "/aquacare";
                File directory = new File(path);
                if (!directory.exists()) {
                    directory.mkdir();
                }

                //generate filename
                String uuid = UUID.randomUUID().toString();
                String filename = commonHelper.generateFileName("IMG-", ".jpg");
                String thumbnailFilename = "Thumbnail-" + filename;

                //save image
                File file = new File(directory, filename);
                File fileThumnbail = new File(directory, thumbnailFilename);
                try {
                    //resize bitmap
                    Bitmap resizedBitmap = commonHelper.resizeBitmap(aquariumBitmap, 600);
                    Bitmap resizedThumbnailBitmap = resizedBitmap;

                    //save main image
                    commonHelper.saveImageJPEG(file, resizedBitmap);
                    //save thumnail image
                    commonHelper.saveImageJPEG(fileThumnbail, resizedThumbnailBitmap);

                    imageUri = directory + "/" + filename;
                    imageThumbnailUri = directory + "/" + thumbnailFilename;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                aquariumModel.setImageUri(imageUri);
                aquariumModel.setImageThumbnailUri(imageThumbnailUri);
            }

            //insert into sqlite
            long newRowId = 0;
            if(isEdit){
                newRowId = aquariumSQLiteHelper.updateData(aquariumModel);
            }else{
                newRowId = aquariumSQLiteHelper.insertData(aquariumModel);
            }

            if(newRowId > 0){
                isSuccess = true;
            }
            isSuccess = true;
            return null;
        }
    }
}
