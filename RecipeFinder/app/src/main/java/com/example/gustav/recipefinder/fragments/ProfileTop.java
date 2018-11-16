package com.example.gustav.recipefinder.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gustav.recipefinder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileTop extends Fragment {

    private String uID;

    public ProfileTop() {
        // Required empty public constructor
    }
    public static ProfileTop newInstance(String uID) {
        Bundle bundle = new Bundle();
        bundle.putString("uID", uID);
        ProfileTop fragment = new ProfileTop();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            uID = bundle.getString("uID");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        readBundle(getArguments());

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/"+uID+"");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView descTV = getView().findViewById(R.id.description);
                TextView nameTV = getView().findViewById(R.id.name);

                String desc = dataSnapshot.child("profile/description").getValue(String.class);
                String name = dataSnapshot.child("name").getValue(String.class);

                nameTV.setText(name);
                descTV.setText(desc);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_top, container, false);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
