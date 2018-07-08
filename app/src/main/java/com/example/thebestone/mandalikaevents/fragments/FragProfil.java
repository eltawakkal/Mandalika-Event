package com.example.thebestone.mandalikaevents.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thebestone.mandalikaevents.R;
import com.example.thebestone.mandalikaevents.activity.AktifitasTambahEvent;
import com.example.thebestone.mandalikaevents.adapters.RecAdapterBeranda;
import com.example.thebestone.mandalikaevents.adapters.RecAdapterProf;
import com.example.thebestone.mandalikaevents.models.UserEvent;
import com.example.thebestone.mandalikaevents.preferences.MandalikaPref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragProfil extends Fragment {

    private RecyclerView mRecyclerMain;
    private FloatingActionButton fabTambahEvent;
    private RecAdapterProf adapterBeranda;
    private MandalikaPref myPref;

    private DatabaseReference refEvents;

    private List<UserEvent> listEvents;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_beranda, container, false);

        initObj(v);

        fabTambahEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AktifitasTambahEvent.class));
            }
        });

        mRecyclerMain.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabTambahEvent.hide();
                } else if (dy < 0) {
                    fabTambahEvent.show();
                }
            }
        });

        return v;
    }

    private void initObj(View v) {

        myPref = new MandalikaPref(getContext());

        refEvents = FirebaseDatabase.getInstance().getReference("events");

        mRecyclerMain = v.findViewById(R.id.rec_main);
        fabTambahEvent = v.findViewById(R.id.fabTambahEvent);
        fabTambahEvent.setVisibility(View.VISIBLE);

        listEvents = new ArrayList<>();

        getEvents();
    }

    public void getEvents(){

        Query query = refEvents.orderByChild("emailUser").equalTo(myPref.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserEvent> tampEvents = new ArrayList<>();


                for (DataSnapshot snapEvent : dataSnapshot.getChildren()) {
                    UserEvent event = snapEvent.getValue(UserEvent.class);
                    tampEvents.add(event);
                }

                Toast.makeText(getContext(), "total : " + tampEvents.size(), Toast.LENGTH_SHORT).show();

                for (int i = tampEvents.size() - 1; i>=0; i--) {
                    listEvents.add(tampEvents.get(i));
                }

                setRecItems();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setRecItems() {

        adapterBeranda = new RecAdapterProf(listEvents, getActivity());
        LinearLayoutManager llManager = new LinearLayoutManager(getContext());

        mRecyclerMain.setLayoutManager(llManager);
        mRecyclerMain.setHasFixedSize(true);
        mRecyclerMain.setAdapter(adapterBeranda);
    }

}
