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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.classes.Recipe;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Recipe> list;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private OnItemClicked onClick;

    public interface OnItemClicked {
        void onItemClicked(int position);
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
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
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
/*
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        return new ViewHolder(itemView); */
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder userViewHolder = (ViewHolder) holder;
            Recipe recipe = list.get(position);
            userViewHolder.id.setText(recipe.title);
            userViewHolder.calories.setText(recipe.calories + " kcal");
            userViewHolder.time.setText(recipe.time);
            userViewHolder.URI.setText(recipe.URI);
            userViewHolder.image.setImageBitmap(list.get(position).bitmap);

            userViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onItemClicked(position);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    /*
        if(list.get(position).bitmap == null) // Så den slipper ladda ner bilderna efter dom redan har blivit nedladdade.
            new SearchAdapter.DownLoadImageTask(holder.image, position).execute(list.get(position).url);
        else
            holder.image.setImageBitmap(list.get(position).bitmap);
            */

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
