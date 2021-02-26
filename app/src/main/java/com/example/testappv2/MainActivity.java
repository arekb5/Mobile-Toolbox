package com.example.testappv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    Button btnLight, btnLocation, btnGPS, btnCompass, btnLeveler, btnSpeedo;
    private FusedLocationProviderClient fusedLocationClient;
    Intent intentLight, intentLocationLK, intentLocationGPS, intentCompass, intentLeveler, intentSpeedo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().setTitle("App name");
        btnLight = findViewById(R.id.btnLight);
        btnLocation = findViewById(R.id.btnLocation);
        btnGPS = findViewById(R.id.btnGPS);
        btnCompass = findViewById(R.id.btnCompass);
        btnLeveler = findViewById(R.id.btnLeveler);
        btnSpeedo = findViewById(R.id.btnSpeedo);
        intentLight = new Intent(MainActivity.this, Light.class);
        intentLocationLK = new Intent(MainActivity.this, LocationLK.class);
        intentLocationGPS = new Intent(MainActivity.this, LocationGPS.class);
        intentCompass = new Intent(MainActivity.this, Compass.class);
        intentLeveler = new Intent(MainActivity.this, Leveler.class);
        intentSpeedo = new Intent(MainActivity.this, Speedometer.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            intentLocationLK.putExtra("longitude", location.getLongitude());
                            intentLocationLK.putExtra("latitude", location.getLatitude());
                            intentLocationLK.putExtra("altitude", location.getAltitude());
                        }
                    }
                });

        setButtonListeners();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            setButtonListeners();
            btnLocation.setEnabled(false);

            btnGPS.setEnabled(false);

            btnSpeedo.setEnabled(false);

        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            intentLocationLK.putExtra("longitude", location.getLongitude());
                            intentLocationLK.putExtra("latitude", location.getLatitude());
                            intentLocationLK.putExtra("altitude", location.getAltitude());

                            setButtonListeners();
                        }
                    }
                });
    }

    private void setButtonListeners(){
        btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentLight);
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentLocationLK);
            }
        });

        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentLocationGPS);
            }
        });

        btnCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentCompass);
            }
        });

        btnLeveler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentLeveler);
            }
        });

        btnSpeedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentSpeedo);
            }
        });
    }
}