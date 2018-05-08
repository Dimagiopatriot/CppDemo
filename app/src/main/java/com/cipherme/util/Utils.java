package com.cipherme.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;

import com.cipherme.api.headers.HeadersProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {

    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private static String uniqueID = null;

    public static final String[] permissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

//    @SuppressLint("MissingPermission")
    public synchronized static String getDeviceUUID(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }

    public static String toStr(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream bous = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bous);
        return Base64.encodeToString(bous.toByteArray(), Base64.DEFAULT);
    }

    public static Map<String, String> baseHeaders() {

        final Map<String, String> headers = new HashMap<>();

        headers.put(HeadersProvider.Key.VERSION.getValue(), "2.0");
        headers.put(HeadersProvider.Key.ACCEPT_LANGUAGE.getValue(), "ru");
//        headers.put(HeadersProvider.Key.CONTENT_TYPE.getValue(), "application/json");

        return headers;
    }

    public static boolean hasPermissions(Context context, String ... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
