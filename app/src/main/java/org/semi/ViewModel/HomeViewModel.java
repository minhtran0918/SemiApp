package org.semi.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import org.semi.databases.SharedPrefs;
import org.semi.object.Location;
import org.semi.object.Product;
import org.semi.object.Store;
import org.semi.utils.Contract;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    public MutableLiveData<List<Store>> listStore = new MutableLiveData<>();
    //public MutableLiveData<List<Store>> listStoreAddMore = new MutableLiveData<>();
    public MutableLiveData<List<Product>> listProduct = new MutableLiveData<>();
    public MutableLiveData<Location> currentLocation = new MutableLiveData<>();


    //Mode Config
    //Navigation 4 Direction -> config here:
    public MutableLiveData<Integer> modeMenuNavigationHome = new MutableLiveData<>();

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

    //Sort
    public MutableLiveData<Integer> modeSort;

    public HomeViewModel(@NonNull Application application) {
        super(application);

        cityId = new MutableLiveData<>();
        districtId = new MutableLiveData<>();
        wardId = new MutableLiveData<>();
        modeRange = new MutableLiveData<>();
        modeRangeValue = new MutableLiveData<>();
        modeSort = new MutableLiveData<>();

        //First menu home
        modeMenuNavigationHome.setValue(Contract.MODE_MENU_HOME);
        //First data store
        modeStoreOrProduct.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_LOAD_STORE_OR_PRODUCT, Integer.class, Contract.MODE_HOME_LOAD_STORE)
        );
        loadAddressFromShared();
        //First data store type all
        categoryStore.setValue(Contract.MODE_HOME_LOAD_STORE_TYPE_ALL);
        //
        modeRange.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_RANGE, Integer.class, Contract.MODE_LOAD_RANGE_AROUND)
        );
        modeRangeValue.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_RANGE_VALUE, Float.class, Contract.MODE_LOAD_RANGE_AROUND_VALUE_DEFAULT)
        );
        modeSort.setValue(
                SharedPrefs.getInstance().get(SharedPrefs.KEY_OPTION_SORT, Integer.class, Contract.MODE_LOAD_SORT_RANGE)
        );
    }

    public void updateListStore(List<Store> stores) {
        List<Store> temp = listStore.getValue();
        temp.addAll(stores);
        listStore.setValue(temp);
    }

    public void loadAddressFromShared() {
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
}
