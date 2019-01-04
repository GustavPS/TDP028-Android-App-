package com.example.gustav.recipefinder.activities;

import android.content.Intent;
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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.adapters.ProfileAdapter;
import com.example.gustav.recipefinder.classes.ProfileSetting;
import com.example.gustav.recipefinder.fragments.BookmarkList;
import com.example.gustav.recipefinder.fragments.FriendList;
import com.example.gustav.recipefinder.fragments.ProfileAdmin;
import com.example.gustav.recipefinder.fragments.ProfileTop;
import com.example.gustav.recipefinder.fragments.Settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements ProfileTop.OnFragmentInteractionListener, ProfileAdmin.OnFragmentInteractionListener , Settings.OnFragmentInteractionListener, BookmarkList.OnFragmentInteractionListener, FriendList.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private Fragment bookmarkFrag;
    private String uID;

    private TextView friends_warning_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        uID = getIntent().getStringExtra("uID");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        friends_warning_text = findViewById(R.id.not_friends_warning);


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
                friends_warning_text.setVisibility(View.GONE);

                frag = new ProfileAdmin();
                transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_container, frag);
                transaction.commit();
            }
        }
    }

    @Override
    public void showRecipe(View view) {
        String URI = ((TextView) view.findViewById(R.id.URI)).getText().toString();
        Intent intent = new Intent(ProfileActivity.this, RecipeActivity.class);
        intent.putExtra("URI", URI);
        startActivity(intent);
    }

    public void onFragmentInteraction(Uri uri) {

    }

    public void onFragmentInteraction(String fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment frag;

        switch(fragment) {
            case "Friends":
                frag = new FriendList();
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

    @Override
    public void remove_friend(String friend_id) {
        mDatabase.child("user_friends/"+mAuth.getUid()+"/"+friend_id).removeValue();
        mDatabase.child("user_friends/"+friend_id+"/"+mAuth.getUid()).removeValue();
    }

    @Override
    public void add_friend(String friend_id) {
        DatabaseReference ref = mDatabase.child("friend_requests/"+friend_id);
        Map<String, Object> updates = new HashMap<>();
        updates.put(mAuth.getUid(), true);
        ref.updateChildren(updates);
    }

    @Override
    public void cancel_friend_request(String friend_id) {
        mDatabase.child("friend_requests/"+friend_id+"/"+mAuth.getUid()).removeValue();
    }

    @Override
    public void accept_friend_request(String friend_id) {
        mDatabase.child("friend_requests/"+mAuth.getUid()+"/"+friend_id).removeValue();

        DatabaseReference ref = mDatabase.child("user_friends/"+mAuth.getUid());
        Map<String, Object> updates = new HashMap<>();
        updates.put(friend_id, true);
        ref.updateChildren(updates);

        ref = mDatabase.child("user_friends/"+friend_id);
        updates = new HashMap<>();
        updates.put(mAuth.getUid(), true);
        ref.updateChildren(updates);
    }

    @Override
    public void deny_friend_request(String friend_id) {
        mDatabase.child("friend_requests/"+mAuth.getUid()+"/"+friend_id).removeValue();
    }

    @Override
    public void friend_status_changed(boolean friends) {
        if(friends) {
            friends_warning_text.setVisibility(View.GONE);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            bookmarkFrag = BookmarkList.newInstance(uID);
            transaction.replace(R.id.fragment_container, bookmarkFrag, "bookmark_frag");
            //transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        } else {
            friends_warning_text.setVisibility(View.VISIBLE);
            if(bookmarkFrag != null)
                getSupportFragmentManager().beginTransaction().remove(bookmarkFrag).commit();
        }
    }

    @Override
    public void cancel() {
        onFragmentInteraction("");
    }
}
