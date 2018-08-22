package com.learning.firhan.aquacare.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.learning.firhan.aquacare.Adapters.AquariumListAdapter;
import com.learning.firhan.aquacare.Helpers.AquariumSQLiteHelper;
import com.learning.firhan.aquacare.Interfaces.IMainActivity;
import com.learning.firhan.aquacare.MainActivity;
import com.learning.firhan.aquacare.Models.AquariumModel;
import com.learning.firhan.aquacare.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    AquariumSQLiteHelper aquariumSQLiteHelper;
    RecyclerView aquariumRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<AquariumModel> aquariumModels;
    IMainActivity iMainActivity;
    ProgressBar listLoader;
    FloatingActionButton addAquariumButton;
    AquariumListAdapter aquariumListAdapter;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //init id
        aquariumRecyclerView = (RecyclerView)view.findViewById(R.id.aquariumRecyclerView);
        listLoader = (ProgressBar)view.findViewById(R.id.listLoader);
        addAquariumButton = (FloatingActionButton)view.findViewById(R.id.addAquariumFab);

        //set listener
        addAquariumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraOrGalleryDialogFragment cameraOrGalleryDialogFragment = new CameraOrGalleryDialogFragment();
                cameraOrGalleryDialogFragment.show(getActivity().getSupportFragmentManager(), "CameraOrGallery");
            }
        });

        populateAquariums();

        return view;
    }

    public void populateAquariums(){
        Log.d(TAG, "populateAquariums: ");
        //layout manager
        layoutManager = new LinearLayoutManager(getContext());

        aquariumRecyclerView.setHasFixedSize(true);
        aquariumRecyclerView.setLayoutManager(layoutManager);

        //get data from sqlite
        aquariumModels = new ArrayList<>();
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

        //set adapter
        aquariumListAdapter = new AquariumListAdapter(aquariumModels, getContext());

        try{
            aquariumRecyclerView.setAdapter(aquariumListAdapter);
        }catch (Exception ex){
            Log.d(TAG, "doInBackground: "+ex.getMessage());
        }
    }

    public class LoadAquariumTask extends AsyncTask{
        private static final String TAG = "LoadAquariumTask";
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ");
            listLoader.setVisibility(View.VISIBLE);
            aquariumRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d(TAG, "onPostExecute: ");
            listLoader.setVisibility(View.GONE);
            aquariumRecyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            //layout manager
            layoutManager = new LinearLayoutManager(getContext());

            aquariumRecyclerView.setHasFixedSize(true);
            aquariumRecyclerView.setLayoutManager(layoutManager);

            //get data from sqlite
            aquariumModels = new ArrayList<>();
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

            //set adapter
            aquariumListAdapter = new AquariumListAdapter(aquariumModels, getContext());

            try{
                aquariumRecyclerView.setAdapter(aquariumListAdapter);
            }catch (Exception ex){
                Log.d(TAG, "doInBackground: "+ex.getMessage());
            }


            return null;
        }
    }
}
