package org.semi.listener;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.semi.MyApp;
import org.semi.R;
import org.semi.contract.Contract;
import org.semi.object.District;
import org.semi.object.Town;
import org.semi.sqlite.AddressDBConnector;

import java.util.ArrayList;
import java.util.List;

public class DistrictSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private IUseAddressSpinner user;

    public DistrictSpinnerItemSelectedListener(IUseAddressSpinner user) {
        this.user = user;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view == null) {
            return;
        }
        final Spinner townSpinner = user.getTownSpinner();
        if (townSpinner == null) {
            return;
        }
        final Context context = view.getContext();
        final District selectedDistrict = (District) parent.getSelectedItem();
        final List<Town> towns = new ArrayList<>();
        towns.add(new Town(-1,
                context.getString(R.string.townLabel)));
        if (selectedDistrict.getId() != -1) {
            final AddressDBConnector connector = AddressDBConnector.getInstance();
            towns.addAll(connector.getTownsFromDistrict(
                    selectedDistrict.getId()));
        }
        final ArrayAdapter<Town> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, towns);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        townSpinner.setAdapter(adapter);
        final SharedPreferences dataStore = MyApp.getContext().getSharedPreferences(Contract.SHARED_MY_STATE,
                Context.MODE_PRIVATE);
        if (position != dataStore.getInt(Contract.SHARED_DISTRICT_KEY, 0)) {
            final SharedPreferences.Editor editor = dataStore.edit();
            editor.putInt(Contract.SHARED_DISTRICT_KEY, position);
            editor.putInt(Contract.SHARED_TOWN_KEY, 0);
            editor.apply();
        } else {
            townSpinner.setSelection(dataStore.getInt(Contract.SHARED_TOWN_KEY, 0));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
