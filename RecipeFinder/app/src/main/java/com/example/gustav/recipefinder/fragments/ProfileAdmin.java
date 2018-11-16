package com.example.gustav.recipefinder.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.activities.RecipeActivity;
import com.example.gustav.recipefinder.activities.SearchActivity;
import com.example.gustav.recipefinder.adapters.ProfileAdapter;
import com.example.gustav.recipefinder.classes.ProfileSetting;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileAdmin.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileAdmin extends Fragment implements ProfileAdapter.OnItemClicked {
    private OnFragmentInteractionListener mListener;
    private List<ProfileSetting> settingList = new ArrayList<>();

    public ProfileAdmin() {
        // Required empty public constructor
    }
    public static ProfileAdmin newInstance(String param1, String param2) {
        ProfileAdmin fragment = new ProfileAdmin();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingList = new ArrayList<>();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        settingList.clear();
        RecyclerView recyclerView = getView().findViewById(R.id.setting_list);
        settingList.add(new ProfileSetting("Friends", BitmapFactory.decodeResource(getResources(), R.drawable.common_google_signin_btn_icon_light)));
        settingList.add(new ProfileSetting("Preferences", BitmapFactory.decodeResource(getResources(), R.drawable.common_google_signin_btn_icon_light)));
        settingList.add(new ProfileSetting("Other", BitmapFactory.decodeResource(getResources(), R.drawable.common_google_signin_btn_icon_light)));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ProfileAdapter adapter = new ProfileAdapter(settingList);
        adapter.setOnClick(ProfileAdmin.this);
        recyclerView.setAdapter(adapter);
    }

    public void onItemClicked(int position) {
        if(mListener != null) {
            mListener.onFragmentInteraction(settingList.get(position).getText());
        }
        System.out.println("klickad: " + position);
    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_admin, container, false);
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
        void onFragmentInteraction(String fragment);
    }
}
