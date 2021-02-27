package com.example.testappv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationLK extends AppCompatActivity {


    private TextView tvLongitude, tvLatitude, tvAltitude, tvLocationCurrentLK;
    private String query;
    private Button btnSearchLK, btnShowMapsLK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getSupportActionBar().setTitle("Last known location");
        tvAltitude = findViewById(R.id.tvAltitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLocationCurrentLK = findViewById(R.id.tvLocationCurrentLK);
        btnSearchLK = findViewById(R.id.btnSearchLK);
        btnShowMapsLK = findViewById(R.id.btnShowMapsLK);

        double longitude, altitude, latitude;
        longitude = getIntent().getDoubleExtra("longitude", 0);
        altitude = getIntent().getDoubleExtra("altitude", 0);
        latitude = getIntent().getDoubleExtra("latitude", 0);
        btnShowMapsLK.setVisibility(View.VISIBLE);
        btnShowMapsLK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+Uri.encode(Double.toString(latitude))+","+Uri.encode(Double.toString(longitude))));
                startActivity(intent);
            }
        });
        if(ActivityCompat.checkSelfPermission(LocationLK.this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
            Geocoder gcd = new Geocoder(LocationLK.this, Locale.getDefault());
            ConnectivityManager cm =
                    (ConnectivityManager) LocationLK.this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                btnSearchLK.setVisibility(View.VISIBLE);
                try {
                    List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        query = "";
                        String locality = (addresses.get(0).getLocality() == null) ? "Unknown" : addresses.get(0).getLocality();
                        String adminArea = (addresses.get(0).getAdminArea() == null) ? "Unknown" : addresses.get(0).getAdminArea();
                        String countryName = (addresses.get(0).getCountryName() == null) ? "Unknown" : addresses.get(0).getCountryName();
                        String street = (addresses.get(0).getThoroughfare() == null || addresses.get(0).getThoroughfare().equals("Unnamed Road")) ? "" : addresses.get(0).getThoroughfare();
                        String number = (addresses.get(0).getSubThoroughfare() == null || street.equals("")) ? "" : addresses.get(0).getSubThoroughfare();
                        if(!street.equals("") && !number.equals("")) street = street + " " + number + ", ";
                        else if(!street.equals("") && number.equals("")) street = street + ", ";
                        else if(street.equals("") && addresses.get(0).getPremises() != null) street = addresses.get(0).getPremises() + ", ";
                        tvLocationCurrentLK.setText(getResources().getString(R.string.currLocation, street, locality, adminArea, countryName));
                        btnSearchLK.setEnabled(true);
                        if(!locality.equals("Unknown")) query += locality;
                        if(!locality.equals("Unknown") && adminArea != "Unknown") query += ", ";
                        if(!adminArea.equals("Unknown")) query += adminArea;
                        if(!adminArea.equals("Unknown") && countryName != "Unknown") query += ", ";
                        if(!countryName.equals("Unknown")) query += countryName;
                        btnSearchLK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=" + Uri.encode(query)));
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), query ,Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        tvLocationCurrentLK.setText("Current location: Unknown");
                        btnSearchLK.setEnabled(false);
                    }
                    tvLocationCurrentLK.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        tvLongitude.setText(getResources().getString(R.string.longitude, longitude));
        tvLatitude.setText(getResources().getString(R.string.latitude, latitude));
        tvAltitude.setText(getResources().getString(R.string.altitude, altitude));
        tvLongitude.setVisibility(View.VISIBLE);
        tvLatitude.setVisibility(View.VISIBLE);
        tvAltitude.setVisibility(View.VISIBLE);
    }


}