package com.learning.firhan.aquacare.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class CommonHelper {
    private static final String TAG = "CommonHelper";
    
    public void saveImageJPEG(File file, Bitmap image){
        try{
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        }catch (Exception ex){
            Log.d(TAG, "saveImage: "+ex.getMessage());
        }
    }

    public Bitmap resizeBitmap(Bitmap bm, int maxWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        //convert to fit screen
        int imageWidth = maxWidth;
        int imageHeight = (int)Math.ceil((imageWidth * height) / width);

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(imageWidth, imageHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, imageWidth, imageHeight, false);
        //bm.recycle();
        return resizedBitmap;
    }

    public String generateFileName(String preName, String extension){
        String filename = "";
        //generate filename
        String uuid = UUID.randomUUID().toString();
        filename = preName+uuid+extension;

        return filename;
    }

    public void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void showSnackBar(View v, String message){
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
    }

    /*========== Keyboard ============= */
    public void showKeyboard(Context context, View view){
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    public void hideKeyboard(Context context, View view){
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
