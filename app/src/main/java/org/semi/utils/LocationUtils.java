package org.semi.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.semi.firebase.IResult;

public class LocationUtils {

    private LocationCallback callback;

    public LocationUtils() {}

    public static final int RESOLUTION_CODE = 0;
    public static final int REQUEST_LOCATION_PERMISSION_CODE = 1;

    public static void checkAndRequestLocationSettings(final Activity activity,@Nullable
                                                    final IResult<LocationSettingsResponse> result) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Contract.LOCATION_ACCURACY)
                .setInterval(Contract.LOCATION_INTERVAL)
                .setFastestInterval(Contract.LOCATION_FASTEST_INTERVAL);


        final LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();


        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(request);
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (result != null) {
                    //Đã bật
                    result.onResult(locationSettingsResponse);
                }
            }
        }).addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                final ResolvableApiException rexp = (ResolvableApiException) e;
                try {
                    rexp.startResolutionForResult(activity, RESOLUTION_CODE);
                } catch (IntentSender.SendIntentException sexp) {
                    if (result != null) {
                        result.onFailure(sexp);
                    }
                    Log.w("my_error", sexp);
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    public static Task<Location> getLastLocation() {
        final Task<Location> task = LocationServices.getFusedLocationProviderClient(MyApp.getContext())
                .getLastLocation();
        return task;
    }

    public static boolean checkAndRequestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            int locationResult = ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationResult != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION_CODE);
                return false;
            }
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates(final IResult<Location> result) {
        final FusedLocationProviderClient locationProvider =
                LocationServices.getFusedLocationProviderClient(MyApp.getContext());
        if(callback != null) {
            locationProvider.removeLocationUpdates(callback);
        }
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                result.onResult(locationResult.getLastLocation());
            }
        };
        final LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Contract.LOCATION_ACCURACY)
                .setInterval(Contract.LOCATION_INTERVAL)
                .setFastestInterval(Contract.LOCATION_FASTEST_INTERVAL);
        LocationServices.getFusedLocationProviderClient(MyApp.getContext())
                .requestLocationUpdates(locationRequest, callback, null);
    }

    public void removeLocationUpdates() {
        if(callback != null) {
            LocationServices.getFusedLocationProviderClient(MyApp.getContext())
                    .removeLocationUpdates(callback);
            callback = null;
        }
    }
}
