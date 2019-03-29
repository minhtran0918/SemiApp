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
import org.semi.object.City;
import org.semi.object.District;
import org.semi.sqlite.AddressDBConnector;

import java.util.ArrayList;
import java.util.List;

public class CitySpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private IUseAddressSpinner user;
    public CitySpinnerItemSelectedListener(IUseAddressSpinner user) {
        this.user = user;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(view == null) {
            return;
        }
        final Spinner districtSpinner = user.getDistrictSpinner();
        if(districtSpinner == null) {
            return;
        }
        final Context context = view.getContext();
        final City selectedCity = (City) parent.getSelectedItem();
        final List<District> districts = new ArrayList<>();
        districts.add(new District(-1,
                context.getString(R.string.districtLabel)));
        if(selectedCity.getId() != -1) {
            final AddressDBConnector connector = AddressDBConnector.getInstance();
            districts.addAll(connector.getDistrictsFromCity(
                    selectedCity.getId()));
        }
        final ArrayAdapter<District> adapter = new ArrayAdapter<District>(
                context, android.R.layout.simple_spinner_item, districts);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapter);
        final SharedPreferences dataStore = MyApp.getContext().getSharedPreferences(Contract.SHARED_MY_STATE,
                Context.MODE_PRIVATE);
        if(position != dataStore.getInt(Contract.SHARED_CITY_KEY, 0)) {
            final SharedPreferences.Editor editor = dataStore.edit();
            editor.putInt(Contract.SHARED_CITY_KEY, position);
            editor.putInt(Contract.SHARED_DISTRICT_KEY, 0);
            editor.putInt(Contract.SHARED_TOWN_KEY, 0);
            editor.apply();
        } else {
            districtSpinner.setSelection(dataStore.getInt(Contract.SHARED_DISTRICT_KEY, 0));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
