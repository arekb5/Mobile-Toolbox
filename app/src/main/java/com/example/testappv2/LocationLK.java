package com.example.testappv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationLK extends AppCompatActivity {


    private TextView tvLongitude;
    private TextView tvLatitude;
    private TextView tvAltitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        tvAltitude = findViewById(R.id.tvAltitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvLatitude = findViewById(R.id.tvLatitude);

        double longitude, altitude, latitude;
        longitude = getIntent().getDoubleExtra("longitude", 0);
        altitude = getIntent().getDoubleExtra("altitude", 0);
        latitude = getIntent().getDoubleExtra("latitude", 0);

        tvLongitude.setText(getResources().getString(R.string.longitude, longitude));
        tvLatitude.setText(getResources().getString(R.string.latitude, latitude));
        tvAltitude.setText(getResources().getString(R.string.altitude, altitude));
        tvLongitude.setVisibility(View.VISIBLE);
        tvLatitude.setVisibility(View.VISIBLE);
        tvAltitude.setVisibility(View.VISIBLE);
    }


}