package com.example.thebestone.mandalikaevents.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.thebestone.mandalikaevents.R;
import com.example.thebestone.mandalikaevents.fragments.FragBeranda;
import com.example.thebestone.mandalikaevents.fragments.FragJoin;
import com.example.thebestone.mandalikaevents.fragments.FragProfil;
import com.example.thebestone.mandalikaevents.preferences.MandalikaPref;
import com.example.thebestone.mandalikaevents.services.MandalikaService;

public class AktifitasUtama extends AppCompatActivity {

    FragBeranda fragBeranda;
    FragJoin fragJoin;
    FragProfil fragProfil;

    BottomNavigationView bottomNavigationView;
    public static TextView tvLogo;

    private int statusMode;

    MandalikaPref myPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktifitas_utama);

        initObj();

        if (statusMode == 1) {
            setFragment(fragJoin);
        } else {
            setFragment(fragBeranda);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                item.setChecked(true);

                switch (item.getItemId()) {
                    case R.id.menu_beranda:
                        setFragment(fragBeranda);
                        break;
                    case R.id.menu_join:

                        if (loggedIn()) {
                            setFragment(fragJoin);
                        } else {
                            startActivity(new Intent(AktifitasUtama.this, AktifitasLogin.class));
                        }

                        break;
                    case R.id.menu_profil:

                        if (loggedIn()) {
                            setFragment(fragProfil);
                        } else {
                            startActivity(new Intent(AktifitasUtama.this, AktifitasLogin.class));
                        }

                        break;

                    default:
                        setFragment(fragBeranda);
                        break;
                }

                return false;
            }
        });
    }

    private void initObj() {

        statusMode = getIntent().getIntExtra("STATUS", 0);

        bottomNavigationView = findViewById(R.id.bottom_nav_main);
        tvLogo = findViewById(R.id.tv_logo);

        fragBeranda = new FragBeranda();
        fragJoin = new FragJoin();
        fragProfil = new FragProfil();

        myPref = new MandalikaPref(this);

    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();

        fragTransaction
                .replace(R.id.frag_container_beranda, fragment)
                .commit();
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
