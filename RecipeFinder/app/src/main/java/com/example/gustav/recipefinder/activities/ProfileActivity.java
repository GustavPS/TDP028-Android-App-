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
import com.example.gustav.recipefinder.fragments.BookmarkList;
import com.example.gustav.recipefinder.fragments.ProfileAdmin;
import com.example.gustav.recipefinder.fragments.ProfileTop;
import com.example.gustav.recipefinder.fragments.Settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ProfileAdmin.OnFragmentInteractionListener , Settings.OnFragmentInteractionListener, BookmarkList.OnFragmentInteractionListener{

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String uID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        uID = getIntent().getStringExtra("uID");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();


        FragmentManager manager = getSupportFragmentManager();
        // Top fragment
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment fragment = ProfileTop.newInstance(uID);
        transaction.add(R.id.fragment_top, fragment);
        transaction.commit();

        // Bottom fragment
        if(mFirebaseUser != null) {
            Fragment frag;
            if(mFirebaseUser.getUid().equals(uID)) {
                frag = new ProfileAdmin();
            } else {
                frag = BookmarkList.newInstance(uID);
            }
            transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container, frag);
            transaction.commit();

        }
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
