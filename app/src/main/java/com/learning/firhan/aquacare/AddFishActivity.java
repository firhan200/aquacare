package com.learning.firhan.aquacare;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;

public class AddFishActivity extends AppCompatActivity {
    RelativeLayout addAquariumMainLayout;
    ImageView fishPhoto;
    int deviceWidth;
    Bitmap fishBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fish);

        //init id
        addAquariumMainLayout = (RelativeLayout)findViewById(R.id.addFishMainLayout);
        fishPhoto = (ImageView)findViewById(R.id.fishPhoto);

        addAquariumMainLayout.post(new Runnable() {
            @Override
            public void run() {
                deviceWidth = addAquariumMainLayout.getWidth();

                //get intent extras
                if(getIntent().getExtras()!=null){
                    if(getIntent().getExtras().get("image")!=null){
                        fishBitmap = (Bitmap) getIntent().getExtras().get("image");
                        setImage(fishBitmap);
                    }else if(getIntent().getExtras().get("imageUri")!=null){
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
        });
    }

    private void setImage(Bitmap bitmap){
        double imageOriginalHeight = bitmap.getHeight();
        double imageOriginalWidth = bitmap.getWidth();

        RequestOptions requestOptions = new RequestOptions().circleCrop();

        //convert to fit screen
        int imageWidth = (int)Math.ceil(deviceWidth/2);
        int imageHeight = imageWidth;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageWidth, imageHeight);
        fishPhoto.setLayoutParams(layoutParams);

        Glide
                .with(getApplicationContext())
                .load(bitmap)
                .apply(requestOptions)
                .into(fishPhoto);
    }
}
