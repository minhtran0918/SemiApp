package org.semi;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.semi.ViewModel.HomeViewModel;
import org.semi.adapter.HomeListAdapter;
import org.semi.adapter.LocationToAddress;
import org.semi.databases.SharedPrefs;
import org.semi.firebase.IResult;
import org.semi.firebase.ProductConnector;
import org.semi.firebase.StoreConnector;
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
    private CardView mLayoutDirect;
    private TextView mTxtState, mTxtCategoryState, mTxtLocateState, mTxtSortState;

    private NestedScrollView mNestedScrollView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    //private StoreAdapter mRcvAdapter;
    private HomeListAdapter mHomeRcvAdapter;
    private boolean mShouldLoadMoreData;

    //private IInteractionWithList<IHaveIdAndName<String>> listener;

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int RC_SELECT_CATEGORY = 11;
    private int RC_SELECT_OPTION = 22;

    private String mQueryKeyword = "";

    public static HomeFragment newInstance() {
        if (sHomeFragment == null) {
            sHomeFragment = new HomeFragment();
        }
        return sHomeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        //loadAllNewStoresOrProducts();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.home_action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(mSearchViewTextListener);
        searchView.setQueryHint("Tìm ");
        /*searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        searchView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
        });*/
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initView() {
        //Toolbar
        //Button Direction
        mLayoutDirect = mRootView.findViewById(R.id.layout_home_direct);
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
                mHomeRcvAdapter.setDataSetStore(stores, mHomeViewModel.currentLocation.getValue()));

        mHomeViewModel.listProduct.observe(this, products ->
                mHomeRcvAdapter.setDataSetProduct(products, mHomeViewModel.currentLocation.getValue()));

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
            //Toast.makeText(getActivity(), "position: " + position, Toast.LENGTH_SHORT).show();
            if (mHomeViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_STORE) {
                //Toast.makeText(getActivity(), "load sTore", Toast.LENGTH_SHORT).show();
                showStoreDetail(mHomeViewModel.listStore.getValue().get(position));
            } else {
                //Toast.makeText(getActivity(), "load product", Toast.LENGTH_SHORT).show();
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
                                    if (mHomeViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_AROUND) {
                                        loadStoresAt(mHomeRcvAdapter.getItemCount());
                                    } else {
                                        getMoreListStoresByKeywordsAt(mHomeRcvAdapter.getItemCount());
                                    }

                                }
                            } else {
                                int size = mHomeRcvAdapter.getItemCount();
                                if (size != 0) {
                                    if (mHomeViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_AROUND) {
                                        loadProductsAt(mHomeRcvAdapter.getItemCount());
                                    } else {
                                        getMoreListProductByKeywordsAt(mHomeRcvAdapter.getItemCount());
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
                        mTxtCategoryState.setText(getString(R.string.home_select_category_title_all));
                        break;
                }
                loadAllNewStoresOrProducts();
            }
        } else {
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

    private SearchView.OnQueryTextListener mSearchViewTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            //Toast.makeText(getActivity(), "Submit", Toast.LENGTH_SHORT).show();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mRootView.getWindowToken(), 0);
            if (s != "") {
                mQueryKeyword = s;
                if (mHomeViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_ALL) {
                    loadAllNewStoresOrProducts();
                } else {
                    /*if (mHomeViewModel == Contract.STORE_MODE) {
                        mStoreConnector.getNearbyStoresByKeywords(mSearchBox.getLocationCenter(), mListMarkers.size(),
                                NUM_STORES_PER_REQUEST, mProductOrStoretype, mStrQuery, mSearchBox.getDimen(), result);
                    } else if (mMode == Contract.PRODUCT_MODE) {
                        mStoreConnector.getNearbyStoresByProducts(mSearchBox.getLocationCenter(), mListMarkers.size(),
                                NUM_STORES_PER_REQUEST, mProductOrStoretype, mStrQuery, mSearchBox.getDimen(), result);
                    }*/
                    Toast.makeText(getActivity(), "Chỉ tìm kiếm được trên 1 khu vực nhất định", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
            /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (s == null || s.trim().isEmpty()) {
                //Toast.makeText(getActivity(), "Query nothing", Toast.LENGTH_SHORT).show();
                mQueryKeyword = "";
                loadAllNewProducts();
                mLayoutDirect.setVisibility(View.VISIBLE);
                return false;
            }
            mLayoutDirect.setVisibility(View.GONE);
            return true;
        }
    };

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
        /*if(LocationUtils.checkAndRequestPermission(getActivity())) {
            onPermissionGranted();
            loadAllNewStoresOrProducts();
        }*/
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
                        Toast.makeText(getActivity(), "Vui lòng cấp quyền để xử dụng tính năng này", Toast.LENGTH_SHORT).show();
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getActivity(), "Lỗi khi yêu cầu quyền ", Toast.LENGTH_SHORT).show();
                        //loadAllNewStoresOrProducts();
                    }
                });
    }

    private void onPermissionGranted() {
        LocationUtils.checkAndRequestLocationSettings(getActivity(), null);
    }

    private void loadAllNewStoresOrProducts() {
        if (mHomeViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_ALL) {
            loadAllNewsStoreOrProductByKey(false);
            return;
        }
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
                if (mHomeViewModel.currentLocation.getValue() == null) {
                    DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorLocationMessage));
                } else {
                    int mode_load = mHomeViewModel.modeStoreOrProduct.getValue();
                    if (mode_load == Contract.MODE_HOME_LOAD_STORE) { //it's store
                        loadAllNewStores();
                    } else if (mode_load == Contract.MODE_HOME_LOAD_PRODUCT) { //it's Product
                        loadAllNewProducts();
                    }
                }

            }
        });
    }

    private void loadAllNewsStoreOrProductByKey(boolean isGetNear) {
        mSwipeRefreshLayout.setRefreshing(true);
        mShouldLoadMoreData = true;
        int mode_load = mHomeViewModel.modeStoreOrProduct.getValue();
        if (isGetNear) {
            /*if (mode_load == Contract.MODE_HOME_LOAD_STORE) { //it's store
                getListNearStoresByKeywords();
            } else if (mode_load == Contract.MODE_HOME_LOAD_PRODUCT) { //it's Product
                getListNearProductByKeywords();
            }*/
        } else {
            if (mode_load == Contract.MODE_HOME_LOAD_STORE) { //it's store
                getListStoresByKeywords();
            } else if (mode_load == Contract.MODE_HOME_LOAD_PRODUCT) { //it's Product
                getListProductByKeywords();
            }
        }

    }

    /*private void getListNearStoresByKeywords() {
        mSwipeRefreshLayout.setRefreshing(true);
        mShouldLoadMoreData = true;
        final StoreConnector storeConnector = StoreConnector.getInstance();
        Object[] address = new Object[4];
        address[0] = 0; //Country vietnam
        address[1] = mHomeViewModel.cityId.getValue();
        address[2] = mHomeViewModel.districtId.getValue();
        address[3] = mHomeViewModel.wardId.getValue();
        Log.d("Semi", "Call all: mode " + mHomeViewModel.modeStoreOrProduct.getValue());
        Log.d("Semi", "Call all: mode range " + mHomeViewModel.modeRange.getValue());
        Log.d("Semi", "Call all: mode value " + mHomeViewModel.modeRange.getValue());
        Log.d("Semi", "Call all: mode sort " + mHomeViewModel.modeSort.getValue());
        Log.d("Semi", "Call all: category store " + mHomeViewModel.categoryStore.getValue());
        Log.d("Semi", "Call all: category product " + mHomeViewModel.categoryProduct.getValue());
        Log.d("Semi", "Call all address: city " + mHomeViewModel.cityId.getValue() + " district: " + mHomeViewModel.districtId.getValue() + " ward: " + mHomeViewModel.wardId.getValue());

        storeConnector.getNearbyStoresByKeywords(mHomeViewModel.currentLocation.getValue(),mHomeViewModel.listStore.getValue().size(),
                Contract.NUM_STORES_PER_REQUEST, mHomeViewModel.categoryStore.getValue(), mQueryKeyword, mSearchBox.getDimen(), result);
        //String key = StringUtils.normalize("Tap hoa");
        storeConnector.getStoresByKeywords(mHomeViewModel.categoryStore.getValue(), mQueryKeyword, "", Contract.NUM_STORES_PER_REQUEST,
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
    }*/

    private void getListNearProductByKeywords() {
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

    private void getListStoresByKeywords() {
        mSwipeRefreshLayout.setRefreshing(true);
        mShouldLoadMoreData = true;
        final StoreConnector storeConnector = StoreConnector.getInstance();
        Object[] address = new Object[4];
        address[0] = 0; //Country vietnam
        address[1] = mHomeViewModel.cityId.getValue();
        address[2] = mHomeViewModel.districtId.getValue();
        address[3] = mHomeViewModel.wardId.getValue();
        Log.d("Semi", "Call all: mode " + mHomeViewModel.modeStoreOrProduct.getValue());
        Log.d("Semi", "Call all: mode range " + mHomeViewModel.modeRange.getValue());
        Log.d("Semi", "Call all: mode value " + mHomeViewModel.modeRange.getValue());
        Log.d("Semi", "Call all: mode sort " + mHomeViewModel.modeSort.getValue());
        Log.d("Semi", "Call all: category store " + mHomeViewModel.categoryStore.getValue());
        Log.d("Semi", "Call all: category product " + mHomeViewModel.categoryProduct.getValue());
        Log.d("Semi", "Call all address: city " + mHomeViewModel.cityId.getValue() + " district: " + mHomeViewModel.districtId.getValue() + " ward: " + mHomeViewModel.wardId.getValue());

        //String key = StringUtils.normalize("Tap hoa");
        storeConnector.getStoresByKeywords(mHomeViewModel.categoryStore.getValue(), mQueryKeyword, "", Contract.NUM_STORES_PER_REQUEST,
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

    private void getMoreListStoresByKeywordsAt(int lastPos) {
        mSwipeRefreshLayout.setRefreshing(true);
        final StoreConnector storeConnector = StoreConnector.getInstance();
        Object[] address = new Object[4];
        address[0] = 0; //Country vietnam
        address[1] = mHomeViewModel.cityId.getValue();
        address[2] = mHomeViewModel.districtId.getValue();
        address[3] = mHomeViewModel.wardId.getValue();
        storeConnector.getStoresByKeywords(mHomeViewModel.categoryStore.getValue(), mQueryKeyword, String.valueOf(lastPos), Contract.NUM_STORES_PER_REQUEST,
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

    private void getListProductByKeywords() {
        mSwipeRefreshLayout.setRefreshing(true);
        mShouldLoadMoreData = true;
        final ProductConnector productConnector = ProductConnector.getInstance();

        Object[] address = new Object[4];
        address[0] = 0; //Country vietnam
        address[1] = mHomeViewModel.cityId.getValue();
        address[2] = mHomeViewModel.districtId.getValue();
        address[3] = mHomeViewModel.wardId.getValue();
        Log.d("Semi", "Call all: mode " + mHomeViewModel.modeStoreOrProduct.getValue());
        Log.d("Semi", "Call all: mode range " + mHomeViewModel.modeRange.getValue());
        Log.d("Semi", "Call all: mode value " + mHomeViewModel.modeRange.getValue());
        Log.d("Semi", "Call all: mode sort " + mHomeViewModel.modeSort.getValue());
        Log.d("Semi", "Call all: category store " + mHomeViewModel.categoryStore.getValue());
        Log.d("Semi", "Call all: category product " + mHomeViewModel.categoryProduct.getValue());
        Log.d("Semi", "Call all address: city " + mHomeViewModel.cityId.getValue() + " district: " + mHomeViewModel.districtId.getValue() + " ward: " + mHomeViewModel.wardId.getValue());
        //mHomeViewModel.categoryProduct.getValue()
        productConnector.getProductsByKeywords(Contract.MODE_HOME_LOAD_PRODUCT_TYPE_ALL, mQueryKeyword, "", Contract.NUM_PRODUCTS_PER_REQUEST,
                address,
                new IResult<List<Product>>() {
                    @Override
                    public void onResult(List<Product> result) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (result.size() == 0) {
                            //Empty Error
                            //Clear RecyclerView
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
                        Log.d("Semi", "exp " + exp.getMessage());
                        //Network Error
                        DialogUtils.showSnackBar(null, mRootView, getString(R.string.errorNetworkMessage));
                    }
                });
    }

    private void getMoreListProductByKeywordsAt(int lastPos) {
        mSwipeRefreshLayout.setRefreshing(true);
        final ProductConnector productConnector = ProductConnector.getInstance();
        Object[] address = new Object[4];
        address[0] = 0; //Country vietnam
        address[1] = mHomeViewModel.cityId.getValue();
        address[2] = mHomeViewModel.districtId.getValue();
        address[3] = mHomeViewModel.wardId.getValue();
        productConnector.getProductsByKeywords(
                mHomeViewModel.categoryProduct.getValue(),
                mQueryKeyword,
                String.valueOf(lastPos),
                Contract.NUM_PRODUCTS_PER_REQUEST,
                address,
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
                        Log.d("Semi", "exp " + exp.getMessage());
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
                        loadAllNewStoresOrProducts();
                    }
                }
            } else {
                mHomeViewModel.modeRange.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_RANGE, Integer.class));
                updateUIRange();
                loadAllNewStoresOrProducts();
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
