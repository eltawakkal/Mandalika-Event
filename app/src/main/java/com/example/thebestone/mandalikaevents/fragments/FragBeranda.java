package com.example.thebestone.mandalikaevents.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragBeranda extends Fragment {

    private RecyclerView mRecyclerMain;
    private FloatingActionButton fabTambahEvent;
    private FloatingActionButton fabFilter;
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

        mRecyclerMain.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabFilter.hide();
                } else if (dy < 0) {
                    fabFilter.show();
                }
            }
        });

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertFilter();
            }
        });

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
        fabFilter = v.findViewById(R.id.fabFilterMain);

        fabFilter.setVisibility(View.VISIBLE);
        fabTambahEvent.setVisibility(View.GONE);

        listEvents = new ArrayList<>();
        myPref = new MandalikaPref(getContext());
    }

    private void setRecBeranda(List<UserEvent> events) {

        adapterBeranda = new RecAdapterBeranda(events, getActivity(), getUser());
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

                setRecBeranda(listEvents);
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

    public void alertFilter() {
        final Spinner spinKab, spinBulan;

        View v = getLayoutInflater().inflate(R.layout.filter_layout, null);
        spinKab = v.findViewById(R.id.spinKabMain);
        spinBulan = v.findViewById(R.id.spinBulanMain);


        AlertDialog.Builder setKab = new AlertDialog.Builder(getContext());
        setKab
                .setView(v)
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        frgEvent.filterData("silicon");

                        String lokasi, bulan;

                        lokasi = spinKab.getSelectedItem().toString();
                        bulan = (spinBulan.getSelectedItemPosition() + 1) + "";

                        getEventSpesifik(bulan, lokasi);

                    }
                });
        Dialog dialog = setKab.create();
        dialog.show();
    }

    public void getEventSpesifik(final String bulan, String lokasi) {
        Query query = refEvents.orderByChild("lokasiEvent").equalTo(lokasi);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<UserEvent> userEventsTamp = new ArrayList<>();
                List<UserEvent> events = new ArrayList<>();

                for (DataSnapshot snapEvent : dataSnapshot.getChildren()) {

                    UserEvent userEvent = snapEvent.getValue(UserEvent.class);
                    userEventsTamp.add(userEvent);

                }

                for (int i = userEventsTamp.size() -1; i >=0; i--) {


                    String tglEvent = userEventsTamp.get(i).getTglEvent();

                    String sparatedTgl[] = tglEvent.split("/");

                    if (sparatedTgl[1].equals(bulan)) {
                        events.add(userEventsTamp.get(i));
                    }

                }

                if (events.size() != 0) {
                    setRecBeranda(events);
                } else {
                    Toast.makeText(getContext(), "Tidak Ada Event Pada Lokasi dan Bulan ini", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
