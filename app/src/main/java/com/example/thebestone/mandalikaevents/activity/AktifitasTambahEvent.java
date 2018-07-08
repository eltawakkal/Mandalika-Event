package com.example.thebestone.mandalikaevents.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.thebestone.mandalikaevents.R;
import com.example.thebestone.mandalikaevents.adapters.ListViewKabAdapter;
import com.example.thebestone.mandalikaevents.models.UserEvent;
import com.example.thebestone.mandalikaevents.preferences.MandalikaPref;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class AktifitasTambahEvent extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener{

    private int PICK_IMAGE = 1;

    private Uri uriImagePicked;
    private String namaImage;

    EditText editNama, editDesk, editTgl, editWaktu, editLokasi;
    Button btnPostEvent;
    ImageView imgEvent;
    Spinner spinJenisEvent;
    ProgressDialog pd;

    String[] arrayKab = {"Lombok Barat", "Lombok Tengah", "Lombok Timur", "Lombok Utara", "Kota Mataram"};
    String[] arrayJenis ={"Workshop", "Seminar", "Festival", "Kompetisi", "Job Fair"};

    DatabaseReference refEvents;
    StorageReference refStorage;

    MandalikaPref myPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktifitas_tambah_event);

        initObj();

        editTgl.setOnFocusChangeListener(this);
        editTgl.setOnClickListener(this);
        editWaktu.setOnFocusChangeListener(this);
        editWaktu.setOnClickListener(this);
        editLokasi.setOnFocusChangeListener(this);
        editLokasi.setOnClickListener(this);

        btnPostEvent.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uriImagePicked = data.getData();
            namaImage = "Mandalika_Event-" + UUID.randomUUID().toString();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImagePicked);

                imgEvent.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initObj() {

        refEvents = FirebaseDatabase.getInstance().getReference("events");
        myPref = new MandalikaPref(this);

        imgEvent = findViewById(R.id.imgEventTambah);
        btnPostEvent = findViewById(R.id.btnPostEvent);
        editNama = findViewById(R.id.editNamaEventTambah);
        editDesk = findViewById(R.id.editDeskripsiEventTambah);
        editTgl = findViewById(R.id.editTglTambah);
        editWaktu = findViewById(R.id.editWaktuTambah);
        editLokasi = findViewById(R.id.editLokasiTambah);
        spinJenisEvent = findViewById(R.id.spinJenisEventTambah);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPostEvent:

                final String nama, desc, tgl, waktu, lokasi, jenisEvent;

                nama = editNama.getText().toString();
                desc = editDesk.getText().toString();
                waktu = editWaktu.getText().toString();
                tgl = editTgl.getText().toString();
                lokasi = editLokasi.getText().toString();
                jenisEvent = spinJenisEvent.getSelectedItem().toString();

                if (editEmpty()) {
                    Toast.makeText(this, "Semua kolom harus di isi", Toast.LENGTH_SHORT).show();
                } else {
                    if (btnPostEvent.getText().equals("Post Event")) {
                        String kodeEvent = refEvents.push().getKey();
                        addEvent(nama, desc, jenisEvent, lokasi, waktu, tgl);
                    } else {
//                        updateEvent(PublicVar.userEventPublic.getKodeEvent(), nama, desc, jenisEvent, waktu, tgl, lokasi);
                    }
                }

                break;
            case R.id.editTglTambah:
                setTanggal();
                break;
            case R.id.editWaktuTambah:
                setWaktu();
                break;
            case R.id.editLokasiTambah:
                setKabupaten();
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.editTglTambah:
                if (b) setTanggal();
                break;
            case R.id.editWaktuTambah:
                if (b) setWaktu();
                break;
            case R.id.editLokasiTambah:
                setKabupaten();
                break;
        }
    }

    public void selectPicture(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public boolean editEmpty() {
        if (TextUtils.isEmpty(editNama.getText().toString()) || TextUtils.isEmpty(editDesk.getText().toString()) ||
                TextUtils.isEmpty(editWaktu.getText().toString()) || TextUtils.isEmpty(editTgl.getText().toString()) ||
                TextUtils.isEmpty(editLokasi.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

    private void setTanggal() {
        final DatePicker datePicker = new DatePicker(this);
        AlertDialog.Builder setWaktu = new AlertDialog.Builder(this);
        setWaktu
                .setView(datePicker)
                .setPositiveButton("Atur", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int tgl, bulan, tahun;
                        tgl = datePicker.getDayOfMonth();
                        bulan = datePicker.getMonth() + 1;
                        tahun = datePicker.getYear();

                        editTgl.setText(tgl + "/" + bulan + "/" + tahun);
                    }
                })
                .create().show();
    }

    public void setKabupaten() {
        ListViewKabAdapter adapter = new ListViewKabAdapter(this, arrayKab);
        ListView listView = new ListView(this);
        listView.setAdapter(adapter);

        AlertDialog.Builder aletKab = new AlertDialog.Builder(this);
        aletKab.setView(listView);

        final Dialog dialog = aletKab.create();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editLokasi.setText(arrayKab[i]);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void setWaktu() {
        final TimePicker timePicker = new TimePicker(this);
        AlertDialog.Builder setWaktu = new AlertDialog.Builder(this);
        setWaktu
                .setView(timePicker)
                .setPositiveButton("Atur", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editWaktu.setText(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                    }
                })
                .create().show();
    }

    public void addEvent(final String nama, final String desc, final String jenisEvent, final String lokasi, final String waktu, final String tgl) {

        refStorage = FirebaseStorage.getInstance().getReference("Mandalika_event/" + namaImage);
        final String urlFotoEvent = "https://firebasestorage.googleapis.com/v0/b/mandalika-event.appspot.com/o/Mandalika_event%2F" + namaImage + "?alt=media&token=65f49863-0475-4979-a42e-1f90c86a63bc";

        if (uriImagePicked == null) {
            Toast.makeText(this, "Silahkan Pilih Foto", Toast.LENGTH_SHORT).show();
        } else {

            showProgresDialog();

            refStorage.putFile(uriImagePicked).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String kodeEvent = refEvents.push().getKey();

                    UserEvent userEvent = new UserEvent(kodeEvent, myPref.getEmail(), myPref.getFotourl(), nama, desc, urlFotoEvent, jenisEvent, lokasi, waktu, tgl);
                    refEvents.child(kodeEvent).setValue(userEvent);

                    Toast.makeText(AktifitasTambahEvent.this, "Event Telah Di Post", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AktifitasTambahEvent.this, "Terjadi Kesalahan Saat mem-Posting", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }

    public void showProgresDialog() {
        pd = new ProgressDialog(this);
        pd.setTitle("Tunggu Sesaat");
        pd.setMessage("Event anda akan segera di post...");
        pd.setCancelable(false);
        pd.setButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(AktifitasTambahEvent.this, "Batal", Toast.LENGTH_SHORT).show();
            }
        });
        pd.show();
    }
}
