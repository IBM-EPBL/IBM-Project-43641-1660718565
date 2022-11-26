package com.example.czalertapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.czalertapp.Locationservice;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class HomePage extends AppCompatActivity {
    static HomePage instance;
    LocationRequest locationRequest;
    TextView loc;
    FusedLocationProviderClient fusedLocationProviderClient;
    SharedPreferences sharedPreferences;

    public static HomePage getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        instance = this;
        loc = findViewById(R.id.gpstext);
        Toast.makeText(HomePage.this, "Fetching Co-ordinates...", Toast.LENGTH_SHORT).show();
        sharedPreferences = getApplicationContext().getSharedPreferences("user_data", 0);
        Log.d("shared_pref",sharedPreferences.getString("name","0"));
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        updateLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(HomePage.this, "No location", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

    }
    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomePage.this);
        if (ActivityCompat.checkSelfPermission(HomePage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(HomePage.this, Locationservice.class);
        intent.setAction(Locationservice.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(HomePage.this,0,intent,PendingIntent.FLAG_MUTABLE);
    }
    private void buildLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(10f);
    }
    public void updateTextView(String location){
        HomePage.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loc.setText(location);
            }
        });
    }
}