package com.example.thebestone.mandalikaevents.fragments;

import android.app.Activity;
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

import com.example.thebestone.mandalikaevents.R;
import com.example.thebestone.mandalikaevents.adapters.RecyclerAdapterFavQ;
import com.example.thebestone.mandalikaevents.models.UserEvent;
import com.example.thebestone.mandalikaevents.sqlite.SqliteHelper;

import java.util.List;

public class FragJoin extends Fragment {

    SqliteHelper dbHelper;
    Activity contex;
    RecyclerAdapterFavQ adapterFavQ;

    List<UserEvent> userEvents;

    RecyclerView mRecyclerFav;

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
        contex = getActivity();

        dbHelper = new SqliteHelper(getActivity());
        userEvents = dbHelper.selectAllFav();
        adapterFavQ = new RecyclerAdapterFavQ(userEvents, getActivity());

        mRecyclerFav = v.findViewById(R.id.rec_main);
    }

}
