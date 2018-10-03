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

    private Context context;
    private LayoutInflater layoutInflater;
    // TODO: gör om så det kan vara mer bilder
    private slideImage[] images = {new slideImage("Taylor Swift", "Test1", "https://d2v9y0dukr6mq2.cloudfront.net/video/thumbnail/SZsu7JDqliztt1ct9/videoblocks-loading-bar-modern-concept-sign-progress-screen-download-idea-animation-design-load-alpha-channel-4k_swkumfvxw_thumbnail-full01.png"),
            new slideImage("T Swift", "Test1", "https://d2v9y0dukr6mq2.cloudfront.net/video/thumbnail/SZsu7JDqliztt1ct9/videoblocks-loading-bar-modern-concept-sign-progress-screen-download-idea-animation-design-load-alpha-channel-4k_swkumfvxw_thumbnail-full01.png"),
            new slideImage("TS", "Test1", "https://d2v9y0dukr6mq2.cloudfront.net/video/thumbnail/SZsu7JDqliztt1ct9/videoblocks-loading-bar-modern-concept-sign-progress-screen-download-idea-animation-design-load-alpha-channel-4k_swkumfvxw_thumbnail-full01.png")};
    private ViewGroup container;

    private recipeHandler rh = new recipeHandler();

    // TODO: Krashar om recipehandler.search to är mer än 3 då images[i] då i = 3 inte finns.
    JSONObject json = rh.search("sausage", new recipeHandler.VolleyCallback() { // Sök efter mat med chicken
        @Override
        public JSONObject onSuccess(JSONObject result) { // Callbackfunktion som körs när vi fått sökinformation
            try {
                for (int i = 0; i < result.getJSONArray("hits").length(); i++) {
                    String url = result.getJSONArray("hits").getJSONObject(i).getJSONObject("recipe").getString("image");
                    images[i] = new slideImage(result.getJSONArray("hits").getJSONObject(i).getJSONObject("recipe").getString("label"), "Test1", url);
                    System.out.println("JSON: " + images[i].getImage());
                }
            }
            catch(Exception ex)
            {
                System.out.println("krash: " + ex);
            }
            ViewPager vp = (ViewPager) container;
            vp.setAdapter(((ViewPager) container).getAdapter()); // Laddar om slideshowen så den kan visa den nya informationen
            return null;
        }
    });

    public ViewPagerAdapter(Context context) {
        this.context = context;
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

        if(images[position].bitmap == null) // Så den slipper ladda ner bilderna efter dom redan har blivit nedladdade.
            new DownLoadImageTask(imageView, position).execute(images[position].getImage());
        else
            imageView.setImageBitmap(images[position].bitmap);

        textView.setText(images[position].getTitle());
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
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