package com.example.gustav.recipefinder.activities;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.adapters.ProfileAdapter;
import com.example.gustav.recipefinder.classes.ProfileSetting;
import com.example.gustav.recipefinder.fragments.ProfileAdmin;
import com.example.gustav.recipefinder.fragments.ProfileTop;
import com.example.gustav.recipefinder.fragments.Settings;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ProfileAdmin.OnFragmentInteractionListener , Settings.OnFragmentInteractionListener{

    private String uID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FragmentManager manager = getSupportFragmentManager();

        uID = "Lc3FVWl4zkPrid16qMJShfUfYSr2"; // Ska hämtas från parameter egentligen

        // Top fragment
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment fragment = ProfileTop.newInstance(uID);
        transaction.add(R.id.fragment_top, fragment);
        transaction.commit();

        // Bottom fragment
        transaction = manager.beginTransaction();
        Fragment frag = new ProfileAdmin();
        transaction.replace(R.id.fragment_container, frag);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void onFragmentInteraction(Uri uri) {

    }

    public void onFragmentInteraction(String fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment frag;

        switch(fragment) {
            case "Friends":
                frag = new Settings();
                break;
            case "Preferences":
                frag = new Settings();
                break;
            case "Other":
                frag = new Settings();
                break;

            default:
                frag = new ProfileAdmin();
                break;
        }
        transaction.replace(R.id.fragment_container, frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
