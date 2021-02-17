package com.example.testappv2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Speedometer extends AppCompatActivity {
    TextView tvSpeed, tvInfo, tvDistance;
    float distance = 0f;
    boolean accuracyFlag = false, firstIteration = true;
    private LocationCallback locationCallback;
    private static final int REQUEST_CHECK_SETTINGS = 111;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnown;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedometer);
        tvSpeed = findViewById(R.id.tvSpeed);
        tvInfo = findViewById(R.id.tvInfo);
        tvDistance = findViewById(R.id.tvDistance);
        if(savedInstanceState != null) distance = savedInstanceState.getFloat("distance");
        locationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();
                //for (Location location : locationResult.getLocations()) {
                    int a = 0;
                    if(firstIteration){
                        lastKnown = location;
                        firstIteration = false;
                        a = 1;
                    }
                    if(location.getAccuracy()>12f && accuracyFlag==false){
                        tvInfo.setText("Waiting for satellites...");
                        tvSpeed.setText("0.0\nkm/h");
                        a = 2;
                        tvInfo.setText(Integer.toString(a));
                        return;
                    }
                    if(location.getAccuracy()>20f && accuracyFlag==true){
                        accuracyFlag = false;
                        a = 3;
                        tvInfo.setText(Integer.toString(a));
                        return;
                    }
                    if(location.getAccuracy()<=12f && accuracyFlag==false){
                        accuracyFlag = true;
                        tvInfo.setText("");
                        a = 4;
                    }
                    distance += location.distanceTo(lastKnown);
                    lastKnown = location;
                    tvSpeed.setText(Float.toString((float) Math.round((location.getSpeed()*3.6f)*10)/10) + "\nkm/h");
                    tvDistance.setText(Float.toString((float) Math.round(distance*10)/10) + "m");
                    tvInfo.setText(Integer.toString(a));
                }
            //}
        };

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true); //this displays dialog box like Google Maps with two buttons - OK and NO,THANKS

            Task<LocationSettingsResponse> task =
                    LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

            task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                    } catch (ApiException exception) {
                        switch (exception.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the
                                // user a dialog.
                                try {
                                    // Cast to a resolvable exception.
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    resolvable.startResolutionForResult(Speedometer.this,
                                            REQUEST_CHECK_SETTINGS);
                                    //getLastKnownLocation();
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                } catch (ClassCastException e) {
                                    // Ignore, should be an impossible error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't`` show the dialog.
                                break;
                        }
                    }
                }
            });
        } else {
            startLocation();
        }
    }
    @SuppressLint("MissingPermission")
    private void startLocation(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocation();
                        Toast.makeText(getApplicationContext(),"User has clicked on OK - So GPS is on", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        /*tvLongitudeCurrent.setText("To see the data GPS has to be turned on. Turn on GPS.");
                        tvLongitudeCurrent.setVisibility(View.VISIBLE);*/
                        Toast.makeText(getApplicationContext(),"User has clicked on NO, THANKS - So GPS is still off.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("distance", distance);
    }
}