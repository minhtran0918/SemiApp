package org.semi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.lifecycle.MutableLiveData;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.semi.databases.SharedPrefs;
import org.semi.utils.Contract;

import static org.semi.databases.SharedPrefs.KEY_OPTION_CATEGORY_PRODUCT;
import static org.semi.databases.SharedPrefs.KEY_OPTION_CATEGORY_STORE;
import static org.semi.databases.SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT;

public class HomeSelectCategoryActivity extends AppCompatActivity {

    private MutableLiveData<Integer> mModeStoreOrProduct = new MutableLiveData<>();
    private MutableLiveData<Integer> mModeCategoryStore = new MutableLiveData<>();
    private MutableLiveData<Integer> mModeCategoryProduct = new MutableLiveData<>();


    private RadioButton radbtnChecked;
    private RadioGroup radgrp_home_option_category_store, radgrp_home_option_category_product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_select_category);
        //new AsyncLayoutInflater(this).inflate(R.layout.activity_home_select_option, null, (view, resid, parent) -> initView());
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_home_select_category);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        //Group Radio btn
        RadioGroup radgrp_home_option_store_or_product;
        radgrp_home_option_store_or_product = findViewById(R.id.radgrp_home_option_store_or_product);
        radgrp_home_option_store_or_product.setOnCheckedChangeListener(mOnRadioCheckedChangeListener);

        radgrp_home_option_category_store = findViewById(R.id.radgrp_home_option_category_store);
        radgrp_home_option_category_store.setOnCheckedChangeListener(mOnRadioCheckedChangeListener);

        radgrp_home_option_category_product = findViewById(R.id.radgrp_home_option_category_product);
        radgrp_home_option_category_product.setOnCheckedChangeListener(mOnRadioCheckedChangeListener);

        //Load DATA
        mModeStoreOrProduct.setValue(SharedPrefs.getInstance().get(KEY_OPTION_LOAD_STORE_OR_PRODUCT, Integer.class, Contract.MODE_HOME_LOAD_STORE));

        mModeCategoryStore.setValue(SharedPrefs.getInstance().get(KEY_OPTION_CATEGORY_STORE, Integer.class, Contract.MODE_HOME_LOAD_STORE_TYPE_ALL));

        //TODO CHANGE CODE IF MORE CATEGORY PRODUCT: NOW - 1 TYPE PRODUT

        mModeCategoryProduct.setValue(Contract.MODE_HOME_LOAD_PRODUCT_TYPE_ALL);


        mModeStoreOrProduct.observe(this, integer -> {
            radgrp_home_option_category_store.setVisibility(integer == Contract.MODE_HOME_LOAD_STORE ? View.VISIBLE : View.GONE);
            radgrp_home_option_category_product.setVisibility(integer == Contract.MODE_HOME_LOAD_PRODUCT ? View.VISIBLE : View.GONE);
            if (integer == Contract.MODE_HOME_LOAD_STORE) {
                radbtnChecked = findViewById(R.id.radbtn_home_option_store);
                radbtnChecked.setChecked(true);
                switch (mModeCategoryStore.getValue()) {
                    case Contract.MODE_LOAD_STORE_TYPE_STORE:
                        radbtnChecked = findViewById(R.id.radbtn_home_option_category_store);
                        radbtnChecked.setChecked(true);
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_SUPER_MARKET:
                        radbtnChecked = findViewById(R.id.radbtn_home_option_category_super_market);
                        radbtnChecked.setChecked(true);
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_MALL:
                        radbtnChecked = findViewById(R.id.radbtn_home_option_category_mall);
                        radbtnChecked.setChecked(true);
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_CONVENIENCE:
                        radbtnChecked = findViewById(R.id.radbtn_home_option_category_convenience);
                        radbtnChecked.setChecked(true);
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_MARKET:
                        radbtnChecked = findViewById(R.id.radbtn_home_option_category_market);
                        radbtnChecked.setChecked(true);
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_ATM:
                        radbtnChecked = findViewById(R.id.radbtn_home_option_category_atm);
                        radbtnChecked.setChecked(true);
                        break;
                    case Contract.MODE_LOAD_STORE_TYPE_PHARMACY:
                        radbtnChecked = findViewById(R.id.radbtn_home_option_category_pharmacy);
                        radbtnChecked.setChecked(true);
                        break;
                    case Contract.MODE_HOME_LOAD_STORE_TYPE_ALL:
                        radbtnChecked = findViewById(R.id.radbtn_home_option_category_all);
                        radbtnChecked.setChecked(true);
                        break;
                    default:
                        radbtnChecked = findViewById(R.id.radbtn_home_option_category_all);
                        radbtnChecked.setChecked(true);
                        break;
                }
            } else {
                radbtnChecked = findViewById(R.id.radbtn_home_option_product);
                radbtnChecked.setChecked(true);
                switch (mModeCategoryProduct.getValue()) {
                    case Contract.MODE_HOME_LOAD_PRODUCT_TYPE_ALL:
                        radbtnChecked = findViewById(R.id.radbtn_home_option_category_product_all);
                        radbtnChecked.setChecked(true);
                        break;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.all_menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                SharedPrefs.getInstance().put(KEY_OPTION_LOAD_STORE_OR_PRODUCT, mModeStoreOrProduct.getValue());
                if (mModeStoreOrProduct.getValue().equals(Contract.MODE_HOME_LOAD_STORE)) {
                    SharedPrefs.getInstance().put(KEY_OPTION_CATEGORY_STORE, mModeCategoryStore.getValue());
                } else {
                    SharedPrefs.getInstance().put(KEY_OPTION_CATEGORY_PRODUCT, mModeCategoryProduct.getValue());
                }
                finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private RadioGroup.OnCheckedChangeListener mOnRadioCheckedChangeListener = (group, checkedId) -> {
        if (group.getId() == R.id.radgrp_home_option_category_store) {
            switch (checkedId) {
                case R.id.radbtn_home_option_category_store:
                    mModeCategoryStore.setValue(Contract.MODE_LOAD_STORE_TYPE_STORE);
                    break;
                case R.id.radbtn_home_option_category_convenience:
                    mModeCategoryStore.setValue(Contract.MODE_LOAD_STORE_TYPE_CONVENIENCE);
                    break;
                case R.id.radbtn_home_option_category_super_market:
                    mModeCategoryStore.setValue(Contract.MODE_LOAD_STORE_TYPE_SUPER_MARKET);
                    break;
                case R.id.radbtn_home_option_category_market:
                    mModeCategoryStore.setValue(Contract.MODE_LOAD_STORE_TYPE_MARKET);
                    break;
                case R.id.radbtn_home_option_category_mall:
                    mModeCategoryStore.setValue(Contract.MODE_LOAD_STORE_TYPE_MALL);
                    break;
                case R.id.radbtn_home_option_category_pharmacy:
                    mModeCategoryStore.setValue(Contract.MODE_LOAD_STORE_TYPE_PHARMACY);
                    break;
                case R.id.radbtn_home_option_category_atm:
                    mModeCategoryStore.setValue(Contract.MODE_LOAD_STORE_TYPE_ATM);
                    break;
                case R.id.radbtn_home_option_category_all:
                    mModeCategoryStore.setValue(Contract.MODE_HOME_LOAD_STORE_TYPE_ALL);
                    break;
            }
        }
        if (group.getId() == R.id.radgrp_home_option_category_product) {
            switch (checkedId) {
                case R.id.radbtn_home_option_category_product_all:
                    mModeCategoryProduct.setValue(Contract.MODE_HOME_LOAD_PRODUCT_TYPE_ALL);
                    break;
            }

        }
        if (group.getId() == R.id.radgrp_home_option_store_or_product) {
            mModeStoreOrProduct.setValue(checkedId == R.id.radbtn_home_option_store ? Contract.MODE_HOME_LOAD_STORE : Contract.MODE_HOME_LOAD_PRODUCT);
        }
    };
}
