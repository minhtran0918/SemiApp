package org.semi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.semi.contract.Contract;
import org.semi.custom.StoreInfoWindowAdapter;
import org.semi.firebase.IResult;
import org.semi.firebase.StorageConnector;
import org.semi.object.Store;
import org.semi.util.LocationUtils;

public class StoreMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private Store store;
    private ThreadLocal<Bitmap> bitmapHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_map);
        setupActionBar();
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
        bitmapHolder = new ThreadLocal<>();
    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.storeMapActivityLabel));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //get store from intent
        Intent intent = getIntent();
        store = (Store) intent.getSerializableExtra(Contract.BUNDLE_STORE_KEY);
        //init map
        this.map = map;
        map.setInfoWindowAdapter(new StoreInfoWindowAdapter(bitmapHolder));
        LatLng storePosition = new LatLng(store.getGeo().getLatitude(), store.getGeo().getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(storePosition, 15));
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                .title(store.getTitle())
                .position(storePosition);
        Marker marker = map.addMarker(markerOptions);
        marker.setTag(store);
        marker.showInfoWindow();
        initInfoWindow(marker);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                initInfoWindow(marker);
                return false;
            }
        });
        checkAndRequestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int request, String[] permission,
                                           int[] results) {
        super.onRequestPermissionsResult(request, permission, results);
        if(request == LocationUtils.REQUEST_LOCATION_PERMISSION_CODE) {
            if(results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted();
            }
        }
    }

    private void checkAndRequestPermission() {
        if(LocationUtils.checkAndRequestPermission(this)) {
            onPermissionGranted();
        }
    }

    private void onPermissionGranted() {
        LocationUtils.checkAndRequestLocationSettings(this, new IResult<LocationSettingsResponse>() {
            @Override
            public void onResult(LocationSettingsResponse result) {
                onPermissionAndSettingsOK();
            }

            @Override
            public void onFailure(@NonNull Exception exp) { }
        });
    }

    @Override
    public void onActivityResult(int request, int result, Intent intent) {
        super.onActivityResult(request, result, intent);
        if(result == Activity.RESULT_OK) {
            if(request == LocationUtils.RESOLUTION_CODE) {
                onPermissionAndSettingsOK();
            }
        }
    }

    private void onPermissionAndSettingsOK() {
        map.setMyLocationEnabled(true);
    }

    private void initInfoWindow(final Marker marker) {
        final Store store = (Store) marker.getTag();
        final int markerImageSizeInPixels = getResources().getDimensionPixelSize(R.dimen.markerImageSize);
        StorageConnector.getInstance().getBitmap(store.getImageURL(), markerImageSizeInPixels, new IResult<Bitmap>() {
            @Override
            public void onResult(Bitmap result) {
                if(result != null) {
                    bitmapHolder.set(result);
                    marker.showInfoWindow();
                }
            }

            @Override
            public void onFailure(@NonNull Exception exp) { }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void actionDirectToStore(View view) {
        Uri url = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + store.getGeo());
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
