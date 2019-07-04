package org.semi.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.semi.databases.AddressRepository;
import org.semi.databases.model.AddressCity;
import org.semi.databases.model.AddressDistrict;
import org.semi.databases.model.AddressWard;

public class HomeSelectOptionViewModel extends AndroidViewModel {

    private AddressRepository addressRepository;

    //Store or Product
    public MutableLiveData<Integer> modeStoreOrProduct = new MutableLiveData<>();

    public MutableLiveData<Integer> cityId = new MutableLiveData<>();
    public MutableLiveData<Integer> districtId = new MutableLiveData<>();
    public MutableLiveData<Integer> wardId = new MutableLiveData<>();

    //Range
    public MutableLiveData<Integer> modeRange = new MutableLiveData<>();

    //Sort
    public MutableLiveData<Integer> modeSort = new MutableLiveData<>();

    private AddressCity mCityByID;
    private AddressDistrict mDistrictByID;
    private AddressWard mWardByID;

    public HomeSelectOptionViewModel(@NonNull Application application) {
        super(application);
        addressRepository = new AddressRepository(application);
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
}
