package org.semi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.semi.ViewModel.HomeViewModel;
import org.semi.ViewModel.SearchViewModel;
import org.semi.adapter.HomeListAdapter;
import org.semi.adapter.LocationToAddress;
import org.semi.databases.SharedPrefs;
import org.semi.firebase.ProductConnector;
import org.semi.firebase.StoreConnector;
import org.semi.utils.Contract;
import org.semi.utils.LocationUtils;
import org.semi.utils.StringUtils;

import java.util.List;

import static org.semi.utils.MyApp.getContext;

public class SearchNewActivity extends AppCompatActivity {

    private static final int RC_SELECT_CATEGORY = 1234;
    private static final int RC_SELECT_OPTION = 4321;
    //Firebase connector
    private StoreConnector storeConnector;
    private ProductConnector productConnector;
    //Location
    private org.semi.object.Location currentLocation;
    private LocationUtils locationUtils;
    private SearchViewModel mSearchViewModel;
    private HomeListAdapter mHomeRcvAdapter;
    private boolean mShouldLoadMoreData;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private NestedScrollView mNestedScrollView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_new);
        initView();
        initViewModel();
    }


    private void initView() {
        ConstraintLayout option_category, option_filter;
        option_category = findViewById(R.id.search_ic_option_category);
        option_filter = findViewById(R.id.search_ic_option_filter);
        option_category.setOnClickListener(mOnClickOption);
        option_filter.setOnClickListener(mOnClickOption);

        //Data
        storeConnector = StoreConnector.getInstance();
        productConnector = ProductConnector.getInstance();

        locationUtils = new LocationUtils();

        //RecyclerView & Adapter
        mRecyclerView = findViewById(R.id.recycler_view_search_list);
        mHomeRcvAdapter = new HomeListAdapter();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mHomeRcvAdapter);

        mNestedScrollView = findViewById(R.id.nested_scroll_search);
        mSwipeRefreshLayout = findViewById(R.id.recycler_view_search_swipe_refresh);
    }

    private void initViewModel() {
    //ViewModel
        mSearchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        mSearchViewModel.modeStoreOrProduct.observe(this, integer -> {
            SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT, integer);
            updateUI(mSearchViewModel.categoryStore.getValue(), mSearchViewModel.categoryProduct.getValue());
        });
        mSearchViewModel.listStore.observe(this, stores ->
                mHomeRcvAdapter.setDataSetStore(stores, mSearchViewModel.currentLocation.getValue()));

        mSearchViewModel.listProduct.observe(this, products ->
                mHomeRcvAdapter.setDataSetProduct(products, mSearchViewModel.currentLocation.getValue()));

        mSearchViewModel.categoryStore.observe(this, integer -> {
            updateUI(integer, Contract.ALL_NOT_AVAILABLE);
        });

        mSearchViewModel.categoryProduct.observe(this, integer -> {
            updateUI(Contract.ALL_NOT_AVAILABLE, integer);
        });
        /*
        mSearchViewModel.modeRangeValue.observe(this, aFloat -> {
            updateUIRange();
            loadAllNewStoresOrProducts();
        });*/
        //Listener
        View.OnClickListener mOnClickListenerRcv = v -> {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            Toast.makeText(this, "position: " + position, Toast.LENGTH_SHORT).show();
            if (mSearchViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_STORE) {
                Toast.makeText(this, "load sTore", Toast.LENGTH_SHORT).show();
                showStoreDetail(mSearchViewModel.listStore.getValue().get(position));
            } else {
                Toast.makeText(this, "load product", Toast.LENGTH_SHORT).show();
                showProductDetail(mSearchViewModel.listProduct.getValue().get(position));
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
                            if (mSearchViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_STORE) {
                                int size = mHomeRcvAdapter.getItemCount();
                                if (size != 0) {
                                    if (mSearchViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_AROUND) {
                                        loadStoresAt(mHomeRcvAdapter.getItemCount());
                                    } else {
                                        getMoreListStoresByKeywordsAt(mHomeRcvAdapter.getItemCount());
                                    }

                                }
                            } else {
                                int size = mHomeRcvAdapter.getItemCount();
                                if (size != 0) {
                                    if (mSearchViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_AROUND) {
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
        if (mSearchViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_STORE) {
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

        /*if (mSearchViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_AROUND) {
            mTxtSortState.setText("Gần nhất");
            mTxtLocateState.setText(getString(R.string.home_lbl_option_around));
            if (currentLocation != null) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                //LocationToAddress.getAddressFromLocation(latitude, longitude, this, new SearchNewActivity().GeocoderHandler());
            } else {
                Spanned text = Html.fromHtml(
                        "<font color=\"" + getString(R.string.home_label_around_location_color) + "\"> " + getString(R.string.home_label_around_nolocation) + " </font>"
                                + StringUtils.toDistanceFormat(mSearchViewModel.modeRangeValue.getValue()));
                mTxtState.setText(text);
            }
        } else {
            mTxtState.setText("Tất cả " + (mHomeViewModel.modeStoreOrProduct.getValue() == Contract.MODE_HOME_LOAD_STORE ? getString(R.string.home_lbl_store_state) : getString(R.string.home_lbl_product_state)));
            mTxtLocateState.setText(mHomeViewModel.getDistrictByID(mHomeViewModel.districtId.getValue()).toLiteName());
            mTxtSortState.setText("Tất cả");
        }*/
    }
    private View.OnClickListener mOnClickOption = v ->{
        Intent callActivity;
        switch (v.getId()) {
            case R.id.search_ic_option_category:
                SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT, mSearchViewModel.modeStoreOrProduct.getValue());
                SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_CATEGORY_STORE, mSearchViewModel.categoryStore.getValue());
                SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_CATEGORY_PRODUCT, mSearchViewModel.categoryProduct.getValue());
                callActivity = new Intent(this, HomeSelectCategoryActivity.class);
                startActivityForResult(callActivity, RC_SELECT_CATEGORY);
                break;
            case R.id.search_ic_option_filter:
                callActivity = new Intent(this, HomeSelectOptionActivity.class);
                startActivityForResult(callActivity, RC_SELECT_OPTION);
                break;
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

}
