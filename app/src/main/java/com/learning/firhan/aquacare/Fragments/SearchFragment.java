package com.learning.firhan.aquacare.Fragments;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.learning.firhan.aquacare.Adapters.SearchFishListAdapter;
import com.learning.firhan.aquacare.Helpers.FishHelper;
import com.learning.firhan.aquacare.Helpers.FishSQLiteHelper;
import com.learning.firhan.aquacare.Models.FishModel;
import com.learning.firhan.aquacare.R;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    //Recycler View
    public RecyclerView searchFishRecyclerView;
    public SearchFishListAdapter searchFishListAdapter;

    //List of Fish
    public ArrayList<FishModel> fishModels;

    //layout
    public ProgressBar searchLoading;
    public TextView noResults;

    //helper
    FishSQLiteHelper fishSQLiteHelper;
    FishHelper fishHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initIds(view);

        initHelpers();

        initRecyclerView();

        return view;
    }

    private void initIds(View view){
        searchFishRecyclerView = (RecyclerView)view.findViewById(R.id.searchFishRecyclerView);
        searchLoading = (ProgressBar)view.findViewById(R.id.searchLoading);
        noResults = (TextView)view.findViewById(R.id.noResults);
    }

    private void initHelpers(){
        fishSQLiteHelper = new FishSQLiteHelper(getContext());
        fishHelper = new FishHelper();
    }

    private void initRecyclerView(){
        //init array list
        fishModels = new ArrayList<>();

        //adapter
        searchFishListAdapter = new SearchFishListAdapter(getContext(), fishModels);

        //recycler view attr
        searchFishRecyclerView.setHasFixedSize(false);
        searchFishRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchFishRecyclerView.setAdapter(searchFishListAdapter);
    }

    public void search(String keyword){
        new SearchFishTask(keyword).execute();
    }

    class SearchFishTask extends AsyncTask{
        String keyword;

        public SearchFishTask(String keyword) {
            this.keyword = keyword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show loading
            noResults.setVisibility(View.GONE);
            searchLoading.setVisibility(View.VISIBLE);
            searchFishRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            searchFish();

            return null;
        }

        private void searchFish(){
            fishModels.clear();

            Cursor fishes = fishSQLiteHelper.getFishByKeyword(keyword);
            while(fishes.moveToNext()){
                FishModel fishModel = fishHelper.convertCursorToModel(fishes);
                fishModels.add(fishModel);
            }

            //refresh adapter
            searchFishListAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Object o) {
            //hide loading
            searchLoading.setVisibility(View.GONE);

            if(fishModels.size() > 0){
                searchFishRecyclerView.setVisibility(View.VISIBLE);
            }else{
                //no result
                noResults.setVisibility(View.VISIBLE);
            }
        }
    }
}
