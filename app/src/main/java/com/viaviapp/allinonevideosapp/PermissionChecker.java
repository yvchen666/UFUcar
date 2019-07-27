package com.viaviapp.allinonevideosapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

public class PermissionChecker {
    public static final String TAG = PermissionChecker.class.getSimpleName();
    public static final int READ_PHONE_STATE = 1;

    private static PermissionChecker checker;

    private PermissionChecker(){
    }

    public static PermissionChecker getInstance(){
        if (checker == null){
            checker = new PermissionChecker();
        }
        return checker;
    }

    public static void requestReadPhoneState(Activity activity) {
        int hasWriteContactsPermission = activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE);
            return;
        }
    }
}