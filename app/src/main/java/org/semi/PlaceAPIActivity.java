package org.semi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
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
    String place_id = "";
    TextView test_address, test_edt_likehood;
    ImageView test_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_api);
        test_address = findViewById(R.id.test_address);
        test_edt_likehood = findViewById(R.id.test_likelihood);
        test_photo = findViewById(R.id.test_photo);
        requestPermission();
        initPlaces();
        setPlaceAutoComplete();
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
                place_id = place.getId();
                Toast.makeText(PlaceAPIActivity.this, place.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("Semi", status.getStatusMessage());
                Toast.makeText(PlaceAPIActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCurrentPlace(View view) {
        getCurrentPlaceTask();
    }

    public void getPhotoListener(View v) {
        getPhoto(place_id);
    }

    private void getPhoto(String place_id) {
        FetchPlaceRequest request = FetchPlaceRequest.builder(place_id, Arrays.asList(Place.Field.PHOTO_METADATAS)).build();
        mPlacesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();

                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
                mPlacesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                    @Override
                    public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                        Glide.with(PlaceAPIActivity.this)
                                .load(bitmap)
                                .placeholder(R.drawable.all_ic_loading_black_24)
                                .error(R.drawable.ic_home_mini_store)
                                .into(test_photo);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Semi", "Fetch photo error: " + e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Semi", "FetchPlaceRequest error: " + e.getMessage());
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

    private void getCurrentPlaceTask() {
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(mPlaceFields).build();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "chưa cấp quyền", Toast.LENGTH_SHORT).show();
            return;
        }
        Task<FindCurrentPlaceResponse> taskReponse = mPlacesClient.findCurrentPlace(request);
        taskReponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    //If success -> Placelihood return list of likelihood place

                    /*Collections.sort(response.getPlaceLikelihoods(), new Comparator<PlaceLikelihood>() {
                        @Override
                        public int compare(PlaceLikelihood placeLikelihood, PlaceLikelihood t1) {
                            return new Double(placeLikelihood.getLikelihood()).compareTo(t1.getLikelihood());
                        }
                    });

                    Collections.reverse(response.getPlaceLikelihoods());*/

                    place_id = response.getPlaceLikelihoods().get(0).getPlace().getId();
                    Log.d("Semi", "Address: " + response.getPlaceLikelihoods().get(0).getPlace().getAddress());
                    StringBuilder sb = new StringBuilder();
                    for (PlaceLikelihood place : response.getPlaceLikelihoods()) {
                        sb.append("Name place: ").append(place.getPlace().getName()).append("\n");
                               // .append("type ").append(place.getPlace().getTypes().size()).append("\n")
                                //.append("lat ").append(place.getPlace().getLatLng().latitude).append("\n")
                                //.append("lng ").append(place.getPlace().getLatLng().longitude).append("\n")
                                //.append("open ").append(place.getPlace().getOpeningHours().toString()).append("\n")
                                //.append("name ").append(place.getPlace().getName()).append("\n");
                    }
                    test_edt_likehood.setText(sb.toString());
                    Log.d("Semi", "Place: " + sb.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


}
