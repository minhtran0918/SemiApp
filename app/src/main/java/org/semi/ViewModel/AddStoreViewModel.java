package org.semi.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.esafirm.imagepicker.model.Image;

import org.semi.databases.AddressRepository;
import org.semi.databases.model.AddressCity;
import org.semi.databases.model.AddressDistrict;
import org.semi.databases.model.AddressWard;

import java.util.ArrayList;

public class AddStoreViewModel extends AndroidViewModel {

    private AddressRepository addressRepository;

    public MutableLiveData<Integer> typeStoreID = new MutableLiveData<>();

    //Category
    public MutableLiveData<Integer> categoryStore = new MutableLiveData<>();
    //Address
    public MutableLiveData<Integer> chooseCityId = new MutableLiveData<>();
    public MutableLiveData<Integer> chooseDistrictId = new MutableLiveData<>();
    public MutableLiveData<Integer> chooseWardId = new MutableLiveData<>();

    private AddressCity mCityByID;
    private AddressDistrict mDistrictByID;
    private AddressWard mWardByID;

    //Location
    public MutableLiveData<Double> latStore = new MutableLiveData<>();
    public MutableLiveData<Double> lngStore = new MutableLiveData<>();
    public MutableLiveData<String> titleLocationStore = new MutableLiveData<>();

    //Time Store
    public MutableLiveData<Integer> timeStoreOpenHour = new MutableLiveData<>();
    public MutableLiveData<Integer> timeStoreOpenMinute = new MutableLiveData<>();
    public MutableLiveData<Integer> timeStoreCloseHour = new MutableLiveData<>();
    public MutableLiveData<Integer> timeStoreCloseMinute = new MutableLiveData<>();

    //List Image
    public MutableLiveData<ArrayList<Image>> listImageSelect = new MutableLiveData<>();

    public AddStoreViewModel(@NonNull Application application) {
        super(application);
        addressRepository = new AddressRepository(application);
    }

    //Get ID City - District - Ward
    /*public MutableLiveData<Integer> getChooseDistrictId() {
        return chooseDistrictId;
    }
    public MutableLiveData<Integer> getChooseCityId() {
        return chooseCityId;
    }
    public MutableLiveData<Integer> getChooseWardId() {
        return chooseWardId;
    }
*/
    //Set ID City - District - Ward
   /* public void setChooseCityId(Integer chooseCityId) {
        this.chooseCityId.setValue(chooseCityId);
    }
    public void setChooseDistrictId(Integer chooseDistrictId) {
        this.chooseDistrictId.setValue(chooseDistrictId);
    }
    public void setChooseWardId(Integer chooseWardId) {
        this.chooseWardId.setValue(chooseWardId);
    }
*/
    //Get Name City - District - Ward
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
    public void setTitleLocationStore(){
        StringBuilder sb = new StringBuilder();
        sb.append(latStore.getValue().doubleValue()).append(" - ");
        sb.append(lngStore.getValue().doubleValue());
        titleLocationStore.setValue(sb.toString());
    }
}
