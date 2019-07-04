package org.semi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.semi.ViewModel.AddStoreViewModel;
import org.semi.ViewModel.HomeSelectOptionViewModel;
import org.semi.databases.model.AddressCity;
import org.semi.databases.model.AddressDistrict;
import org.semi.databases.model.AddressWard;
import org.semi.utils.Contract;
import org.semi.views.SelectAddressActivity;

public class HomeSelectOptionActivity extends AppCompatActivity {


    private int RC_SELECT_STORE_ADDRESS = 11;

    private HomeSelectOptionViewModel mHomeOptionViewModel;
    private TextView txt_select_city, txt_select_district, txt_select_ward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_select_option);
        initView();
        initViewModel();
    }

    private void initViewModel() {
        mHomeOptionViewModel = ViewModelProviders.of(this).get(HomeSelectOptionViewModel.class);

        //
        mHomeOptionViewModel.modeStoreOrProduct.setValue(Contract.MODE_HOME_LOAD_STORE);
        //79 - HCM, 762 - Thủ Đức, 0 - Không rõ
        mHomeOptionViewModel.cityId.setValue(79);
        mHomeOptionViewModel.districtId.setValue(762);
        mHomeOptionViewModel.wardId.setValue(0);
        //Around
        mHomeOptionViewModel.modeStoreOrProduct.setValue(Contract.MODE_LOAD_RANGE_AROUND);

        //Sort
        mHomeOptionViewModel.modeStoreOrProduct.setValue(Contract.MODE_LOAD_SORT_POPULAR);

        mHomeOptionViewModel.cityId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                AddressCity addressCity = mHomeOptionViewModel.getCityByID(integer);
                if (addressCity != null) {
                    txt_select_city.setText(addressCity.toLiteName());
                }
            }
        });

        mHomeOptionViewModel.districtId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                AddressDistrict addressDistrict = mHomeOptionViewModel.getDistrictByID(integer);
                if (addressDistrict != null) {
                    txt_select_district.setText(addressDistrict.toLiteName());
                }
            }
        });
        mHomeOptionViewModel.wardId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                AddressWard addressWard = mHomeOptionViewModel.getWardByID(integer);
                if (addressWard != null) {
                    txt_select_ward.setText(addressWard.toLiteName());
                }
            }
        });
    }

    private void initView() {

        //Address
        txt_select_city = findViewById(R.id.txt_home_option_city);
        txt_select_district = findViewById(R.id.txt_home_option_district);
        txt_select_ward = findViewById(R.id.txt_home_option_ward);

        txt_select_city.setOnClickListener(mOnClickSelectAddress);
        txt_select_district.setOnClickListener(mOnClickSelectAddress);
        txt_select_ward.setOnClickListener(mOnClickSelectAddress);

        //Group button
        RadioGroup radgrp_mode;
        RadioButton radbtn_mode_store, radbtn_mode_product;
        radgrp_mode = findViewById(R.id.radgrp_home_option_mode);
        radbtn_mode_store = findViewById(R.id.radbtn_home_option_mode_store);
        radbtn_mode_product = findViewById(R.id.radbtn_home_option_mode_product);
        radgrp_mode.setOnCheckedChangeListener(mOnCheckedChangeListener);


        RadioGroup radgrp_range;
        RadioButton radbtn_range_around, radbtn_range_all;
        radgrp_range = findViewById(R.id.radgrp_home_option_range);
        radbtn_range_around = findViewById(R.id.radbtn_home_option_range_around);
        radbtn_range_all = findViewById(R.id.radbtn_home_option_range_all);
        radgrp_range.setOnCheckedChangeListener(mOnCheckedChangeListener);


        RadioGroup radgrp_sort;
        RadioButton radbtn_sort_popular;
        radgrp_sort = findViewById(R.id.radgrp_home_option_sort);
        radbtn_sort_popular = findViewById(R.id.radbtn_home_option_sort_popular);
        radgrp_sort.setOnCheckedChangeListener(mOnCheckedChangeListener);

    }

    private View.OnClickListener mOnClickSelectAddress = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent callSelectAddress = new Intent(HomeSelectOptionActivity.this, SelectAddressActivity.class);
            switch (v.getId()) {
                case R.id.txt_home_option_city:
                    callSelectAddress.putExtra(Contract.MODE_CHOOSE_ADDRESS_KEY, Contract.SELECT_CITY_ADDRESS_MODE);
                    callSelectAddress.putExtra(Contract.CITY_ADDRESS_ID_KEY, mHomeOptionViewModel.cityId.getValue());
                    callSelectAddress.putExtra(Contract.DISTRICT_ADDRESS_ID_KEY, mHomeOptionViewModel.districtId.getValue());
                    callSelectAddress.putExtra(Contract.WARD_ADDRESS_ID_KEY, mHomeOptionViewModel.wardId.getValue());

                    startActivityForResult(callSelectAddress, RC_SELECT_STORE_ADDRESS);
                    break;
                case R.id.txt_home_option_district:
                    callSelectAddress.putExtra(Contract.MODE_CHOOSE_ADDRESS_KEY, Contract.SELECT_DISTRICT_ADDRESS_MODE);
                    callSelectAddress.putExtra(Contract.CITY_ADDRESS_ID_KEY, mHomeOptionViewModel.cityId.getValue());
                    callSelectAddress.putExtra(Contract.DISTRICT_ADDRESS_ID_KEY, mHomeOptionViewModel.districtId.getValue());
                    callSelectAddress.putExtra(Contract.WARD_ADDRESS_ID_KEY, mHomeOptionViewModel.wardId.getValue());

                    startActivityForResult(callSelectAddress, RC_SELECT_STORE_ADDRESS);
                    break;
                case R.id.txt_home_option_ward:
                    callSelectAddress.putExtra(Contract.MODE_CHOOSE_ADDRESS_KEY, Contract.SELECT_WARD_ADDRESS_MODE);
                    callSelectAddress.putExtra(Contract.DISTRICT_ADDRESS_ID_KEY, mHomeOptionViewModel.districtId.getValue());
                    callSelectAddress.putExtra(Contract.WARD_ADDRESS_ID_KEY, mHomeOptionViewModel.wardId.getValue());

                    startActivityForResult(callSelectAddress, RC_SELECT_STORE_ADDRESS);
                    break;
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (group.getId()) {
                case R.id.radgrp_home_option_mode:
                    Toast.makeText(HomeSelectOptionActivity.this, "change Mode", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.radgrp_home_option_range:
                    Toast.makeText(HomeSelectOptionActivity.this, "change range", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.radgrp_home_option_sort:
                    Toast.makeText(HomeSelectOptionActivity.this, "change sort", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_SELECT_STORE_ADDRESS && resultCode == RESULT_OK) {
            int city_id = data.getIntExtra(Contract.CITY_ADDRESS_ID_KEY, -1);
            if (city_id != -1) {
                mHomeOptionViewModel.cityId.setValue(city_id);
            }
            int district_id = data.getIntExtra(Contract.DISTRICT_ADDRESS_ID_KEY, -1);
            if (district_id != -1) {
                mHomeOptionViewModel.districtId.setValue(district_id);
            }
            int ward_id = data.getIntExtra(Contract.WARD_ADDRESS_ID_KEY, -1);
            if (ward_id != -1) {
                mHomeOptionViewModel.wardId.setValue(ward_id);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
