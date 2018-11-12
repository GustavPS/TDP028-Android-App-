package com.example.gustav.recipefinder.activities;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.adapters.ProfileAdapter;
import com.example.gustav.recipefinder.classes.ProfileSetting;
import com.example.gustav.recipefinder.fragments.ProfileTop;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ProfileTop.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        RecyclerView recyclerView = findViewById(R.id.setting_list);
        List<ProfileSetting> settingList = new ArrayList<>();
        settingList.add(new ProfileSetting("Friends", BitmapFactory.decodeResource(getResources(),R.drawable.common_google_signin_btn_icon_light)));
        settingList.add(new ProfileSetting("Preferences", BitmapFactory.decodeResource(getResources(),R.drawable.common_google_signin_btn_icon_light)));
        settingList.add(new ProfileSetting("Other", BitmapFactory.decodeResource(getResources(),R.drawable.common_google_signin_btn_icon_light)));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ProfileAdapter adapter = new ProfileAdapter(settingList);
        recyclerView.setAdapter(adapter);

    }

    public void onFragmentInteraction(Uri uri) {

    }
}
