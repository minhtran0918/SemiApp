package org.semi.views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.semi.databases.SharedPrefs;
import org.semi.utils.Contract;

import java.util.List;

import static org.semi.databases.SharedPrefs.KEY_ALL_ADDRESS_CITY;
import static org.semi.databases.SharedPrefs.KEY_ALL_ADDRESS_DISTRICT;
import static org.semi.databases.SharedPrefs.KEY_ALL_ADDRESS_WARD;
import static org.semi.databases.SharedPrefs.KEY_OPTION_CATEGORY_PRODUCT;
import static org.semi.databases.SharedPrefs.KEY_OPTION_CATEGORY_STORE;
import static org.semi.databases.SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT;
import static org.semi.databases.SharedPrefs.KEY_OPTION_RANGE;
import static org.semi.databases.SharedPrefs.KEY_OPTION_RANGE_VALUE;
import static org.semi.databases.SharedPrefs.KEY_OPTION_SORT;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDataSharedPref();
        checkRequestPermission(this);
    }

    private void setupDataSharedPref() {
        if (SharedPrefs.getInstance().get(KEY_OPTION_LOAD_STORE_OR_PRODUCT, Integer.class, Contract.ALL_NOT_AVAILABLE) == Contract.ALL_NOT_AVAILABLE) {
            SharedPrefs.getInstance().put(KEY_OPTION_LOAD_STORE_OR_PRODUCT, Contract.MODE_HOME_LOAD_STORE);
        }
        if (SharedPrefs.getInstance().get(KEY_OPTION_CATEGORY_STORE, Integer.class, Contract.ALL_NOT_AVAILABLE) == Contract.ALL_NOT_AVAILABLE) {
            SharedPrefs.getInstance().put(KEY_OPTION_CATEGORY_STORE, Contract.MODE_HOME_LOAD_STORE_TYPE_ALL);
        }
        if (SharedPrefs.getInstance().get(KEY_OPTION_CATEGORY_PRODUCT, Integer.class, Contract.ALL_NOT_AVAILABLE) == Contract.ALL_NOT_AVAILABLE) {
            SharedPrefs.getInstance().put(KEY_OPTION_CATEGORY_PRODUCT, Contract.MODE_HOME_LOAD_PRODUCT_TYPE_ALL);
        }
        if (SharedPrefs.getInstance().get(KEY_OPTION_RANGE, Integer.class, Contract.ALL_NOT_AVAILABLE) == Contract.ALL_NOT_AVAILABLE) {
            SharedPrefs.getInstance().put(KEY_OPTION_RANGE, Contract.MODE_LOAD_RANGE_AROUND);
        }
        if (SharedPrefs.getInstance().get(KEY_OPTION_RANGE_VALUE, Float.class, -1f) == -1f) {
            SharedPrefs.getInstance().put(KEY_OPTION_RANGE_VALUE, Contract.MODE_LOAD_RANGE_AROUND_VALUE_DEFAULT);
        }
        if (SharedPrefs.getInstance().get(KEY_OPTION_SORT, Integer.class, Contract.ALL_NOT_AVAILABLE) == Contract.ALL_NOT_AVAILABLE) {
            SharedPrefs.getInstance().put(KEY_OPTION_SORT, Contract.MODE_LOAD_SORT_RANGE);
        }
        if (SharedPrefs.getInstance().get(KEY_ALL_ADDRESS_CITY, Integer.class, Contract.ALL_NOT_AVAILABLE) == Contract.ALL_NOT_AVAILABLE) {
            SharedPrefs.getInstance().put(KEY_ALL_ADDRESS_CITY, Contract.ALL_ADDRESS_CITY_DEFAULT);
        }
        if (SharedPrefs.getInstance().get(KEY_ALL_ADDRESS_DISTRICT, Integer.class, Contract.ALL_NOT_AVAILABLE) == Contract.ALL_NOT_AVAILABLE) {
            SharedPrefs.getInstance().put(KEY_ALL_ADDRESS_DISTRICT, Contract.ALL_ADDRESS_DISTRICT_DEFAULT);
        }
        if (SharedPrefs.getInstance().get(KEY_ALL_ADDRESS_WARD, Integer.class, Contract.ALL_NOT_AVAILABLE) == Contract.ALL_NOT_AVAILABLE) {
            SharedPrefs.getInstance().put(KEY_ALL_ADDRESS_WARD, Contract.ALL_ADDRESS_WARD_DEFAULT);
        }
    }

    private void checkRequestPermission(final Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            SharedPrefs.getInstance().editLocationPermission(true);
                        }
                        callIntentHomeActivity();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, final PermissionToken token) {
                        SharedPrefs.getInstance().editLocationPermission(false);
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog_Alert);
                        builder.setMessage("Ứng dụng cần quyền truy cập vị trí để có thể sử dụng hết tính năng")
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        token.continuePermissionRequest();
                                    }
                                });
                        final AlertDialog dialog = builder.create();
                        dialog.show(); //show() should be called before dialog.getButton().
/*
                        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
                        positiveButtonLL.gravity = Gravity.CENTER;
                        positiveButton.setLayoutParams(positiveButtonLL);*/
                    }
                })
                .check();
    }

    private void callIntentHomeActivity() {
        Intent toHomeIntent = new Intent(this, HomeActivity.class);
        startActivity(toHomeIntent);
        finish();
    }
}
