package org.semi.views;

import android.app.Activity;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.semi.listener.CallBackSelectAddressListener;
import org.semi.utils.Contract;
import org.semi.R;
import org.semi.ViewModel.AddressViewModel;
import org.semi.fragment.SelectCityFragment;
import org.semi.fragment.SelectDistrictFragment;
import org.semi.fragment.SelectWardFragment;


public class SelectAddressActivity extends AppCompatActivity implements CallBackSelectAddressListener {

    private AddressViewModel mAddressViewModel;
    private Fragment mSelectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_select_address);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
            //white arrow toolbar
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.all_ic_arrow_back_white_24);
        upArrow.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        //
        mAddressViewModel = ViewModelProviders.of(this).get(AddressViewModel.class);

        //Initialize the value for the ViewModel from the intent received
        int mode = getIntent().getIntExtra(Contract.MODE_CHOOSE_ADDRESS_KEY, 0);
        mAddressViewModel.modeSelect.setValue(mode);

        int id_city = getIntent().getIntExtra(Contract.CITY_ADDRESS_ID_KEY, -1);
        mAddressViewModel.cityId.setValue(id_city);

        int id_district = getIntent().getIntExtra(Contract.DISTRICT_ADDRESS_ID_KEY, -1);
        mAddressViewModel.districtId.setValue(id_district);

        int id_ward = getIntent().getIntExtra(Contract.WARD_ADDRESS_ID_KEY, -1);
        mAddressViewModel.wardId.setValue(id_ward);

        callSelectFragment(mode, true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void callSelectFragment(int mode, Boolean first_add) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!first_add) {
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_right);
        }
        if (mode == 1) {
            mSelectFragment = new SelectCityFragment();
        } else if (mode == 2) {
            mSelectFragment = new SelectDistrictFragment();
        } else {
            mSelectFragment = new SelectWardFragment();
        }
        fragmentTransaction.add(R.id.container_select_address, mSelectFragment).commit();
    }

    @Override
    public void onCallBackReturnAddress(final Boolean change_ok) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (mAddressViewModel.modeSelect.getValue()) {
                    case 1:
                        mAddressViewModel.modeSelect.setValue(2);
                        callSelectFragment(mAddressViewModel.modeSelect.getValue(), false);
                        break;

                    case 2:
                        mAddressViewModel.modeSelect.setValue(3);
                        callSelectFragment(mAddressViewModel.modeSelect.getValue(), false);
                        break;

                    case 3:
                        sendDataToBackActivity();
                        break;
                }
            }
        }, 200);
    }

    private void sendDataToBackActivity() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Contract.CITY_ADDRESS_ID_KEY, mAddressViewModel.cityId.getValue());
        returnIntent.putExtra(Contract.DISTRICT_ADDRESS_ID_KEY, mAddressViewModel.districtId.getValue());
        returnIntent.putExtra(Contract.WARD_ADDRESS_ID_KEY, mAddressViewModel.wardId.getValue());
        setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
    }
}
