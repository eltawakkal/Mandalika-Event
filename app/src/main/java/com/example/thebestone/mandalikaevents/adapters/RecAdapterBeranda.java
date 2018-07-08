package com.example.thebestone.mandalikaevents.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thebestone.mandalikaevents.R;
import com.example.thebestone.mandalikaevents.activity.AktifitasDetail;
import com.example.thebestone.mandalikaevents.models.User;
import com.example.thebestone.mandalikaevents.models.UserEvent;
import com.example.thebestone.mandalikaevents.preferences.MandalikaPref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecAdapterBeranda extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int HEADER_VIEW = 0;

    List<UserEvent> userEvents;
    List<UserEvent> userEventsBackUp;
    Activity activity;
    User user;

    public RecAdapterBeranda(List<UserEvent> events, Activity activity, User user) {
        this.userEvents = events;
        this.userEventsBackUp = events;
        this.activity = activity;
        this.user = user;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (viewType == HEADER_VIEW) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_header_main, parent, false);
            return new HeaderViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_event_main, parent, false);
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder hVH = (HeaderViewHolder) holder;

            hVH.imgRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userEvents = userEventsBackUp;
                    notifyDataSetChanged();
                }
            });

            hVH.btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filterKab("Workshop");
                }
            });

            hVH.btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filterKab("Seminar");
                }
            });

            hVH.btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filterKab("Festival");
                }
            });

            hVH.btn4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filterKab("Kompetisi");
                }
            });

            hVH.btn5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filterKab("Job Fair");
                }
            });
        } else {
            final ItemViewHolder itemsViewHolder = (ItemViewHolder)holder;

            String tglEvent = userEvents.get(position-1).getTglEvent() + ", " + userEvents.get(position-1).getWaktuEvent();

            itemsViewHolder.txtNamaEvent.setText(userEvents.get(position-1).getNamaEvent());
            itemsViewHolder.txtLokasiEvent.setText(userEvents.get(position-1).getLokasiEvent());
            itemsViewHolder.txtWaktuEvent.setText(tglEvent);
            itemsViewHolder.tvJenisEvent.setText(userEvents.get(position-1).getJenisEvent());

            if (user.getStatus().equals("Admin")) {
                itemsViewHolder.imgDelete.setVisibility(View.VISIBLE);
            } else {
                itemsViewHolder.imgDelete.setVisibility(View.INVISIBLE);
            }

            try {
                Picasso.get().load(userEvents.get(position-1).getPhotoUser()).placeholder(R.mipmap.ic_launcher_round).resize(200, 200)
                        .into(itemsViewHolder.imgUser);
            } catch (Exception e) {
                Picasso.get().load(R.mipmap.ic_launcher_round).resize(200, 200)
                        .into(itemsViewHolder.imgUser);
            }

            ImageView imgE = itemsViewHolder.imgEvent;

            try {
                Picasso.get().load(userEvents.get(position-1).getPhotoEvent())
                        .into(imgE);
            } catch (Exception e) {
                Picasso.get().load(R.drawable.wall_sample).into(imgE);
            }

            itemsViewHolder.imgEvent.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    Pair pair = new Pair<View, String>(itemsViewHolder.imgEvent, "transImgEvent");
                    Intent intent = new Intent(activity, AktifitasDetail.class);
                    ActivityOptions ao = ActivityOptions.makeSceneTransitionAnimation(activity, pair);
                    PublicVar.userEventPublic = userEvents.get(position-1);

                    activity.startActivity(intent, ao.toBundle());
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == HEADER_VIEW) {
            return HEADER_VIEW;
        } else {
            return position;
        }
    }

    @Override
    public int getItemCount() {
        return userEvents.size() + 1;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        Button btn1, btn2, btn3, btn4, btn5;
        ImageView imgRefresh;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            imgRefresh = itemView.findViewById(R.id.imgRefresh);
            btn1 = itemView.findViewById(R.id.btn1);
            btn2 = itemView.findViewById(R.id.btn2);
            btn3 = itemView.findViewById(R.id.btn3);
            btn4 = itemView.findViewById(R.id.btn4);
            btn5 = itemView.findViewById(R.id.btn5);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView imgEvent, imgUser, imgDelete;
        TextView txtNamaEvent, txtLokasiEvent, txtWaktuEvent, tvJenisEvent;
        FloatingActionButton fabFav;

        public ItemViewHolder(View v) {
            super(v);

            imgEvent = v.findViewById(R.id.imgEventBeranda);
            imgUser = v.findViewById(R.id.imgUserBeranda);
            imgDelete = v.findViewById(R.id.imgDeleteEventBeranda);
            txtNamaEvent = v.findViewById(R.id.txtNamaEvent);
            txtLokasiEvent = v.findViewById(R.id.txtLokasi);
            txtWaktuEvent = v.findViewById(R.id.txtTime);
            tvJenisEvent = v.findViewById(R.id.tvJenisEventBeranda);
        }

    }

    public void filterKab(String jenisEvent) {

        userEvents = userEventsBackUp;

        List<UserEvent> listEvent = new ArrayList<>();

        for (UserEvent userEvent : userEvents) {
            if (userEvent.getJenisEvent().contains(jenisEvent)) {
                listEvent.add(userEvent);
            }
        }

        this.userEvents = listEvent;
        notifyDataSetChanged();
    }
}
