package org.semi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zhouyou.view.seekbar.SignSeekBar;

import org.semi.ViewModel.HomeSelectOptionViewModel;
import org.semi.databases.SharedPrefs;
import org.semi.databases.model.AddressCity;
import org.semi.databases.model.AddressDistrict;
import org.semi.databases.model.AddressWard;
import org.semi.utils.Contract;
import org.semi.utils.StringUtils;
import org.semi.views.SelectAddressActivity;

public class HomeSelectOptionActivity extends AppCompatActivity {

    private int RC_SELECT_STORE_ADDRESS = 11;

    private HomeSelectOptionViewModel mHomeOptionViewModel;

    private RadioButton radbtn_range_around;
    private RadioButton radbtn_range_all;
    private RadioButton radbtn_sort_range;
    private RadioButton radbtn_sort_popular;
    private TextView txt_select_city, txt_select_district, txt_select_ward;
    private TextView lbl_home_option_address;
    private LinearLayout linearLayout_home_option_address;
    SignSeekBar seek_range;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_select_option);
        initView();
        new AsyncLayoutInflater(this).inflate(R.layout.activity_home_select_option, null, (view, resid, parent) -> initDataToView());
    }

    private void initView() {
        //Address
        lbl_home_option_address = findViewById(R.id.lbl_home_option_address);
        linearLayout_home_option_address = findViewById(R.id.linearLayout_home_option_address);
        txt_select_city = findViewById(R.id.txt_home_option_city);
        txt_select_district = findViewById(R.id.txt_home_option_district);
        txt_select_ward = findViewById(R.id.txt_home_option_ward);

        txt_select_city.setOnClickListener(mOnClickSelectAddress);
        txt_select_district.setOnClickListener(mOnClickSelectAddress);
        txt_select_ward.setOnClickListener(mOnClickSelectAddress);

        //Group button

        RadioGroup radgrp_range = findViewById(R.id.radgrp_home_option_store_or_product);
        radbtn_range_around = findViewById(R.id.radbtn_home_option_range_around);
        radbtn_range_all = findViewById(R.id.radbtn_home_option_range_all);
        radgrp_range.setOnCheckedChangeListener(mOnCheckedChangeListener);

        seek_range = findViewById(R.id.seekbar_home_option_range_around);

        seek_range.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                mHomeOptionViewModel.modeRangeValue.setValue(progressFloat);
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
            }
        });

        RadioGroup radgrp_sort = findViewById(R.id.radgrp_home_option_sort);
        radgrp_sort.setOnCheckedChangeListener(mOnCheckedChangeListener);
        radbtn_sort_range = findViewById(R.id.radbtn_home_option_sort_range);
        radbtn_sort_popular = findViewById(R.id.radbtn_home_option_sort_popular);

        //Button End
        Button btn_reset_option, btn_finish;
        btn_reset_option = findViewById(R.id.btn_home_option_reset_option);
        btn_reset_option.setOnClickListener(mOnClickLastAction);
        btn_finish = findViewById(R.id.btn_home_option_finish);
        btn_finish.setOnClickListener(mOnClickLastAction);

        //View Model
        mHomeOptionViewModel = ViewModelProviders.of(this).get(HomeSelectOptionViewModel.class);
        mHomeOptionViewModel.cityId.observe(this, integer -> {
            AddressCity addressCity = mHomeOptionViewModel.getCityByID(integer);
            if (addressCity != null) {
                txt_select_city.setText(addressCity.toLiteName());
            }
        });

        mHomeOptionViewModel.districtId.observe(this, integer -> {
            AddressDistrict addressDistrict = mHomeOptionViewModel.getDistrictByID(integer);
            if (addressDistrict != null) {
                txt_select_district.setText(addressDistrict.toLiteName());
            }
        });

        mHomeOptionViewModel.wardId.observe(this, integer -> {
            AddressWard addressWard = mHomeOptionViewModel.getWardByID(integer);
            if (addressWard != null) {
                txt_select_ward.setText(addressWard.toLiteName());
            }
        });

        mHomeOptionViewModel.modeRange.observe(this, integer -> {
            seek_range.setVisibility(integer == Contract.MODE_LOAD_RANGE_AROUND ? View.VISIBLE : View.GONE);
            lbl_home_option_address.setVisibility(integer == Contract.MODE_LOAD_RANGE_ALL ? View.VISIBLE : View.GONE);
            linearLayout_home_option_address.setVisibility(integer == Contract.MODE_LOAD_RANGE_ALL ? View.VISIBLE : View.GONE);
        });
    }

    private void initDataToView() {
        //Get data from Shared Prefrenrece
        //79 - HCM, 762 - Thủ Đức, 0 - Không rõ
        mHomeOptionViewModel.cityId.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_CITY, Integer.class, Contract.ALL_ADDRESS_CITY_DEFAULT)
        );
        mHomeOptionViewModel.districtId.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_DISTRICT, Integer.class, Contract.ALL_ADDRESS_DISTRICT_DEFAULT)
        );
        mHomeOptionViewModel.wardId.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_WARD, Integer.class, Contract.ALL_ADDRESS_WARD_DEFAULT)
        );
        //Range Around
        mHomeOptionViewModel.modeRange.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_RANGE, Integer.class, Contract.MODE_LOAD_RANGE_AROUND)
        );

        //0.5 km->10 km
        mHomeOptionViewModel.modeRangeValue.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_RANGE_VALUE, Float.class, Contract.MODE_LOAD_RANGE_AROUND_VALUE_DEFAULT)
        );

        //Sort -> default range acs
        mHomeOptionViewModel.modeSort.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_SORT, Integer.class, Contract.MODE_LOAD_SORT_RANGE)
        );

        //range
        if (mHomeOptionViewModel.modeRange.getValue() == Contract.MODE_LOAD_RANGE_AROUND) {
            radbtn_range_around.setChecked(true);
        } else {
            radbtn_range_all.setChecked(true);
        }
        seek_range.setProgress(mHomeOptionViewModel.modeRangeValue.getValue());

        seek_range.setValueFormatListener(StringUtils::toDistanceFormat);

        //around


        if (mHomeOptionViewModel.modeSort.getValue() == Contract.MODE_LOAD_SORT_RANGE) {
            radbtn_sort_range.setChecked(true);
        } else {
            radbtn_sort_popular.setChecked(true);
        }
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

    private View.OnClickListener mOnClickLastAction = v -> {
        switch (v.getId()) {
            case R.id.btn_home_option_reset_option:
                initDataToView();
                break;
            case R.id.btn_home_option_finish:
                finishActivity();
                break;
        }
    };

    private RadioGroup.OnCheckedChangeListener  mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (group.getId()) {
                case R.id.radgrp_home_option_store_or_product:
                    mHomeOptionViewModel.modeRange.setValue(
                            checkedId == R.id.radbtn_home_option_range_around ?
                                    Contract.MODE_LOAD_RANGE_AROUND : Contract.MODE_LOAD_RANGE_ALL);
                    break;
                case R.id.radgrp_home_option_sort:
                    switch (checkedId) {
                        case R.id.radbtn_home_option_sort_range:
                            mHomeOptionViewModel.modeSort.setValue(Contract.MODE_LOAD_SORT_RANGE);
                            break;
                        case R.id.radbtn_home_option_sort_popular:
                            mHomeOptionViewModel.modeSort.setValue(Contract.MODE_LOAD_SORT_POPULAR);
                            break;
                    }
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

    private void finishActivity() {
        SharedPrefs.getInstance().put(SharedPrefs.KEY_ALL_ADDRESS_CITY, mHomeOptionViewModel.cityId.getValue());
        SharedPrefs.getInstance().put(SharedPrefs.KEY_ALL_ADDRESS_DISTRICT, mHomeOptionViewModel.districtId.getValue());
        SharedPrefs.getInstance().put(SharedPrefs.KEY_ALL_ADDRESS_WARD, mHomeOptionViewModel.wardId.getValue());
        SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_RANGE, mHomeOptionViewModel.modeRange.getValue());
        SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_RANGE_VALUE, mHomeOptionViewModel.modeRangeValue.getValue());
        SharedPrefs.getInstance().put(SharedPrefs.KEY_OPTION_SORT, mHomeOptionViewModel.modeSort.getValue());
        setResult(Activity.RESULT_OK);
        finish();
    }
}
