package com.example.gustav.recipefinder.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.recipeHandler;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class RecipeActivity extends AppCompatActivity {

    private String URI;
    private recipeHandler rh = new recipeHandler();

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

        URI = getIntent().getStringExtra("URI");

        JSONObject recipe = rh.searchByURI(URI, new recipeHandler.VolleyCallback() {
            @Override
            public JSONObject onSuccess(JSONObject result) { // Initiera alla views
                ImageView imageView = findViewById(R.id.food_image);
                try {
                    new DownLoadImageTask(imageView).execute(result.getString("image"));
                    ((TextView)findViewById(R.id.food_title)).setText(result.getString("label"));
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
        }
    }

}
