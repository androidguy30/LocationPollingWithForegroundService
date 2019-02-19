package com.example.locationpollingservice.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MyService extends Service {

    public static final String KEY_START_FOREGROUND_SERVICE = "START FOREGROUND SERVICE";
    public static final String KEY_STOP_FOREGROUND_SERVICE = "STOP FOREGROUND SERVICE";
    public static final String CHANNEL_ID = "SERVICE CHANNEL";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getAction()) {
                case KEY_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    break;

                case KEY_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    break;
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * Start Foreground Service with the notification Implementation.
     */
    public void startForegroundService() {
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //Add channel ID.
        String channelName = "My Background Service";
        NotificationChannel chan = null;
        NotificationCompat.Builder notificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        }else{
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        NotificationCompat.BigTextStyle textStyle = new NotificationCompat.BigTextStyle();
        textStyle.setSummaryText("Testing background location polling");
        textStyle.setBigContentTitle("Location Polling");

        notificationBuilder.setStyle(textStyle);

        Notification notification = notificationBuilder.build();
        startForeground(1, notification);
        startLocationPolling();
    }


    /**
     * Stop Foreground service.
     */
    public void stopForegroundService() {
        stopLocationPolling();
        stopForeground(true);
        stopSelf();
    }


    /**
     * Setting up mFusedLocationProvider, LocationRequest & LocationCallback
     */
    @SuppressLint("MissingPermission")
    public void startLocationPolling() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 60);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location :locationResult.getLocations()){
                    Toast.makeText(MyService.this,location.getLatitude()+" , "+location.getLongitude(),Toast.LENGTH_SHORT).show();
                }
            }
        };
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    /**
     * Stop Location Polling.
     */
    public void stopLocationPolling() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

}
