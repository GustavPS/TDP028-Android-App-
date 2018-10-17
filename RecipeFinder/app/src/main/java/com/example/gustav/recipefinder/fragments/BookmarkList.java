package com.example.gustav.recipefinder.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gustav.recipefinder.Bookmark;
import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.activities.MainActivity;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookmarkList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookmarkList#newInstance} factory method to
 * create an instance of this fragment.
 */

public class BookmarkList extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private Map<String, Bitmap> images = new HashMap<>();

    private OnFragmentInteractionListener mListener;

    public BookmarkList() {
        // Required empty public constructor
    }

    public static BookmarkList newInstance() {
        BookmarkList fragment = new BookmarkList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ListView mListView = (ListView) getView().findViewById(R.id.bookmark_list);
        DatabaseReference ref = mDatabase.child("users/"+mAuth.getUid()+"/bookmarks");
        FirebaseListAdapter<Bookmark> mAdapter = new FirebaseListAdapter<Bookmark>(this.getActivity(), Bookmark.class, R.layout.fragment_item, ref) {
            @Override
            protected void populateView(View view, Bookmark model, int position) {
                ((TextView) view.findViewById(R.id.id)).setText(model.getTitle());
                ImageView imageView = (ImageView) view.findViewById(R.id.bookmark_image);
                ((TextView) view.findViewById(R.id.calories_value)).setText(model.getCalories() + " kcal");
                ((TextView) view.findViewById(R.id.time_value)).setText("Time: " + model.getTime());
                ((TextView) view.findViewById(R.id.URI)).setText(model.getURI()); // Sätt URI så vi vet vilken maträtt det är ( Invisible TextView )

                // TODO: Alla bilder blir samma eftersom URI är samma på alla. Det beror på databasen. Borde funka när databasen har riktiga värden

                if(images.containsKey(model.getURI())) // Ifall vi redan har ladat ner bilden, ladda in den istället för att ladda ner den igen
                    imageView.setImageBitmap(images.get(model.getURI()));
                else
                    new DownLoadImageTask(imageView, model.getURI()).execute(model.getImage());
            }

        };
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemClick(view);
                //TextView textView = view.findViewById(R.id.id);
                //Log.d("HALLI: ", textView.getText().toString());
            }
        });
    }

    public void itemClick(View view) {
        ((MainActivity) getActivity()).showRecipe(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark_list, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> { // Används för att göra en URL till en bitmap ( Ladda ner en bild )
        ImageView imageView;
        String uri;

        public DownLoadImageTask(ImageView iv, String uri){
            this.imageView = iv;
            this.uri = uri;
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
            images.put(this.uri, result);
            this.imageView.setImageBitmap(result);
            imageView.setImageBitmap(result);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
