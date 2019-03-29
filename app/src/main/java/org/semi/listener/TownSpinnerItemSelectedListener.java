package org.semi.listener;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;

import org.semi.MyApp;
import org.semi.contract.Contract;

public class TownSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private IUseAddressSpinner user;
    public TownSpinnerItemSelectedListener(IUseAddressSpinner user) {
        this.user = user;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final SharedPreferences dataStore = MyApp.getContext().getSharedPreferences(Contract.SHARED_MY_STATE,
                Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = dataStore.edit();
        editor.putInt(Contract.SHARED_TOWN_KEY, position);
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
