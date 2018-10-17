package com.example.gustav.recipefinder.adapters;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.AsyncTask;
        import android.support.v4.view.PagerAdapter;
        import android.support.v4.view.ViewPager;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.example.gustav.recipefinder.R;
        import com.example.gustav.recipefinder.recipeHandler;
        import com.example.gustav.recipefinder.slideImage;

        import org.json.JSONObject;

        import java.io.InputStream;
        import java.net.URL;


public class ViewPagerAdapter extends PagerAdapter {

    private OnItemClicked onClick;

    public interface OnItemClicked {
        void OnItemClicked(View view);
    }

    private Context context;
    private LayoutInflater layoutInflater;
    // TODO: gör om så det kan vara mer bilder
    private slideImage[] images;
    private ViewGroup container;

    public ViewPagerAdapter(Context context, slideImage[] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) { // Körs varje gång användaren swipar
        this.container = container;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView textView = (TextView) view.findViewById(R.id.Title);
        TextView URIText = view.findViewById(R.id.URI);
        TextView caloriesText = view.findViewById(R.id.calories_value);
        TextView timeText = view.findViewById(R.id.time_value);

        if(images[position].bitmap == null) // Så den slipper ladda ner bilderna efter dom redan har blivit nedladdade.
            new DownLoadImageTask(imageView, position).execute(images[position].getImage());
        else
            imageView.setImageBitmap(images[position].bitmap);

        textView.setText(images[position].getTitle());
        URIText.setText(images[position].getURI());
        caloriesText.setText(images[position].getCalories() + " kcal");
        timeText.setText(images[position].getTime());

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        vp.findViewById(R.id.cardView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.OnItemClicked(view);
            }
        });
        return view;
    }

    public void setOnClick(ViewPagerAdapter.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }


    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> { // Används för att göra en URL till en bitmap ( Ladda ner en bild )
        ImageView imageView;
        int position = -1;

        public DownLoadImageTask(ImageView imageView, int position){
            this.imageView = imageView;
            this.position = position;
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
            images[position].bitmap = result;
        }
    }





    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}