package org.semi;

import android.Manifest;
import android.annotation.SuppressLint;

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

import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.semi.ViewModel.HomeViewModel;
import org.semi.adapter.HomeListAdapter;
import org.semi.adapter.LocationToAddress;
import org.semi.databases.SharedPrefs;
import org.semi.firebase.IResult;
import org.semi.firebase.ProductConnector;
import org.semi.firebase.StoreConnector;
import org.semi.fragment.StoreViewFragment;
import org.semi.object.Location;
import org.semi.object.Product;
import org.semi.object.Store;
import org.semi.utils.Contract;
import org.semi.utils.DialogUtils;
import org.semi.utils.LocationUtils;
import org.semi.utils.ObjectUtils;
import org.semi.utils.StringUtils;
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
    private TextView mTxtState, mTxtCategoryState, mTxtLocateState, mTxtSortState;

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
        mTxtLocateState = mRootView.findViewById(R.id.txt_home_option_locate);
        mTxtSortState = mRootView.findViewById(R.id.txt_home_option_sort);

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

        mHomeViewModel.modeStoreOrProduct.observe(this, integer -> {
            SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT, integer);
            updateUI(mHomeViewModel.categoryStore.getValue(), mHomeViewModel.categoryProduct.getValue());
        });

        mHomeViewModel.listStore.observe(this, stores ->
                mHomeRcvAdapter.setDataSet(stores, mHomeViewModel.currentLocation.getValue()));

        mHomeViewModel.listProduct.observe(this, products ->
                mHomeRcvAdapter.setDataSet(null, mHomeViewModel.currentLocation.getValue()));

        mHomeViewModel.categoryStore.observe(this, integer -> {
            updateUI(integer, Contract.ALL_NOT_AVAILABLE);
        });

        mHomeViewModel.categoryProduct.observe(this, integer -> {
            updateUI(Contract.ALL_NOT_AVAILABLE, integer);
        });

        mHomeViewModel.modeRangeValue.observe(this, aFloat -> {
            updateUIRange();
            loadAllNewStoresOrProducts();
        });

        //Listener
        View.OnClickListener mOnClickListenerRcv = v -> {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            Toast.makeText(getActivity(), "position: " + position, Toast.LENGTH_SHORT).show();
            if (mHomeViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_STORE) {
                Toast.makeText(getActivity(), "load sTore", Toast.LENGTH_SHORT).show();
                showStoreDetail(mHomeViewModel.listStore.getValue().get(position));
            } else {
                showProductDetail(mHomeViewModel.listProduct.getValue().get(position));
            }
        };
        mHomeRcvAdapter.setOnItemClickListener(mOnClickListenerRcv);
        mNestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                int my_value = v.getMeasuredHeight() / 20; // Để chưa xuống cuối recyclerview 1 khoảng đã load tiếp
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight()) - my_value) && scrollY > oldScrollY) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (mShouldLoadMoreData && !mSwipeRefreshLayout.isRefreshing()) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            Log.d("Semi", "last of list -> load more");
                            if (mHomeViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_STORE) {
                                int size = mHomeRcvAdapter.getItemCount();
                                if (size != 0) {
                                    loadStoresAt(mHomeRcvAdapter.getItemCount());
                                }
                            } else {
                                int size = mHomeRcvAdapter.getItemCount();
                                if (size != 0) {
                                    loadProductsAt(mHomeRcvAdapter.getItemCount());
                                }
                            }

                        }
                    }
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> loadAllNewStoresOrProducts());
    }

    private void updateUI(int category_store, int category_product) {
        if (mHomeViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_STORE) {
            if (category_store != Contract.ALL_NOT_AVAILABLE) {
                SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_CATEGORY_STORE, category_store);
                switch (category_store) {
                    case Contract.MODE_LOAD_STORE_TYPE_STORE:
                        mTxtCategoryState.setText(getString(R.string.all_title_store_store));
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_CONVENIENCE:
                        mTxtCategoryState.setText(getString(R.string.all_title_convenience));
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_SUPER_MARKET:
                        mTxtCategoryState.setText(getString(R.string.all_title_super_market));
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_MALL:
                        mTxtCategoryState.setText(getString(R.string.all_title_mall));
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_MARKET:
                        mTxtCategoryState.setText(getString(R.string.all_title_market));
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_ATM:
                        mTxtCategoryState.setText(getString(R.string.all_title_atm));
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_PHARMACY:
                        mTxtCategoryState.setText(getString(R.string.all_title_pharmacy));
                        break;
                    default:
                        mTxtCategoryState.setText(getString(R.string.all_title_more));
                        break;
                }
                loadAllNewStoresOrProducts();
            }
        } else {
            if (mHomeViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_PRODUCT) {
                if (category_product != Contract.ALL_NOT_AVAILABLE) {
                    SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_CATEGORY_PRODUCT, category_product);
                    switch (category_product) {
                        case Contract.MODE_HOME_LOAD_PRODUCT_TYPE_ALL:
                            //mTxtCategoryState.setText(getString(R.string.all_title_product));
                            mTxtCategoryState.setText(getString(R.string.all_title_product));
                            break;
                    }
                    loadAllNewStoresOrProducts();
                }
            }
        }
    }

    private void updateUIRange() {

        if (mHomeViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_AROUND) {
            mTxtSortState.setText("Gần nhất");
            mTxtLocateState.setText(getString(R.string.home_lbl_option_around));
            if (currentLocation != null) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                LocationToAddress.getAddressFromLocation(latitude, longitude, getContext(), new GeocoderHandler());
            } else {
                Spanned text = Html.fromHtml(
                        "<font color=\"" + getString(R.string.home_label_around_location_color) + "\"> " + getString(R.string.home_label_around_nolocation) + " </font>"
                                + StringUtils.toDistanceFormat(mHomeViewModel.modeRangeValue.getValue()));
                mTxtState.setText(text);
            }
        } else {
            mTxtState.setText("Tất cả " + (mHomeViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_STORE ? getString(R.string.home_lbl_store_state) : getString(R.string.home_lbl_product_state)));
            mTxtLocateState.setText(mHomeViewModel.getDistrictByID(mHomeViewModel.districtId.getValue()).toLiteName());
            mTxtSortState.setText("Tất cả");
        }
    }

    private View.OnClickListener mOnClickOption = v -> {
        Intent callActivity;
        switch (v.getId()) {
            case R.id.home_ic_option_category:
                SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT, mHomeViewModel.modeStoreOrProduct.getValue());
                SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_CATEGORY_STORE, mHomeViewModel.categoryStore.getValue());
                SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_CATEGORY_PRODUCT, mHomeViewModel.categoryProduct.getValue());
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
            if (mHomeViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_STORE &&
                    v.getId() == mHomeViewModel.categoryStore.getValue())
                return;

            switch (v.getId()) {
                case R.id.home_ic_direct_store:
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_STORE;
                    break;
                case R.id.home_ic_direct_convenience:
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_CONVENIENCE;
                    break;
                case R.id.home_ic_direct_super_market:
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_SUPER_MARKET;
                    break;
                case R.id.home_ic_direct_mall:
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_MALL;
                    break;
                case R.id.home_ic_direct_market:
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_MARKET;
                    break;
                case R.id.home_ic_direct_atm:
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_ATM;
                    break;
                case R.id.home_ic_direct_pharmacy:
                    selectCategory = Contract.MODE_LOAD_STORE_TYPE_PHARMACY;
                    break;
                case R.id.home_ic_direct_all:
                    selectCategory = Contract.MODE_HOME_LOAD_STORE_TYPE_ALL;
                    break;
                default:
                    selectCategory = Contract.MODE_HOME_LOAD_STORE_TYPE_ALL;
                    break;
            }
            if (mHomeViewModel.modeStoreOrProduct.getValue() != Contract.MODE_HOME_LOAD_STORE) {
                mHomeViewModel.modeStoreOrProduct.setValue(Contract.MODE_HOME_LOAD_STORE);
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
        mSwipeRefreshLayout.setRefreshing(true);
        mShouldLoadMoreData = true;
        getLastLocation(new IResult<android.location.Location>() {
            @Override
            public void onResult(@NonNull android.location.Location location) {
                mHomeViewModel.currentLocation.setValue(new Location(location.getLatitude(), location.getLongitude()));
                updateUIRange();
                int mode_load = mHomeViewModel.modeStoreOrProduct.getValue();
                if (mode_load == Contract.MODE_HOME_LOAD_STORE) { //it's store
                    loadAllNewStores();
                } else if (mode_load == Contract.MODE_HOME_LOAD_PRODUCT) { //it's Product
                    loadAllNewProducts();
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

    private void loadAllNewsStoreOrProductByKey() {
        mSwipeRefreshLayout.setRefreshing(true);
        mShouldLoadMoreData = true;
        if (mHomeViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_ALL) {
            int mode_load = mHomeViewModel.modeStoreOrProduct.getValue();
            if (mode_load == Contract.MODE_HOME_LOAD_STORE) { //it's store
                getListStoresByKeywords();
            } else if (mode_load == Contract.MODE_HOME_LOAD_PRODUCT) { //it's Product
                //loadAllNewProducts();
            }
        }
    }

    private void getListStoresByKeywords() {
        mSwipeRefreshLayout.setRefreshing(true);
        mShouldLoadMoreData = true;
        final StoreConnector storeConnector = StoreConnector.getInstance();
        //TODO MẢNG ĐỊA CHỈ Ở ĐÂY
        Object[] address = new Object[4];
        address[0] = 0;
        address[1] = mHomeViewModel.cityId.getValue();
        address[2] = mHomeViewModel.districtId.getValue();
        address[3] = (mHomeViewModel.wardId.getValue() == 0 ? -1 : mHomeViewModel.wardId.getValue());
        //String key = StringUtils.normalize("Tap hoa");
        storeConnector.getStoresByKeywords(mHomeViewModel.categoryStore.getValue(), "", "", Contract.NUM_STORES_PER_REQUEST,
                address,
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
                        Log.d("Semi", "exp " + exp.getMessage());
                        //Network Error
                        DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorNetworkMessage));
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
        float distance = mHomeViewModel.modeRangeValue.getValue();
        storeConnector.getNearbyStores(currentLocation, 0, distance, Contract.NUM_STORES_PER_REQUEST, type,
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

    private void getMoreListStoresByKeywordsAt(int lastPos, int category) {
        mSwipeRefreshLayout.setRefreshing(true);
        final StoreConnector storeConnector = StoreConnector.getInstance();
        Object[] address = new Object[4];
        address[0] = 0;
        address[1] = -1;
        address[2] = -1;
        address[3] = -1;
        storeConnector.getStoresByKeywords(category, "", String.valueOf(lastPos), Contract.NUM_STORES_PER_REQUEST,
                address,
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
                        Log.d("Semi", "exp " + exp.getMessage());
                        mShouldLoadMoreData = true;
                        mSwipeRefreshLayout.setRefreshing(false);
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
        float distance = mHomeViewModel.modeRangeValue.getValue();
        storeConnector.getNearbyStores(currentLocation, position, distance, Contract.NUM_STORES_PER_REQUEST, store_type,
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

    private void loadAllNewProducts() {
        if (currentLocation == null) {
            //Location error
            DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorLocationMessage));
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        int product_type = mHomeViewModel.categoryProduct.getValue();
        float distance = mHomeViewModel.modeRangeValue.getValue();

        productConnector.getNearbyProducts(currentLocation, 0, distance, Contract.NUM_PRODUCTS_PER_REQUEST, product_type,
                new IResult<List<Product>>() {
                    @Override
                    public void onResult(List<Product> result) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (result.size() == 0) {
                            mHomeViewModel.listProduct.setValue(null);
                            DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorEmptyResultMessage));
                            return;
                        }
                        mHomeViewModel.listProduct.setValue(result);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mHomeViewModel.listProduct.setValue(null);
                        //Network Error
                        DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorNetworkMessage));
                    }
                });
    }

    private void loadProductsAt(int index) {
        mSwipeRefreshLayout.setRefreshing(true);
        if (currentLocation == null) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        int product_type = mHomeViewModel.categoryProduct.getValue();
        float distance = mHomeViewModel.modeRangeValue.getValue();
        productConnector.getNearbyProducts(currentLocation, index, distance, Contract.NUM_PRODUCTS_PER_REQUEST, product_type,
                new IResult<List<Product>>() {
                    @Override
                    public void onResult(List<Product> result) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (result.size() == 0 && mHomeViewModel.listProduct.getValue().size() == 0) {
                            mShouldLoadMoreData = false;
                            //Error Empty
                            DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorEmptyResultMessage));
                            return;
                        }
                        if (result.isEmpty()) {
                            mShouldLoadMoreData = false;
                            return;
                        }
                        mHomeViewModel.updateListProduct(result);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        mShouldLoadMoreData = true;
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void showProductDetail(Product product) {
        Intent storeIntent = new Intent(getContext(), StoreDetailActivity.class);
        storeIntent.putExtra(Contract.BUNDLE_STORE_KEY, product.getStore().getId());
        storeIntent.putExtra(Contract.BUNDLE_PRODUCT_KEY, product.getId());
        startActivity(storeIntent);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(final IResult<android.location.Location> result) {
        final Task<android.location.Location> task = LocationUtils.getLastLocation();
        task.addOnSuccessListener(getActivity(), location -> {
            if (location == null) {
                Snackbar.make(mRootView, getString(R.string.errorLocationMessage), Snackbar.LENGTH_SHORT).show();
                return;
            }
            currentLocation = ObjectUtils.toMyLocation(location);
            mHomeViewModel.currentLocation.setValue(currentLocation);
            result.onResult(location);
        }).addOnFailureListener(getActivity(), e -> {
            Snackbar.make(mRootView, getString(R.string.errorLocationMessage), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void showStoreDetail(Store store) {
        Intent storeIntent = new Intent(getActivity(), StoreDetailActivity.class);
        storeIntent.putExtra(Contract.BUNDLE_STORE_KEY, store.getId());
        startActivity(storeIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        if (requestCode == RC_SELECT_CATEGORY && result == RESULT_OK) {
            //Xét mode hiện tại có là mode cũ
            if (mHomeViewModel.modeStoreOrProduct.getValue().equals(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT, Integer.class, Contract.MODE_HOME_LOAD_STORE))) {
                //check có thay đổi mode store hay product k
                if (mHomeViewModel.modeStoreOrProduct.getValue().equals(Contract.MODE_HOME_LOAD_STORE)) {
                    //Nếu là mode store
                    if (!mHomeViewModel.categoryStore.getValue().equals(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_CATEGORY_STORE, Integer.class, Contract.MODE_HOME_LOAD_STORE_TYPE_ALL))) {
                        //khác mode hiện tại
                        mHomeViewModel.modeStoreOrProduct.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT, Integer.class, Contract.MODE_HOME_LOAD_STORE));
                        mHomeViewModel.categoryStore.setValue(
                                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_CATEGORY_STORE, Integer.class, Contract.MODE_HOME_LOAD_STORE_TYPE_ALL));
                    }

                } else {
                    if (!mHomeViewModel.categoryProduct.getValue().equals(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_CATEGORY_STORE, Integer.class, Contract.MODE_HOME_LOAD_PRODUCT_TYPE_ALL))) {
                        mHomeViewModel.modeStoreOrProduct.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT, Integer.class, Contract.MODE_HOME_LOAD_STORE));
                        mHomeViewModel.categoryProduct.setValue(
                                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_CATEGORY_STORE, Integer.class, Contract.MODE_HOME_LOAD_PRODUCT_TYPE_ALL));
                    }
                }
            } else {
                mHomeViewModel.modeStoreOrProduct.setValue(
                        SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT, Integer.class, Contract.MODE_HOME_LOAD_STORE)
                );
            }
        }
        if (requestCode == RC_SELECT_OPTION && result == RESULT_OK) {
            //check range ? = range hiện tại
            if (SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_RANGE, Integer.class).equals(mHomeViewModel.modeRange.getValue())) {
                if (mHomeViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_AROUND) {
                    //Kiểm tra range value có thay đổi
                    if (!mHomeViewModel.modeRangeValue.getValue()
                            .equals(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_RANGE_VALUE, Float.class))) {
                        mHomeViewModel.modeRangeValue.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_RANGE_VALUE, Float.class));
                    }
                } else {
                    //range all ? check city
                    if (!SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_CITY, Integer.class).equals(mHomeViewModel.cityId.getValue()) ||
                            !SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_DISTRICT, Integer.class).equals(mHomeViewModel.districtId.getValue()) ||
                            !SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_WARD, Integer.class).equals(mHomeViewModel.wardId.getValue())
                    ) {
                        mHomeViewModel.cityId.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_CITY, Integer.class));
                        mHomeViewModel.districtId.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_DISTRICT, Integer.class));
                        mHomeViewModel.wardId.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_WARD, Integer.class));
                        updateUIRange();
                        getListStoresByKeywords();
                    }
                }
            } else {
                mHomeViewModel.modeRange.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_RANGE, Integer.class));
                updateUIRange();
                if (mHomeViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_AROUND) {
                    loadAllNewStoresOrProducts();
                }else{
                    getListStoresByKeywords();
                }
            }
            //TODO mode sort
            //mHomeViewModel.modeSort.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_SORT, Integer.class));
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Spanned textt = Html.fromHtml(
                            "<font color=\"" + getString(R.string.home_label_around_location_color) + "\"> " + getString(R.string.home_label_around_location) + " </font>"
                                    + StringUtils.toDistanceFormat(mHomeViewModel.modeRangeValue.getValue()));
                    mTxtState.setText(textt);
                    break;
                case 1:
                    Bundle bundle = msg.getData();
                    String locationAddress = bundle.getString(Contract.LOCATION_TO_ADDRESS_KEY);
                    String around = "<font color=\"" + getString(R.string.home_label_around_location_color) + "\"> " + getString(R.string.home_label_around_location) + " </font>";
                    Spanned text = Html.fromHtml(StringUtils.toDistanceFormat(mHomeViewModel.modeRangeValue.getValue())
                            + around
                            + locationAddress);

                    mTxtState.setText(text);
                    break;
            }

        }
    }


}
