package org.semi.listener;

import android.view.View;

import org.semi.HomeActivity;

public class NearbyTextViewClickListener implements View.OnClickListener {
    private HomeActivity activity;
    public NearbyTextViewClickListener(HomeActivity activity) {
        this.activity = activity;
    }
    @Override
    public void onClick(View v) {
        activity.loadAllNewStoresOrProducts();
    }
}
