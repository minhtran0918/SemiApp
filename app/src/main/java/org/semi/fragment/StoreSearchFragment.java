package org.semi.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.semi.MyApp;
import org.semi.R;
import org.semi.contract.Contract;
import org.semi.firebase.IResult;
import org.semi.firebase.StoreConnector;
import org.semi.listener.CitySpinnerItemSelectedListener;
import org.semi.listener.CountrySpinnerItemSelectedListener;
import org.semi.listener.DistrictSpinnerItemSelectedListener;
import org.semi.listener.IUseAddressSpinner;
import org.semi.listener.TownSpinnerItemSelectedListener;
import org.semi.object.Country;
import org.semi.object.IHaveIdAndName;
import org.semi.object.Location;
import org.semi.object.Store;
import org.semi.sqlite.AddressDBConnector;
import org.semi.util.LocationUtils;
import org.semi.util.ObjectUtils;

import java.util.List;

public class StoreSearchFragment extends Fragment implements ISearch,
        IUseAddressSpinner {

    private FragmentCreator fragmentCreator;
    private static final int STORE_VIEW = 0;
    private static final int NETWORK_ERR_VIEW = 1;
    private static final int EMPTY_ERR_VIEW = 2;
    private static final int LOAD_VIEW = 3;
    private Spinner countrySpinner;
    private Spinner citySpinner;
    private Spinner districtSpinner;
    private Spinner townSpinner;
    private Location currentLocation;
    private LocationUtils locationUtils;
    private int type;
    private String query;

    @Override
    public void search(int type, String query, int mode) {
        this.type = type;
        this.query = query;
        searchStores();
    }

    @Override
    public void scroll(String lastId) {
        getMoreStores(lastId);
    }

    @Override
    public void clickItem(String id) {
    }

    public StoreSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof IUseFragment) {
            ((IUseFragment)context).onFragmentAttached(this);
        }
        setupFragmentCreator();
        locationUtils = new LocationUtils();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupFragmentCreator() {
        final Bundle networkError = new Bundle();
        final Bundle emptyError = new Bundle();
        final Bundle loading = new Bundle();
        networkError.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_trees);
        networkError.putString(ErrorFragment.MESSAGE, getString(R.string.networkErrorMessage));
        emptyError.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_blank);
        emptyError.putString(ErrorFragment.MESSAGE, getString(R.string.emptyResultMessage));
        loading.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_beach);
        loading.putString(ErrorFragment.MESSAGE, getString(R.string.loadMessage));
        fragmentCreator = new FragmentCreator(R.id.fragmentContainer,
                getChildFragmentManager());
        fragmentCreator.add(StoreViewFragment.class, (Bundle)null)
                .add(ErrorFragment.class, networkError)
                .add(ErrorFragment.class, emptyError)
                .add(ErrorFragment.class, loading);
        fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_store_search, container, false);
        countrySpinner = view.findViewById(R.id.countrySpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        districtSpinner = view.findViewById(R.id.districtSpinner);
        townSpinner = view.findViewById(R.id.townSpinner);
        //event;
        countrySpinner.setOnItemSelectedListener(
                new CountrySpinnerItemSelectedListener(this));
        citySpinner.setOnItemSelectedListener(
                new CitySpinnerItemSelectedListener(this));
        districtSpinner.setOnItemSelectedListener(
                new DistrictSpinnerItemSelectedListener(this));
        townSpinner.setOnItemSelectedListener(
                new TownSpinnerItemSelectedListener(this));
        //init country spinner
        final AddressDBConnector connector = AddressDBConnector.getInstance();
        List<Country> countries = connector.getCountries();
        ArrayAdapter<Country> adapter = new ArrayAdapter<Country>(
                inflater.getContext(), android.R.layout.simple_spinner_item,
                countries
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);
        //Get country's saved value from SharedPreferences, and set it's selected.
        final SharedPreferences dataStore = MyApp.getContext().getSharedPreferences(Contract.SHARED_MY_STATE,
                Context.MODE_PRIVATE);
        countrySpinner.setSelection(dataStore.getInt(Contract.SHARED_COUNTRY_KEY, 0));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeLocationUpdates();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentCreator.recovery();
    }

    private void searchStores() {

        final StoreConnector storeConnector = StoreConnector.getInstance();
        fragmentCreator.setCurrentFragment(LOAD_VIEW);
        storeConnector.getStoresByKeywords(type, query, "", Contract.NUM_STORES_PER_REQUEST,
                getAddress(),
                new IResult<List<Store>>() {
                    @Override
                    public void onResult(List<Store> result) {
                        if (result.size() == 0) {
                            fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        StoreViewFragment fragment = (StoreViewFragment)
                                fragmentCreator.setCurrentFragmentNoArgs(STORE_VIEW);
                        fragment.updateDataSet(result, currentLocation);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        fragmentCreator.setCurrentFragment(NETWORK_ERR_VIEW);
                    }
                });
    }

    private void getMoreStores(String lastId) {
        final StoreConnector storeConnector = StoreConnector.getInstance();
        storeConnector.getStoresByKeywords(type, query, lastId, Contract.NUM_STORES_PER_REQUEST,
                getAddress(),
                new IResult<List<Store>>() {
                    @Override
                    public void onResult(List<Store> result) {
                        Fragment fragment = fragmentCreator.getCurrentFragment();
                        if(!(fragment instanceof StoreViewFragment)) {
                            return;
                        }
                        StoreViewFragment storeFragment = (StoreViewFragment) fragment;
                        if (result.size() == 0 && storeFragment.getItemCount() == 0) {
                            fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        storeFragment.addDataSet(result, currentLocation);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) { }
                });
    }

    private Object[] getAddress() {
        Object[] address = {
                countrySpinner.getSelectedItem(),
                citySpinner.getSelectedItem(),
                districtSpinner.getSelectedItem(),
                townSpinner.getSelectedItem()
        };
        Object[] ids = new Object[4];
        for(int i = 0; i < address.length; i++) {
            if(address[i] == null) {
                ids[i] = -1;
            } else {
                ids[i] = ((IHaveIdAndName)address[i]).getId();
            }
        }
        return ids;
    }

    private void requestLocationUpdate() {
        locationUtils.requestLocationUpdates(new IResult<android.location.Location>() {
            @Override
            public void onResult(android.location.Location result) {
                if(result == null) {
                    currentLocation = null;
                } else {
                    currentLocation = ObjectUtils.toMyLocation(result);
                }
                Fragment fragment = fragmentCreator.getCurrentFragment();
                if(fragment instanceof StoreViewFragment) {
                    ((StoreViewFragment)fragment).updateLocation(currentLocation);
                }
            }
            @Override
            public void onFailure(@NonNull Exception exp) { }
        });
    }

    private void removeLocationUpdates() {
        locationUtils.removeLocationUpdates();
    }

    @Override
    public Spinner getCountrySpinner() {
        return countrySpinner;
    }

    @Override
    public Spinner getCitySpinner() {
        return citySpinner;
    }

    @Override
    public Spinner getDistrictSpinner() {
        return districtSpinner;
    }

    @Override
    public Spinner getTownSpinner() {
        return townSpinner;
    }
}
