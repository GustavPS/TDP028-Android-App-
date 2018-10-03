package com.example.gustav.recipefinder.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.appyvet.materialrangebar.RangeBar;
import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.adapters.SearchAdapter;
import com.example.gustav.recipefinder.classes.Recipe;
import com.example.gustav.recipefinder.recipeHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.OnItemClicked {
    private String calories_start = "0";
    private String calories_end = "5000";

    private List<Recipe> recipeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchAdapter fAdapter;
    private Boolean initiated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RangeBar rangeBar = findViewById(R.id.calories_range);
        SearchView searchView = findViewById(R.id.search_field);


        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                calories_start = leftPinValue;
                calories_end   = rightPinValue;
            }

        });

        recyclerView = findViewById(R.id.search_results);
        fAdapter = new SearchAdapter(recipeList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fAdapter);
        fAdapter.setOnClick(SearchActivity.this);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                recipeHandler rh = new recipeHandler();
                rh.search_with_calories(query, calories_start, calories_end, new recipeHandler.VolleyCallback() {
                    @Override
                    public JSONObject onSuccess(JSONObject result) {
                        try {
                            List<Recipe> recipeListOld = recipeList;
                            recipeList.clear();
                            JSONArray results = result.getJSONArray("hits");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject recipe = results.getJSONObject(i).getJSONObject("recipe");
                                String url = recipe.getString("image");
                                String title = recipe.getString("label");
                                String calories = recipe.getString("calories");
                                String uri = recipe.getString("uri");
                                String time = recipe.getString("totalTime");
                                recipeList.add(new Recipe(uri, title, url, calories, time));
                            }
                            if((recipeList != recipeListOld) || !initiated)
                                fAdapter.notifyDataSetChanged(); // Uppdaterar listan
                            initiated = true;
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }
                });
                return false;
            }

        });
    }

    public void onItemClicked(int position) {
        String URI = recipeList.get(position).URI;
        Intent intent = new Intent(SearchActivity.this, RecipeActivity.class);
        intent.putExtra("URI", URI);
        startActivity(intent);
    }
}
