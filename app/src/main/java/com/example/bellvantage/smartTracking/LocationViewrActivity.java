package com.example.bellvantage.smartTracking;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class LocationViewrActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Google Map
    private GoogleMap mMap;
    private List<Marker> originMarkers = new ArrayList<>();
    SupportMapFragment mapFragment;


    private double Longitude;
    private double Latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_viewr);

        if (mapFragment == null){
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        if (getIntent().getSerializableExtra("longitude")!=null && getIntent().getSerializableExtra("latitude")!=null){
            Longitude = (double) getIntent().getSerializableExtra("longitude");
            Latitude = (double) getIntent().getSerializableExtra("latitude");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        System.out.println("latitude: "+ Latitude+ " Longitude: "+ Longitude);


        mMap = googleMap;
        LatLng latLang = new LatLng(Latitude,Longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang, 16));

        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title("Attendance Location")
                .position(latLang)));


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
}
