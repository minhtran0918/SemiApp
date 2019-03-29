package org.semi.listener;

import android.view.View;
import android.widget.AdapterView;

import org.semi.HomeActivity;

public class TypeSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private HomeActivity activity;
    public TypeSpinnerItemSelectedListener(HomeActivity activity) {
        this.activity = activity;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activity.loadAllNewStoresOrProducts();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
