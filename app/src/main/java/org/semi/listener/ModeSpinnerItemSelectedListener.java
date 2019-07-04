package org.semi.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.semi.utils.Contract;
import org.semi.views.HomeOldActivity;
import org.semi.R;
import org.semi.object.IHaveIdAndName;
import org.semi.object.Product;
import org.semi.object.Store;
import org.semi.utils.ObjectUtils;

import java.util.List;

public class ModeSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private HomeOldActivity activity;
    public ModeSpinnerItemSelectedListener(HomeOldActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case Contract.STORE_MODE:
                activity.getNearbyTextView().setText(R.string.nearbyStoreLabel);
                loadStoreTypesToSpinner();
                break;
            case Contract.PRODUCT_MODE:
                activity.getNearbyTextView().setText(R.string.nearbyProductLabel);
                loadProductTypesToSpinner();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    private void loadStoreTypesToSpinner() {
        List<Store.Type> storeTypeList = ObjectUtils.getStoreTypes();
        Store.Type[] storeTypes = new Store.Type[storeTypeList.size()];
        storeTypeList.toArray(storeTypes);
        ArrayAdapter<IHaveIdAndName> adapter = new ArrayAdapter<IHaveIdAndName>(activity,
                android.R.layout.simple_spinner_item, storeTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activity.getTypeSpinner().setAdapter(adapter);
    }

    private void loadProductTypesToSpinner() {
        List<Product.Type> productTypeList = ObjectUtils.getProductTypes();
        Product.Type[] productTypes = new Product.Type[productTypeList.size()];
        productTypeList.toArray(productTypes);
        ArrayAdapter<IHaveIdAndName> adapter= new ArrayAdapter<IHaveIdAndName>(activity,
                android.R.layout.simple_spinner_item, productTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activity.getTypeSpinner().setAdapter(adapter);
    }
}
