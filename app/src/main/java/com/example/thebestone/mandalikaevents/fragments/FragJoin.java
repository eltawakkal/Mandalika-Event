package com.example.thebestone.mandalikaevents.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thebestone.mandalikaevents.R;
import com.example.thebestone.mandalikaevents.adapters.RecAdapterJoin;
import com.example.thebestone.mandalikaevents.models.UserEvent;
import com.example.thebestone.mandalikaevents.preferences.MandalikaPref;
import com.example.thebestone.mandalikaevents.sqlite.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

public class FragJoin extends Fragment {

    SqliteHelper dbHelper;
    Activity contex;
    RecAdapterJoin adapterFavQ;

    List<UserEvent> userEvents;

    RecyclerView mRecyclerFav;

    MandalikaPref myPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_beranda, container, false);

        init(v);

        mRecyclerFav.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerFav.setAdapter(adapterFavQ);

        return v;
    }

    private void init(View v) {

        myPref = new MandalikaPref(getContext());
        contex = getActivity();
        dbHelper = new SqliteHelper(getActivity());
        userEvents = dbHelper.selectAllFav();

        List<UserEvent> userEventsReady = new ArrayList<>();

        for (UserEvent userEvent: userEvents) {
            if (userEvent.getEmailUser().equals(myPref.getEmail())) {
                userEventsReady.add(userEvent);
            }
        }

        if (userEventsReady.size()!=0) {
            adapterFavQ = new RecAdapterJoin(userEvents, getActivity());
        } else {
            Toast.makeText(contex, "Anda Belum Bergabung Ke Event Apapun!", Toast.LENGTH_SHORT).show();
        }


        mRecyclerFav = v.findViewById(R.id.rec_main);
    }

}
