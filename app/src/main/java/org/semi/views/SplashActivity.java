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

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkRequestPermission(this);
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
