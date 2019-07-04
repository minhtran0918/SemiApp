package org.semi.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import android.util.Log;

import org.semi.utils.Contract;
import org.semi.databases.AddressRepository;
import org.semi.databases.model.AddressCity;
import org.semi.databases.model.AddressDistrict;
import org.semi.databases.model.AddressWard;

import java.util.List;

public class AddressViewModel extends AndroidViewModel {

    private AddressRepository repository;

    public LiveData<List<AddressCity>> mListCities;
    public LiveData<List<AddressDistrict>> mListDistricts;
    public LiveData<List<AddressWard>> mListWards;

    public MutableLiveData<Integer> modeSelect = new MutableLiveData<>();
    public MutableLiveData<Integer> cityId = new MutableLiveData<>();
    public MutableLiveData<Integer> districtId = new MutableLiveData<>();
    public MutableLiveData<Integer> wardId = new MutableLiveData<>();

    public AddressViewModel(@NonNull Application application) {
        super(application);
        repository = new AddressRepository(application);
    }

    public LiveData<List<AddressCity>> getListAllCities() {
        mListCities = repository.getAllCities();
        return mListCities;
    }

    public LiveData<List<AddressDistrict>> getListDistrictByID() {
        mListDistricts = repository.getListDistrictByID(cityId.getValue());
        return mListDistricts;
    }

    public LiveData<List<AddressWard>> getListWardByID() {
        mListWards = repository.getListWardByID(districtId.getValue());
        return mListWards;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(Contract.TAG, "ViewModel destroyed");
    }

}
