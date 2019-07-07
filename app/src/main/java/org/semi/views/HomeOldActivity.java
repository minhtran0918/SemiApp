package org.semi.views;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.semi.utils.Contract;
import org.semi.R;
import org.semi.firebase.IResult;
import org.semi.firebase.ProductConnector;
import org.semi.firebase.StoreConnector;
import org.semi.fragment.ErrorFragment;
import org.semi.fragment.FragmentCreator;
import org.semi.fragment.IInteractionWithList;
import org.semi.fragment.ProductViewFragment;
import org.semi.fragment.StoreViewFragment;
import org.semi.listener.ModeSpinnerItemSelectedListener;
import org.semi.object.IHaveIdAndName;
import org.semi.object.Product;
import org.semi.object.Store;
import org.semi.utils.LocationUtils;
import org.semi.utils.ObjectUtils;

import java.util.List;

public class HomeOldActivity extends AppCompatActivity implements IInteractionWithList<IHaveIdAndName<String>> {
    private static final int STORE_VIEW = 0;
    private static final int PRODUCT_VIEW = 1;
    private static final int NETWORK_ERR_VIEW = 2;
    private static final int EMPTY_ERR_VIEW = 3;
    private static final int LOCATION_ERR_VIEW = 4;
    private static final int LOAD_VIEW = 5;
    //View
    private AppCompatSpinner searchModeSpinner;
    private AppCompatSpinner typeSpinner;
    private ModeSpinnerItemSelectedListener modeListener;
    //this Fragment's desired to be attached to Activity, but not sure it's attached
    private TextView nearbyTextView;
    private Toolbar toolbar;
    private FragmentCreator fragmentCreator;
    //Firebase connector
    private StoreConnector storeConnector;
    private ProductConnector productConnector;
    //Location
    private org.semi.object.Location currentLocation;
    private LocationUtils locationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_old);
        //Set Toolbar for ActionBar
        setupActionBar();
        //init
        searchModeSpinner = findViewById(R.id.searchModeSpinner);
        typeSpinner = findViewById(R.id.typesSpinner);
        nearbyTextView = findViewById(R.id.nearbyTextView);
        modeListener = new ModeSpinnerItemSelectedListener(this);

        storeConnector = StoreConnector.getInstance();
        productConnector = ProductConnector.getInstance();

        locationUtils = new LocationUtils();
        setupFragmentCreator();
        /*
        //init bottom navigation bar
        mBottomNavigationView = findViewById(R.id.nav_bot);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_bot_item_bookmark:
                        Intent actionToBookmarkActivity = new Intent(HomeOldActivity.this, BookmarkActivity.class);
                        startActivity(actionToBookmarkActivity);
                        return true;
                }
                return false;
            }
        });*/
        //event, permission, config
        searchModeSpinner.setOnItemSelectedListener(modeListener);
        //typeSpinner.setOnItemSelectedListener(new TypeSpinnerItemSelectedListener(this));
        //nearbyTextView.setOnClickListener(new NearbyTextViewClickListener(this));

        checkAndRequestPermission();
    }

    private void setupFragmentCreator() {
        final Bundle networkError = new Bundle();
        final Bundle emptyError = new Bundle();
        final Bundle locationError = new Bundle();
        final Bundle loading = new Bundle();
        networkError.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_trees);
        networkError.putString(ErrorFragment.MESSAGE, getString(R.string.errorNetworkMessage));
        emptyError.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_blank);
        emptyError.putString(ErrorFragment.MESSAGE, getString(R.string.errorEmptyResultMessage));
        locationError.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_desert);
        locationError.putString(ErrorFragment.MESSAGE, getString(R.string.errorLocationMessage));
        loading.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_beach);
        loading.putString(ErrorFragment.MESSAGE, getString(R.string.loadMessage));
        fragmentCreator = new FragmentCreator(R.id.homeFragmentContainer,
                getSupportFragmentManager());
        fragmentCreator.add(StoreViewFragment.class, (Bundle) null)
                .add(ProductViewFragment.class, (Bundle) null)
                .add(ErrorFragment.class, networkError)
                .add(ErrorFragment.class, emptyError)
                .add(ErrorFragment.class, locationError)
                .add(ErrorFragment.class, loading);
    }

    private void setupActionBar() {
        toolbar = findViewById(R.id.toolbar_home_old);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_actionbar_logo);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public AppCompatSpinner getModeSpinner() {
        return searchModeSpinner;
    }

    public AppCompatSpinner getTypeSpinner() {
        return typeSpinner;
    }

    public TextView getNearbyTextView() {
        return nearbyTextView;
    }

    private void checkAndRequestPermission() {
        if (LocationUtils.checkAndRequestPermission(this)) {
            onPermissionGranted();
        }
    }

    private void onPermissionGranted() {
        LocationUtils.checkAndRequestLocationSettings(this, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LocationUtils.REQUEST_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        if (result == RESULT_OK) {
            if (requestCode == LocationUtils.RESOLUTION_CODE) {
                //Settings OK, load stores
                loadFirst();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(final IResult<Location> result) {
        final Task<Location> task = LocationUtils.getLastLocation();
        task.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) {
                    fragmentCreator.setCurrentFragment(LOCATION_ERR_VIEW);
                    return;
                }
                currentLocation = ObjectUtils.toMyLocation(location);
                result.onResult(location);
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fragmentCreator.setCurrentFragment(LOCATION_ERR_VIEW);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void loadFirst() {
        locationUtils.requestLocationUpdates(new IResult<Location>() {
            @Override
            public void onResult(Location result) {
                if (result != null) {
                    locationUtils.removeLocationUpdates();
                    currentLocation = ObjectUtils.toMyLocation(result);
                    final int modePosition = searchModeSpinner.getSelectedItemPosition();
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
    }

    //load from offset 0,discard old data in RecyclerView and load new data to it.
    public void loadAllNewStores() {
        if (currentLocation == null) {
            fragmentCreator.setCurrentFragment(LOCATION_ERR_VIEW);
            return;
        }
        if (searchModeSpinner.getSelectedItemPosition()
                != Contract.STORE_MODE) {
            return;
        }
        final IHaveIdAndName<Integer> selectedItem = (IHaveIdAndName<Integer>) typeSpinner.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        fragmentCreator.setCurrentFragment(LOAD_VIEW);
        storeConnector.getNearbyStores(currentLocation, 0, 0.5f, Contract.NUM_STORES_PER_REQUEST, selectedItem.getId(),
                new IResult<List<Store>>() {
                    @Override
                    public void onResult(List<Store> result) {
                        //when network error, or some error occur, currentFragment is replaced with ErrorFragment
                        //we must assign currentFragment to appropriate fragment
                        if (searchModeSpinner.getSelectedItemPosition()
                                != Contract.STORE_MODE) {
                            return;
                        }
                        if (result.size() == 0) {
                            fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        StoreViewFragment fragment = (StoreViewFragment)
                                fragmentCreator.setCurrentFragmentNoArgs(STORE_VIEW);
                        fragment.updateDataSet(result, currentLocation);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        fragmentCreator.setCurrentFragment(NETWORK_ERR_VIEW);
                    }
                });
    }

    //load from offset 0,discard old data in RecyclerView and load new data to it.
    public void loadAllNewProducts() {
        if (currentLocation == null) {
            fragmentCreator.setCurrentFragment(LOCATION_ERR_VIEW);
            return;
        }
        if (searchModeSpinner.getSelectedItemPosition()
                != Contract.PRODUCT_MODE) {
            return;
        }
        final IHaveIdAndName<Integer> selectedItem = (IHaveIdAndName<Integer>) typeSpinner.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        fragmentCreator.setCurrentFragment(LOAD_VIEW);
        productConnector.getNearbyProducts(currentLocation, 0,0.5f, Contract.NUM_PRODUCTS_PER_REQUEST, selectedItem.getId(),
                new IResult<List<Product>>() {
                    @Override
                    public void onResult(List<Product> result) {
                        //when network error, or some error occur, currentFragment is replaced with ErrorFragment
                        //we must assign currentFragment to appropriate fragment
                        if (searchModeSpinner.getSelectedItemPosition()
                                != Contract.PRODUCT_MODE) {
                            return;
                        }
                        if (result.size() == 0) {
                            fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        ProductViewFragment fragment = (ProductViewFragment)
                                fragmentCreator.setCurrentFragmentNoArgs(PRODUCT_VIEW);
                        fragment.updateDataSet(result, currentLocation);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        fragmentCreator.setCurrentFragment(NETWORK_ERR_VIEW);
                    }
                });
    }

    private void loadProductsAt(int index) {
        if (currentLocation == null) {
            return;
        }
        if (searchModeSpinner.getSelectedItemPosition()
                != Contract.PRODUCT_MODE) {
            return;
        }
        final IHaveIdAndName<Integer> selectedItem = (IHaveIdAndName<Integer>) typeSpinner.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        productConnector.getNearbyProducts(currentLocation, index,0.5f, Contract.NUM_PRODUCTS_PER_REQUEST, selectedItem.getId(),
                new IResult<List<Product>>() {
                    @Override
                    public void onResult(List<Product> result) {
                        //when network error, or some error occur, currentFragment is replaced with ErrorFragment
                        //we must assign currentFragment to appropriate fragment
                        if (searchModeSpinner.getSelectedItemPosition()
                                != Contract.PRODUCT_MODE) {
                            return;
                        }
                        Fragment tmpFragment = fragmentCreator.getCurrentFragment();
                        if (!(tmpFragment instanceof ProductViewFragment)) {
                            return;
                        }
                        ProductViewFragment fragment = (ProductViewFragment) tmpFragment;
                        if (result.size() == 0 && fragment.getItemCount() == 0) {
                            fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        fragment.addDataSet(result, currentLocation);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exp) {
                    }
                });
    }

    private void loadStoresAt(int position) {
        if (currentLocation == null) {
            return;
        }
        if (searchModeSpinner.getSelectedItemPosition()
                != Contract.STORE_MODE) {
            return;
        }
        final IHaveIdAndName<Integer> selectedItem = (IHaveIdAndName<Integer>) typeSpinner.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        storeConnector.getNearbyStores(currentLocation, position, 0.5f, Contract.NUM_STORES_PER_REQUEST, selectedItem.getId(),
                new IResult<List<Store>>() {
                    @Override
                    public void onResult(List<Store> result) {
                        //when network error, or some error occur, currentFragment is replaced with ErrorFragment
                        //we must assign currentFragment to appropriate fragment
                        if (searchModeSpinner.getSelectedItemPosition()
                                != Contract.STORE_MODE) {
                            return;
                        }
                        Fragment tmpFragment = fragmentCreator.getCurrentFragment();
                        if (!(tmpFragment instanceof StoreViewFragment)) {
                            return;
                        }
                        StoreViewFragment fragment = (StoreViewFragment) tmpFragment;
                        if (result.size() == 0 && fragment.getItemCount() == 0) {
                            fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        fragment.addDataSet(result, currentLocation);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exp) {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSearchItem:
                launchSearchActivity();
                break;
        }
        return true;
    }

    private void launchSearchActivity() {
        final int mode = searchModeSpinner.getSelectedItemPosition();
        final Intent intent = new Intent(this, SearchActivity.class);
        final IHaveIdAndName<Integer> selectedItem = ((IHaveIdAndName<Integer>) typeSpinner.getSelectedItem());
        if (selectedItem == null) {
            return;
        }
        if (mode == Contract.STORE_MODE) {
            intent.putExtra(Contract.BUNDLE_MODE_KEY, Contract.STORE_MODE);
            intent.putExtra(Contract.BUNDLE_SEARCH_HINT_KEY, R.string.storeSearchHint);
            intent.putExtra(Contract.BUNDLE_ACTION_LOGO_KEY, R.drawable.ic_actionbar_shop);
            intent.putExtra(Contract.BUNDLE_MODE_TYPE_KEY, selectedItem.getId());
        } else if (mode == Contract.PRODUCT_MODE) {
            intent.putExtra(Contract.BUNDLE_MODE_KEY, Contract.PRODUCT_MODE);
            intent.putExtra(Contract.BUNDLE_SEARCH_HINT_KEY, R.string.productSearchHint);
            intent.putExtra(Contract.BUNDLE_ACTION_LOGO_KEY, R.drawable.ic_actionbar_product);
            intent.putExtra(Contract.BUNDLE_MODE_TYPE_KEY, selectedItem.getId());
        } else {
            return;
        }
        final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                this, toolbar, "Toolbar"
        );
        startActivity(intent, options.toBundle());
    }

    public void loadAllNewStoresOrProducts() {
        getLastLocation(new IResult<Location>() {
            @Override
            public void onResult(@NonNull Location location) {
                if (getModeSpinner().getSelectedItemPosition() == 0) { //it's store
                    loadAllNewStores();
                } else { //it's Product
                    loadAllNewProducts();
                }
            }

            @Override
            public void onFailure(@NonNull Exception exp) {
                fragmentCreator.setCurrentFragment(LOCATION_ERR_VIEW);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragmentCreator.recovery();
    }

    @Override
    public void onItemClick(IHaveIdAndName<String> obj) {
        if (obj instanceof Store) {
            showStoreDetail((Store) obj);
        } else if (obj instanceof Product) {
            showProductDetail((Product) obj);
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
        if (obj instanceof Store) {
            loadStoresAt(position + 1);
        } else if (obj instanceof Product) {
            loadProductsAt(position + 1);
        }
    }
}
