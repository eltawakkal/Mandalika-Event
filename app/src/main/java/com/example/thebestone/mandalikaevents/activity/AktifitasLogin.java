package com.example.thebestone.mandalikaevents.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.thebestone.mandalikaevents.R;
import com.example.thebestone.mandalikaevents.models.User;
import com.example.thebestone.mandalikaevents.preferences.MandalikaPref;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AktifitasLogin extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;

    Button btGoogleSignIn;

    GoogleSignInClient mGoogleClient;
    GoogleSignInOptions mGoogleOption;
    GoogleSignInAccount mAccount;

    MandalikaPref userPref;

    DatabaseReference refUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktifitas_login);

        initGoogleOBj();
        initOther();
    }

    private void initOther() {
        userPref = new MandalikaPref(this);
        refUser = FirebaseDatabase.getInstance().getReference("users");
    }

    private void initGoogleOBj() {

        btGoogleSignIn = findViewById(R.id.bt_google_sign_in);

        mGoogleOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleClient = GoogleSignIn.getClient(this, mGoogleOption);

        btGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("users");

            Query query = refUser.orderByChild("emailUser").equalTo(account.getEmail());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    cekPengguna(dataSnapshot, account);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    private void cekPengguna(DataSnapshot dataSnapshot, GoogleSignInAccount account) {

        User user = null;

        for (DataSnapshot snapUser : dataSnapshot.getChildren()) {
             user = snapUser.getValue(User.class);
        }

        if (user != null) {
            setUserPref(user);
            gotoAktifitasUtama();
        } else {
            tampilkanPersetujuan(account);
        }
    }

    private void setUserPref(User user) {
        userPref.setUser(user);
    }

    private void gotoAktifitasUtama() {
        Intent intentUtama = new Intent(this, AktifitasUtama.class);
        intentUtama.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentUtama);
    }

    public void tampilkanPersetujuan(final GoogleSignInAccount account) {
        View v = getLayoutInflater().inflate(R.layout.dialog_persetujuan, null);

        Button btRegister = v.findViewById(R.id.btRegister);

        AlertDialog.Builder alert = new AlertDialog.Builder(this).setView(v);
        final Dialog dialog = alert.create();
        dialog.show();

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user;

                try {
                    user = new User(
                            account.getDisplayName(),
                            account.getEmail(),
                            account.getPhotoUrl().toString(),
                            "Member"
                    );
                } catch (Exception e) {

                    Toast.makeText(AktifitasLogin.this, "Anda Tidak Memiliki Foto", Toast.LENGTH_SHORT).show();

                    user = new User(
                            account.getDisplayName(),
                            account.getEmail(),
                            "tidak ada",
                            "Member"
                    );
                }


                String id = refUser.push().getKey();
                refUser.child(id).setValue(user);

                setUserPref(user);

                gotoAktifitasUtama();

                dialog.dismiss();
            }
        });
    }

    public void skipLogin(View v) {
        gotoAktifitasUtama();
    }
}
