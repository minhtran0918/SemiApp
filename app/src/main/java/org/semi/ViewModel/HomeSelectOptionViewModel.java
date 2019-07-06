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

    public MutableLiveData<Integer> cityId,districtId,wardId;
    //Range
    public MutableLiveData<Integer> modeRange;
    //500-10000
    public MutableLiveData<Float> modeRangeValue;
    //Sort
    public MutableLiveData<Integer> modeSort;

    private AddressCity mCityByID;
    private AddressDistrict mDistrictByID;
    private AddressWard mWardByID;

    public HomeSelectOptionViewModel(@NonNull Application application) {
        super(application);
        addressRepository = new AddressRepository(application);

        cityId = new MutableLiveData<>();
        districtId = new MutableLiveData<>();
        wardId = new MutableLiveData<>();

        //Range
        modeRange = new MutableLiveData<>();

        //500-10000
        modeRangeValue = new MutableLiveData<>();

        //Sort
        modeSort = new MutableLiveData<>();
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
