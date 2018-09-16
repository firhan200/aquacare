package com.learning.firhan.aquacare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.learning.firhan.aquacare.Helpers.FishHelper;
import com.learning.firhan.aquacare.Models.FishModel;

import java.util.Objects;

public class FishDetailActivity extends AppCompatActivity {
    private static final String TAG = "FishDetailActivity";

    //layout
    TextView fishDetailName,fishDetailType,fishDetailDescription,fishDetailPurchaseDate;
    ImageView fishPhoto;

    //helpers
    FishHelper fishHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_detail);

        initIds();
        initHelpers();
        setToolbar();

        FishModel fishModel = getFishBundle();
        populateFishDetail(fishModel);
    }

    private void initIds(){
        fishPhoto = (ImageView)findViewById(R.id.fishDetailPhoto);
        fishDetailName = (TextView)findViewById(R.id.fishDetailName);
        fishDetailType = (TextView)findViewById(R.id.fishDetailType);
        fishDetailDescription = (TextView)findViewById(R.id.fishDetailDescription);
        fishDetailPurchaseDate = (TextView)findViewById(R.id.fishDetailPurchaseDate);
    }

    private void initHelpers(){
        fishHelper = new FishHelper();
    }

    private FishModel getFishBundle(){
        FishModel fishModel = null;

        if(getIntent()!=null){
            if(getIntent().getParcelableExtra("fish")!=null){
                fishModel = getIntent().getParcelableExtra("fish");
            }
        }

        return fishModel;
    }

    private void populateFishDetail(FishModel fishModel){
        if(fishModel!=null){
            //fish model is not empty
            setActivityTitle(fishModel.getName());

            //populate photo
            try{
                if(!fishModel.getImageUri().isEmpty()){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap fishBitmap = BitmapFactory.decodeFile(fishModel.getImageUri(), options);

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

            //populate detail
            fishDetailName.setText(fishModel.getName());
            fishDetailType.setText(fishModel.getType());
            fishDetailDescription.setText(fishModel.getDescription());

            //set purchase date text
            String ageInDaysLabel = fishHelper.getAgeInDays(fishModel.getPurchaseDate());
            String purchaseDate = fishModel.getPurchaseDate()+" ("+ageInDaysLabel+")";

            fishDetailPurchaseDate.setText(purchaseDate);
        }
    }

    private void setToolbar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setActivityTitle(String title){
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
