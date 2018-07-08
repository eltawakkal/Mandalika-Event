package com.example.thebestone.mandalikaevents.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thebestone.mandalikaevents.R;
import com.example.thebestone.mandalikaevents.adapters.RecAdapterBeranda;
import com.example.thebestone.mandalikaevents.models.User;
import com.example.thebestone.mandalikaevents.models.UserEvent;
import com.example.thebestone.mandalikaevents.preferences.MandalikaPref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragBeranda extends Fragment {

    private RecyclerView mRecyclerMain;
    private FloatingActionButton fabTambahEvent;
    private RecAdapterBeranda adapterBeranda;

    private DatabaseReference refEvents;

    private List<UserEvent> listEvents;
    private MandalikaPref myPref;

    public static FragBeranda fragBeranda;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_beranda, container, false);

        initObj(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        getEvents();
    }

    private void initObj(View v) {

        fragBeranda = this;

        refEvents = FirebaseDatabase.getInstance().getReference("events");
        mRecyclerMain = v.findViewById(R.id.rec_main);
        fabTambahEvent = v.findViewById(R.id.fabTambahEvent);
        fabTambahEvent.setVisibility(View.GONE);

        listEvents = new ArrayList<>();
        myPref = new MandalikaPref(getContext());
    }

    private void setRecBeranda() {

        adapterBeranda = new RecAdapterBeranda(listEvents, getActivity(), getUser());
        LinearLayoutManager llManager = new LinearLayoutManager(getContext());

        mRecyclerMain.setLayoutManager(llManager);
        mRecyclerMain.setHasFixedSize(true);
        mRecyclerMain.setAdapter(adapterBeranda);
    }

    public void getEvents(){
        refEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listEvents.clear();
                List<UserEvent> tampEvents = new ArrayList<>();


                for (DataSnapshot snapEvent : dataSnapshot.getChildren()) {
                    UserEvent event = snapEvent.getValue(UserEvent.class);
                    tampEvents.add(event);
                }

//                Toast.makeText(getContext(), "total : " + tampEvents.size(), Toast.LENGTH_SHORT).show();

                for (int i = tampEvents.size() - 1; i>=0; i--) {
                    listEvents.add(tampEvents.get(i));
                }

                setRecBeranda();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public User getUser() {
        User user = myPref.getUser();
        return user;
    }
}
