package org.semi;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;

import org.semi.contract.Contract;
import org.semi.custom.MyFragmentAdapter;
import org.semi.custom.NonScrollingViewPager;
import org.semi.fragment.IInteractionWithList;
import org.semi.fragment.ISearch;
import org.semi.fragment.IUseFragment;
import org.semi.fragment.MyMapFragment;
import org.semi.fragment.ProductSearchFragment;
import org.semi.fragment.StoreSearchFragment;
import org.semi.minh.StoreDetailActivity;
import org.semi.object.IHaveIdAndName;
import org.semi.object.Product;
import org.semi.object.Store;
import org.semi.util.LocationUtils;
import org.semi.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements IUseFragment,
        IInteractionWithList<IHaveIdAndName<String>> {
    private int mode;
    private int typeId;
    private TabLayout tabLayout;
    private NonScrollingViewPager viewPager;
    private MyFragmentAdapter fragmentAdapter;
    //init hear because onFragmentAttached was called before onCreate
    private List<ISearch> fragments = new ArrayList<>(2);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final Intent intent = getIntent();
        setupActionBar(intent);
        mode = intent.getIntExtra(Contract.BUNDLE_MODE_KEY, -1);
        typeId = intent.getIntExtra(Contract.BUNDLE_MODE_TYPE_KEY, -1);
        //TabLayout, ViewPager
        tabLayout = findViewById(R.id.tabLayout);
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        checkAndRequestPermission();
    }


    private void setupActionBar(Intent intent) {
        final int logoResource = intent.getIntExtra(Contract.BUNDLE_ACTION_LOGO_KEY, -1);
        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(logoResource);
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPaper);
        viewPager.setPagingEnabled(false);
        fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager());
        Fragment searchFragment = null;
        if(mode == Contract.STORE_MODE) {
            searchFragment = new StoreSearchFragment();
        } else if(mode == Contract.PRODUCT_MODE) {
            searchFragment = new ProductSearchFragment();
        }
        MyMapFragment mapFragment = new MyMapFragment();
        fragmentAdapter.addFragment(searchFragment, getString(R.string.searchTabText))
                .addFragment(mapFragment, getString(R.string.nearbyTabText));
        viewPager.setAdapter(fragmentAdapter);
    }

    private void setupSearchView(Menu menu) {
        final Intent intent = getIntent();
        final int hintResource = intent.getIntExtra(Contract.BUNDLE_SEARCH_HINT_KEY, -1);
        final MenuItem searchItem = menu.findItem(R.id.actionSearchItem);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(hintResource));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                int currentItem = viewPager.getCurrentItem();
                fragments.get(currentItem).search(typeId, StringUtils.normalize(s), mode);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
        //expand SearchView first time
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() { searchItem.expandActionView(); }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        setupSearchView(menu);
        return true;
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int request, int result, Intent intent) {
        super.onActivityResult(request, result, intent);
        if(result == Activity.RESULT_OK) {
            if(request == LocationUtils.RESOLUTION_CODE) { }
        }
    }

    private void checkAndRequestPermission() {
        if(LocationUtils.checkAndRequestPermission(this)) {
            onPermissionGranted();
        }
    }

    private void onPermissionGranted() {
        LocationUtils.checkAndRequestLocationSettings(this, null);
    }

    @Override
    public void onFragmentAttached(Fragment fragment) {
        if(fragment instanceof ISearch) {
            fragments.add((ISearch) fragment);
        }
    }

    @Override
    public void onItemClick(IHaveIdAndName<String> obj) {
        final int currentItem = viewPager.getCurrentItem();
        fragments.get(currentItem).clickItem(obj.getId());
        if(mode == Contract.STORE_MODE) {
            showStoreDetail((Store)obj);
        } else if(mode == Contract.PRODUCT_MODE) {
            showProductDetail((Product)obj);
        }
    }

    private void showStoreDetail(Store store) {
        Intent storeIntent = new Intent(this, StoreDetailActivity.class);
        storeIntent.putExtra(Contract.BUNDLE_STORE_KEY, store.getId());
        startActivity(storeIntent);
    }

    private void showProductDetail(Product product) {
        Intent storeIntent = new Intent(this, StoreDetailActivity.class);
        storeIntent.putExtra(Contract.BUNDLE_STORE_KEY, product.getStore().getId());
        storeIntent.putExtra(Contract.BUNDLE_PRODUCT_KEY, product.getId());
        startActivity(storeIntent);
    }

    @Override
    public void onScrollToLimit(IHaveIdAndName<String> obj, int position) {
        final int currentItem = viewPager.getCurrentItem();
        fragments.get(currentItem).scroll(obj.getId());
    }
}
