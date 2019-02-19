package com.example.locationpollingservice.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.locationpollingservice.R;
import com.example.locationpollingservice.service.MyService;

public class MainActivity extends AppCompatActivity {

    TextView locationData;
    Button locationUpdateControl;
    boolean isPolling = false;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initApplication();
    }

    public void initApplication() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }


        }

        locationUpdateControl = findViewById(R.id.control);
        locationUpdateControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPolling) {
                    isPolling = false;
                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    intent.setAction(MyService.KEY_STOP_FOREGROUND_SERVICE);
                    startService(intent);
                    locationUpdateControl.setText("START");
                } else {
                    locationUpdateControl.setText("STOP");
                    isPolling = true;
                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    intent.setAction(MyService.KEY_START_FOREGROUND_SERVICE);
                    startService(intent);
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    locationUpdateControl.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
