package com.example.android.shushme;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static com.example.android.shushme.AndroidPermissions.PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION;
import static com.example.android.shushme.AndroidPermissions.PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION_GRANTED_NOW;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String REQUEST_CODE = "LOCATION_REQUEST_CODE";

    // Member variables
    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    CheckBox location;
    CheckBox ringtone;
    Button addNewLocation;
    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState The Bundle that contains the data supplied in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        addNewLocation=findViewById(R.id.addNewLocation);
        location=findViewById(R.id.enable_location);
        ringtone=findViewById(R.id.enable_rington);

        addNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddNewLocation(view);
            }
        });
    }

    public void onAddNewLocation(View view){
        if(location.isChecked() && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
            AndroidPermissions.getAndroidPermissionLocation(view.getContext(),location);
        }else{
            location.setChecked(true);
        }    }

    @Override
    protected void onResume() {
        super.onResume();
        //isGooglePlayServicesAvailable();


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            location.setChecked(true);
            location.setEnabled(false);
        }else{
            location.setChecked(false);
            location.setEnabled(true);
        }
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(location.isChecked()){
//                    location.setChecked(false);
//                }
//                else{
                if(location.isChecked() && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
                    AndroidPermissions.getAndroidPermissionLocation(view.getContext(),location);
                }else{
                    location.setChecked(true);
                }
            }
        });

        ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  AndroidPermissions.getAndroidPermissionringtone(this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION_GRANTED_NOW:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.

                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(this,R.string.location_permission_denied,Toast.LENGTH_LONG).show();
                }
                return;
            default:    Toast.makeText(this,R.string.location_permission_denied,Toast.LENGTH_LONG).show();
                        return;
        }
    }

}
