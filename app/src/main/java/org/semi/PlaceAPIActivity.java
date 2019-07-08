package org.semi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlaceAPIActivity extends AppCompatActivity {
    private PlacesClient mPlacesClient;
    private List<Place.Field> mPlaceFields = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS);
    AutocompleteSupportFragment places_fragment;
    Button btn_find_current_place;
    String place_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_api);
        btn_find_current_place = findViewById(R.id.test_find_current_place);
        btn_find_current_place.setOnClickListener(view -> {

        });
        initPlaces();
        setPlaceAutoComplete();
    }

    private void getCurrentPlace() {
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(mPlaceFields).build();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<FindCurrentPlaceResponse> taskReponse = mPlacesClient.findCurrentPlace(request);
        taskReponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();

                    Collections.sort(response.getPlaceLikelihoods(), new Comparator<PlaceLikelihood>() {
                        @Override
                        public int compare(PlaceLikelihood placeLikelihood, PlaceLikelihood t1) {
                            return new Double(placeLikelihood.getLikelihood()).compareTo(t1.getLikelihood());
                        }
                    });

                    Collections.reverse(response.getPlaceLikelihoods());

                    place_id = response.getPlaceLikelihoods().get(0).getPlace().getId();
                    Log.d("Semi", "Address: " + response.getPlaceLikelihoods().get(0).getPlace().getAddress());
                    StringBuilder sb = new StringBuilder();
                    for (PlaceLikelihood place : response.getPlaceLikelihoods()) {
                        sb.append(place.getPlace().getName())
                                .append(" - Likelihoods value: ")
                                .append("\n");
                    }
                    Log.d("Semi", "Place: " + sb.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();
    }

    private void initPlaces() {
        Places.initialize(getApplicationContext(), getString(R.string.place_api_key));
        mPlacesClient = Places.createClient(this);
    }

    private void setPlaceAutoComplete() {
        places_fragment = (AutocompleteSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.places_autocomplete_fragment);
        places_fragment.setPlaceFields(mPlaceFields);
        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Toast.makeText(PlaceAPIActivity.this, place.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("Semi", status.getStatusMessage());
                Toast.makeText(PlaceAPIActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
