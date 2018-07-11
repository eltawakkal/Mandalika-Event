package com.example.thebestone.mandalikaevents.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.thebestone.mandalikaevents.R;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

public class OriginalPhoto extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktifitas_foto_asli);

        String photoEventUrl = getIntent().getStringExtra("photo");

        ZoomageView imgOriginal = findViewById(R.id.img_original);

        try {
            Picasso.get().load(photoEventUrl).into(imgOriginal);
        } catch (Exception e) {
            Toast.makeText(this, "Terjadi Kesalahan Memuat Gambar", Toast.LENGTH_SHORT).show();
        }

    }
}
