package org.semi;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import org.semi.adapter.StoreInfoWindowAdapter;
import org.semi.firebase.IResult;
import org.semi.firebase.StorageConnector;
import org.semi.firebase.StoreConnector;
import org.semi.fragment.ISearch;
import org.semi.fragment.IUseFragment;
import org.semi.object.Store;
import org.semi.utils.Contract;
import org.semi.utils.LocationUtils;
import org.semi.utils.MyApp;
import org.semi.utils.SearchBox;
import org.semi.views.StoreDetailActivity;

import java.util.ArrayList;
import java.util.List;
public class DiscoverFragment extends Fragment implements OnMapReadyCallback, ISearch, View.OnClickListener{

    private static DiscoverFragment mIntance;
    private static final int NUM_STORES_PER_REQUEST = 5;
    private GoogleMap mMap;
    private int mProductOrStoretype;
    private int mMode;
    private String mStrQuery;
    private SearchBox mSearchBox;
    private StoreConnector mStoreConnector;
    private List<Marker> mListMarkers;
    private AppCompatImageView mFlagImageView;
    private int mScaleFactor;
    private long mLastCallId;
    private ThreadLocal<Bitmap> mThreadBitmapHolder;

    private Bitmap mBitmapMarker;

    public static DiscoverFragment getInstance() {
        if (mIntance == null) {
            mIntance = new DiscoverFragment();
        }
        return mIntance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IUseFragment) {
            ((IUseFragment) context).onFragmentAttached(this);
        }
        mMode = Contract.STORE_MODE;
        mStoreConnector = StoreConnector.getInstance();
        mProductOrStoretype = -1;
        mStrQuery = "";
        mListMarkers = new ArrayList<>();
        mThreadBitmapHolder = new ThreadLocal<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mFlagImageView = view.findViewById(R.id.flagImageView);
        AppCompatImageButton upButton = view.findViewById(R.id.upButton);
        AppCompatImageButton downButton = view.findViewById(R.id.downButton);
        mapFragment.getMapAsync(this);
        upButton.setOnClickListener(this);
        downButton.setOnClickListener(this);

        //Icon
        //Config my icon
        int height = 75;
        int width = 75;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.map_ic_marker_128);
        Bitmap b = bitmapdraw.getBitmap();
        mBitmapMarker = Bitmap.createScaledBitmap(b, width, height, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap map) {
        this.mMap = map;
        mSearchBox = new SearchBox();
        mSearchBox.setDimen(Contract.VISIBLE_BOX_MIN_DIMEN);
        map.setMyLocationEnabled(true);
        map.setIndoorEnabled(false);
        map.setInfoWindowAdapter(new StoreInfoWindowAdapter(mThreadBitmapHolder));
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                updatePlacesWhenMoveIdle();
            }
        });
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                mFlagImageView.setVisibility(View.VISIBLE);
            }
        });
        getLastLocation(new IResult<Location>() {
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
                storeIntent.putExtra(Contract.BUNDLE_STORE_KEY, ((Store) marker.getTag()).getId());
                startActivity(storeIntent);
            }
        });
    }

    private void updatePlacesWhenMoveIdle() {
        LatLng cameraTarget = mMap.getCameraPosition().target;
        mFlagImageView.setVisibility(View.INVISIBLE);
        if (!mSearchBox.isContains(cameraTarget)) {
            mSearchBox.setCenter(cameraTarget);
            mSearchBox.draw(mMap);
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
                if (result != null) {
                    mThreadBitmapHolder.set(result);
                    marker.showInfoWindow();
                }
            }

            @Override
            public void onFailure(@NonNull Exception exp) {
            }
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
        mSearchBox.setCenter(location);
        mSearchBox.draw(mMap);
        LatLngBounds bounds = new LatLngBounds(mSearchBox.getSouthWest(),
                mSearchBox.getNorthEast());
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, Contract.VISIBLE_BOX_PADDING));
    }

    @Override
    public void search(int type, String query, int mode) {
        this.mProductOrStoretype = type;
        this.mStrQuery = query;
        this.mMode = mode;
        searchNearbyCenterStores(true);
    }

    @Override
    public void scroll(String lastId) {
    }

    @Override
    public void clickItem(String id) {
    }

    private void searchNearbyCenterStores(final boolean isGetNew) {
        if (isGetNew) {
            removePlaces();
        }
        final long currentCallId = ++mLastCallId;
        IResult<List<Store>> result = new IResult<List<Store>>() {
            @Override
            public void onResult(@NonNull List<Store> result) {
                if (currentCallId != mLastCallId || result.size() == 0) {
                    return;
                }
                addPlaces(result);
            }

            @Override
            public void onFailure(@NonNull Exception exp) {
            }
        };
        if (mMode == Contract.STORE_MODE) {
            mStoreConnector.getNearbyStoresByKeywords(mSearchBox.getLocationCenter(), mListMarkers.size(),
                    NUM_STORES_PER_REQUEST, mProductOrStoretype, mStrQuery, mSearchBox.getDimen(), result);
        } else if (mMode == Contract.PRODUCT_MODE) {
            mStoreConnector.getNearbyStoresByProducts(mSearchBox.getLocationCenter(), mListMarkers.size(),
                    NUM_STORES_PER_REQUEST, mProductOrStoretype, mStrQuery, mSearchBox.getDimen(), result);
        }
    }

    private void addPlaces(List<Store> stores) {
        for (Store store : stores) {
            MarkerOptions options = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(mBitmapMarker))
                    .title(store.getTitle())
                    .anchor(0.5f, 0.5f) //center icon
                    .position(new LatLng(store.getGeo().getLatitude(),
                            store.getGeo().getLongitude()));
            Marker marker = mMap.addMarker(options);
            marker.setTag(store);
            mListMarkers.add(marker);
        }
    }

    private void removePlaces() {
        for (Marker marker : mListMarkers) {
            marker.remove();
        }
        mListMarkers.clear();
    }


    private void updatePlacesWhenScaleSearchBox() {
        mSearchBox.setDimen(Contract.VISIBLE_BOX_MIN_DIMEN * (1 << mScaleFactor));
        moveCameraToSearchBox(mSearchBox.getCenter());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downButton:
                if (mScaleFactor == Contract.DIMEN_SCALE_FACTOR_MAX) {
                    return;
                }
                mScaleFactor++;
                updatePlacesWhenScaleSearchBox();
                break;
            case R.id.upButton:
                if (mScaleFactor == 0) {
                    return;
                }
                mScaleFactor--;
                removePlaces();
                updatePlacesWhenScaleSearchBox();
                break;
        }
    }
}
