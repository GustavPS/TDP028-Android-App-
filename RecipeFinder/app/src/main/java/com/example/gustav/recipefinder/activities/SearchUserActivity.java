package com.example.gustav.recipefinder.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.gustav.recipefinder.Bookmark;
import com.example.gustav.recipefinder.R;
import com.example.gustav.recipefinder.adapters.SearchAdapter;
import com.example.gustav.recipefinder.adapters.SearchUserAdapter;
import com.example.gustav.recipefinder.classes.User;
import com.example.gustav.recipefinder.fragments.BookmarkList;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity implements SearchUserAdapter.OnItemClicked {

    private SearchUserAdapter fAdapter;
    private RecyclerView      recyclerView;
    private List<User>        userList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        SearchView searchView = findViewById(R.id.search_field);


        recyclerView = findViewById(R.id.search_results);
        fAdapter = new SearchUserAdapter(userList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fAdapter);
        fAdapter.setOnClick(SearchUserActivity.this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search_for_user(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search_for_user(s);
                return false;
            }
        });
    }

    private void search_for_user(final String name) {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userList.clear();
                        String uName;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            user.setID(snapshot.getKey());
                            uName = user.getName().toLowerCase();
                            if(uName.contains(name.toLowerCase()))
                                userList.add(user);
                        }
                        fAdapter.notifyDataSetChanged(); // Uppdaterar listan
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void onItemClicked(int position) {
        String uID = userList.get(position).getID();
        Intent intent = new Intent(SearchUserActivity.this, ProfileActivity.class);
        intent.putExtra("uID", uID);
        startActivity(intent);
    }
}
