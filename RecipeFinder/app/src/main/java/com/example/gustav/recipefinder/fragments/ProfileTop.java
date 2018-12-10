package com.example.gustav.recipefinder.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gustav.recipefinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileTop extends Fragment {
    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String uID;

    // Ifall den inloggade användaren har skickat en friendrequest
    private boolean sent_friend_request = false;
    // Ifall den inloggade användaren har fått en friendrequest
    private boolean recieved_friend_request = false;
    private boolean friends = false;

    /*
    0 = Inte vänner
    1 = Pending friend request
    2 = Vänner
     */
    private int friend_state = 0;

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
    /*
    private void add_friend() { KLAR
        DatabaseReference ref = mDatabase.child("friend_requests/"+uID);
        Map<String, Object> updates = new HashMap<>();
        updates.put(mAuth.getUid(), true);
        ref.updateChildren(updates);
    }

    private void remove_friend() {
    }

    private void deny_friend_request() {

    }

    // TODO: Dubbelkolla så detta fungerar ifall det finns mer än 1 request för användaren
    private void cancel_friend_request() { KLAR
        mDatabase.child("friend_requests/"+uID+"/"+mAuth.getUid()).removeValue();
    }
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        readBundle(getArguments());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setup_description_listener();
        if(!mAuth.getUid().equals(uID)) {
            setup_friend_listener();
        }
    }

    private void setup_description_listener() {
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
    }

    private void setup_friend_listener() {
        final ImageButton friend_button = getView().findViewById(R.id.friend_button);
        final TextView friend_status_text = getView().findViewById(R.id.friend_status_text);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("friend_requests");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sent_friend_request = false;
                recieved_friend_request = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> sender = (Map<String, Object>) snapshot.getValue();
                    String reciever = snapshot.getKey();
                    // Ifall den inloggade användaren har skickat en friendrequest
                    if(reciever.equals(uID)) {
                        if(sender.containsKey(mAuth.getUid())) {
                            sent_friend_request = true;
                            mListener.friend_status_changed(false);
                            friend_status_text.setText(R.string.friendPending);
                            friend_button.setBackgroundResource(R.drawable.remove_friend);
                            friend_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mListener.cancel_friend_request(uID);
                                }
                            });
                        }
                    }
                    // Ifall den inloggade användaren har fått en friendrequest
                    if(reciever.equals(mAuth.getUid())) {
                        if(sender.containsKey(uID)) {
                            recieved_friend_request = true;
                            mListener.friend_status_changed(false);
                            friend_status_text.setText(R.string.friendRecieved);
                            friend_button.setBackgroundResource(R.drawable.add_friend);
                            friend_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mListener.friend_status_changed(true);
                                    mListener.accept_friend_request(uID);

                                }
                            });
                        }
                    }
                }
                if(!sent_friend_request && !recieved_friend_request && !friends) {
                    mListener.friend_status_changed(false);
                    friend_status_text.setText(R.string.notFriends);
                    friend_button.setBackgroundResource(R.drawable.add_friend);
                    friend_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.add_friend(uID);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref = database.getReference("user_friends");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friends = false;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    Map<String, Object> values = (Map<String, Object>) snapshot.getValue();

                    if(key.equals(uID)) {
                        if(values.containsKey(mAuth.getUid())) {
                            friends = true;
                            mListener.friend_status_changed(true);
                            friend_status_text.setText(R.string.friends);
                            friend_button.setBackgroundResource(R.drawable.remove_friend);
                            friend_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mListener.friend_status_changed(false);
                                    mListener.remove_friend(uID);
                                }
                            });
                        }
                    }
                }

                if(!sent_friend_request && !recieved_friend_request && !friends) {
                    mListener.friend_status_changed(false);
                    friend_status_text.setText(R.string.notFriends);
                    friend_button.setBackgroundResource(R.drawable.add_friend);
                    friend_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.add_friend(uID);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_top, container, false);
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
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void remove_friend(String friend_id);
        void add_friend(String friend_id);
        void cancel_friend_request(String friend_id);
        void accept_friend_request(String friend_id);
        void deny_friend_request(String friend_id);
        void friend_status_changed(boolean friends);
    }
}
