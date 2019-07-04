package org.semi.listener;

import android.view.View;

import org.semi.views.HomeActivity;
import org.semi.views.HomeOldActivity;

public class NearbyTextViewClickListener implements View.OnClickListener {
    private HomeActivity mActivity;
    public NearbyTextViewClickListener(HomeActivity activity) {
        mActivity = activity;
    }
    @Override
    public void onClick(View v) {
        //mActivity.loadAllNewStoresOrProducts();
    }
}
