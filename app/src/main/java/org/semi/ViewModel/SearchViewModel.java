package org.semi.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.semi.databases.AddressRepository;
import org.semi.databases.SharedPrefs;
import org.semi.databases.model.AddressCity;
import org.semi.databases.model.AddressDistrict;
import org.semi.databases.model.AddressWard;
import org.semi.object.Location;
import org.semi.object.Product;
import org.semi.object.Store;
import org.semi.utils.Contract;

import java.security.cert.Extension;
import java.time.chrono.Era;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private AddressRepository addressRepository;

    public MutableLiveData<List<Store>> listStore = new MutableLiveData<>();
    public MutableLiveData<List<Product>> listProduct = new MutableLiveData<>();
    public MutableLiveData<Location> currentLocation = new MutableLiveData<>();

    public MutableLiveData<Integer> modeSelect = new MutableLiveData<>();

    private AddressCity mCityByID;
    private AddressDistrict mDistrictByID;
    private AddressWard mWardByID;

    //Mod config
    //Load Store or Product List
    public MutableLiveData<Integer> modeStoreOrProduct = new MutableLiveData<>();
    //Type of stores or products
    public MutableLiveData<Integer> categoryStore = new MutableLiveData<>();
    public MutableLiveData<Integer> categoryProduct = new MutableLiveData<>();
    //Address
    public MutableLiveData<Integer> cityId, districtId, wardId;
    //Range
    public MutableLiveData<Integer> modeRange;
    //500-10000
    public MutableLiveData<Float> modeRangeValue;


    public SearchViewModel(@NonNull Application application) {
        super(application);
        addressRepository = new AddressRepository(application);
        cityId = new MutableLiveData<>();
        districtId = new MutableLiveData<>();
        wardId = new MutableLiveData<>();

        modeRange = new MutableLiveData<>();

        //First data store
        modeStoreOrProduct.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT, Integer.class, Contract.MODE_HOME_LOAD_STORE)
        );

        loadAddressFromShared();
        categoryStore.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_CATEGORY_STORE, Integer.class, Contract.MODE_HOME_LOAD_STORE_TYPE_ALL));
        categoryProduct.setValue(SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_CATEGORY_PRODUCT, Integer.class, Contract.MODE_HOME_LOAD_PRODUCT_TYPE_ALL));
        modeRange.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_RANGE, Integer.class, Contract.MODE_LOAD_RANGE_AROUND)
        );
        modeRangeValue.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_RANGE_VALUE, Float.class, Contract.MODE_LOAD_RANGE_AROUND_VALUE_DEFAULT)
        );
    }
    private void loadAddressFromShared() {
        cityId.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_CITY, Integer.class, Contract.ALL_ADDRESS_CITY_DEFAULT)
        );
        districtId.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_DISTRICT, Integer.class, Contract.ALL_ADDRESS_DISTRICT_DEFAULT)
        );
        wardId.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_ALL_ADDRESS_WARD, Integer.class, Contract.ALL_ADDRESS_WARD_DEFAULT)
        );
    }
    public AddressCity getCityByID(int city_id) {
        mCityByID = addressRepository.getCityByID(city_id);
        return mCityByID;
    }

    public AddressDistrict getDistrictByID(int district_id) {
        mDistrictByID = addressRepository.getDistrictByID(district_id);
        return mDistrictByID;
    }

    public AddressWard getWardByID(int ward_id) {
        mWardByID = addressRepository.getWardByID(ward_id);
        return mWardByID;
    }

    public void updateListStore(List<Store> stores) {
        List<Store> temp = listStore.getValue();
        temp.addAll(stores);
        listStore.setValue(temp);
    }

    public void updateListProduct(List<Product> products) {
        List<Product> temp = listProduct.getValue();
        temp.addAll(products);
        listProduct.setValue(temp);
    }

}
