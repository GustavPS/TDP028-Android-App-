package com.example.gustav.recipefinder.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private String calories_start_before = "";
    private String calories_end_before = "";
    private int from = 0;
    private int to = 10;
    private int increment = 20;
    private String search_query = "";

    private Boolean can_continue_search = true; // Ifall vi kan söka när den scrollar ner

    private List<Recipe> recipeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private SearchAdapter fAdapter;
    private Boolean initiated = false;


    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

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

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);


        // Ladda nytt innehåll när användaren skrollar längst ner
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                // Ifall användaren har skrollat längst ner
                if (can_continue_search && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (!recyclerView.canScrollVertically(1)) {
                        if(search_query == null || !can_continue_search || recipeList.size() == 0)
                            return;

                        recipeList.add(null); // Så vi visar laddnings symbol
                        // TODO: Exception här eftersom notifyDataSetChanged inte kan köra is scroll. Funkar men fixa det
                        fAdapter.notifyDataSetChanged();

                        can_continue_search = false; // Så man inte laddar två gånger
                        recipeHandler rh = new recipeHandler();
                        to = to + increment;
                        from = to - increment + 1;
                        rh.search_with_calories(search_query, calories_start, calories_end, Integer.toString(from), Integer.toString(to), new recipeHandler.VolleyCallback() {
                            @Override
                            public JSONObject onSuccess(JSONObject result) {
                                try {
                                    recipeList.remove(recipeList.size()-1); // Tar bort laddnings objektet
                                    List<Recipe> recipeListOld = recipeList;
                                    JSONArray results = result.getJSONArray("hits");
                                    for(int i = 0; i < results.length(); i++) {
                                        JSONObject recipe = results.getJSONObject(i).getJSONObject("recipe");
                                        String url = recipe.getString("image");
                                        String title = recipe.getString("label");
                                        String calories = recipe.getString("calories").substring(0, recipe.getString("calories").indexOf("."));
                                        String uri = recipe.getString("uri");
                                        int totalTime = Integer.parseInt(recipe.getString("totalTime").substring(0, recipe.getString("totalTime").indexOf(".")));
                                        String time = format_time(totalTime);

                                        recipeList.add(new Recipe(uri, title, url, calories, time));
                                        new SearchActivity.DownLoadImageTask(recipeList.size()-1).execute(url);
                                    }
                                } catch(Exception ex) {
                                    updateList();
                                    ex.printStackTrace();
                                }
                                return null;
                            }
                        });
                    }
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!can_continue_search || (search_query.equals(query) && (calories_end.equals(calories_end_before) && calories_start.equals(calories_start_before))))
                    return false;

                calories_start_before = calories_start;
                calories_end_before  = calories_end;

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                can_continue_search = false;
                recipeHandler rh = new recipeHandler();
                search_query = query;
                rh.search_with_calories(query, calories_start, calories_end, "0", Integer.toString(increment), new recipeHandler.VolleyCallback() {
                    @Override
                    public JSONObject onSuccess(JSONObject result) {
                        try {
                            recipeList.clear();
                            JSONArray results = result.getJSONArray("hits");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject recipe = results.getJSONObject(i).getJSONObject("recipe");
                                String url = recipe.getString("image");
                                String title = recipe.getString("label");
                                String calories = recipe.getString("calories").substring(0, recipe.getString("calories").indexOf("."));
                                String uri = recipe.getString("uri");
                                int totalTime = Integer.parseInt(recipe.getString("totalTime").substring(0, recipe.getString("totalTime").indexOf(".")));
                                String time = format_time(totalTime);

                                new SearchActivity.DownLoadImageTask(i).execute(url);
                                recipeList.add(new Recipe(uri, title, url, calories, time));
                            }
                            from = 0;
                            to = 10;
                            LinearLayoutManager layoutManager = ((LinearLayoutManager)recyclerView.getLayoutManager());
                            layoutManager.scrollToPositionWithOffset(0, 0);
                            if(recipeList.isEmpty())
                                updateList();
                        } catch(Exception ex) {
                            updateList();
                            ex.printStackTrace();
                        }
                        return null;
                    }
                });
                return false;
            }

        });
    }

    private void updateList() {
        fAdapter.notifyDataSetChanged(); // Uppdaterar listan
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        can_continue_search = true;
        initiated = true;
    }

    private String format_time(int time) {
        if(time <= 30)
            return "Short";
        else if(time <= 60)
            return "Medium";
        else
            return "Long";
    }

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> { // Används för att göra en URL till en bitmap ( Ladda ner en bild )
        int position = -1;

        public DownLoadImageTask(int position) {
            this.position = position;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result) {
            //imageView.setImageBitmap(result);
            recipeList.get(position).bitmap = result;

            // Visar listan om alla bilder är nedladdade
            Boolean check = true;
            for(int i = 0; i < recipeList.size(); i++) {
                if(recipeList.get(i) != null && recipeList.get(i).bitmap == null) {
                    check = false;
                    break;
                }
            }
            if(check) {
                updateList();
            }
        }
    }

    public void onItemClicked(int position) {
        String URI = recipeList.get(position).URI;
        Intent intent = new Intent(SearchActivity.this, RecipeActivity.class);
        intent.putExtra("URI", URI);
        startActivity(intent);
    }
}
