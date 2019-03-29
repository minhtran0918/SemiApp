package org.semi.minh;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.semi.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minh_activity_main);
        BottomNavigationView nav_bot = findViewById(R.id.nav_bot);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        nav_bot.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectFragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.nav_bot_item_home:
                        selectFragment = new HomeFragment();
                        break;
                    case R.id.nav_bot_item_discover:
                        selectFragment = new DiscoverFragment();
                        break;
                    case R.id.nav_bot_item_bookmark:
                        Intent toStore = new Intent(MainActivity.this, StoreDetailActivity.class);
                        startActivity(toStore);
                        return true;
                    case R.id.nav_bot_item_me:
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectFragment).commit();
                return true;
            }
        });
    }
}
