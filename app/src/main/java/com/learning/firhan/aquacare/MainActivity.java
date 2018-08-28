package com.learning.firhan.aquacare;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.learning.firhan.aquacare.Constants.ActivityResultsCode;
import com.learning.firhan.aquacare.Fragments.HomeFragment;
import com.learning.firhan.aquacare.Helpers.StorageHelper;
import com.learning.firhan.aquacare.Interfaces.IMainActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity implements IMainActivity {
    private static final String TAG = "MainActivity";

    FragmentManager fragmentManager;

    LayoutInflater layoutInflater;
    Toolbar customToolbar;
    AppBarLayout customToolbarLayout;
    RelativeLayout frameContainer;
    StorageHelper storageHelper;

    /* Fragments */
    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initIds();

        setCustomToolbar();

        storageHelper = new StorageHelper();
        layoutInflater = getLayoutInflater();
        fragmentManager = getSupportFragmentManager();

        //set default fragment
        setFragment(getString(R.string.fragment_home), false, null);
    }

    private void setCustomToolbar(){
        //set toolbar
        customToolbarLayout.bringToFront();
        setSupportActionBar(customToolbar);
    }

    private void initIds(){
        //init id
        customToolbar = (Toolbar)findViewById(R.id.customToolbar);
        customToolbarLayout = (AppBarLayout)findViewById(R.id.customToolbarLayout);
        frameContainer = (RelativeLayout)findViewById(R.id.frameContainer);
    }

    public void setFragment(String fragmentTag, boolean addToBackStack, Bundle bundle){
        String homeTag = getString(R.string.fragment_home);

        //begin fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //get fragment by tag
        Fragment fragment = new HomeFragment();
        if(fragmentTag.equals(homeTag)){
            homeFragment = new HomeFragment();
            fragment = homeFragment;
        }

        //replace view with fragment
        fragmentTransaction.replace(R.id.frameContainer, fragment, fragmentTag);

        //add back stack
        if(addToBackStack){
            fragmentTransaction.addToBackStack(fragmentTag);
        }

        //add bundle
        if(bundle!=null){
            fragment.setArguments(bundle);
        }

        //commit transaction
        fragmentTransaction.commit();
    }

    @Override
    public void setTitle(String title, boolean hasBack) {
        try{
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getSupportActionBar().setTitle(title);
            if(hasBack){
                //make toolbar transparent
                customToolbarLayout.setBackgroundColor(0);
                frameContainer.setLayoutParams(layoutParams);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }else{
                customToolbarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                layoutParams.addRule(RelativeLayout.BELOW, R.id.customToolbarLayout);
                frameContainer.setLayoutParams(layoutParams);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }catch (Exception ex){
            Log.d(TAG, "setTitle: "+ex.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                try{
                    getSupportFragmentManager().popBackStack();
                }catch (Exception ex){
                    Log.d(TAG, "onOptionsItemSelected: "+ex.getMessage());
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: get request code: "+requestCode+", result code: "+resultCode);

        if (requestCode == ActivityResultsCode.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            //goto add aquarium activity
            Intent addAquariumIntent = new Intent(this, AddAquariumActivity.class);
            addAquariumIntent.putExtra("image", photo);
            startActivityForResult(addAquariumIntent, ActivityResultsCode.ADD_AQUARIUM);
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
                Intent addAquariumIntent = new Intent(this, AddAquariumActivity.class);
                addAquariumIntent.putExtra("imageUri", uri.toString());
                startActivityForResult(addAquariumIntent, ActivityResultsCode.ADD_AQUARIUM);
            } catch (Exception ex) {
                Log.d(TAG, "onActivityResult: "+ex.getMessage());
            }
        }else if(requestCode==ActivityResultsCode.ADD_AQUARIUM && resultCode==Activity.RESULT_OK){
            //reload recycler view
            homeFragment.populateAquariumList(true);
        }else if(requestCode==ActivityResultsCode.AQUARIUM_DETAIL){
            if(resultCode==ActivityResultsCode.REFRESH_LATEST_FISH){
                //repopulate latest fish
                homeFragment.populateFishList(true);
            }
        }
    }
}
