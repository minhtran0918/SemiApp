package org.semi.views;

import android.app.ActivityOptions;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.semi.BookmarkFragment;
import org.semi.DiscoverFragment;
import org.semi.R;
import org.semi.SearchNewActivity;
import org.semi.ViewModel.HomeViewModel;
import org.semi.fragment.MeFragment;
import org.semi.HomeFragment;
import org.semi.utils.Contract;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView mNavigationHome;
    private View mRootView;

    private Toolbar mToolbarHome;

    private HomeViewModel mHomeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mRootView = findViewById(android.R.id.content);

        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        initView();

        //Config default
        switchFragment(R.id.nav_bot_item_home);
    }

    private void initView() {
        /*//Set Toolbar for ActionBar
        mToolbarHome = findViewById(R.id.toolbar_home);
        setSupportActionBar(mToolbarHome);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.toolbar_ic_semi_logo);
        actionBar.setDisplayShowTitleEnabled(false);*/

        //Bottom Navigation
        mNavigationHome = findViewById(R.id.nav_home);
        mNavigationHome.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Tranh viec tái tạo lại fragment
                if (menuItem.getItemId() == mNavigationHome.getSelectedItemId()) {
                    //TODO: chữ này để debug test, có thể xóa
                    Toast.makeText(HomeActivity.this, "trùng", Toast.LENGTH_SHORT).show();
                    return false;
                }
                switchFragment(menuItem.getItemId());
                return true;
            }
        });
    }

    //TODO create 3 fragment
    private void switchFragment(int itemIndex) {

        Fragment selectFragment = null;
        switch (itemIndex) {
            case R.id.nav_bot_item_home:
                selectFragment = HomeFragment.newInstance();
                break;
            case R.id.nav_bot_item_discover:
                selectFragment = DiscoverFragment.getInstance();
                break;
            case R.id.nav_bot_item_bookmark:
                //selectFragment = MyMapFragment.getInstance();
                selectFragment = new BookmarkFragment();
                break;
            case R.id.nav_bot_item_me:
                selectFragment = MeFragment.getInstance();
                break;
                /*Intent intent = new Intent(this, SearchNewActivity.class);
                startActivity(intent);
                return;*/

        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        //??? transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.home_fragment_container, selectFragment).commit();
    }

    //TODO Config menu search -> MenuSearchActivity
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int modeHome = mHomeViewModel.modeMenuNavigationHome.getValue();
        switch (modeHome) {
            case Contract.MODE_MENU_HOME:
                if (item.getItemId() == R.id.actionSearchItem) {
                    Toast.makeText(this, "Search Activity", Toast.LENGTH_SHORT).show();
                    launchSearchActivity();
                    return true;
                }
                break;
            case Contract.MODE_MENU_DISCOVER:
                break;
            case Contract.MODE_MENU_BOOKMARK:
                break;
            case Contract.MODE_MENU_ME:
                break;
        }
        return true;
    }*/

   /* private void launchSearchActivity() {
        //final int mode = searchModeSpinner.getSelectedItemPosition();
        int mode = mHomeViewModel.modeStoreOrProduct.getValue();
        final Intent intent = new Intent(this, SearchActivity.class);

        if (mode == Contract.STORE_MODE) {
            intent.putExtra(Contract.BUNDLE_MODE_KEY, Contract.STORE_MODE);
            intent.putExtra(Contract.BUNDLE_SEARCH_HINT_KEY, R.string.storeSearchHint);
            intent.putExtra(Contract.BUNDLE_ACTION_LOGO_KEY, R.drawable.ic_actionbar_shop);
            //Test
            intent.putExtra(Contract.BUNDLE_MODE_TYPE_KEY, mHomeViewModel.categoryStore.getValue());
        } else if (mode == Contract.PRODUCT_MODE) {
            intent.putExtra(Contract.BUNDLE_MODE_KEY, Contract.PRODUCT_MODE);
            intent.putExtra(Contract.BUNDLE_SEARCH_HINT_KEY, R.string.productSearchHint);
            intent.putExtra(Contract.BUNDLE_ACTION_LOGO_KEY, R.drawable.ic_actionbar_product);
            //Test
            intent.putExtra(Contract.BUNDLE_MODE_TYPE_KEY, mHomeViewModel.categoryProduct.getValue());
        } else {
            return;
        }
        final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                this, mToolbarHome, "Toolbar"
        );
        startActivity(intent, options.toBundle());
    }*/
}

