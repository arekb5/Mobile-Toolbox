package com.example.testappv2;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
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

public class LocationGPS extends AppCompatActivity {

    private TextView tvLongitudeCurrent;
    private TextView tvLatitudeCurrent;
    private TextView tvAltitudeCurrent;
    private TextView tvPrecisionCurrent;
    private TextView tvSpeedCurrent;
    private TextView tvLocationCurrent;
    private Button btnShowMaps;
    private Button btnSearch;
    private LocationCallback locationCallback;
    private String query = "";
    FusedLocationProviderClient fusedLocationClient;

    private static final int REQUEST_CHECK_SETTINGS = 111;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_g_p_s);

        tvAltitudeCurrent = findViewById(R.id.tvAltitudeCurrent);
        tvLongitudeCurrent = findViewById(R.id.tvLongitudeCurrent);
        tvLatitudeCurrent = findViewById(R.id.tvLatitudeCurrent);
        tvPrecisionCurrent = findViewById(R.id.tvPrecisionCurrent);
        tvSpeedCurrent = findViewById(R.id.tvSpeedCurrent);
        tvLocationCurrent = findViewById(R.id.tvLocationCurrent);
        btnShowMaps = findViewById(R.id.btnShowMaps);
        btnSearch = findViewById(R.id.btnSearch);
        locationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                tvLongitudeCurrent.setVisibility(View.VISIBLE);
                tvLatitudeCurrent.setVisibility(View.VISIBLE);
                tvAltitudeCurrent.setVisibility(View.VISIBLE);
                tvPrecisionCurrent.setVisibility(View.VISIBLE);
                tvSpeedCurrent.setVisibility(View.VISIBLE);
                btnShowMaps.setVisibility(View.VISIBLE);
                for (Location location : locationResult.getLocations()) {
                    tvLongitudeCurrent.setText(getResources().getString(R.string.currLongitude, location.getLongitude()));
                    tvLatitudeCurrent.setText(getResources().getString(R.string.currLatitude, location.getLatitude()));
                    tvAltitudeCurrent.setText(getResources().getString(R.string.currAltitude, location.getAltitude()));
                    tvPrecisionCurrent.setText(getResources().getString(R.string.currPrecision, location.getAccuracy()));
                    tvSpeedCurrent.setText(getResources().getString(R.string.currSpeed, location.getSpeed()));

                    if(ActivityCompat.checkSelfPermission(LocationGPS.this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
                        Geocoder gcd = new Geocoder(LocationGPS.this, Locale.getDefault());
                        ConnectivityManager cm =
                                (ConnectivityManager) LocationGPS.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                        if (isConnected) {
                            btnSearch.setVisibility(View.VISIBLE);
                            try {
                                List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addresses.size() > 0) {
                                    query = "";
                                    String locality = (addresses.get(0).getLocality() == null) ? "Unknown" : addresses.get(0).getLocality();
                                    String adminArea = (addresses.get(0).getAdminArea() == null) ? "Unknown" : addresses.get(0).getAdminArea();
                                    String countryName = (addresses.get(0).getCountryName() == null) ? "Unknown" : addresses.get(0).getCountryName();
                                    tvLocationCurrent.setText(getResources().getString(R.string.currLocation, locality, adminArea, countryName));
                                    btnSearch.setEnabled(true);
                                    if(locality != "Unknown") query += locality;
                                    if(locality != "Unknown" && adminArea != "Unknown") query += ", ";
                                    if(adminArea != "Unknown") query += adminArea;
                                    if(adminArea != "Unknown" && countryName != "Unknown") query += ", ";
                                    if(countryName != "Unknown") query += countryName;
                                    btnSearch.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=" + Uri.encode(query)));
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    tvLocationCurrent.setText("Current location: Unknown");
                                    btnSearch.setEnabled(false);
                                }
                                tvLocationCurrent.setVisibility(View.VISIBLE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    btnShowMaps.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+Uri.encode(Double.toString(location.getLatitude()))+","+Uri.encode(Double.toString(location.getLongitude()))));
                            startActivity(intent);
                        }
                    });
                }
            }
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
                                    resolvable.startResolutionForResult(LocationGPS.this,
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
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(100);
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
                        tvLongitudeCurrent.setText("To see the data GPS has to be turned on. Turn on GPS.");
                        tvLongitudeCurrent.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),"User has clicked on NO, THANKS - So GPS is still off.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }


}

