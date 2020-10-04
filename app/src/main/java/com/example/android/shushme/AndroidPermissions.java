package com.example.android.shushme;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.CheckBox;

import androidx.core.app.ActivityCompat;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class AndroidPermissions {
    public static final int PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION = 0;
    public static final int PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION_ALREADYGRANTED = 0;
    public static final int PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION_GRANTED_NOW = 1;

    public static final int PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION_DENIED = 2;
    String LOG_TAG= AndroidPermissions.class.getSimpleName();
    private static View mLayout;
    static Context context;
    static Activity activity;
    public static int getAndroidPermissionLocation(Context context1, CheckBox location){
        activity = (Activity) context1;
        context=context1;

            // Check if the Camera permission has been granted
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                // Permission is already available, return 0 so that I can get OAuth Autorizations from repository
                //getOAuthAuthorizationPermission(context);
                return PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION_ALREADYGRANTED;
            } else {
                // Permission is missing and must be requested.
                requestActivityRecognitionPermission(context);
            }
            return PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION_GRANTED_NOW;
    }

    /**
     * Requests the {@link Manifest.permission#ACCESS_FINE_LOCATION} permission.
     */
    private static void requestActivityRecognitionPermission(final Context context) {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted and the user would benefit from additional context for the use of the permission.
            requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION);
        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION);
        }
    }
}
