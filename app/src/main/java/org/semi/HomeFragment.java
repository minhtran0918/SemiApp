package org.semi;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.semi.ViewModel.HomeViewModel;
import org.semi.adapter.HomeListAdapter;
import org.semi.databases.SharedPrefs;
import org.semi.firebase.IResult;
import org.semi.firebase.ProductConnector;
import org.semi.firebase.StoreConnector;
import org.semi.object.Store;
import org.semi.utils.Contract;
import org.semi.utils.DialogUtils;
import org.semi.utils.LocationUtils;
import org.semi.utils.ObjectUtils;
import org.semi.views.StoreDetailActivity;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    //Firebase connector
    private StoreConnector storeConnector;
    private ProductConnector productConnector;
    //Location
    private org.semi.object.Location currentLocation;
    private LocationUtils locationUtils;

    private static HomeFragment sHomeFragment;
    private HomeViewModel mHomeViewModel;
    private View mRootView;
    private TextView mTxtState, mTxtCategoryState;

    private NestedScrollView mNestedScrollView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    //private StoreAdapter mRcvAdapter;
    private HomeListAdapter mHomeRcvAdapter;
    private boolean mShouldLoadMoreData;
    //private IInteractionWithList<IHaveIdAndName<String>> listener;

    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int RC_SELECT_CATEGORY = 11;
    private int RC_SELECT_OPTION = 22;

    public static HomeFragment newInstance() {
        if (sHomeFragment == null) {
            sHomeFragment = new HomeFragment();
        }
        return sHomeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();
        checkAndRequestPermission();
        loadAllNewStoresOrProducts();
    }

    private void initView() {
        //Button Direction
        ConstraintLayout option_category, option_address, option_filter;
        option_category = mRootView.findViewById(R.id.home_ic_option_category);
        option_address = mRootView.findViewById(R.id.home_ic_option_address);
        option_filter = mRootView.findViewById(R.id.home_ic_option_filter);
        option_category.setOnClickListener(mOnClickOption);
        option_address.setOnClickListener(mOnClickOption);
        option_filter.setOnClickListener(mOnClickOption);

        LinearLayout directStore, directConvenience, directSupermarket, directMall,
                directMarket, directATM, directPharmacy, directAll;
        directStore = mRootView.findViewById(R.id.home_ic_direct_store);
        directConvenience = mRootView.findViewById(R.id.home_ic_direct_convenience);
        directSupermarket = mRootView.findViewById(R.id.home_ic_direct_super_market);
        directMall = mRootView.findViewById(R.id.home_ic_direct_mall);
        directMarket = mRootView.findViewById(R.id.home_ic_direct_market);
        directATM = mRootView.findViewById(R.id.home_ic_direct_atm);
        directPharmacy = mRootView.findViewById(R.id.home_ic_direct_pharmacy);
        directAll = mRootView.findViewById(R.id.home_ic_direct_all);

        directStore.setOnClickListener(mOnClickListenerDirect);
        directConvenience.setOnClickListener(mOnClickListenerDirect);
        directSupermarket.setOnClickListener(mOnClickListenerDirect);
        directMall.setOnClickListener(mOnClickListenerDirect);
        directMarket.setOnClickListener(mOnClickListenerDirect);
        directATM.setOnClickListener(mOnClickListenerDirect);
        directPharmacy.setOnClickListener(mOnClickListenerDirect);
        directAll.setOnClickListener(mOnClickListenerDirect);

        mTxtCategoryState = mRootView.findViewById(R.id.txt_home_category_state);
        mTxtCategoryState.setText(getString(R.string.all_title_more));
        mTxtState = mRootView.findViewById(R.id.txt_home_state_select);

        //Data
        storeConnector = StoreConnector.getInstance();
        productConnector = ProductConnector.getInstance();

        locationUtils = new LocationUtils();

        //RecyclerView & Adapter
        mRecyclerView = mRootView.findViewById(R.id.recycler_view_fragment_home_list);
        mHomeRcvAdapter = new HomeListAdapter();
        mLayoutManager = new LinearLayoutManager(mRootView.getContext());
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mHomeRcvAdapter);

        mNestedScrollView = mRootView.findViewById(R.id.nested_scroll_fragment_home);
        mSwipeRefreshLayout = mRootView.findViewById(R.id.recycler_view_fragment_home_swipe_refresh);
    }

    private void initViewModel() {
        //ViewModel
        mHomeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);

        mHomeViewModel.cityId.observe(this, integer -> {
            Log.d("Semi", "a");
        });

        mHomeViewModel.listStore.observe(this, stores ->
                mHomeRcvAdapter.setDataSet(stores, mHomeViewModel.currentLocation.getValue()));
        mHomeViewModel.categoryStore.observe(this, integer -> {
            mSwipeRefreshLayout.setRefreshing(true);
            loadAllNewStoresOrProducts();
        });
        //Listener
        View.OnClickListener mOnClickListenerRcv = v -> {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            Toast.makeText(getActivity(), "position: " + position, Toast.LENGTH_SHORT).show();
            showStoreDetail(mHomeViewModel.listStore.getValue().get(position));
        };
        mHomeRcvAdapter.setOnItemClickListener(mOnClickListenerRcv);
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    int my_value = v.getMeasuredHeight() / 20; // Để chưa xuống cuối recyclerview 1 khoảng đã load tiếp
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight()) - my_value) && scrollY > oldScrollY) {
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                        if (mShouldLoadMoreData && !mSwipeRefreshLayout.isRefreshing()) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                Log.d("Semi", "last");
                                if (mHomeViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_STORE) {
                                    int size = mHomeRcvAdapter.getItemCount();
                                    if (size != 0) {
                                        loadStoresAt(mHomeRcvAdapter.getItemCount());
                                    }
                                } else {
                                    int size = mHomeRcvAdapter.getItemCount();
                                    if (size != 0) {
                                        //TODO Load More Products
                                    }
                                }

                            }
                        }
                    }
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> loadAllNewStoresOrProducts());
    }

    private View.OnClickListener mOnClickOption = v -> {
        Intent callActivity;
        switch (v.getId()) {
            case R.id.home_ic_option_category:
                callActivity = new Intent(getActivity(), HomeSelectCategoryActivity.class);
                startActivityForResult(callActivity, RC_SELECT_CATEGORY);
                break;

            case R.id.home_ic_option_address:
                callActivity = new Intent(getActivity(), HomeSelectOptionActivity.class);
                startActivityForResult(callActivity, RC_SELECT_OPTION);
                break;

            case R.id.home_ic_option_filter:
                callActivity = new Intent(getActivity(), HomeSelectOptionActivity.class);
                startActivityForResult(callActivity, RC_SELECT_OPTION);
                break;
        }
    };

    private View.OnClickListener mOnClickListenerDirect = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int selectCategory;
            switch (v.getId()) {
                case R.id.home_ic_direct_store:
                    mTxtCategoryState.setText(getString(R.string.all_title_store));
                    selectCategory = Contract.MODE_HOME_LOAD_STORE;
                    break;
                case R.id.home_ic_direct_convenience:
                    mTxtCategoryState.setText(getString(R.string.all_title_convenience));
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_CONVENIENCE;
                    break;
                case R.id.home_ic_direct_super_market:
                    mTxtCategoryState.setText(getString(R.string.all_title_super_market));
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_SUPER_MARKET;
                    break;
                case R.id.home_ic_direct_mall:
                    mTxtCategoryState.setText(getString(R.string.all_title_mall));
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_MALL;
                    break;
                case R.id.home_ic_direct_market:
                    mTxtCategoryState.setText(getString(R.string.all_title_market));
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_MARKET;
                    break;
                case R.id.home_ic_direct_atm:
                    mTxtCategoryState.setText(getString(R.string.all_title_atm));
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_ATM;
                    break;
                case R.id.home_ic_direct_pharmacy:
                    mTxtCategoryState.setText(getString(R.string.all_title_pharmacy));
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_PHARMACY;
                    break;
                case R.id.home_ic_direct_all:
                    mTxtCategoryState.setText(getString(R.string.all_title_more));
                    selectCategory = Contract.MODE_HOME_LOAD_STORE_TYPE_ALL;
                    break;
                default:
                    selectCategory = Contract.MODE_HOME_LOAD_STORE_TYPE_ALL;
                    break;
            }
            mHomeViewModel.categoryStore.setValue(selectCategory);

        }
    };

    private void checkAndRequestPermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            onPermissionGranted();
                            loadAllNewStoresOrProducts();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(getContext(), "Vui lòng cấp quyền để xử dụng tính năng này - HomeFragment", Toast.LENGTH_SHORT).show();
                        token.continuePermissionRequest();
                    }
                });
    }

    private void onPermissionGranted() {
        LocationUtils.checkAndRequestLocationSettings(getActivity(), null);
    }

    private void loadAllNewStoresOrProducts() {
        mShouldLoadMoreData = true;
        getLastLocation(new IResult<android.location.Location>() {
            @Override
            public void onResult(@NonNull android.location.Location location) {
                int mode_load = mHomeViewModel.modeStoreOrProduct.getValue();
                if (mode_load == Contract.MODE_HOME_LOAD_STORE) { //it's store
                    loadAllNewStores();
                } else if (mode_load == Contract.MODE_HOME_LOAD_PRODUCT) { //it's Product
                    //loadAllNewProducts();
                }
            }

            @Override
            public void onFailure(@NonNull Exception exp) {
                //Error Location
                mSwipeRefreshLayout.setRefreshing(false);
                DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorLocationMessage));

            }
        });
    }


    //load from offset 0,discard old data in RecyclerView and load new data to it.
    private void loadAllNewStores() {
        if (currentLocation == null) {
            //Location error
            DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorLocationMessage));
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        int type = mHomeViewModel.categoryStore.getValue(); //
        storeConnector.getNearbyStores(currentLocation, 0, 2f, Contract.NUM_STORES_PER_REQUEST, type,
                new IResult<List<Store>>() {
                    @Override
                    public void onResult(List<Store> result) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (result.size() == 0) {
                            //Empty Error
                            //Clear RecyclerView
                            mHomeViewModel.listStore.setValue(null);
                            DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorEmptyResultMessage));
                            return;
                        }
                        mHomeViewModel.listStore.setValue(result);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mHomeViewModel.listStore.setValue(null);
                        //Network Error
                        DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorNetworkMessage));
                    }
                });
    }


    private void loadStoresAt(int position) {
        mSwipeRefreshLayout.setRefreshing(true);
        if (currentLocation == null) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        int store_type = mHomeViewModel.categoryStore.getValue();
        storeConnector.getNearbyStores(currentLocation, position, 0.5f, Contract.NUM_STORES_PER_REQUEST, store_type,
                new IResult<List<Store>>() {
                    @Override
                    public void onResult(List<Store> result) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (result.size() == 0 && mHomeViewModel.listStore.getValue().size() == 0) {
                            mShouldLoadMoreData = false;
                            //Error Empty
                            DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorEmptyResultMessage));
                            return;
                        }
                        if (result.isEmpty()) {
                            mShouldLoadMoreData = false;
                            return;
                        }
                        mHomeViewModel.updateListStore(result);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        mShouldLoadMoreData = true;
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(final IResult<android.location.Location> result) {
        final Task<android.location.Location> task = LocationUtils.getLastLocation();
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                if (location == null) {
                    //TODO - Error location
                    Snackbar.make(mRootView, getString(R.string.errorLocationMessage), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                currentLocation = ObjectUtils.toMyLocation(location);
                mHomeViewModel.currentLocation.setValue(currentLocation);
                result.onResult(location);
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO - Error location
                Snackbar.make(mRootView, getString(R.string.errorLocationMessage), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void showStoreDetail(Store store) {
        Intent storeIntent = new Intent(getActivity(), StoreDetailActivity.class);
        storeIntent.putExtra(Contract.BUNDLE_STORE_KEY, store.getId());
        startActivity(storeIntent);
    }


    /*@SuppressLint("MissingPermission")
    private void loadFirst() {
        locationUtils.requestLocationUpdates(new IResult<android.location.Location>() {
            @Override
            public void onResult(android.location.Location result) {
                if (result != null) {
                    locationUtils.removeLocationUpdates();
                    currentLocation = ObjectUtils.toMyLocation(result);
                    //Fix
                    //final int modePosition = searchModeSpinner.getSelectedItemPosition();
                    int modePosition = mHomeViewModel.modeStoreOrProduct.getValue();
                    if (modePosition == Contract.STORE_MODE) {
                        loadAllNewStores();
                    } else if (modePosition == Contract.PRODUCT_MODE) {
                        loadAllNewProducts();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Exception exp) {
            }
        });
    }*/

    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        if (requestCode == RC_SELECT_OPTION && result == RESULT_OK) {
            mHomeViewModel.cityId.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_CITY, Integer.class));
            Toast.makeText(getActivity(), mHomeViewModel.cityId.getValue().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDataConfig() {
        //
       /* SharedPrefs.getInstance().put(SharedPrefs.KEY_ALL_ADDRESS_CITY, mHomeOptionViewModel.cityId.getValue());
        SharedPrefs.getInstance().put(SharedPrefs.KEY_ALL_ADDRESS_DISTRICT, mHomeOptionViewModel.districtId.getValue());
        SharedPrefs.getInstance().put(SharedPrefs.KEY_ALL_ADDRESS_WARD, mHomeOptionViewModel.wardId.getValue());
        SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT, mHomeOptionViewModel.modeStoreOrProduct.getValue());
        SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_RANGE, mHomeOptionViewModel.modeRange.getValue());
        SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_RANGE_VALUE, mHomeOptionViewModel.modeRangeValue.getValue());
        SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_SORT, mHomeOptionViewModel.modeSort.getValue());*/
    }


}
