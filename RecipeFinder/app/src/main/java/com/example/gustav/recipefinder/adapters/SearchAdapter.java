package com.example.gustav.recipefinder.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.classes.Recipe;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<Recipe> list;

    private OnItemClicked onClick;

    public interface OnItemClicked {
        void onItemClicked(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView id, calories, time, URI;
        LinearLayout layout;
        public ViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.bookmark_image);
            id = v.findViewById(R.id.id);
            calories = v.findViewById(R.id.calories_value);
            time = v.findViewById(R.id.time_value);
            URI = v.findViewById(R.id.URI);
            layout = v.findViewById(R.id.layout);
        }
    }

    public SearchAdapter(List<Recipe> recipeList) {
        this.list = recipeList;
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Recipe recipe = list.get(position);
        holder.id.setText(recipe.title);
        holder.calories.setText(recipe.calories);
        holder.time.setText(recipe.time);
        holder.URI.setText(recipe.URI);


        if(list.get(position).bitmap == null) // Så den slipper ladda ner bilderna efter dom redan har blivit nedladdade.
            new SearchAdapter.DownLoadImageTask(holder.image, position).execute(list.get(position).url);
        else
            holder.image.setImageBitmap(list.get(position).bitmap);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }


    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> { // Används för att göra en URL till en bitmap ( Ladda ner en bild )
        ImageView imageView;
        int position = -1;

        public DownLoadImageTask(ImageView imageView, int position) {
            this.imageView = imageView;
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
            imageView.setImageBitmap(result);
            list.get(position).bitmap = result;
        }
    }
}
