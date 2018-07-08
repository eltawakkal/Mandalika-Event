package com.example.thebestone.mandalikaevents.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.thebestone.mandalikaevents.models.User;

public class MandalikaPref {

    private static String NAMA_PREF = "MANDALIKAPREF";
    private static String KEY_NAMA = "NAMA";
    private static String KEY_EMAIL = "EMAIL";
    private static String KEY_FOTOURL = "FOTO";
    private static String KEY_STATUS = "STATUS";
    Context context;

    SharedPreferences mandalikaPref;
    SharedPreferences.Editor editorPref;

    public MandalikaPref(Context context) {
        this.context = context;

        mandalikaPref = context.getSharedPreferences(NAMA_PREF, Context.MODE_PRIVATE);
    }

    public void setUser(User user) {
        editorPref = mandalikaPref.edit();

        editorPref
                .putString(KEY_NAMA, user.getNamaUser())
                .putString(KEY_EMAIL, user.getEmailUser())
                .putString(KEY_FOTOURL, user.getImgUrlUser())
                .putString(KEY_STATUS, user.getStatus())
                .commit();
    }

    public void deleteUser() {
        editorPref = mandalikaPref.edit();
        editorPref
                .remove("kodeUser")
                .remove("nama")
                .remove("email")
                .remove("imgUrl")
                .remove("status");

        editorPref.commit();
    }

    public User getUser() {
        String nama, email, fotoUrl, status;

        nama = mandalikaPref.getString(KEY_NAMA, null);
        email = mandalikaPref.getString(KEY_EMAIL, null);
        fotoUrl = mandalikaPref.getString(KEY_FOTOURL, null);
        status = mandalikaPref.getString(KEY_STATUS, null);

        User user = new User(nama, email, fotoUrl, status);

        return user;
    }

    public String getUserStatus() {
        return mandalikaPref.getString(KEY_STATUS, null);
    }

    public String getNama() {
        return mandalikaPref.getString(KEY_NAMA, null);
    }
    public String getEmail() {
        return mandalikaPref.getString(KEY_EMAIL, null);
    }
    public String getFotourl() {
        return mandalikaPref.getString(KEY_FOTOURL, null);
    }
}
