package com.example.thebestone.mandalikaevents.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thebestone.mandalikaevents.R;
import com.example.thebestone.mandalikaevents.activity.AktifitasDetail;
import com.example.thebestone.mandalikaevents.activity.AktifitasLogin;
import com.example.thebestone.mandalikaevents.activity.AktifitasTambahEvent;
import com.example.thebestone.mandalikaevents.activity.AktifitasUtama;
import com.example.thebestone.mandalikaevents.models.UserEvent;
import com.example.thebestone.mandalikaevents.preferences.MandalikaPref;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecAdapterProf extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int HEADER_VIEW = 0;

    List<UserEvent> userEvents;
    Activity activity;

    MandalikaPref myPref;

    GoogleSignInOptions mGoogleOption;
    GoogleSignInClient mGoogleClient;

    public RecAdapterProf(List<UserEvent> events, Activity activity) {
        this.userEvents = events;
        this.activity = activity;

        myPref = new MandalikaPref(activity);

        mGoogleOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleClient = GoogleSignIn.getClient(activity, mGoogleOption);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (viewType == HEADER_VIEW) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_header_profile, parent, false);
            return new HeaderViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_event_prof, parent, false);
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof HeaderViewHolder) {

            HeaderViewHolder vHD = (HeaderViewHolder) holder;

            vHD.tvLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoogleClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(activity, "Kamu Telah Keluar", Toast.LENGTH_SHORT).show();

                            myPref.deleteUser();
                            Pair pair = new Pair<View, String> (AktifitasUtama.tvLogo, "transLogo");
                            Intent i = new Intent(activity, AktifitasLogin.class);
                            ActivityOptions ao = ActivityOptions.makeSceneTransitionAnimation(activity, pair);

                            myPref.deleteUser();

                            activity.startActivity(i, ao.toBundle());
                            activity.finish();
                        }
                    });
                }
            });

            vHD.tvNama.setText(myPref.getNama());
            vHD.tvEmail.setText(myPref.getEmail());

            try {
                Picasso.get().load(myPref.getFotourl()).placeholder(R.mipmap.ic_launcher_round)
                        .resize(200, 200).into(vHD.imgProfil);
            } catch (Exception e) {
                Picasso.get().load(R.mipmap.ic_launcher_round).resize(200, 200)
                        .into(vHD.imgProfil);
            }

        } else {
            ItemViewHolder vhItem = (ItemViewHolder) holder;

            String tglEvent = userEvents.get(position - 1).getTglEvent() + ", " + userEvents.get(position-1).getWaktuEvent();

            vhItem.txtJudul.setText(userEvents.get(position - 1).getNamaEvent());
            vhItem.txtLokasi.setText(userEvents.get(position - 1).getLokasiEvent());
            vhItem.txtWaktu.setText(tglEvent);

            vhItem.imgEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.startActivity(new Intent(activity, AktifitasDetail.class));
                    PublicVar.userEventPublic = userEvents.get(position-1);
                }
            });

            vhItem.imgPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(view, position);
                }
            });

            try {
                Picasso.get().load(userEvents.get(position-1).getPhotoEvent()).
                        resize(700, 400).into(vhItem.imgEvent);
            } catch (Exception e) {
                Picasso.get().load(R.drawable.wall_sample).into(vhItem.imgEvent);
            }
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

        TextView tvNama, tvEmail, tvLogout;
        CircleImageView imgProfil;

        public HeaderViewHolder(View v) {
            super(v);

            tvLogout = v.findViewById(R.id.tvLogout);
            tvNama = v.findViewById(R.id.tvNamaProfil);
            tvEmail = v.findViewById(R.id.tvEmailProfil);

            imgProfil = v.findViewById(R.id.imgUserProfil);
        }

    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView txtJudul, txtLokasi, txtWaktu;
        ImageView imgEvent, imgPopup;

        public ItemViewHolder(View v) {
            super(v);

            txtJudul = v.findViewById(R.id.txtJudulEventProfil);
            txtLokasi = v.findViewById(R.id.txtLokasiEventProfil);
            txtWaktu = v.findViewById(R.id.txtWaktuEventProfil);
            imgEvent = v.findViewById(R.id.imgWallEventProw);
            imgPopup = v.findViewById(R.id.imgPopupProfil);
        }

    }

    public void showPopupMenu(View v, final int position) {
        PopupMenu popMenu = new PopupMenu(activity, v);
        final MenuInflater inflater = popMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_card_event_user, popMenu.getMenu());
        popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.mnu_card_user_hapus:
                        deleteEvent(userEvents.get(position-1).getKodeEvent(), userEvents.get(position-1).getPhotoEvent());
                        break;
                    case R.id.mnu_card_user_edit:
                        PublicVar.userEventPublic = userEvents.get(position-1);
                        Intent intent = new Intent(activity, AktifitasTambahEvent.class);
                        intent.putExtra("edit", true);
                        activity.startActivity(intent);
                        break;
                }
                return false;
            }
        });
        popMenu.show();
    }

    private void deleteEvent(final String kodeEvent, final String photoEventUrl) {

        final DatabaseReference[] dbRef = {FirebaseDatabase.getInstance().getReference("events")};
        final StorageReference[] storageRef = {FirebaseStorage.getInstance().getReference("Mandalika_event/")};

        AlertDialog.Builder hapus = new AlertDialog.Builder(activity);
        hapus
                .setTitle("Hapus Event")
                .setMessage("Yakin Hapus Event Ini?")
                .setCancelable(false)
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final ProgressDialog pd = new ProgressDialog(activity);
                        pd.setTitle("Menghapus");
                        pd.setMessage("Proses Menghapus Event...");
                        pd.setCancelable(false);
                        pd.show();

                        dbRef[0] = FirebaseDatabase.getInstance().getReference("events").child(kodeEvent);
                        storageRef[0] = FirebaseStorage.getInstance().getReferenceFromUrl(photoEventUrl);

                        storageRef[0].delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dbRef[0].removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismiss();
                                        Toast.makeText(activity, "Event Dihapus", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton("Batal", null)
                .create().show();

    }

}
