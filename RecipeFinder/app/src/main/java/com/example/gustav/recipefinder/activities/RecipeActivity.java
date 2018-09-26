package com.example.gustav.recipefinder.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.adapters.HealthAdapter;
import com.example.gustav.recipefinder.recipeHandler;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {

    private String URI;
    private recipeHandler rh = new recipeHandler();
    private ProgressDialog dialog;
    private Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        c = this;
        dialog = new ProgressDialog(RecipeActivity.this);
        URI = getIntent().getStringExtra("URI");


        this.dialog.setMessage("Loading");
        this.dialog.show();
        JSONObject recipe = rh.searchByURI(URI, new recipeHandler.VolleyCallback() {
            @Override
            public JSONObject onSuccess(JSONObject result) { // Initiera alla views
                ImageView imageView = findViewById(R.id.food_image);
                try {
                    // Sätt bild och titel
                    new DownLoadImageTask(imageView).execute(result.getString("image"));
                    ((TextView)findViewById(R.id.food_title)).setText(result.getString("label"));

                    // Ställ in ingridienser
                    ArrayList<String> iItems = new ArrayList<>();
                    HealthAdapter iAdapter;

                    for(int i = 0; i < result.getJSONArray("ingredientLines").length(); i++) {
                        iItems.add(result.getJSONArray("ingredientLines").getString(i));
                    }
                    iAdapter = new HealthAdapter(iItems);
                    RecyclerView rv = (RecyclerView) findViewById(R.id.ingredient_list);
                    rv.setHasFixedSize(true);
                    RecyclerView.LayoutManager layout = new LinearLayoutManager(c);
                    rv.setLayoutManager(layout);
                    ((RecyclerView)findViewById(R.id.ingredient_list)).setAdapter(iAdapter);


                    // Ställ in kalorier
                    String cal = result.getString("calories");
                    cal = cal.substring(0, cal.indexOf("."));
                    int calories = Integer.parseInt(cal);
                    ((TextView)findViewById(R.id.calories_text)).setText(String.format(getResources().getString(R.string.calories), Integer.toString(calories)));

                    // Ställ in tid
                    String time = "";
                    int totalTime = Integer.parseInt(result.getString("totalTime").substring(0, result.getString("totalTime").indexOf(".")));
                    if(totalTime <= 30) {
                        time = String.format(getResources().getString(R.string.time), "Short");
                    } else if(totalTime <= 60) {
                        time = String.format(getResources().getString(R.string.time), "Medium");
                    } else {
                        time = String.format(getResources().getString(R.string.time), "Long");
                    }
                    ((TextView)findViewById(R.id.time_text)).setText(time);

                    // Ställ in hälso listan
                    ArrayList<String> healthItems = new ArrayList<>();
                    HealthAdapter healthAdapter;

                    Log.d("Length: ", Integer.toString(result.getJSONArray("healthLabels").length()));
                    for(int i = 0; i < result.getJSONArray("healthLabels").length(); i++) {
                        healthItems.add(result.getJSONArray("healthLabels").getString(i));
                    }
                    healthAdapter = new HealthAdapter(healthItems);
                    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.health_list);
                    mRecyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(c);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    ((RecyclerView)findViewById(R.id.health_list)).setAdapter(healthAdapter);
                }
                catch (Exception ex) {
                    System.out.println("Krash vid searchByUri: " + ex);
                }
                return null;
            }
        });

    }

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> { // Används för att göra en URL till en bitmap ( Ladda ner en bild )
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}
