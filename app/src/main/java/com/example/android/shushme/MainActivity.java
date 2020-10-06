package com.example.android.shushme;


import android.Manifest;

import com.example.android.shushme.mvvmArch.MainViewModel;
import com.example.android.shushme.provider.PlaceContract;
import com.example.android.shushme.room.ListItemsEntity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.libraries.places.api.model.Place;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.android.shushme.AndroidPermissions.PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION_GRANTED_NOW;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, PlaceListAdapter.ListItemClickListener {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String REQUEST_CODE = "LOCATION_REQUEST_CODE";
    static final int  SERVICE_MISSING=1;
    static final int SERVICE_VERSION_UPDATE_REQUIRED=2;
    static final int  SERVICE_DISABLED=3;
    static final int PLACE_PICKER_REQUEST_CODE =4;
    // Member variables
    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    CheckBox location;
    CheckBox ringtone;
    Button addNewLocation;
    PlacesClient placesClient;
    MainViewModel viewModel;
    List<ListItemsEntity> taskEntriesAll=new ArrayList<>();
    private GoogleApiClient mClient;

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

        mAdapter = new PlaceListAdapter(this,taskEntriesAll, this);
        mRecyclerView.setAdapter(mAdapter);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this,new Observer<List<ListItemsEntity>>(){
            @Override
            public void onChanged(List<ListItemsEntity> taskEntries) {
                taskEntriesAll=taskEntries;
                mAdapter.setData(taskEntriesAll);
                mRecyclerView.setAdapter(mAdapter);
            }
        });


        addNewLocation=findViewById(R.id.addNewLocation);
        location=findViewById(R.id.enable_location);
        ringtone=findViewById(R.id.enable_rington);

        addNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddNewLocation(view);
            }
        });

        // Initialize the Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(this, String.valueOf(R.string.API_KEY));
        }

        // Create a new PlacesClient instance
        placesClient = Places.createClient(this);
    }

    public void onAddNewLocation(View view){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            AndroidPermissions.getAndroidPermissionLocation(view.getContext(),location);
            Toast.makeText(this,R.string.location_permission_needed,Toast.LENGTH_LONG).show();
        }
        if (!Places.isInitialized()) {
            Places.initialize(view.getContext(), String.valueOf(R.string.API_KEY));
        }

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        // Start the autocomplete intent.
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent intent=builder.build((Activity) view.getContext());
            startActivityForResult(intent, PLACE_PICKER_REQUEST_CODE);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();



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
                if(location.isChecked() && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
                    AndroidPermissions.getAndroidPermissionLocation(view.getContext(),location);
                }
            }
        });

        ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  AndroidPermissions.getAndroidPermissionringtone(this);
            }
        });

        GoogleApiAvailability resu = GoogleApiAvailability.getInstance();
        if(resu.equals(SERVICE_MISSING) || resu.equals(SERVICE_VERSION_UPDATE_REQUIRED) ||resu.equals(SERVICE_DISABLED))
        {
            //resu.getErrorDialog(this,resu.isGooglePlayServicesAvailable(this),startActivityForResult());
        }
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        client.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()){
                            Log.i(TAG, "API Client Connection Successful!");
                        }else if(task.isCanceled()){
                            Log.i(TAG, "API Client Connection Suspended!");
                        }else {
                            Exception exception = task.getException();
                            Log.i(TAG, "API Client Connection Failed! "+exception);
                        }
                    }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST_CODE) {
            
            if (resultCode == RESULT_OK) {
                com.google.android.gms.location.places.Place place = PlacePicker.getPlace(this, data);

                if(place==null){
                    Log.i(TAG, String.valueOf(R.string.noPlaceSelected));
                }else{
                    Log.i(TAG, "Place: " + place.getName() + ", ID: " + place.getId());

                    ListItemsEntity lie=new ListItemsEntity(place.getId());
                    lie= refreshPlacesData(place.getId(),lie);
                    viewModel.insertTasks(lie);
                }
            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                   // Toast.makeText(this,R.string.location_permission_denied,Toast.LENGTH_LONG).show();
                }
                return;
            default:   // Toast.makeText(this,R.string.location_permission_denied,Toast.LENGTH_LONG).show();
                        return;
        }
    }




    @Override
    public void onListItemClick(int position) {

    }

    public ListItemsEntity refreshPlacesData(String id,ListItemsEntity listItemsEntity) {
        final String placeId = id;

// Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME);

// Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i(TAG, "Place found: " + place.getName());
            listItemsEntity.setPlaceAddress(place.getAddress());
            listItemsEntity.setPlaceName(place.getName());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });

        Log.i(TAG, "2. Place ID: " + listItemsEntity.getPlaceID());
        Log.i(TAG, "2. Place found: " + listItemsEntity.getPlaceName());
        Log.i(TAG, "2. Address found: " + listItemsEntity.getPlaceAddress());

        return listItemsEntity;
    }


}
