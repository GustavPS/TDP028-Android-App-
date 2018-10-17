package com.example.gustav.recipefinder.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gustav.recipefinder.Bookmark;
import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.adapters.HealthAdapter;
import com.example.gustav.recipefinder.recipeHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeActivity extends AppCompatActivity {

    private recipeHandler rh = new recipeHandler();
    private ProgressDialog dialog;
    private Context c;

    // Firebase variabler
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    // Variabler för detta receptet
    private String URI;
    private String URL;
    private String title;
    private String image;
    private Boolean bookmarked = false;

    // Bookmark knappen
    private FloatingActionButton bookmarkBtn;

    // Byt ut "." med "_" då man inte kan ha "." i firebase
    private String URI_Regex(String URI) {
        return URI.replaceAll("[\\.#$/]", "_");
    }

    private void bookmark(String URI, String image, String title) {
        Bookmark bookmark = new Bookmark(URI, title, image);
        URI = URI_Regex(URI);
        DatabaseReference ref = mDatabase.child("users");
        String id = mAuth.getUid();
        ref.child(id+"/bookmarks/"+URI).setValue(bookmark);
    }

    private void removeBookmark(String URI) {
        URI = URI_Regex(URI);
        mDatabase.child("users/"+mAuth.getUid()+"/bookmarks/"+URI).removeValue();
    }

    private void setBookmarkIcon() { // Måste köras efter URI sätts
        bookmarkBtn = findViewById(R.id.fab);
        // Kolla om receptet är bookmarked
        DatabaseReference ref = mDatabase.child("users/"+mAuth.getUid()+"/bookmarks");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = URI_Regex(URI);
                if(dataSnapshot.hasChild(id)) // Ifall receptet är bookmarkat
                    bookmarked = true;

                // Sätt onclicklistener till att ta bort bookmarken eller lägga till den
                bookmarkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(bookmarked) {
                            removeBookmark(URI);
                            Snackbar.make(view, R.string.remove_bookmark, Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                            bookmarked = false;
                        } else {
                            bookmark(URI, image, title);
                            Snackbar.make(view, R.string.add_bookmark, Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                            bookmarked = true;
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ställ in databas och login referense så vi kan använda det senare
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        c = this;
        dialog = new ProgressDialog(RecipeActivity.this);
        URI = getIntent().getStringExtra("URI");


        this.dialog.setMessage("Loading");
        this.dialog.show();
        load_recipe();

        Button open_recipe_btn = findViewById(R.id.open_recipe);
        open_recipe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_recipe();
            }
        });

    }

    private void open_recipe() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(URL));
        startActivity(i);
    }

    private void load_recipe() {
        JSONObject recipe = rh.searchByURI(URI, new recipeHandler.VolleyCallback() {
            @Override
            public JSONObject onSuccess(JSONObject result) { // Initiera alla views
                ImageView imageView = findViewById(R.id.food_image);
                try {
                    // Sätt bild och titel
                    image = result.getString("image");
                    title = result.getString("label");
                    new DownLoadImageTask(imageView).execute(image);
                    ((TextView)findViewById(R.id.food_title)).setText(title);

                    // Bookmark ikonen
                    setBookmarkIcon();

                    URL = result.getString("url");
                    System.out.println("DETTA ÄR URL: " + URL);
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
