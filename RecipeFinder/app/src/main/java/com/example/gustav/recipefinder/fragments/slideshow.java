package com.example.gustav.recipefinder.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.activities.MainActivity;
import com.example.gustav.recipefinder.adapters.ViewPagerAdapter;
import com.example.gustav.recipefinder.recipeHandler;
import com.example.gustav.recipefinder.slideImage;

import org.json.JSONObject;

public class slideshow extends Fragment implements ViewPagerAdapter.OnItemClicked {
    ViewPager viewPager;

    private OnFragmentInteractionListener mListener;

    public slideshow() {
        // Required empty public constructor
    }

    private String formatTime(int totalTime) {
        if(totalTime <= 30) {
            return "Short";
        } else if(totalTime <= 60) {
            return "Medium";
        } else {
            return "Long";
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recipeHandler rh = new recipeHandler();
        final slideImage[] images = new slideImage[3];

        // TODO: Krashar om recipehandler.search to är mer än 3 då images[i] då i = 3 inte finns.
        rh.search("sausage", new recipeHandler.VolleyCallback() { // Sök efter mat med chicken
            @Override
            public JSONObject onSuccess(JSONObject result) { // Callbackfunktion som körs när vi fått sökinformation
                try {
                    for (int i = 0; i < result.getJSONArray("hits").length(); i++) {
                        String url = result.getJSONArray("hits").getJSONObject(i).getJSONObject("recipe").getString("image");

                        String time = result.getJSONArray("hits").getJSONObject(i).getJSONObject("recipe").getString("totalTime");
                        int totalTime = Integer.parseInt(time.substring(0, time.indexOf(".")));
                        time = String.format(getResources().getString(R.string.time), formatTime(totalTime));

                        String calories = result.getJSONArray("hits").getJSONObject(i).getJSONObject("recipe").getString("calories");
                        calories = calories.substring(0, calories.indexOf("."));

                        images[i] = new slideImage(
                                result.getJSONArray("hits").getJSONObject(i).getJSONObject("recipe").getString("label"),
                                "Test1",
                                url,
                                result.getJSONArray("hits").getJSONObject(i).getJSONObject("recipe").getString("uri"),
                                calories,
                                time
                                );
                    }
                    setViewPager(images);
                }
                catch(Exception ex)
                {
                    System.out.println("krash: " + ex);
                }
                return null;
            }
        });

    }

    private void setViewPager(slideImage[] images)
    {
        viewPager = (ViewPager) getView().findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getContext(), images);
        viewPagerAdapter.setOnClick(slideshow.this);
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slideshow, container, false);
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

    @Override
    public void OnItemClicked(View view) {
        ((MainActivity) getActivity()).showRecipe(view);
    }
}
