package com.example.android.shushme;


import android.Manifest;

import com.example.android.shushme.mvvmArch.MainViewModel;
import com.example.android.shushme.room.ListItemsEntity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.libraries.places.api.model.Place;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.android.shushme.AndroidPermissions.PERMISSION_REQUEST_ACTIVITY_ACCESS_FINE_LOCATION_GRANTED_NOW;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, PlaceListAdapter.ListItemClickListener {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String REQUEST_CODE = "LOCATION_REQUEST_CODE";
    public static final String API_KEY = "AIzaSyBULrgQ9hEpw9syljf-VToZ-sm-pHUxYxo";

    static final int  SERVICE_MISSING=1;
    static final int SERVICE_VERSION_UPDATE_REQUIRED=2;
    static final int  SERVICE_DISABLED=3;
    static final int PLACE_PICKER_REQUEST_CODE =4;
    static final int AUTOCOMPLETE_REQUEST_CODE =5;
    private static final int ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS =6 ;
    Geofencing mGeofencing;

    // Member variables
    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    CheckBox location;
    Switch geofences;
    CheckBox ringtone;
    Button addNewLocation;
    PlacesClient placesClient;
    MainViewModel viewModel;
    List<ListItemsEntity> taskEntriesAll=new ArrayList<>();
    GeofencingClient geofencingClient;
    private boolean mIsEnabled;

    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState The Bundle that contains the data supplied in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addNewLocation=findViewById(R.id.addNewLocation);
        location=findViewById(R.id.enable_location);
        ringtone=findViewById(R.id.ringer_permissions_checkbox);
        geofences=findViewById(R.id.enable_switch);

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

        addNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddNewLocation(view);
            }
        });

        // Initialize the Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(this, API_KEY);
        }

        // Create a new PlacesClient instance
        placesClient = Places.createClient(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        mGeofencing=new Geofencing(this,geofencingClient);


        Switch onOffSwitch = (Switch) findViewById(R.id.enable_switch);
        mIsEnabled = getPreferences(MODE_PRIVATE).getBoolean(getString(R.string.setting_enabled), false);
        onOffSwitch.setChecked(mIsEnabled);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.setting_enabled), isChecked);
                mIsEnabled = isChecked;
                editor.commit();
                if (isChecked) mGeofencing.registerAllGeofences();
                else mGeofencing.unRegisterAllGeofences();
            }

        });

    }

    public void onAddNewLocation(View view){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            AndroidPermissions.getAndroidPermissionLocation(view.getContext(),location);
            Toast.makeText(this,R.string.location_permission_needed,Toast.LENGTH_LONG).show();
        }
        if (!Places.isInitialized()) {
            Places.initialize(view.getContext(), API_KEY);
        }

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        // Start the autocomplete intent.
        try {
//            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//            Intent intent=builder.build(MainActivity.this);
//             startActivityForResult(intent, PLACE_PICKER_REQUEST_CODE);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        }

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



        geofences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    AndroidPermissions.getAndroidPermissionLocation(view.getContext(),location);
                }
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

        CheckBox ringerPermissions = (CheckBox) findViewById(R.id.ringer_permissions_checkbox);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Check if the API supports such permission change and check if permission is granted

        if (android.os.Build.VERSION.SDK_INT >= 23 && !nm.isNotificationPolicyAccessGranted()) {
            ringerPermissions.setChecked(false);

        } else {
            ringerPermissions.setChecked(true);
            ringerPermissions.setEnabled(false);
        }
    }

    public void onRingerPermissionsClicked(View view) {
        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "3. Place: " + place.getName() + ", " + place.getId());
                ListItemsEntity lie=new ListItemsEntity(place.getId());
                refreshPlacesData(place.getId(),lie);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
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
                    // Explain to the user that the feature is unavailable because the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to system settings in an effort to convince the user to change
                    // their decision.Toast.makeText(this,R.string.location_permission_denied,Toast.LENGTH_LONG).show();
                }
                return;
            default:   // Toast.makeText(this,R.string.location_permission_denied,Toast.LENGTH_LONG).show();
                        return;
        }
    }




    @Override
    public void onListItemClick(int position) {

    }

    public void refreshPlacesData(String id,ListItemsEntity listItemsEntity) {
        final String placeId =id;
                //"ChIJvavNLritEmsRbFHX3aQcmJ4";
// Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME,Place.Field.LAT_LNG);

// Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i(TAG, "------------------ Place found: " + place.getName());
            listItemsEntity.setPlaceAddress(place.getAddress());
            listItemsEntity.setPlaceName(place.getName());
            String result = String.valueOf(place.getLatLng().latitude).concat(" ").concat(String.valueOf(place.getLatLng().longitude));
            listItemsEntity.setPlaceLAT_LNG(result);
            viewModel.insertTasks(listItemsEntity);

            viewModel.getTasks().observe(this, new Observer<List<ListItemsEntity>>() {
                @Override
                public void onChanged(List<ListItemsEntity> taskEntries) {
                    mGeofencing.updateGeofencesList(taskEntries );
                }
            });


            if (mIsEnabled)
                mGeofencing.registerAllGeofences();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });

  //  }
       // viewModel.fetchPlacesbyId(placeId,viewModel);
        Log.i(TAG, "2. Place ID: " + listItemsEntity.getPlaceID());
        Log.i(TAG, "2. Place found: " + listItemsEntity.getPlaceName());
        Log.i(TAG, "2. Address found: " + listItemsEntity.getPlaceAddress());

        //return listItemsEntity;
    }


}
