package org.semi.views;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.semi.R;
import org.semi.adapter.LocationToAddress;
import org.semi.utils.Contract;

import java.util.List;

public class AddStoreChooseLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLocation = new LatLng(10.850288, 106.771602);
    private boolean mCheckHaveLocation;

    private Marker mCenterMarker;
    private Bitmap mBitmapMarker;         //My icon
    //Zoom level does not display the building
    private float DEFAULT_ZOOM = 16.988f;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_REQUEST_PERMISSION_CODE = 1789;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store_choose_location);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_add_store_choose_location);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        mCheckHaveLocation = getIntent().getBooleanExtra(Contract.CHECK_HAVE_LOCATION, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(Contract.TAG, "onMapReady");
        initMap();
        //Request premission
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            mLocationPermissionGranted = true;
                        }
                        updateLocationUI();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        if (mCheckHaveLocation) {
            double latPos = getIntent().getDoubleExtra(Contract.LAT_STORE_KEY, 0.0);
            double lngPos = getIntent().getDoubleExtra(Contract.LNG_STORE_KEY, 0.0);
            if (latPos != 0.0 && lngPos != 0.0) {
                LatLng currentPos = new LatLng(latPos, lngPos);
                if (mCenterMarker == null) {
                    mCenterMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(currentPos)
                                    .icon(BitmapDescriptorFactory.fromBitmap(mBitmapMarker)));
                    getAddressToLocation(currentPos);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, DEFAULT_ZOOM));
            }
        } else {
            getDeviceLocation();
        }
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (mCenterMarker != null) {
                    getAddressToLocation(new LatLng(mCenterMarker.getPosition().latitude, mCenterMarker.getPosition().longitude));
                }
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                CameraPosition currentPos = mMap.getCameraPosition();
                double latCurrentPos = currentPos.target.latitude % 0.00000001;
                double lngCurrentPos = currentPos.target.longitude % 0.00000001;
                if (mCenterMarker == null) {
                    mCenterMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(currentPos.target)
                                    .icon(BitmapDescriptorFactory.fromBitmap(mBitmapMarker)));
                    return;
                }
                double latMarkerPos = mCenterMarker.getPosition().latitude % 0.00000001;
                double lngMarkerPos = mCenterMarker.getPosition().longitude % 0.00000001;
                if (latCurrentPos != latMarkerPos && lngCurrentPos != lngMarkerPos) {
                    if (mCenterMarker != null) {
                        mCenterMarker.remove();
                    }
                    mCenterMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(currentPos.target)
                                    .icon(BitmapDescriptorFactory.fromBitmap(mBitmapMarker)));
                }
            }
        });
    }

    private void initMap() {

        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMinZoomPreference(5f);
        mMap.setMaxZoomPreference(20.5f);
        //2 nut zoom in & zoom out
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //Thay doi goc nhin camera
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        //Xoay map
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        //2 nut goi GoogleMap App
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //Config my icon
        int height = 75;
        int width = 75;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.pin_128);
        Bitmap b = bitmapdraw.getBitmap();
        mBitmapMarker = Bitmap.createScaledBitmap(b, width, height, false);
        //Add marker
        /*mMap.addMarker(new MarkerOptions()
                .position(mDefaultLocation)
                .title("HCMUTE")
                .snippet("Đây là bảng nhỏ")
                //.icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 17f));*/
        /*mCenterMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(mDefaultLocation)
                        .icon(BitmapDescriptorFactory.fromBitmap(mBitmapMarker)));*/
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        LatLng currentPos;
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            currentPos = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        } else {
                            Log.d(Contract.TAG, "Current location is null. Using defaults.");
                            Log.e(Contract.TAG, "Exception: %s", task.getException());
                            currentPos = mDefaultLocation;
                            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                        if (mCenterMarker == null) {
                            mCenterMarker = mMap.addMarker(
                                    new MarkerOptions()
                                            .position(currentPos)
                                            .icon(BitmapDescriptorFactory.fromBitmap(mBitmapMarker)));
                            getAddressToLocation(currentPos);
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, DEFAULT_ZOOM));

                    }
                });
            } else {
                mCenterMarker = mMap.addMarker(
                        new MarkerOptions()
                                .position(mDefaultLocation)
                                .icon(BitmapDescriptorFactory.fromBitmap(mBitmapMarker)));
                getAddressToLocation(mDefaultLocation);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void onClickSelectPlace(View view) {
        Intent resultIntent = new Intent();
        double lat = mCenterMarker.getPosition().latitude;
        lat = Math.round(lat * 1000000.0) / 1000000.0;
        double lng = mCenterMarker.getPosition().longitude;
        lng = Math.round(lng * 1000000.0) / 1000000.0;

        resultIntent.putExtra(Contract.LAT_STORE_KEY, lat);
        resultIntent.putExtra(Contract.LNG_STORE_KEY, lng);
        setResult(RESULT_OK, resultIntent);
        this.finish();
    }

    private void getAddressToLocation(LatLng location) {
        if (location != null) {
            double latitude = location.latitude;
            double longitude = location.longitude;
            LocationToAddress.getAddressFromLocation(latitude, longitude, this, new GeocoderHandler());
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mCenterMarker.setTitle("");
                    mCenterMarker.hideInfoWindow();
                case 1:
                    Bundle bundle = msg.getData();
                    String locationAddress = bundle.getString(Contract.LOCATION_TO_ADDRESS_KEY);
                    mCenterMarker.setTitle(locationAddress);
                    mCenterMarker.showInfoWindow();
                    break;
            }
        }
    }
}
