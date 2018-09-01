package com.learning.firhan.aquacare.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.learning.firhan.aquacare.Adapters.AquariumListAdapter;
import com.learning.firhan.aquacare.Adapters.FishListAdapter;
import com.learning.firhan.aquacare.Adapters.LatestFishListAdapter;
import com.learning.firhan.aquacare.Helpers.AquariumSQLiteHelper;
import com.learning.firhan.aquacare.Helpers.FishHelper;
import com.learning.firhan.aquacare.Helpers.FishSQLiteHelper;
import com.learning.firhan.aquacare.Interfaces.IMainActivity;
import com.learning.firhan.aquacare.MainActivity;
import com.learning.firhan.aquacare.Models.AquariumModel;
import com.learning.firhan.aquacare.Models.FishModel;
import com.learning.firhan.aquacare.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    //SQLite
    AquariumSQLiteHelper aquariumSQLiteHelper;
    FishSQLiteHelper fishSQLiteHelper;

    //helpers
    FishHelper fishHelper;

    //View
    public RecyclerView aquariumRecyclerView, latestFishRecyclerView;
    public ProgressBar aquariumListLoader,latestFishListLoader;
    public NestedScrollView homeContainer;
    public TextView aquariumTitle, latestFishTitle;
    public LinearLayout submitAquariumLabel;
    FloatingActionButton addAquariumButton;

    //models
    ArrayList<AquariumModel> aquariumModels;
    ArrayList<FishModel> latestFishModels;

    IMainActivity iMainActivity;
    AquariumListAdapter aquariumListAdapter;
    LatestFishListAdapter latestFishListAdapter;

    //variable
    boolean hasBack = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iMainActivity = (MainActivity)context;
        iMainActivity.setTitle(getString(R.string.app_name), hasBack);
    }

    @Override
    public void onResume() {
        super.onResume();
        iMainActivity.setTitle(getString(R.string.app_name), hasBack);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init db
        aquariumSQLiteHelper = new AquariumSQLiteHelper(getContext());
        fishSQLiteHelper = new FishSQLiteHelper(getContext());

        //init helpers
        fishHelper = new FishHelper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initIds(view);

        setSubmitAquariumLabelListener();

        //set listener
        addAquariumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        populateAquariumList(false);
        populateFishList(false);

        return view;
    }

    private void showAddDialog(){
        CameraOrGalleryDialogFragment cameraOrGalleryDialogFragment = new CameraOrGalleryDialogFragment();
        cameraOrGalleryDialogFragment.show(getActivity().getSupportFragmentManager(), "CameraOrGallery");
    }

    private void initIds(View view){
        //init id
        aquariumRecyclerView = (RecyclerView)view.findViewById(R.id.aquariumRecyclerView);
        latestFishRecyclerView = (RecyclerView)view.findViewById(R.id.latestFishRecyclerView);
        aquariumListLoader = (ProgressBar)view.findViewById(R.id.aquariumListLoader);
        latestFishListLoader = (ProgressBar)view.findViewById(R.id.latestFishListLoader);
        addAquariumButton = (FloatingActionButton)view.findViewById(R.id.addAquariumFab);
        homeContainer = (NestedScrollView)view.findViewById(R.id.homeContainer);
        latestFishTitle = (TextView)view.findViewById(R.id.latestFishTitle);
        aquariumTitle = (TextView)view.findViewById(R.id.aquariumTitle);
        submitAquariumLabel = (LinearLayout) view.findViewById(R.id.submitAquariumLabel);
    }

    private void setSubmitAquariumLabelListener(){
        submitAquariumLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
    }

    public void populateAquariumList(Boolean isRefreshList){
        new LoadAquariumTask(isRefreshList).execute();
    }

    public void populateFishList(Boolean isRefreshList){
        new LoadLatestFishTask(isRefreshList).execute();
    }

    public class LoadAquariumTask extends AsyncTask{
        private static final String TAG = "LoadAquariumTask";
        private Boolean isRefreshList = false;

        public LoadAquariumTask(Boolean isRefreshList) {
            this.isRefreshList = isRefreshList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ");
            aquariumListLoader.setVisibility(View.VISIBLE);
            aquariumRecyclerView.setVisibility(View.GONE);
            submitAquariumLabel.setVisibility(View.GONE);
            aquariumTitle.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d(TAG, "onPostExecute: ");
            aquariumListLoader.setVisibility(View.GONE);
            if(aquariumModels.size() > 0){
                aquariumRecyclerView.setVisibility(View.VISIBLE);
            }else{
                //empty
                aquariumTitle.setVisibility(View.GONE);
                submitAquariumLabel.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            setAquariumList();

            return null;
        }

        private void setAquariumList(){
            //get data from sqlite
            if(!isRefreshList){
                aquariumModels = new ArrayList<>();
            }else{
                //clear data
                aquariumModels.clear();
            }

            Cursor aquariums = aquariumSQLiteHelper.getAllData();
            while (aquariums.moveToNext()){
                AquariumModel aquariumModel = new AquariumModel(0,"", "","","",0,0,0);
                aquariumModel.setId(aquariums.getInt(0));
                aquariumModel.setImageUri(aquariums.getString(1));
                aquariumModel.setImageThumbnailUri(aquariums.getString(2));
                aquariumModel.setName(aquariums.getString(3));
                aquariumModel.setDescription(aquariums.getString(4));
                aquariumModel.setAquariumLength(aquariums.getDouble(5));
                aquariumModel.setAquariumHeight(aquariums.getDouble(6));
                aquariumModel.setAquariumWide(aquariums.getDouble(7));
                aquariumModels.add(aquariumModel);
            }

            if(!isRefreshList){
                //first populate
                setAquariumRecyclerView(aquariumModels);
            }else{
                //only refreshing data
                aquariumListAdapter.notifyDataSetChanged();
            }
        }

        private void setAquariumRecyclerView(ArrayList<AquariumModel> aquariumModels){
            //layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

            aquariumRecyclerView.setHasFixedSize(true);
            aquariumRecyclerView.setLayoutManager(layoutManager);

            //set adapter
            aquariumListAdapter = new AquariumListAdapter(aquariumModels, getContext(), HomeFragment.this);

            try{
                aquariumRecyclerView.setAdapter(aquariumListAdapter);
            }catch (Exception ex){
                Log.d(TAG, "doInBackground: "+ex.getMessage());
            }
        }
    }

    public class LoadLatestFishTask extends AsyncTask{
        private static final String TAG = "LoadLatestFishTask";
        private Boolean isRefreshList = false;

        public LoadLatestFishTask(Boolean isRefreshList) {
            this.isRefreshList = isRefreshList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ");
            latestFishListLoader.setVisibility(View.VISIBLE);
            latestFishTitle.setVisibility(View.VISIBLE);
            latestFishRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d(TAG, "onPostExecute: ");
            latestFishListLoader.setVisibility(View.GONE);
            if(latestFishModels.size() > 0){
                latestFishRecyclerView.setVisibility(View.VISIBLE);
            }else{
                //empty
                latestFishTitle.setVisibility(View.GONE);
            }

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            setFishList();

            return null;
        }

        private void setFishList(){
            //get data from sqlite
            if(!isRefreshList){
                latestFishModels = new ArrayList<>();
            }else{
                //clear data
                latestFishModels.clear();
            }

            Cursor fishes = fishSQLiteHelper.getLatestFishes(10);
            if(fishes.getCount() > 0){
                while (fishes.moveToNext()){
                    FishModel fishModel = fishHelper.convertCursorToModel(fishes);
                    latestFishModels.add(fishModel);
                }

                if(!isRefreshList){
                    //first populate
                    setLatestFishRecyclerView(latestFishModels);
                }else{
                    //only refreshing data
                    latestFishListAdapter.notifyDataSetChanged();
                }
            }else{
                //first init populate
                setLatestFishRecyclerView(latestFishModels);
                //no result
                latestFishListLoader.setVisibility(View.GONE);
            }
        }

        private void setLatestFishRecyclerView(ArrayList<FishModel> latestFishModels){
            //layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

            latestFishRecyclerView.setHasFixedSize(false);
            latestFishRecyclerView.setLayoutManager(layoutManager);

            //set adapter
            latestFishListAdapter = new LatestFishListAdapter(latestFishModels);

            try{
                latestFishRecyclerView.setAdapter(latestFishListAdapter);
            }catch (Exception ex){
                Log.d(TAG, "doInBackground: "+ex.getMessage());
            }
        }
    }
}
