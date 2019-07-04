package org.semi.listener;

import android.view.View;
import android.widget.AdapterView;

import org.semi.views.HomeActivity;
import org.semi.views.HomeOldActivity;

public class TypeSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private HomeOldActivity old_activity;
    private HomeActivity mActivity;
    public TypeSpinnerItemSelectedListener(HomeActivity activity) {
        mActivity = activity;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //mActivity.loadAllNewStoresOrProducts();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
