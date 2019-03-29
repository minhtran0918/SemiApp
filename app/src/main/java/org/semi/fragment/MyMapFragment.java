package org.semi.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.semi.MyApp;
import org.semi.R;
import org.semi.contract.Contract;
import org.semi.custom.StoreInfoWindowAdapter;
import org.semi.firebase.IResult;
import org.semi.firebase.StorageConnector;
import org.semi.firebase.StoreConnector;
import org.semi.minh.StoreDetailActivity;
import org.semi.object.Store;
import org.semi.util.LocationUtils;
import org.semi.util.SearchBox;

import java.util.ArrayList;
import java.util.List;

public class MyMapFragment extends Fragment implements OnMapReadyCallback, ISearch, View.OnClickListener {
    private static final int NUM_STORES_PER_REQUEST = 5;
    private GoogleMap map;
    private int productOrStoretype;
    private int mode;
    private String query;
    private SearchBox searchBox;
    private StoreConnector storeConnector;
    private List<Marker> markers;
    private AppCompatImageView flagImageView;
    private int scaleFactor;
    private long lastCallId;
    private ThreadLocal<Bitmap> bitmapHolder;

    public MyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IUseFragment) {
            ((IUseFragment) context).onFragmentAttached(this);
        }
        mode = Contract.STORE_MODE;
        storeConnector = StoreConnector.getInstance();
        productOrStoretype = -1;
        query = "";
        markers = new ArrayList<>();
        bitmapHolder = new ThreadLocal<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        flagImageView = view.findViewById(R.id.flagImageView);
        AppCompatImageButton upButton = view.findViewById(R.id.upButton);
        AppCompatImageButton downButton = view.findViewById(R.id.downButton);
        mapFragment.getMapAsync(this);
        upButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;
        searchBox = new SearchBox();
        searchBox.setDimen(Contract.VISIBLE_BOX_MIN_DIMEN);
        map.setMyLocationEnabled(true);
        map.setIndoorEnabled(false);
        map.setInfoWindowAdapter(new StoreInfoWindowAdapter(bitmapHolder));
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                updatePlacesWhenMoveIdle();
            }
        });
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                flagImageView.setVisibility(View.VISIBLE);
            }
        });
        getLastLocation(new IResult<android.location.Location>() {
            @Override
            public void onResult(android.location.Location result) {
                final LatLng location = new LatLng(result.getLatitude(), result.getLongitude());
                moveCameraToSearchBox(location);
            }

            @Override
            public void onFailure(@NonNull Exception exp) {
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                initInfoWindow(marker);
                return false;
            }
        });
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent storeIntent = new Intent(MyApp.getContext(), StoreDetailActivity.class);
                storeIntent.putExtra(Contract.BUNDLE_STORE_KEY, ((Store)marker.getTag()).getId());
                startActivity(storeIntent);
            }
        });
    }

    private void updatePlacesWhenMoveIdle() {
        LatLng cameraTarget = map.getCameraPosition().target;
        flagImageView.setVisibility(View.INVISIBLE);
        if (!searchBox.isContains(cameraTarget)) {
            searchBox.setCenter(cameraTarget);
            searchBox.draw(map);
            searchNearbyCenterStores(true);
        } else {
            searchNearbyCenterStores(false);
        }
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

    private void getLastLocation(final IResult<android.location.Location> result) {
        LocationUtils.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(@Nullable android.location.Location location) {
                        if (location == null) {
                            return;
                        }
                        result.onResult(location);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                result.onFailure(e);
            }
        });
    }

    private void moveCameraToSearchBox(LatLng location) {
        searchBox.setCenter(location);
        searchBox.draw(map);
        LatLngBounds bounds = new LatLngBounds(searchBox.getSouthWest(),
                searchBox.getNorthEast());
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, Contract.VISIBLE_BOX_PADDING));
    }

    @Override
    public void search(int type, String query, int mode) {
        this.productOrStoretype = type;
        this.query = query;
        this.mode = mode;
        searchNearbyCenterStores(true);
    }

    private void searchNearbyCenterStores(final boolean isGetNew) {
        if (isGetNew) {
            removePlaces();
        }
        final long currentCallId = ++lastCallId;
        IResult<List<Store>> result = new IResult<List<Store>>() {
            @Override
            public void onResult(@NonNull List<Store> result) {
                if (currentCallId != lastCallId || result.size() == 0) {
                    return;
                }
                addPlaces(result);
            }

            @Override
            public void onFailure(@NonNull Exception exp) {
            }
        };
        if (mode == Contract.STORE_MODE) {
            storeConnector.getNearbyStoresByKeywords(searchBox.getLocationCenter(), markers.size(),
                    NUM_STORES_PER_REQUEST, productOrStoretype, query, searchBox.getDimen(), result);
        } else if (mode == Contract.PRODUCT_MODE) {
            storeConnector.getNearbyStoresByProducts(searchBox.getLocationCenter(), markers.size(),
                    NUM_STORES_PER_REQUEST, productOrStoretype, query, searchBox.getDimen(), result);
        }
    }

    private void addPlaces(List<Store> stores) {
        for (Store store : stores) {
            MarkerOptions options = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.place))
                    .title(store.getTitle())
                    .anchor(0.5f, 0.5f) //center icon
                    .position(new LatLng(store.getGeo().getLatitude(),
                            store.getGeo().getLongitude()));
            Marker marker = map.addMarker(options);
            marker.setTag(store);
            markers.add(marker);
        }
    }

    private void removePlaces() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

    @Override
    public void scroll(String lastId) {
    }

    @Override
    public void clickItem(String id) {
    }

    private void updatePlacesWhenScaleSearchBox() {
        searchBox.setDimen(Contract.VISIBLE_BOX_MIN_DIMEN * (1 << scaleFactor));
        moveCameraToSearchBox(searchBox.getCenter());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upButton:
                if (scaleFactor == Contract.DIMEN_SCALE_FACTOR_MAX) {
                    return;
                }
                scaleFactor++;
                updatePlacesWhenScaleSearchBox();
                break;
            case R.id.downButton:
                if (scaleFactor == 0) {
                    return;
                }
                scaleFactor--;
                removePlaces();
                updatePlacesWhenScaleSearchBox();
                break;
        }
    }
}
