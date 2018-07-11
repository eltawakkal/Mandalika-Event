package com.example.thebestone.mandalikaevents.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thebestone.mandalikaevents.R;
import com.example.thebestone.mandalikaevents.adapters.PublicVar;
import com.example.thebestone.mandalikaevents.models.UserEvent;
import com.example.thebestone.mandalikaevents.preferences.MandalikaPref;
import com.example.thebestone.mandalikaevents.services.SimpleAlarmManager;
import com.example.thebestone.mandalikaevents.sqlite.SqliteHelper;
import com.squareup.picasso.Picasso;

public class AktifitasDetail extends AppCompatActivity {

    TextView tvDeskEvent, tvLokasi, tvTgl, tvWaktu;
    String lokEvent, tglEvent, waktuEvent, namaEvent, desEvent, emailuser, kodeEvent, photoUser, jenisEvent, photoEvent;
    ImageView imgPhotoEventDetail;
    SqliteHelper dbHelper;

    int tglJadi, bulanJadi, tahunJadi;

    MandalikaPref myPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktifitas_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getDataFromExtras();

        setTitle(namaEvent);

        init();

        tvDeskEvent.setText(desEvent);
        tvLokasi.setText(lokEvent);
        tvTgl.setText(tglEvent);
        tvWaktu.setText(waktuEvent);

        imgPhotoEventDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOriginalImage();
            }
        });

        try {
            Picasso.get().load(photoEvent).into(imgPhotoEventDetail);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.wall_sample).into(imgPhotoEventDetail);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loggedIn()) {

                    if (dbHelper.dataTidakDouble(kodeEvent, myPref.getEmail())) {
                        dbHelper.addEvent(kodeEvent, myPref.getEmail(), photoUser, namaEvent, desEvent, photoEvent, jenisEvent, waktuEvent, tglEvent, lokEvent, myPref.getEmail());
                        sparatedTgl(tglEvent);

                        new SimpleAlarmManager(AktifitasDetail.this).setup(
                                SimpleAlarmManager.INTERVAL_DAY,
                                7,
                                0,
                                0

                        ).register(1).start();

                        Snackbar.make(view, "Dimasukkan Didalam Favorit", Snackbar.LENGTH_LONG)
                                .setAction("Batalkan", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dbHelper.deleteEvent(kodeEvent);
                                    }
                                }).show();
                    } else {
                        Toast.makeText(AktifitasDetail.this, "Event ini tsudah ada di list agenda", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    startActivity(new Intent(AktifitasDetail.this, AktifitasLogin.class));
                }

            }
        });
    }

    private void openOriginalImage() {
        Intent intent = new Intent(this, OriginalPhoto.class);
        intent.putExtra("photo", photoEvent);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sparatedTgl(String tgl) {
        String[] dataWaktu = tgl.split("/");

        tglJadi = Integer.parseInt(dataWaktu[0]);
        bulanJadi = Integer.parseInt(dataWaktu[1]);
        tahunJadi = Integer.parseInt(dataWaktu[2]);

//        addAlarm(tglJadi, bulanJadi, tahunJadi);
    }

    private void getDataFromExtras() {
        UserEvent userEvent = PublicVar.userEventPublic;

        kodeEvent = userEvent.getKodeEvent();
        emailuser = userEvent.getEmailUser();
        photoUser = userEvent.getPhotoUser();
        namaEvent = userEvent.getNamaEvent();
        desEvent = userEvent.getDescEvent();
        lokEvent = userEvent.getLokasiEvent();
        waktuEvent = userEvent.getWaktuEvent();
        tglEvent = userEvent.getTglEvent();
        jenisEvent = userEvent.getJenisEvent();
        photoEvent = userEvent.getPhotoEvent();
    }

    private void init() {
        myPref = new MandalikaPref(this);
        dbHelper = new SqliteHelper(this);

        tvDeskEvent = findViewById(R.id.tvDescEventDetail);
        tvLokasi = findViewById(R.id.tvLokasiDetails);
        tvTgl = findViewById(R.id.tvTglDetails);
        tvWaktu = findViewById(R.id.tvWaktuDetails);

        imgPhotoEventDetail = findViewById(R.id.imgEventDetail);
    }

    private boolean loggedIn() {
        String nama = myPref.getNama();

        if (nama == null) {
            return false;
        } else {
            return true;
        }
    }

}
