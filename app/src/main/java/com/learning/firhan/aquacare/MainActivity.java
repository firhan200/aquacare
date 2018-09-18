package com.learning.firhan.aquacare;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.learning.firhan.aquacare.Constants.ActivityResultsCode;
import com.learning.firhan.aquacare.Fragments.HomeFragment;
import com.learning.firhan.aquacare.Fragments.SearchFragment;
import com.learning.firhan.aquacare.Helpers.CommonHelper;
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

    //toolbar
    LinearLayout defaultToolbar,searchToolbar;
    ImageView searchFishButton,closeSearchButton;
    TextView mainActivityTitle;
    EditText searchKeyword;

    /* Fragments */
    HomeFragment homeFragment;
    public SearchFragment searchFragment;

    /* Helpers */
    StorageHelper storageHelper;
    CommonHelper commonHelper;

    Boolean isSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initIds();

        //init helpers
        initHelpers();

        setCustomToolbar();

        layoutInflater = getLayoutInflater();
        fragmentManager = getSupportFragmentManager();

        setSearchFishButtonListener();
        setCloseSearchButtonListener();
        setSearchKeywordListener();

        //set default fragment
        setFragment(getString(R.string.fragment_home), false, null);
    }

    private void setCustomToolbar(){
        //set toolbar
        customToolbarLayout.bringToFront();
        setSupportActionBar(customToolbar);

        //set main activity title
        if(getTitle()!=null){
            mainActivityTitle.setText(getTitle());
        }
    }

    private void initIds(){
        //init id
        customToolbar = (Toolbar)findViewById(R.id.customToolbar);
        customToolbarLayout = (AppBarLayout)findViewById(R.id.customToolbarLayout);
        frameContainer = (RelativeLayout)findViewById(R.id.frameContainer);
        mainActivityTitle = (TextView)findViewById(R.id.mainActivityTitle);

        //inner toolbar
        defaultToolbar = (LinearLayout)findViewById(R.id.defaultToolbar);
        searchToolbar = (LinearLayout)findViewById(R.id.searchToolbar);
        searchFishButton = (ImageView)findViewById(R.id.searchFishButton);
        closeSearchButton = (ImageView)findViewById(R.id.closeSearchButton);
        searchKeyword = (EditText)findViewById(R.id.searchKeyword);
    }

    private void initHelpers(){
        commonHelper = new CommonHelper();
        storageHelper = new StorageHelper();
    }

    private void setSearchFishButtonListener(){
        searchFishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToSearchAction(true, v);
            }
        });
    }

    private void setToSearchAction(Boolean setToSearch, View view){
        if(setToSearch){
            defaultToolbar.setVisibility(View.GONE);
            searchToolbar.setVisibility(View.VISIBLE);

            searchKeyword.requestFocus(); //focus on keyword input
            commonHelper.showKeyboard(getApplicationContext(), view); //show keboard

            //set fragment to search
            setFragment(getString(R.string.fragment_search), true, null);
        }else{
            defaultToolbar.setVisibility(View.VISIBLE);
            searchToolbar.setVisibility(View.GONE);

            searchKeyword.setText("");

            //set fragment to search
            getSupportFragmentManager().popBackStack();
        }
    }

    private void setCloseSearchButtonListener(){
        closeSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToSearchAction(false, v);
            }
        });
    }

    private void setSearchKeywordListener(){
        searchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //if user idle for 2 second start searching
                if(!isSearching){
                    isSearching = true;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            searchFish(searchKeyword.getText().toString());
                        }
                    }, 1000);
                }
            }
        });
    }

    public void searchFish(String keyword){
        if(!keyword.equals("")){
            searchFragment.search(keyword);
        }

        //set searching to false | ready to search again
        isSearching = false;
    }

    public void setFragment(String fragmentTag, boolean addToBackStack, Bundle bundle){
        String homeTag = getString(R.string.fragment_home);
        String searchTag = getString(R.string.fragment_search);

        //begin fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //get fragment by tag
        Fragment fragment = new HomeFragment();
        if(fragmentTag.equals(homeTag)){
            homeFragment = new HomeFragment();
            fragment = homeFragment;
        }else if(fragmentTag.equals(searchTag)){
            searchFragment = new SearchFragment();
            fragment = searchFragment;
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
                    if(searchToolbar.getVisibility()==View.VISIBLE){
                        //on search must hide search toolbar
                        setToSearchAction(false, searchKeyword);
                    }
                }catch (Exception ex){
                    Log.d(TAG, "onOptionsItemSelected: "+ex.getMessage());
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(searchToolbar.getVisibility()==View.VISIBLE){
            //on search must hide search toolbar
            setToSearchAction(false, searchKeyword);
        }else{
            super.onBackPressed();
        }
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
        }else if((requestCode==ActivityResultsCode.ADD_AQUARIUM || requestCode==ActivityResultsCode.EDIT_AQUARIUM) && resultCode==Activity.RESULT_OK){
            //reload recycler view
            homeFragment.populateAquariumList(true);
        }else if(requestCode==ActivityResultsCode.AQUARIUM_DETAIL){
            if(resultCode==ActivityResultsCode.REFRESH_LATEST_FISH){
                //repopulate latest fish
                homeFragment.populateFishList(true);
                homeFragment.populateAquariumList(true);
            }
        }
    }
}
